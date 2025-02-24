package com.example.template.repo.ibatis.handler;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @title: json字符串转对象
 * @author: trifolium.wang
 * @date: 2024/7/11
 */
@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class String2Handler extends BaseTypeHandler<Object> {

    private final Class<?> type;

    public String2Handler(Class<?> type) {
        if (log.isTraceEnabled()) {
            log.trace("FastjsonTypeHandler({})", type);
        }

        Assert.notNull(type, "Type argument cannot be null");
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, this.toJson(parameter));
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return StrUtil.isBlank(json) ? null : this.parse(json);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return StrUtil.isBlank(json) ? null : this.parse(json);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return StrUtil.isBlank(json) ? null : this.parse(json);
    }

    private Object parse(String json) {
        return JSON.parseObject(json, this.type);
    }

    private String toJson(Object obj) {
        return JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty);
    }
}
