package com.example.template.service.template.service;

import cn.xbatis.core.sql.executor.chain.QueryChain;
import co.tunan.tucache.core.annotation.TuCache;
import co.tunan.tucache.core.annotation.TuCacheClear;
import com.example.template.common.helper.BeanFiller;
import com.example.template.repo.entity.Admin;
import com.example.template.repo.mapper.AdminMapper;
import com.example.template.service.template.model.vo.AdminBaseVo;
import com.example.template.services.common.model.ro.SearchRo;
import com.example.template.services.common.response.Paging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * @title: TestService
 * @author: trifolium
 * @date: 2023/9/8
 * @modified :
 */
@Slf4j
@Service
@Transactional
public class TestService {

    @Inject
    private AdminMapper adminMapper;

    public Paging<AdminBaseVo> adminList(SearchRo searchRo) {
        return Paging.of(QueryChain.of(adminMapper)
                .forSearch()
                .eq(Admin::getUserName, searchRo.getKeyword())
                .returnType(AdminBaseVo.class)
                .paging(searchRo.getPager()));
    }

    public Paging<AdminBaseVo> adminList2(SearchRo searchRo) {

        return Paging.of(QueryChain.of(adminMapper)
                .returnType(AdminBaseVo.class)
                .paging(searchRo.getPager()));
    }

    @TuCache(key = "admin:admin_detail:#{#id}")
    public AdminBaseVo adminById(Long id) {

        Admin admin = adminMapper.getById(id);
        if (admin == null) {
            return null;
        }

        return BeanFiller.target(AdminBaseVo.class).accept(admin);
    }

    @TuCache(key = "admin:admin_list:cache:#{#ro.pageIndex}_#{#ro.pageSize}:#{#ro.keyword}", expire = 60)
    public Paging<AdminBaseVo> adminListHasCache(SearchRo ro) {

        return Paging.of(QueryChain.of(adminMapper)
                .returnType(AdminBaseVo.class)
                .paging(ro.getPager()));
    }

    @TuCacheClear(keys = "admin")
    public void clearAllCache() {
    }
}
