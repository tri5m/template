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
 * mb_admin
 * </p>
 *
 * @author trifolium
 * @since 2025-02-24
 */
@Data
@FieldNameConstants
@Table(value ="t_admin")
public class Admin {

    /**
     * id
     */
    @TableId(IdAutoType.AUTO)
    private Long id;

    /**
     * name
     */
    private String name;

    /**
     * userName
     */
    private String userName;

    /**
     * password
     */
    private String password;

    /**
     * roleCodes
     */
    private String roleCodes;

    /**
     * createBy
     */
    private Long createBy;

    /**
     * createTime
     */
    private LocalDateTime createTime;

    /**
     * updateBy
     */
    private Long updateBy;

    /**
     * updateTime
     */
    private LocalDateTime updateTime;

    /**
     * isBanned
     */
    private Boolean isBanned;

    /**
     * isSuper
     */
    private Boolean isSuper;

    /**
     * isDel
     */
    @LogicDelete
    private Boolean isDel;

    /**
     * ext
     */
    private String ext;

}
