package com.example.template.service.template.model.vo.account;

import com.example.template.service.template.model.vo.AdminBaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @title: AdminListVo
 * @author: trifolium.wang
 * @date: 2023/11/6
 * @modified :
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "管理员列表vo")
public class AdminListVo extends AdminBaseVo {


    @Schema(description = "是否是超级管理员")
    private Boolean isSuper;

    @Schema(description = "是否被禁用")
    private Boolean isBanned;

    @Schema(description = "角色名列表")
    private List<String> roleNames;

    private String roleCodes;

    private String remark;
}
