package com.example.template.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 非对称加密算法RSA算法组件
 *
 * @author: trifolium.wang
 * @date: 2024/9/24
 */
@Slf4j
public class RSAUtil {

    //非对称密钥算法
    private static final String KEY_ALGORITHM = "RSA";


    /**
     * 密钥长度，DH算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 1024;

    //公钥
    public static final String PUBLIC_KEY = "RSAPublicKey";

    //私钥
    public static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 生成密钥对,默认密钥长度为1024
     *
     * @return Map 密钥的Map
     */
    public static Map<String, String> generateKeyPair() throws Exception {
        return generateKeyPair(KEY_SIZE);
    }

    /**
     * 生成密钥对
     *
     * @return Map 密钥的Map
     */
    public static Map<String, String> generateKeyPair(int keySize) throws Exception {
        //实例化密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥生成器
        keyPairGenerator.initialize(keySize);
        //生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //甲方公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //甲方私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //将密钥存储在map中
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put(PUBLIC_KEY, RSAUtil.getPublicKey(publicKey));
        keyMap.put(PRIVATE_KEY, RSAUtil.getPrivateKey(privateKey));

        log.debug("公钥：{}", keyMap.get(PUBLIC_KEY));
        log.debug("私钥：{}", keyMap.get(PRIVATE_KEY));

        return keyMap;
    }

    /**
     * 公钥加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     * @return 加密数据
     */
    public static String encryptByPublicKey(String data, String publicKey) throws Exception {

        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        //分段加密
        byte[] bytesContent = data.getBytes(StandardCharsets.UTF_8);
        int inputLen = bytesContent.length;
        int offLen = 0;
        int i = 0;
        ByteArrayOutputStream bops = new ByteArrayOutputStream();
        while (inputLen - offLen > 0) {
            byte[] cache;
            if (inputLen - offLen > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(bytesContent, offLen, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(bytesContent, offLen, inputLen - offLen);
            }
            bops.write(cache);
            i++;
            offLen = MAX_ENCRYPT_BLOCK * i;
        }
        bops.close();
        byte[] encryptedData = bops.toByteArray();

        return Base64.encodeBase64String(encryptedData);
    }

    /**
     * 私钥解密
     *
     * @param data       待解密数据
     * @param privateKey 私钥
     * @return 解密数据
     */
    public static String decryptByPrivateKey(String data, String privateKey) throws Exception {
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(
                new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);

        // 分段解密
        byte[] bytesContent = Base64.decodeBase64(data);
        int inputLen = bytesContent.length;
        int offLen = 0;
        int i = 0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (inputLen - offLen > 0) {
            byte[] cache;
            if (inputLen - offLen > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(bytesContent, offLen, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(bytesContent, offLen, inputLen - offLen);
            }
            byteArrayOutputStream.write(cache);
            i++;
            offLen = MAX_DECRYPT_BLOCK * i;

        }
        byteArrayOutputStream.close();
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return new String(byteArray, StandardCharsets.UTF_8);
    }

    /**
     * 私钥加密
     *
     * @param data       待加密数据
     * @param privateKey 密钥
     * @return 加密数据
     */
    public static String encryptByPrivateKey(String data, String privateKey) throws Exception {

        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, priKey);
        //分段加密
        byte[] bytesContent = data.getBytes(StandardCharsets.UTF_8);
        int inputLen = bytesContent.length;
        int offLen = 0;
        int i = 0;
        ByteArrayOutputStream bops = new ByteArrayOutputStream();
        while (inputLen - offLen > 0) {
            byte[] cache;
            if (inputLen - offLen > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(bytesContent, offLen, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(bytesContent, offLen, inputLen - offLen);
            }
            bops.write(cache);
            i++;
            offLen = MAX_ENCRYPT_BLOCK * i;
        }
        bops.close();
        byte[] encryptedData = bops.toByteArray();

        return Base64.encodeBase64String(encryptedData);
    }

    /**
     * 公钥解密
     *
     * @param data      待解密数据
     * @param publicKey 密钥
     * @return byte[] 解密数据
     */
    public static String decryptByPublicKey(String data, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        // 分段解密
        byte[] bytesContent = Base64.decodeBase64(data);
        int inputLen = bytesContent.length;
        int offLen = 0;
        int i = 0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (inputLen - offLen > 0) {
            byte[] cache;
            if (inputLen - offLen > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(bytesContent, offLen, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(bytesContent, offLen, inputLen - offLen);
            }
            byteArrayOutputStream.write(cache);
            i++;
            offLen = MAX_DECRYPT_BLOCK * i;

        }
        byteArrayOutputStream.close();

        return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    }

    /**
     * 取得私钥
     *
     * @param privateKey 私钥对象
     * @return byte[] 私钥
     */
    private static String getPrivateKey(Key privateKey) {
        return Base64.encodeBase64String(privateKey.getEncoded());
    }

    /**
     * 取得公钥
     *
     * @param publicKey 公钥对象
     * @return byte[] 公钥
     */
    private static String getPublicKey(Key publicKey) {
        return Base64.encodeBase64String(publicKey.getEncoded());
    }

}
