package com.example.template.repo.entity;

import cn.xbatis.db.IdAutoType;
import cn.xbatis.db.annotations.LogicDelete;
import cn.xbatis.db.annotations.Table;
import cn.xbatis.db.annotations.TableId;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

/**
 * <p>
 * mb_admin_role
 * </p>
 *
 * @author trifolium
 * @since 2025-02-24
 */
@Data
@FieldNameConstants
@Table(value ="t_admin_role")
public class AdminRole {

    @TableId(IdAutoType.AUTO)
    private Long id;

    private String code;

    private String name;

    private String type;

    private Long createBy;

    private LocalDateTime createTime;

    private Boolean isBanned;

    private Boolean isSysRole;

    /**
     * isDel
     */
    @LogicDelete
    private Boolean isDel;

    /**
     * 描述
     */
    private String description;

}
