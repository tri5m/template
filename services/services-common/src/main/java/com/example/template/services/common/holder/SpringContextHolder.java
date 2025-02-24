package com.example.template.services.common.holder;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @title: Spring上下文
 *
 * @author: trifolium.wang
 * @date: 2024/9/24
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    /**
     * 以静态变量保存ApplicationContext,可在任意代码中取出ApplicationContext.
     */
    private static ApplicationContext context;

    /**
     * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
     */

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.context = applicationContext;
    }

    /**
     * 获取applicationContext
     */
    public ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return context;
    }

    /**
     * 通过name获取 Bean.
     */
    public static Object getBean(String name){
        checkApplicationContext();
        return context.getBean(name);
    }


    /**
     * 通过class获取Bean.
     */
    public static <T> T getBean(Class<T> clazz){
        checkApplicationContext();
        return context.getBean(clazz);
    }

    /**
     * 通过class获取Bean.
     */
    public static <T> Map<String, T> getBeans(Class<T> clazz){
        checkApplicationContext();
        return context.getBeansOfType(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     */
    public static <T> T getBean(String name, Class<T> clazz){
        checkApplicationContext();
        return context.getBean(name, clazz);
    }

    private static void checkApplicationContext() {
        if (context == null) {
            throw new IllegalStateException("ApplicationContext未注入");
        }
    }
}


