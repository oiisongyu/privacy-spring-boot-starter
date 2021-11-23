package cn.zhz.privacy.config;

import cn.zhz.privacy.interceptor.CryptoInterceptor;
import cn.zhz.privacy.interceptor.DesensitizeInterceptor;
import cn.zhz.privacy.properties.CryptoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZHZ
 * @date 2021-11-17
 * @apiNote
 */
@Configuration
@EnableConfigurationProperties(CryptoProperties.class)
public class PrivacyAutoConfiguration {

    @Bean
    public CryptoInterceptor cryptoInterceptor(){
        return new CryptoInterceptor();
    }

    @Bean
    public DesensitizeInterceptor desensitizeInterceptor(){
        return new DesensitizeInterceptor();
    }


}
