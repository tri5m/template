package com.example.template.service.template.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.xbatis.core.sql.executor.Where;
import cn.xbatis.core.sql.executor.chain.DeleteChain;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.example.template.common.helper.BeanFiller;
import com.example.template.common.helper.exception.InvokeException;
import com.example.template.common.helper.exception.NotFondException;
import com.example.template.common.helper.exception.ValidateException;
import com.example.template.common.helper.tree.TreeBuilder;
import com.example.template.common.util.JsonUtil;
import com.example.template.common.util.MD5Util;
import com.example.template.repo.entity.Admin;
import com.example.template.repo.entity.AdminRole;
import com.example.template.repo.entity.AuthRoleIdx;
import com.example.template.repo.entity.Authorization;
import com.example.template.repo.mapper.AdminMapper;
import com.example.template.repo.mapper.AdminRoleMapper;
import com.example.template.repo.mapper.AuthRoleIdxMapper;
import com.example.template.repo.mapper.AuthorizationMapper;
import com.example.template.service.template.model.ro.account.SaveAdminRo;
import com.example.template.service.template.model.ro.account.SaveRoleRo;
import com.example.template.service.template.model.vo.AdminBaseVo;
import com.example.template.service.template.model.vo.account.AdminDetailVo;
import com.example.template.service.template.model.vo.account.AdminListVo;
import com.example.template.service.template.model.vo.account.AuthTreeVo;
import com.example.template.service.template.model.vo.account.RoleListVo;
import com.example.template.services.common.common.response.Paging;
import com.example.template.services.common.context.UserContext;
import com.example.template.services.common.model.ro.SearchRo;
import com.example.template.services.common.model.vo.BaseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @title: AccountAdminService
 * @author: trifolium.wang
 * @date: 2023/11/10
 * @modified :
 */
@Slf4j
@Service
@Transactional
public class AccountAdminService {

    @Inject
    private AdminRoleMapper adminRoleMapper;

    @Inject
    private UserContext adminContext;

    @Inject
    private AuthRoleIdxMapper authRoleIdxMapper;

    @Inject
    private AdminMapper adminMapper;

    @Inject
    private AuthorizationMapper authorizationMapper;

    public Paging<RoleListVo> roles(SearchRo ro) {
        return Paging.of(QueryChain.of(adminRoleMapper)
                .forSearch().or()
                .like(AdminRole::getCode, ro.getKeyword())
                .like(AdminRole::getName, ro.getKeyword())
                .returnType(RoleListVo.class, (vo)
                        -> vo.setCreateUser(new AdminBaseVo(adminContext.getAdmin(vo.getCreateBy()))))
                .paging(ro.getPager()));
    }

    public void saveRole(SaveRoleRo ro) {
        Long adminId = adminContext.getAdmin().getId();
        String roleCode;
        AdminRole role;
        if (ro.getId() == null) {
            roleCode = ro.getCode();
            role = BeanFiller.target(AdminRole.class).accept(ro);
            role.setCreateBy(adminId);
            role.setCreateTime(LocalDateTime.now());
            role.setIsBanned(Boolean.FALSE);
            role.setIsDel(Boolean.FALSE);
            role.setIsSysRole(Boolean.FALSE);

            if (CollUtil.isNotEmpty(adminRoleMapper.
                    findByNameOrCode(ro.getName(), ro.getCode()))) {
                throw new ValidateException("名称或code已经存在!");
            }

            adminRoleMapper.save(role);
        } else {
            role = adminRoleMapper.getById(ro.getId());
            if (role == null) {
                throw new NotFondException("角色不存在");
            }
            role.setName(ro.getName());
            role.setDescription(ro.getDescription());
            List<AdminRole> byName = adminRoleMapper.findByNameOrCode(ro.getName(), role.getCode());
            if (CollUtil.isNotEmpty(byName) && byName.stream()
                    .anyMatch(n -> !n.getId().equals(ro.getId()))) {
                throw new ValidateException("名称已经存在!");
            }

            adminRoleMapper.update(role);
            roleCode = role.getCode();
        }
        DeleteChain.of(authRoleIdxMapper).eq(AuthRoleIdx::getRoleCode, roleCode).execute();
        if (CollUtil.isNotEmpty(ro.getAuths())) {
            authRoleIdxMapper.saveBatch(ro.getAuths().stream().map(auth -> {
                AuthRoleIdx idx = new AuthRoleIdx();
                idx.setCreateBy(adminId);
                idx.setCreateTime(LocalDateTime.now());
                idx.setRoleCode(roleCode);
                idx.setAuthCode(auth);
                return idx;
            }).collect(Collectors.toList()));
        }
    }

    public void disEnAbleRole(String code) {

        AdminRole role = adminRoleMapper.findByCode(code);
        if (role == null) {
            throw new NotFondException("角色不存在!");
        }
        if (role.getIsSysRole()) {
            throw new InvokeException("系统角色无法禁用!");
        }

        role.setIsBanned(!Boolean.TRUE.equals(role.getIsBanned()));
        adminRoleMapper.update(role);
    }

