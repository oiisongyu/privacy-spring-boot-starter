package cn.zhz.privacy.crypto;


import cn.zhz.privacy.enums.Algorithm;
import cn.zhz.privacy.interceptor.CryptoInterceptor;
import cn.zhz.privacy.utils.AESUtil;
import cn.zhz.privacy.utils.CryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ZHZ
 * @date 2021-11-15
 * @apiNote
 */
public class DefaultCrypto implements ICrypto {

    private static final Logger log = LoggerFactory.getLogger(DefaultCrypto.class.getName());

    private final static String KEY = "edcb87b4-68b1-466b-8f6d-256ef53e50f0";

    /**
     * 加密
     *
     * @param algorithm 加密算法
     * @param value     加密前的值
     * @param key       秘钥
     * @return 加密后的值
     */
    @Override
    public String encrypt(Algorithm algorithm, String value, String key) throws Exception {
        String result;

        if (key == null || key.length() == 0) {
            key = KEY;
        }

        switch (algorithm) {
            case MD5:
                result = CryptoUtil.encryptBASE64(CryptoUtil.encryptMD5(value.getBytes()));
                break;
            case AES:
                result = AESUtil.encryptBase64(key, value);
                break;
            default:
                result = AESUtil.encryptBase64(key, value);
        }
        return result;
    }

    /**
     * 解密
     *
     * @param algorithm 解密算法
     * @param value     解密前的值
     * @param key       秘钥
     * @return 解密后的值
     */
    @Override
    public String decrypt(Algorithm algorithm, String value, String key) {
        String result;
        if (key == null || key.length() == 0) {
            key = KEY;
        }

        try {
            switch (algorithm) {
                case MD5:
                    log.debug("该算法不支持解密");
                    result = "";
                    break;
                case AES:
                    result = AESUtil.decryptBase64(key, value);
                    break;
                default:
                    result = AESUtil.decryptBase64(key, value);
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            log.debug("值：‘" + value + "’不支持解密");
            result = "";
        }

        return result;

    }

}
