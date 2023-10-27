package cn.zhz.privacy.config;

import cn.zhz.privacy.crypto.DefaultCrypto;
import cn.zhz.privacy.desensitizer.DefaultDesensitizer;
import cn.zhz.privacy.handler.CryptHandler;
import cn.zhz.privacy.handler.DesensitizeHandler;
import cn.zhz.privacy.interceptor.CryptoInterceptor;
import cn.zhz.privacy.interceptor.DesensitizeInterceptor;
import cn.zhz.privacy.interceptor.IInnerInterceptor;
import cn.zhz.privacy.interceptor.PrivacyInterceptor;
import cn.zhz.privacy.properties.CryptoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author ZHZ
 * @date 2021-11-17
 * @apiNote
 */
@Configuration
@EnableConfigurationProperties(CryptoProperties.class)
public class PrivacyAutoConfiguration {

    @Bean
    public PrivacyInterceptor privacyInterceptor(List<IInnerInterceptor> iInnerInterceptorList) {
        return new PrivacyInterceptor(iInnerInterceptorList);
    }

    @Bean
    public DesensitizeHandler desensitizeHandler() {
        return new DesensitizeHandler();
    }

    @Bean
    public CryptHandler cryptHandler(CryptoProperties cryptoProperties) {
        return new CryptHandler(cryptoProperties);
    }

    @Bean
    public CryptoInterceptor cryptoInterceptor(CryptHandler cryptHandler) {
        return new CryptoInterceptor(cryptHandler);
    }

    @Bean
    public DesensitizeInterceptor desensitizeInterceptor(DesensitizeHandler desensitizeHandler) {
        return new DesensitizeInterceptor(desensitizeHandler);
    }

    @Bean
    public DefaultCrypto defaultCrypto() {
        return new DefaultCrypto();
    }

    @Bean
    public DefaultDesensitizer defaultDesensitizer() {
        return new DefaultDesensitizer();
    }


}
