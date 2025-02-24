package com.example.template.service.template.model.vo.account;

import com.example.template.service.template.model.vo.AdminBaseVo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @title: RoleListVo
 * @author: trifolium.wang
 * @date: 2023/11/6
 * @modified :
 */
@Data
@Schema(description = "角色列表")
public class RoleListVo {

    private String code;

    private String name;

    private String description;

    @Schema(description = "是否是系统角色")
    private Boolean isSysRole;

    @Schema(description = "是否被禁用")
    private Boolean isBanned;

    @JsonIgnore
    private Long createBy;

    @Schema(description = "创建人")
    private AdminBaseVo createUser;
}
