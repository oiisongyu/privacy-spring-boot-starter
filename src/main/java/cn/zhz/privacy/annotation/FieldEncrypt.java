package cn.zhz.privacy.annotation;


import cn.zhz.privacy.crypto.DefaultCrypto;
import cn.zhz.privacy.crypto.ICrypto;
import cn.zhz.privacy.enums.Algorithm;

import java.lang.annotation.*;

/**
 * @author ZHZ
 * @date 2021-11-16
 * @apiNote
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldEncrypt {

    String key() default "";

    Algorithm algorithm() default Algorithm.AES;

    Class<? extends ICrypto> iCrypto() default DefaultCrypto.class;

}
