package cn.zhz.privacy.crypto;

import cn.zhz.privacy.enums.Algorithm;

/**
 * @author ZHZ
 * @date 2021-11-16
 * @apiNote
 */
public interface ICrypto {

    /**
     * 加密
     *
     * @param algorithm 加密算法
     * @param value     加密前的值
     * @param key       秘钥
     * @return 加密后的值
     */
    String encrypt(Algorithm algorithm, String value, String key) throws Exception;

    /**
     * 解密
     *
     * @param algorithm 解密算法
     * @param value     解密前的值
     * @param key       秘钥
     * @return 解密后的值
     */
    String decrypt(Algorithm algorithm, String value, String key) throws Exception;
}
