package com.gitee.zjbqdzq.core.annotation;

import java.lang.annotation.*;

/**
 * @author Administrator
 * 用于 QueryVo
 * 如果 QueryVo中存在 实体类中没有的字段 则使用此注解映射到 对应的字段
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Alias {

    String[] value();
}
