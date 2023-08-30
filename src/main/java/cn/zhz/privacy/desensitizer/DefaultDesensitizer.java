package cn.zhz.privacy.desensitizer;

import cn.zhz.privacy.utils.DesensitizeUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ZHZ
 * @date 2021-11-22
 * @apiNote
 */
@Slf4j
public class DefaultDesensitizer implements IDesensitizer {

    /**
     * 执行脱敏处理
     *
     * @param value     要脱敏的值
     * @param fillValue 填充的副号
     * @return 脱敏后的值
     */
    @Override
    public String execute(String value, String fillValue) {
        if (value == null || value.isEmpty() || fillValue == null || fillValue.isEmpty()) {
            return "";
        }
        String sensitiveInfo = DesensitizeUtil.encryptSensitiveInfo(value, fillValue);
        log.debug("脱敏原值：" + value);
        log.debug("脱敏后值：" + sensitiveInfo);
        return sensitiveInfo;
    }
}
