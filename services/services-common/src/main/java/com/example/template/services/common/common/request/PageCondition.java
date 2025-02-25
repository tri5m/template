package com.example.template.services.common.common.request;

import cn.xbatis.core.mybatis.mapper.context.Pager;
import com.alibaba.fastjson2.annotation.JSONField;
import com.example.template.common.enumeration.OrderType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @title:
 * @author: trifolium
 * @date: 2019/3/12 15:05
 * @modified :
 */
@Getter
@Setter
@Schema(name = "PageCondition", description = "分页组件请求对象")
public class PageCondition {

    @Schema(description = "页号", example = "1", type="int")
    @Min(value = 1, message = "页号最小为1")
    private Integer pageIndex = 1;

    @Schema(description = "页容量", example = "10", type = "int")
    @Min(value = 1, message = "也容量最小为1")
    @Max(value = 1000, message = "最大页容量为1000")
    private Integer pageSize = 10;

    @Schema(description = "排序字段", example = "id,name", type = "string")
    private String sort = "";

    @Schema(description = "排序方式", example = "asc,desc", type = "string")
    private OrderType order;

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    @com.alibaba.fastjson.annotation.JSONField(serialize = false, deserialize = false)
    public <T> Pager<T> getPager() {

        return new Pager<>(pageIndex, pageSize);
    }
}
