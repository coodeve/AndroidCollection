package com.coodev.app_apt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解,用于注解处理器
 * 参考:{@link javax.lang.model.element.Element}
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface GetSet {
    String value() default "";
}

