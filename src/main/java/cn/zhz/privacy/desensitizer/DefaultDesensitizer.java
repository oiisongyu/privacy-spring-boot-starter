package cn.zhz.privacy.desensitizer;

import cn.zhz.privacy.crypto.DefaultCrypto;
import cn.zhz.privacy.utils.DesensitizeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ZHZ
 * @date 2021-11-22
 * @apiNote
 */
public class DefaultDesensitizer implements IDesensitizer {

    private static final Logger log = LoggerFactory.getLogger(DefaultCrypto.class.getName());

    /**
     * 执行脱敏处理
     *
     * @param value     要脱敏的值
     * @param fillValue 填充的副号
     * @return
     */
    @Override
    public String execute(String value, String fillValue) {
        if (value == null || value.length() == 0 || fillValue == null || fillValue.length() == 0) {
            return "";
        }
        String sensitiveInfo = DesensitizeUtil.encryptSensitiveInfo(value, fillValue);
        log.debug("脱敏原值：" + value);
        log.debug("脱敏后值：" + sensitiveInfo);
        return sensitiveInfo;
    }
}
