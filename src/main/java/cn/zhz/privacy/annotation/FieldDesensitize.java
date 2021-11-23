package cn.zhz.privacy.annotation;

import cn.zhz.privacy.desensitizer.DefaultDesensitizer;
import cn.zhz.privacy.desensitizer.IDesensitizer;

import java.lang.annotation.*;

/**
 * @author ZHZ
 * @date 2021-11-22
 * @apiNote
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface FieldDesensitize {

    /**
     * 填充值
     *
     * @return
     */
    String fillValue() default "*";

    /**
     * 脱敏器
     *
     * @return
     */
    Class<? extends IDesensitizer> desensitizer() default DefaultDesensitizer.class;
}
