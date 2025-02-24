package com.example.template.common.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Title:Json工具类
 *
 * @author: trifolium.wang
 * @date: 2024/9/24
 */
public class JsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * 将对象转换为json字符串
     *
     * @param o Object
     */
    public static String toJson(Object o) {
        if (o == null) {
            return null;
        }
        return JSON.toJSONString(o);
    }

    /**
     * 将json字符串转化为对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * 将json字符串转化为对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json, typeReference);
    }

    /**
     * 将Json字符转换为List
     */
    public static <T> List<T> jsonToList(String json, Class<T> clazz) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        return JSON.parseArray(json, clazz);
    }

    /**
     * 将 json 转化为JSONObject
     */
    public static JSONObject jsonToJsonObject(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json);
    }

    /**
     * 将简单Json对象转换成Map
     *
     * @param jsonStr json对象
     * @return Map对象
     */
    public static Map<String, Object> jsonToObjectMap(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return new HashMap<>();
        }
        return JSON.parseObject(jsonStr, new TypeReference<>() {
        });
    }

    /**
     * 将简单Json对象转换成Map
     *
     * @param jsonStr json对象
     * @return Map对象
     */
    public static Map<String, String> jsonToStrMap(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {

            return new HashMap<>();
        }
        return JSON.parseObject(jsonStr, new TypeReference<>() {
        });
    }

    /**
     * 将Javabean转换为Map
     *
     * @param javaBean javaBean
     * @return Map对象
     */
    public static Map<String, Object> javabeanToMap(Object javaBean) {
        Map<String, Object> result = new HashMap<>(5);
        Method[] methods = javaBean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            try {
                if (method.getName().startsWith("get")) {
                    String field = method.getName();
                    field = field.substring(field.indexOf("get") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    Object value = method.invoke(javaBean, (Object[]) null);
                    result.put(field, value);
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return result;
    }

    /**
     * 将json数组转成List<Map<String,Object>
     */
    public static List<Map<String, Object>> jsonToListOfObjectMap(String json) {
        List<Object> lists = jsonToList(json, Object.class);
        if (lists != null) {
            return lists.stream().map(JsonUtil::javabeanToMap).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 将json数组转成List<Map<String,String>
     */
    public static List<Map<String, String>> jsonToListOfStrMap(String json) {
        List<Object> lists = jsonToList(json, Object.class);
        if (lists != null) {
            return lists.stream().map(obj -> JsonUtil.jsonToStrMap(toJson(obj))).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * clone 对象浅克隆
     */
    @SuppressWarnings("unchecked")
    public static <T> T clone(T t) {
        if (t == null) {

            return null;
        }
        String json = toJson(t);
        return (T) fromJson(json, t.getClass());
    }

}
