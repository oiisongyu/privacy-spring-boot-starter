package cn.zhz.privacy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ZHZ
 * @date 2021-11-17
 * @apiNote
 */

@ConfigurationProperties(prefix = "privacy.crypto")
@Component
public class CryptoProperties {
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
