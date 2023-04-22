package com.example.euc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    // Hash password using SHA-256 algorithm
//    public static String hashPassword(String password) {
//        String hashedPassword = null;
//
//        try {
//            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
//            byte[] hashBytes = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
//            StringBuilder stringBuilder = new StringBuilder();
//
//            for (byte hashByte : hashBytes) {
//                stringBuilder.append(String.format("%02x", hashByte));
//            }
//
//            hashedPassword = stringBuilder.toString();
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//        return hashedPassword;
//    }
    public static String hashPassword(String text) {
        String sha256 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            sha256 = String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sha256;
    }
}
