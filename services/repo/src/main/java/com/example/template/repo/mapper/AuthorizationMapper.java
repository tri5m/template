package com.example.template.repo.mapper;

import cn.xbatis.core.mybatis.mapper.MybatisMapper;
import com.example.template.repo.entity.Authorization;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * mb_authorization Mapper 接口
 * </p>
 *
 * @author trifolium
 * @since 2025-02-24
 */
public interface AuthorizationMapper extends MybatisMapper<Authorization> {

    List<Authorization> findAuthorizationByRoleCode(@Param("roleCode") String roleCode);
}
