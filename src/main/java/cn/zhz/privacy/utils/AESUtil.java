package cn.zhz.privacy.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;


/**
 * @author ZHZ
 * @date 2021-11-16
 * @apiNote
 */

public class AESUtil {

    private final static String KEY = "edcb87b4-68b1-466b-8f6d-256ef53e50f0";

    private final static String ALGORITHM = "AES";

    /**
     * @param key     密钥
     * @param content 需要加密的字符串
     * @return 密文字节数组
     */
    private static byte[] encrypt(String key, String content) {
        byte[] rawKey = genKey(key.getBytes());
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(rawKey, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encypted = cipher.doFinal(content.getBytes());
            return encypted;
        } catch (Exception e) {
            return null;
        }
    }

    public static String encryptBase64(String key, String content) {
        byte[] encrypt = encrypt(key, content);
        return Base64.getEncoder().encodeToString(encrypt);
    }

    public static String encryptBase64(String content) {
        return encryptBase64(KEY, content);
    }

    public static String decryptBase64(String key, String content) {
        byte[] decodeContent = Base64.getDecoder().decode(content);
        return decrypt(key, decodeContent);
    }

    public static String decryptBase64(String content) {
        return decryptBase64(KEY, content);
    }


    /**
     * @param encrypted 密文字节数组
     * @param key       密钥
     * @return 解密后的字符串
     */
    private static String decrypt(String key, byte[] encrypted) {
        byte[] rawKey = genKey(key.getBytes());
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(rawKey, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param seed 种子数据
     * @return 密钥数据
     */
    private static byte[] genKey(byte[] seed) {
        byte[] rawKey = null;
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(seed);
            // AES加密数据块分组长度必须为128比特，密钥长度可以是128比特、192比特、256比特中的任意一个
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            rawKey = secretKey.getEncoded();
        } catch (NoSuchAlgorithmException ignored) {
        }
        return rawKey;
    }


    public static void main(String[] args) {
        // 密钥的种子，可以是任何形式，本质是字节数组
        String key = UUID.randomUUID().toString();
        System.out.println(key);
        // 密码的明文
        String clearPwd = "123456";

        // 密码加密后的密文
        byte[] encryptedByteArr = encrypt(key, clearPwd);
        String encryptedPwd = Base64.getEncoder().encodeToString(encryptedByteArr);
        System.out.println(encryptedPwd);

        // 解密后的字符串
        byte[] decode = Base64.getDecoder().decode(encryptedPwd);
        String decryptedPwd = decrypt(key, decode);
        System.out.println(decryptedPwd);
    }

}

