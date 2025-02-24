package com.example.template.services.common.response;

import cn.xbatis.core.mybatis.mapper.context.Pager;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Title:分页信息类
 * Description:处理储存分页的一些信息
 *
 * @author trifolium
 * @version 1.0
 */
@Data
@Schema(name = "Paging<T>", description = "分页信息基础数据")
public class Paging<T> {


    private int pageIndex = 1;
    private int pageSize = 10;
    private long totalCount = 0;
    private int pageCount = 0;

    @Schema(name = "data", description = "数据集合")
    private Collection<T> data = new ArrayList<>();

    public Paging(){}

    /**
     * @param pageIndex 页标,从1开始
     * @param pageSize  页容量
     */
    public Paging(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public Paging(PageInfo<T> phPage) {
        this.pageIndex = phPage.getPageNum();
        this.pageSize = phPage.getPageSize();
        this.totalCount = phPage.getTotal();
        this.pageCount = phPage.getPages();
        if (phPage.getList() != null) {
            this.data = phPage.getList();
        }
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        this.pageCount = (int) (totalCount / pageSize) + (totalCount % pageSize == 0 ? 0 : 1);
    }

    public static <T> Paging<T> of(Pager<T> mpPage){
        return new Paging<>(mpPage);
    }

    public Paging(Pager<T> mpPage) {
        this.pageIndex = mpPage.getNumber();
        this.pageSize = mpPage.getSize();
        this.totalCount = mpPage.getTotal();
        this.pageCount = (int) (totalCount / pageSize) + (totalCount % pageSize == 0 ? 0 : 1);
        if (mpPage.getResults() != null) {
            this.data = mpPage.getResults();
        }
    }

}
