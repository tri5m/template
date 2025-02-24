package com.example.template.services.common.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 业务状态code
 * @author : trifolium
 * @title:
 * @date: 2020/03/10 14:01
 */
public enum ResultCode {

    // 成功
    SUCCESS(0),
    
    // 失败
    FAILURE(1),

    // 异常
    EXCEPTION(-1),

    // 无权限
    NO_AUTH(3),

    // Token 失效
    TOKEN_INVALID(7);

    final int value;

    ResultCode(int value) {
        this.value = value;
    }

    @JsonCreator
    public static ResultCode getResultCode(int value) {
        for (ResultCode item : values()) {
            if (item.getValue() == value) {

                return item;
            }
        }
        return null;
    }

    @JsonValue
    public int getValue() {

        return this.value;
    }
}
