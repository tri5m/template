package com.example.template.service.template.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.example.template.common.helper.BeanFiller;
import com.example.template.common.helper.exception.InvokeException;
import com.example.template.common.helper.exception.NoAuthException;
import com.example.template.common.helper.exception.ValidateException;
import com.example.template.common.util.ImageVerCodeUtil;
import com.example.template.common.util.JsonUtil;
import com.example.template.common.util.MD5Util;
import com.example.template.repo.entity.Authorization;
import com.example.template.repo.mapper.AdminMapper;
import com.example.template.repo.mapper.AuthorizationMapper;
import com.example.template.service.template.model.ro.AdminLoginRo;
import com.example.template.service.template.model.ro.EditPwdRo;
import com.example.template.service.template.model.vo.auth.LoginVo;
import com.example.template.services.common.cache.RedisCacheService;
import com.example.template.services.common.configuration.AppConfig;
import com.example.template.services.common.context.UserContext;
import com.example.template.services.common.holder.RequestHolder;
import com.example.template.services.common.model.enumeration.AuthorizationType;
import com.example.template.services.common.model.enumeration.Const;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @title: UserAuthService
 * @author: trifolium
 * @date: 2023/1/9
 * @modified :
 */
@Service
@Transactional
public class UserAuthService {

    @Inject
    private RedissonClient redissonClient;

    @Inject
    private RedisCacheService redisCacheService;

    @Inject
    private UserContext adminContext;

    @Inject
    private AdminMapper adminMapper;

    @Inject
    private AuthorizationMapper authorizationMapper;

    @Inject
    private AppConfig appConfig;

    public LoginVo login(AdminLoginRo ro) {
        var rateLimiter = redissonClient.getRateLimiter("rate_limiter:user_login:" + ro.getUserName());
        // 通过用户名限制刷验证码
        rateLimiter.trySetRate(RateType.OVERALL, 10, 5, RateIntervalUnit.MINUTES);
        if (rateLimiter.tryAcquire()) {
            checkCaptcha(ro.getCaptcha());
            var admins = adminMapper.findByUserName(ro.getUserName());
            var admin = CollUtil.isEmpty(admins) ? null : admins.get(0);
            if (admin == null || !StrUtil.equalsIgnoreCase(MD5Util.getMD5String(ro.getPassword()),
                    admin.getPassword())) {
                throw new ValidateException("账号或密码错误");
            }
            if (BooleanUtil.isTrue(admin.getIsBanned())) {
                throw new NoAuthException("账号被禁用，无法登录");
            }

            // 组装前台需要的数据
            var loginVo = new LoginVo();
            var adminVo = BeanFiller.target(LoginVo.LoginAdminVo.class).accept(admin);

            List<Authorization> adminAuthorizations = new ArrayList<>();
            if (!admin.getIsSuper()) {
                adminVo.setRoleCodes(JsonUtil.jsonToList(admin.getRoleCodes(), String.class));
                if (CollUtil.isNotEmpty(adminVo.getRoleCodes())) {
                    for (String roleCode : adminVo.getRoleCodes()) {
                        List<Authorization> roleAuth = authorizationMapper.findAuthorizationByRoleCode(roleCode);
                        adminAuthorizations.addAll(roleAuth);
                    }

                    // 设置接口权限
                    loginVo.setAuthorities(BeanFiller.target(LoginVo.LoginAuthorityVo.class)
                            .acceptList(adminAuthorizations.stream().filter(
                                    a -> Objects.equals(AuthorizationType.INTERFACE.getCode(),
                                            String.valueOf(a.getType()))).collect(Collectors.toList())));
                } else {
                    throw new InvokeException("角色禁用");
                }
            }
            loginVo.setAdminVo(adminVo);
            long expires = System.currentTimeMillis() + (appConfig.getTimeout() * 1000);
            // 生成jwt
            loginVo.setToken(JWT.create()
                    .setSubject(String.valueOf(admin.getId()))
                    .setExpiresAt(new Date(expires))
                    .setIssuedAt(new Date())
                    .setJWTId(StrUtil.uuid().replace("-", ""))
                    .setPayload("name", admin.getName())
                    .setKey(Const.JWT_PRIVATE_KEY.getBytes()).sign());

            loginVo.setTokenType("Bearer");
            loginVo.setExpires(expires);

            // 缓存用户与权限数据
            adminContext.login(admin, adminAuthorizations, appConfig.getTimeout());
            return loginVo;

        } else {

            throw new InvokeException("连续输入错误次数太多,请5分钟之后再试!");
        }
    }

