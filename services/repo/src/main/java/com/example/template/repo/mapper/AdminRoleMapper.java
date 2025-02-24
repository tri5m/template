package com.example.template.repo.mapper;

import cn.xbatis.core.mybatis.mapper.MybatisMapper;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.example.template.repo.entity.AdminRole;

import java.util.List;

/**
 * <p>
 * mb_admin_role Mapper 接口
 * </p>
 *
 * @author trifolium
 * @since 2025-02-24
 */
public interface AdminRoleMapper extends MybatisMapper<AdminRole> {

    default List<AdminRole> findByNameOrCode(String name, String code){
        return QueryChain.of(this).or().eq(AdminRole::getName, name)
                .eq(AdminRole::getCode, code).list();
    }

    default AdminRole findByCode(String code) {
        return QueryChain.of(this).eq(AdminRole::getCode, code).get();
    }
}
