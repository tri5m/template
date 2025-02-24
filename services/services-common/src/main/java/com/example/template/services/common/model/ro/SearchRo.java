package com.example.template.services.common.model.ro;

import com.example.template.services.common.request.PageCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @title: SearchRo
 * @author: trifolium
 * @date: 2023/1/10
 * @modified :
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "SearchRo", description = "分页+关键字搜索")
public class SearchRo extends PageCondition {

    @Schema(description = "关键词", type = "string", nullable = true)
    private String keyword;
}
