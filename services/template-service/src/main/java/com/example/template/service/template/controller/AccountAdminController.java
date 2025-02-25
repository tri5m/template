package com.example.template.service.template.controller;

import com.example.template.service.template.model.ro.account.SaveAdminRo;
import com.example.template.service.template.model.ro.account.SaveRoleRo;
import com.example.template.service.template.model.vo.account.AdminDetailVo;
import com.example.template.service.template.model.vo.account.AdminListVo;
import com.example.template.service.template.model.vo.account.AuthTreeVo;
import com.example.template.service.template.model.vo.account.RoleListVo;
import com.example.template.service.template.service.AccountAdminService;
import com.example.template.services.common.annotion.AuthValidator;
import com.example.template.services.common.common.response.Paging;
import com.example.template.services.common.common.response.ResponseResult;
import com.example.template.services.common.model.ro.SearchRo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

/**
 * @title: AccountAdminController
 * @author: trifolium.wang
 * @date: 2023/11/10
 * @modified :
 */
@Tag(name = "账户管理")
@RestController
@AuthValidator(authorities = {"account_admin"})
@RequestMapping("/user-admin")
public class AccountAdminController {

    private final AccountAdminService accountAdminService;

    @Inject
    public AccountAdminController(AccountAdminService accountAdminService) {
        this.accountAdminService = accountAdminService;
    }

    @Operation(summary = "权限树")
    @GetMapping("/auth/tree")
    public ResponseResult<List<AuthTreeVo>> authTree() {

        return ResponseResult.success(accountAdminService.getAuthTree());
    }

    @Operation(summary = "角色列表")
    @PostMapping("/role/roles")
    public ResponseResult<Paging<RoleListVo>> roles(@Valid @RequestBody SearchRo ro) {

        return ResponseResult.success(accountAdminService.roles(ro));
    }

    @Operation(summary = "保存角色")
    @PostMapping("/role/save")
    public ResponseResult<Void> roles(@Valid @RequestBody SaveRoleRo ro) {

        accountAdminService.saveRole(ro);
        return ResponseResult.success();
    }

    @Operation(summary = "禁用或者启用角色")
    @GetMapping("/role/dis-en-able")
    public ResponseResult<Void> disEnAbleRole(@RequestParam String code) {

        accountAdminService.disEnAbleRole(code);
        return ResponseResult.success();
    }

    @Operation(summary = "删除角色")
    @GetMapping("/role/del")
    public ResponseResult<Void> allHosCategory(@RequestParam String code) {

        accountAdminService.deleteRole(code);
        return ResponseResult.success();
    }

    @Operation(summary = "用户列表")
    @PostMapping("/user/list")
    public ResponseResult<Paging<AdminListVo>> adminList(@Valid @RequestBody SearchRo ro) {

        return ResponseResult.success(accountAdminService.adminList(ro));
    }

    @Operation(summary = "删除角色")
    @GetMapping("/user/detail")
    public ResponseResult<AdminDetailVo> allHosCategory(@RequestParam Long id) {


        return ResponseResult.success(accountAdminService.adminDetail(id));
    }

    @Operation(summary = "保存用户")
    @PostMapping("/user/save")
    public ResponseResult<Paging<AdminListVo>> saveAdmin(@Valid @RequestBody SaveAdminRo ro) {

        accountAdminService.saveAdmin(ro);
        return ResponseResult.success();
    }

    @Operation(summary = "删除用户")
    @GetMapping("/user/del")
    public ResponseResult<Void> delAdmin(@RequestParam Long id) {

        accountAdminService.deleteAdmin(id);
        return ResponseResult.success();
    }

    @Operation(summary = "启用/禁用管理员")
    @GetMapping("/user/dis-en-able")
    public ResponseResult<Void> disEnAbleAdmin(@RequestParam Long id) {

        accountAdminService.disEnAbleAdmin(id);
        return ResponseResult.success();
    }

}
