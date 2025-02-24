package com.example.template.service.template.controller;

import com.example.template.service.template.model.ro.AdminLoginRo;
import com.example.template.service.template.model.ro.EditPwdRo;
import com.example.template.service.template.model.vo.auth.LoginVo;
import com.example.template.service.template.service.UserAuthService;
import com.example.template.services.common.annotion.AuthValidator;
import com.example.template.services.common.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @title: UserAuthController
 * @author: trifolium
 * @date: 2023/1/9
 * @modified :
 */
@Tag(name = "授权相关接口")
@RestController
@RequestMapping("/auth")
public class UserAuthController {

    private final UserAuthService authService;

    @Inject
    public UserAuthController(UserAuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public ResponseResult<LoginVo> login(@Valid @RequestBody AdminLoginRo adminLoginRo) {

        return ResponseResult.success(authService.login(adminLoginRo));
    }

    @Operation(summary = "修改密码")
    @AuthValidator
    @PostMapping("/edit-pwd")
    public ResponseResult<Void> editPwd(@Valid @RequestBody EditPwdRo editPwdRo) {

        authService.editPwd(editPwdRo);
        return ResponseResult.success();
    }

    @Operation(summary = "管理员登出")
    @AuthValidator
    @GetMapping("/logout")
    public ResponseResult<Object> logout() {
        authService.logout();
        return ResponseResult.success();
    }

    @Operation(summary = "获取验证码")
    @GetMapping("/captcha-image")
    public void getCaptcha(HttpServletResponse response) throws IOException {
        authService.captchaImage(response);
    }

}
