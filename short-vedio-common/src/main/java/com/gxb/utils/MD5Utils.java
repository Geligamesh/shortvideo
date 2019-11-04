package com.gxb.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("md5");
        String newStr = Base64.encodeBase64String(md5.digest(strValue.getBytes()));
        return newStr;
    }

    public static void main(String[] args) {
        try {
            String md5Str = getMD5Str("123456");
            System.out.println(md5Str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
