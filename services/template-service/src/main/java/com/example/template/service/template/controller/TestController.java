package com.example.template.service.template.controller;

import com.example.template.service.template.model.vo.AdminBaseVo;
import com.example.template.service.template.service.TestService;
import com.example.template.services.common.common.response.Paging;
import com.example.template.services.common.common.response.ResponseResult;
import com.example.template.services.common.model.ro.SearchRo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * @title: TestController
 * @author: trifolium
 * @date: 2023/9/8
 * @modified :
 */
@Tag(name = "test")
@RestController
@RequestMapping("/test")
public class TestController {

    private final TestService testService;

    @Inject
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @Operation(summary = "用户列表", description = "查询管理员的列表")
    @PostMapping("/list")
    public ResponseResult<Paging<AdminBaseVo>> list(@Valid @RequestBody SearchRo searchRo) {

        return ResponseResult.success(testService.adminList(searchRo));
    }

    @Operation(summary = "用户列表", description = "查询管理员的列表")
    @PostMapping("/list2")
    public ResponseResult<Paging<AdminBaseVo>> list2(@Valid @RequestBody SearchRo searchRo) {

        return ResponseResult.success(testService.adminList2(searchRo));
    }

    @Operation(summary = "用户列表有缓存", description = "查询管理员的列表但又缓存")
    @PostMapping("/list3")
    public ResponseResult<Paging<AdminBaseVo>> list3(@Valid @RequestBody SearchRo searchRo) {

        return ResponseResult.success(testService.adminListHasCache(searchRo));
    }

    @Operation(summary = "一个用户")
    @GetMapping("/one")
    public ResponseResult<AdminBaseVo> one(@RequestParam(value = "id") Long id) {

        return ResponseResult.success(testService.adminById(id));
    }

    @Operation(summary = "清理所有缓存")
    @GetMapping("/clear-all-cache")
    public ResponseResult<Void> clearAllCache() {

        testService.clearAllCache();
        return ResponseResult.success();
    }
}
