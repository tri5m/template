package com.example.template.common.util;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * MD5hash签名工具类
 *
 * @author: trifolium.wang
 * @date: 2024/9/24
 */
public class MD5Util {

    private static final Logger log = LoggerFactory.getLogger(MD5Util.class);

    /**
     * 获取32位MD5签名Hex字符串
     */
    public static String getMD5String(byte[] bytes) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(bytes);
            return new String(new Hex().encode(bs), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取32位MD5摘要Hex字符串
     */
    public static String getMD5String(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(str.getBytes(StandardCharsets.UTF_8));
            return new String(new Hex().encode(bs), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 获取16位MD5签名Hex字符串
     */
    public static String get16MD5String(String str) {
        String md516 = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(str.getBytes(StandardCharsets.UTF_8));
            md516 = new String(new Hex().encode(bs), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (md516 != null && md516.length() >= 32) {
            md516 = md516.substring(8, 24);
        }

        return md516;
    }

    /**
     * 获取32位MD5签名Hex字符串
     */
    public static String getMD5String(File file) throws Exception {
        MessageDigest messagedigest = getMessageDigest();
        InputStream fis = null;
        try {
            fis = Files.newInputStream(file.toPath());
            byte[] buffer = new byte[1024];
            int numRead;
            while ((numRead = fis.read(buffer)) > 0) {
                messagedigest.update(buffer, 0, numRead);
            }
            return new BigInteger(1, messagedigest.digest()).toString(16);
        } catch (IOException e) {
            throw new Exception("File load failed.", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.warn("Close file failed");
                }
            }
        }
    }

    /**
     * 验证签名
     *
     * @param signature 签名
     */
    public static boolean checkMD5(String data, String signature) {
        String s = getMD5String(data);
        return signature.equals(s);
    }

    /**
     * 生成含有随机盐的MD5密码
     *
     * @param data 明文数据
     * @return 加密后的密码
     */
    public static String getSaltMD5String(String data) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(16);
        sb.append(r.nextInt(99999999)).append(r.nextInt(99999999));
        int len = sb.length();
        if (len < 16) {
            sb.append("0".repeat(Math.max(0, 16 - len)));
        }
        String salt = sb.toString();
        data = getMD5String(data + salt); //加盐后加密
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {   //把盐按照一定的规则存入加密码中
            cs[i] = data.charAt(i / 3 * 2);  //把加密后的密码按一定规则排列
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = data.charAt(i / 3 * 2 + 1);
        }

        return new String(cs);
    }

    /**
     * 校验密码是否正确
     *
     * @param data      要检测的数据
     * @param signature 加密后含随机盐的的密码
     * @return 是否一样
     */
    public static boolean checkSaltMD5(String data, String signature) {
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = signature.charAt(i);
            cs1[i / 3 * 2 + 1] = signature.charAt(i + 2);
            cs2[i / 3] = signature.charAt(i + 1);
        }
        String salt = new String(cs2);
        String res = getMD5String(data + salt);
        return res.equals(new String(cs1));
    }

    /**
     * 获取md5签名
     */
    private static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Not support MD5 digest.", e);
        }
    }


}