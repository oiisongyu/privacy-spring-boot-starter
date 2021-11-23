package cn.zhz.privacy.desensitizer;

/**
 * @author ZHZ
 * @date 2021-11-22
 * @apiNote 脱敏器
 */
public interface IDesensitizer {

    /**
     * 执行脱敏处理
     *
     * @param value            要脱敏的值
     * @param fillValue        填充的副号
     * @return
     */
    String execute(String value, String fillValue);
}
