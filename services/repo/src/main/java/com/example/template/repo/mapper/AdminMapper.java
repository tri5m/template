package com.example.template.repo.mapper;

import cn.xbatis.core.mybatis.mapper.MybatisMapper;
import cn.xbatis.core.sql.executor.chain.QueryChain;
import com.example.template.repo.entity.Admin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * mb_admin Mapper 接口
 * </p>
 *
 * @author trifolium
 * @since 2025-02-24
 */
public interface AdminMapper extends MybatisMapper<Admin> {

    int delAdminOneRole(@Param("roleCode") String roleCode);

    default List<Admin> findByUserName(String userName){
        return QueryChain.of(this).eq(Admin::getUserName, userName).list();
    }
}
