package cn.zhz.privacy.crypto;


import cn.zhz.privacy.enums.Algorithm;
import cn.zhz.privacy.utils.AESUtil;
import cn.zhz.privacy.utils.CryptoUtil;

/**
 * @author ZHZ
 * @date 2021-11-15
 * @apiNote
 */
public class DefaultCrypto implements ICrypto {


    /**
     * 加密
     *
     * @param value 加密前的值
     * @param key   秘钥
     * @return 加密后的值
     */
    @Override
    public String encrypt(Algorithm algorithm, String value, String key) throws Exception {
        String result;
        switch (algorithm) {
            case MD5:
                result = CryptoUtil.encryptBASE64(CryptoUtil.encryptMD5(value.getBytes()));
                break;
            case AES:
                if (key != null && key.length() > 0) {
                    result = AESUtil.encryptBase64(value, key);
                } else {
                    result = AESUtil.encryptBase64(value);
                }
                break;
            default:
                result = AESUtil.encryptBase64(value);
        }
        return result;
    }

    /**
     * 解密
     *
     * @param value 解密前的值
     * @param key   秘钥
     * @return 解密后的值
     */
    @Override
    public String decrypt(Algorithm algorithm, String value, String key) {
        String result;
        switch (algorithm) {
            case MD5:
                result = value;
                break;
            case AES:
                if (key != null && key.length() > 0) {
                    result = AESUtil.decryptBase64(value, key);
                } else {
                    result = AESUtil.decryptBase64(value);
                }
                break;
            default:
                result = AESUtil.decryptBase64(value);
        }
        return result;

    }

}
