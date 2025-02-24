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
 * mb_authorization
 * </p>
 *
 * @author trifolium
 * @since 2025-02-24
 */
@Data
@FieldNameConstants
@Table(value ="t_authorization")
public class Authorization {

    @TableId(IdAutoType.AUTO)
    private Long id;

    private String code;

    private String name;

    private String description;

    private String authority;

    private Integer type;

    private String parentCode;

    private Long createBy;

    private LocalDateTime createTime;

    /**
     * isDel
     */
    @LogicDelete
    private Boolean isDel;

}
