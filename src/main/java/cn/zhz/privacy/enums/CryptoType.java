package cn.zhz.privacy.enums;

/**
 * @author ZHZ
 * @date 2021-11-19
 * @apiNote
 */
public enum CryptoType {
    /**
     * ENCRYPT 加密
     * DECRYPT 解密
     */
    ENCRYPT("encrypt"), DECRYPT("decrypt");

    /**
     * 对应加密器方法名称
     */
    private String method;

    CryptoType(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
