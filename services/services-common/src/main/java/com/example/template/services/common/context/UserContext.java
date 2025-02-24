package com.example.template.services.common.context;

import cn.hutool.core.util.StrUtil;
import com.example.template.common.util.JsonUtil;
import com.example.template.repo.entity.Admin;
import com.example.template.repo.entity.Authorization;
import com.example.template.repo.mapper.AdminMapper;
import com.example.template.services.common.cache.RedisCacheService;
import com.example.template.services.common.holder.RequestHolder;
import com.example.template.services.common.model.enumeration.Const;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @title: 登录用户上下文
 * 基于请求上下文
 * @author: trifolium
 * @date: 2021/9/1
 * @modified :
 */
@Slf4j
@Component
public class UserContext {

    @Inject
    private RedisCacheService cacheService;

    @Inject
    private AdminMapper adminMapper;

    private static final Cache<Long, Admin> USER_INFO_CACHE = Caffeine.newBuilder()
            .maximumSize(100).expireAfterWrite(1, TimeUnit.HOURS).build();

    /**
     * 获取当前请求管理员
     */
    public Admin getAdmin() {
        HttpServletRequest request = RequestHolder.getRequest();
        if (request == null) {

            return null;
        }
        Object object = request.getAttribute(Const.CURRENT_USER_REQUEST_ATTRIBUTE_KEY);
        if (object != null) {

            return (Admin) object;
        }

        return null;
    }

    public Long getAdminIdIfDefault(Long defaultId) {
        Admin admin = getAdmin();
        if (admin == null) {
            return defaultId;
        }

        return admin.getId();
    }

    /**
     * 根据id获取管理员，如果缓存中不存在直接从数据库中获取
     */
    public Admin getAdmin(Long id) {
        return USER_INFO_CACHE.get(id, (userId) -> {
            Admin admin = cacheService.get("user_info:" + id, Admin.class);
            if (admin != null) {
                return admin;
            }
            Admin db = adminMapper.getById(id);
            if (db != null) {
                cacheService.set("user_info:" + id, db, 1, TimeUnit.HOURS);
            }
            return db;
        });
    }

    public void login(Admin admin, List<Authorization> authorizationList, Long expiration) {
        cacheService.set(Const.REDIS_USER_KEY_PREFIX + admin.getId(), admin, expiration);
        cacheService.set(Const.REDIS_USER_AUTHORITY_PREFIX + admin.getId(), JsonUtil.toJson(authorizationList),
                expiration);
    }

    public void logout() {
        Admin admin = getAdmin();
        logout(admin.getId());
    }

    public void logout(Long id) {
        cacheService.delete(Const.REDIS_USER_KEY_PREFIX + id);
        cacheService.delete(Const.REDIS_USER_AUTHORITY_PREFIX + id);
    }

    public void updateLoginAdmin(Admin admin, List<Authorization> authorizationList) {
        cacheService.set(Const.REDIS_USER_KEY_PREFIX + admin.getId(), admin);
        cacheService.set(Const.REDIS_USER_AUTHORITY_PREFIX + admin.getId(), JsonUtil.toJson(authorizationList));
    }

    public List<Authorization> authorities() {

        return JsonUtil.jsonToList(cacheService.getString(
                        Const.REDIS_USER_AUTHORITY_PREFIX + getAdmin().getId(), Const.REDIS_USER_CACHE_EXPIRE),
                Authorization.class);
    }

    /**
     * 判断当前用户是否有权限
     */
    public boolean hasAuthority(String authCode) {
        if (getAdmin().getIsSuper()) {

            return true;
        }
        List<Authorization> authorizationList = authorities();

        return authorizationList != null
                && authorizationList.stream().anyMatch(a -> StrUtil.equalsIgnoreCase(a.getCode(), authCode));
    }

}