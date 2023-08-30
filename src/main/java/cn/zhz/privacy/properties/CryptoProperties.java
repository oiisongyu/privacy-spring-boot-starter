package cn.zhz.privacy.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ZHZ
 * @date 2021-11-17
 * @apiNote
 */

@Data
@ConfigurationProperties(prefix = "privacy.crypto")
public class CryptoProperties {
    /**
     * 秘钥
     */
    private String key;

}