    /**
     * 输出验证码
     */
    public void captchaImage(HttpServletResponse response) throws IOException {
        var session = RequestHolder.getSession();
        if (session == null) {
            throw new RuntimeException("请求错误");
        }
        var sessionId = session.getId();
        var ip = RequestHolder.getRemoteIp();
        // 通过ip简单限制刷验证码
        var rateLimiter = redissonClient.getRateLimiter("rate_limiter:get_captcha_image:" + ip);
        rateLimiter.trySetRate(RateType.OVERALL, 60, 3, RateIntervalUnit.MINUTES);
        if (rateLimiter.tryAcquire()) {

            var cookie = new Cookie(Const.CAPTCHA_SESSION_NAME, sessionId);
            cookie.setPath("/");
            response.addCookie(cookie);
            response.addHeader(Const.CAPTCHA_SESSION_NAME, sessionId);
            response.addHeader("Access-Control-Expose-Headers", "*");
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            session.setAttribute(Const.CAPTCHA_SESSION_NAME, sessionId);
            String captcha = new ImageVerCodeUtil().outputVerifyImage(200, 50, response.getOutputStream(), 4);
            // 图片验证码有效期10分钟
            redisCacheService.set(Const.REDIS_USER_LOGIN_CAPTCHA_PREFIX + sessionId, captcha, 60 * 10L);
        } else {

            throw new InvokeException("请求次数过多，请稍后再试.");
        }
    }

    /**
     * 校验登录验证码
     */
    private void checkCaptcha(String captcha) {
        // 验证图片验证码
        var request = RequestHolder.getRequest();
        if (request == null) {
            throw new RuntimeException("请求错误");
        }
        Cookie[] cookies = request.getCookies();
        String sessionId = null;
        if (cookies != null) {
            sessionId = Arrays.stream(cookies)
                    .filter(c -> c.getName().equals(Const.CAPTCHA_SESSION_NAME)).map(
                            Cookie::getValue).findFirst().orElse(null);
        }
        if (StrUtil.isEmpty(sessionId)) {
            sessionId = request.getHeader(Const.CAPTCHA_SESSION_NAME);
            if (StrUtil.isEmpty(sessionId)) {
                var session = RequestHolder.getSession();
                sessionId = session == null ? null : ((String) session.getAttribute(Const.CAPTCHA_SESSION_NAME));
            }
        }

        String cacheCaptcha = redisCacheService.getString(Const.REDIS_USER_LOGIN_CAPTCHA_PREFIX + sessionId);

        if (StrUtil.isEmpty(captcha) || !captcha.equalsIgnoreCase(cacheCaptcha)) {

            redisCacheService.delete(Const.REDIS_USER_LOGIN_CAPTCHA_PREFIX + sessionId);
            throw new ValidateException("验证码错误");
        }
    }

    /**
     * 修改密码
     */
    public void editPwd(EditPwdRo ro) {
        var admin = adminContext.getAdmin();

        var adminDb = adminMapper.getById(admin.getId());

        if (adminDb == null || !StrUtil.equalsIgnoreCase(MD5Util.getMD5String(ro.getOldPwd()), adminDb.getPassword())) {
            throw new InvokeException("原密码错误");
        }

        adminDb.setPassword(MD5Util.getMD5String(ro.getNewPwd()));

        adminMapper.update(adminDb);
    }

    /**
     * 管理员登出
     */
    public void logout() {

        adminContext.logout();
    }

}
