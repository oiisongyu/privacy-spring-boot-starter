package cn.zhz.privacy;

import cn.zhz.privacy.config.PrivacyAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author z.h.z
 * @since 2023/10/12
 */
@SpringBootApplication
@Import({PrivacyAutoConfiguration.class})
public class PrivacyApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrivacyApplication.class, args);
    }
}
