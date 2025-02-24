package com.example.template.services.common.aspect;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.RegisteredPayload;
import com.example.template.common.helper.exception.InvokeException;
import com.example.template.common.helper.exception.NoAuthException;
import com.example.template.common.helper.exception.TokenInvalidException;
import com.example.template.common.util.JsonUtil;
import com.example.template.repo.entity.Admin;
import com.example.template.repo.entity.Authorization;
import com.example.template.services.common.annotion.AuthValidator;
import com.example.template.services.common.cache.RedisCacheService;
import com.example.template.services.common.holder.RequestHolder;
import com.example.template.services.common.model.enumeration.AuthorizationType;
import com.example.template.services.common.model.enumeration.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @title: 权限过滤注解
 * @author: trifolium
 * @date: 2019/3/12 17:41
 * @modified :
 */
@Order(1113)
@Slf4j
@Aspect
@Component
public class AuthValidatorAspect {

    @Inject
    private RedisCacheService cacheService;

    /**
     * pointcut
     */
    @Pointcut("@within(com.example.template.services.common.annotion.AuthValidator) " +
            "|| @annotation(com.example.template.services.common.annotion.AuthValidator)")
    private void tokenAuthCheck() {

    }

    @Around("tokenAuthCheck()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        AuthValidator tokenValidator = targetMethod.getAnnotation(AuthValidator.class);

        // 方法上的注解优先级大于类
        if (tokenValidator == null) {
            tokenValidator = pjp.getTarget().getClass().getAnnotation(AuthValidator.class);
            if (tokenValidator != null) {
                checkToken(tokenValidator);
            }
        } else {
            checkToken(tokenValidator);
        }

        return pjp.proceed();
    }

    public void checkToken(AuthValidator adminTokenValidator) {

        String errMsg = "令牌验证失败";

        boolean isCheckLogin = adminTokenValidator.isCheckLogin();
        if (!isCheckLogin) {
            return;
        }

        HttpServletRequest request = RequestHolder.getRequest();
        if (request == null) {
            throw new InvokeException("无法获取请求对象");
        }
        String token = "";
        Enumeration<String> headers = request.getHeaders(Const.HTTP_HEADER_AUTH_KEY);
        while (headers.hasMoreElements()) {
            String hv = headers.nextElement();
            if (StringUtils.isNoneBlank(hv)) {
                token = hv;
            }
        }
        if (StringUtils.isBlank(token)) {
            // 未携带token
            throw new TokenInvalidException(errMsg);
        }

        String userId;
        try {
            int space = token.indexOf(" ");
            token = token.substring(space == -1 ? 0 : space).trim();
            JWT jwt = JWT.of(token);
            if (!jwt.setKey(Const.JWT_PRIVATE_KEY.getBytes()).verify()) {
                throw new TokenInvalidException(errMsg);
            }
            userId = String.valueOf(jwt.getPayload(RegisteredPayload.SUBJECT));
            long expires = Long.parseLong(jwt.getPayload(RegisteredPayload.EXPIRES_AT).toString());
            if (System.currentTimeMillis() > expires * 1000) {
                throw new TokenInvalidException("令牌失效");
            }
        } catch (TokenInvalidException e) {
            throw e;
        } catch (Exception e) {

            throw new TokenInvalidException(errMsg, e);
        }

        // 校验权限

        // 将管理员信息放到request中
        Admin admin = cacheService.get(Const.REDIS_USER_KEY_PREFIX + userId, Admin.class);
        if (admin == null) {
            throw new TokenInvalidException("令牌失效");
        }
        request.setAttribute(Const.CURRENT_USER_REQUEST_ATTRIBUTE_KEY, admin);
        // 超级管理员用户可不用鉴权
        if (admin.getIsSuper() != null && admin.getIsSuper()) {
            return;
        }
        // 需要检查权限
        if (adminTokenValidator.isCheckPermission()) {
            String[] authorities = adminTokenValidator.authorities();
            if (authorities.length == 0) {
                return;
            }

            List<Authorization> authorizationList = JsonUtil.jsonToList(cacheService
                            .getString(Const.REDIS_USER_AUTHORITY_PREFIX + userId),
                    Authorization.class);

            if (authorizationList != null) {
                authorizationList = authorizationList.stream().filter(a ->
                        Objects.equals(AuthorizationType.INTERFACE.getCode(),
                                String.valueOf(a.getType()))).collect(Collectors.toList());
            }

            boolean isAnd = adminTokenValidator.isAnd();
            if (isAnd) {
                // 与权限,必须同时拥有注解上的所有权限
                for (String authority : authorities) {
                    if (authorizationList == null || authorizationList.stream()
                            .noneMatch(a -> a.getCode().equals(authority))) {

                        throw new NoAuthException("无权限");
                    }
                }
            } else {
                // 或权限，有注解上其中一个权限
                boolean isPermit = false;
                for (String authority : authorities) {
                    if (authorizationList != null
                            && authorizationList.stream().anyMatch(a -> a.getCode().equals(authority))) {
                        isPermit = true;
                    }
                }
                if (!isPermit) {
                    throw new NoAuthException("无权限");
                }
            }

        }

    }

}
