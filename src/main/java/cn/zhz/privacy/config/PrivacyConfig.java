package cn.zhz.privacy.config;

import cn.zhz.privacy.interceptor.CryptoInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZHZ
 * @date 2021-11-17
 * @apiNote
 */
//@Configuration
//@EnableConfigurationProperties(CryptoProperties.class)
//@ConditionalOnProperty(
//        prefix = "privacy.crypto",
//        name = "key",
//        havingValue = "true"
//)
public class PrivacyConfig {


    @Bean
    public CryptoInterceptor cryptoInterceptor(){
        return new CryptoInterceptor();
    }


}
