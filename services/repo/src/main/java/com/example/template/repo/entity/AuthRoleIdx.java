package com.example.template.repo.entity;

import cn.xbatis.db.IdAutoType;
import cn.xbatis.db.annotations.Table;
import cn.xbatis.db.annotations.TableId;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

/**
 * <p>
 * mb_auth_role_idx
 * </p>
 *
 * @author trifolium
 * @since 2025-02-24
 */
@Data
@FieldNameConstants
@Table(value ="t_auth_role_idx")
public class AuthRoleIdx {

    /**
     * id
     */
    @TableId(IdAutoType.AUTO)
    private Long id;

    /**
     * roleCode
     */
    private String roleCode;

    /**
     * authCode
     */
    private String authCode;

    /**
     * createBy
     */
    private Long createBy;

    /**
     * createTime
     */
    private LocalDateTime createTime;

}
