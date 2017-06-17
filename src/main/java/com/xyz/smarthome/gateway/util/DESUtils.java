package com.xyz.smarthome.gateway.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * Created by lenovo on 2017/6/14.
 */
public class DESUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DESUtils.class);

    private static final String ENCRYPTION_ALGORITHM = "DES";

    private static final String CHAR_ENCODING = "UTF-8";

    /**
     * 加密字符串(DES算法)
     * @param plainText 明文
     * @param desKey DES加密秘钥
     * @return 加密成功返回密文，失败返回原始明文
     */
    public static String desEncrypt(String plainText, String desKey) {
        try {
            SecureRandom sr = new SecureRandom();
            DESKeySpec dks = new DESKeySpec(Hex.decodeHex(desKey.toCharArray()));

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM);
            SecretKey securekey = keyFactory.generateSecret(dks);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

            return Base64.encodeBase64String(cipher.doFinal(plainText.getBytes(CHAR_ENCODING)));
//            return Hex.encodeHexString(cipher.doFinal(plainText.getBytes(CHAR_ENCODING)));
        } catch (Exception e) {
            LOGGER.error("加密字符串[" + plainText + "]出错", e);
            return plainText;
        }
    }


    /**
     * 解密字符串(DES算法)
     * @param cipherText 密文
     * @param desKey DES加密秘钥
     * @return 解密成功返回明文，失败返回传入的密文
     */
    public static String desDecrypt(String cipherText, String desKey) {
        try {
            SecureRandom sr = new SecureRandom();
            DESKeySpec dks = new DESKeySpec(Hex.decodeHex(desKey.toCharArray()));

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM);
            SecretKey securekey = keyFactory.generateSecret(dks);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

//            return new String(cipher.doFinal(Hex.decodeHex(cipherText.toCharArray())), CHAR_ENCODING);
            return new String(cipher.doFinal(Base64.decodeBase64(cipherText)), CHAR_ENCODING);
        } catch (Exception e) {
            LOGGER.error("解密字符串[" + cipherText + "]出错", e);
            return cipherText;
        }
    }
}