    public void deleteRole(String code) {

        AdminRole role = adminRoleMapper.findByCode(code);
        if (role == null) {
            throw new NotFondException("角色不存在!");
        }
        if (role.getIsSysRole()) {
            throw new InvokeException("系统角色无法删除!");
        }

        adminMapper.delAdminOneRole(code);
        adminRoleMapper.deleteById(role.getId());
        authRoleIdxMapper.delete(Where.create().eq(AuthRoleIdx::getRoleCode, code));
    }

    public List<AuthTreeVo> getAuthTree() {
        List<Authorization> auths = authorizationMapper.listAll();
        return TreeBuilder.build(auths.stream().map(a -> {
            AuthTreeVo vo = new AuthTreeVo();
            vo.setId(a.getCode());
            vo.setName(a.getName());
            vo.setType(a.getType());
            vo.setParentId(a.getParentCode());
            vo.setAuthority(a.getAuthority());
            return vo;
        }).collect(Collectors.toList()), true);
    }

    public Paging<AdminListVo> adminList(SearchRo ro) {

        List<AdminRole> roles = adminRoleMapper.listAll();
        return Paging.of(QueryChain.of(adminMapper)
                .forSearch().or()
                .like(Admin::getName, ro.getKeyword())
                .like(Admin::getUserName, ro.getKeyword())
                .returnType(AdminListVo.class, (vo) -> vo.setRoleNames(roles.stream().filter(
                        r -> CollUtil.contains(JsonUtil.jsonToList(vo.getRoleCodes(), String.class),
                                r.getCode())).map(AdminRole::getName).collect(Collectors.toList())))
                .paging(ro.getPager()));
    }

    public AdminDetailVo adminDetail(Long id) {

        var admin = adminMapper.getById(id);
        if (admin == null) {
            throw new NotFondException("用户不存在!");
        }

        var vo = BeanFiller.target(AdminDetailVo.class).accept(admin);
        List<String> roleCodes = JsonUtil.jsonToList(admin.getRoleCodes(), String.class);
        if (roleCodes != null) {
            List<AdminRole> allRoles = adminRoleMapper.listAll();
            vo.setRoles(allRoles.stream().filter(
                    r -> CollUtil.contains(JsonUtil.jsonToList(admin.getRoleCodes(), String.class),
                            r.getCode())).map(r -> new BaseVo(r.getCode(), r.getName())).collect(Collectors.toList())

            );
        }

        return vo;
    }

    public void saveAdmin(SaveAdminRo ro) {

        List<Admin> byUserName = adminMapper.findByUserName(ro.getUserName());

        if (ro.getId() == null) {
            if (!byUserName.isEmpty()) {
                throw new InvokeException("账号已存在!");
            }
            Admin newAdmin = new Admin();
            newAdmin.setName(ro.getName());
            newAdmin.setUserName(ro.getUserName());
            newAdmin.setRoleCodes(JsonUtil.toJson(ro.getRoles()));
            newAdmin.setCreateBy(adminContext.getAdminIdIfDefault(null));
            newAdmin.setCreateTime(LocalDateTime.now());
            newAdmin.setIsBanned(Boolean.FALSE);
            newAdmin.setIsDel(Boolean.FALSE);
            newAdmin.setIsSuper(Boolean.FALSE);
            if (StrUtil.isNotBlank(ro.getPassword())) {
                newAdmin.setPassword(MD5Util.getMD5String(ro.getPassword()));
            } else {
                newAdmin.setPassword(RandomUtil.randomString(32));
            }
            adminMapper.save(newAdmin);

        } else {
            var oldAdmin = adminMapper.getById(ro.getId());
            if (oldAdmin == null) {
                throw new NotFondException("用户不存在!");
            }
            if (oldAdmin.getIsSuper()) {
                throw new InvokeException("超级管理员无法修改!");
            }
            if (byUserName.stream().anyMatch(a -> !Objects.equals(a.getId(), ro.getId()))) {
                throw new InvokeException("账号已存在!");
            }
            Admin newAdmin = new Admin();
            newAdmin.setId(ro.getId());
            newAdmin.setName(ro.getName());
            newAdmin.setUserName(ro.getUserName());
            newAdmin.setRoleCodes(JsonUtil.toJson(ro.getRoles()));
            newAdmin.setUpdateBy(adminContext.getAdminIdIfDefault(null));
            newAdmin.setUpdateTime(LocalDateTime.now());
            if (StrUtil.isNotBlank(ro.getPassword())) {
                newAdmin.setPassword(MD5Util.getMD5String(ro.getPassword()));
            }
            adminMapper.update(newAdmin);
        }
    }

    public void deleteAdmin(Long id) {
        var admin = adminMapper.getById(id);
        if (admin == null) {
            throw new NotFondException("用户不存在!");
        }
        if (admin.getIsSuper()) {
            throw new InvokeException("超级管理员无法删除!");
        }

        adminMapper.deleteById(id);
    }

    public void disEnAbleAdmin(Long id) {

        var admin = adminMapper.getById(id);
        if (admin == null) {
            throw new NotFondException("用户不存在!");
        }
        if (admin.getIsSuper()) {
            throw new InvokeException("超级管理员无法禁用!");
        }
        admin.setIsBanned(!Boolean.TRUE.equals(admin.getIsBanned()));
        adminMapper.update(admin);
    }

}
