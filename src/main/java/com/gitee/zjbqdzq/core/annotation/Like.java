package com.gitee.zjbqdzq.core.annotation;

import java.lang.annotation.*;

/**
 * @author Administrator
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Like {
    boolean left() default true;

    boolean right() default true;
}
