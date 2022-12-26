package org.ryuu.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class AES {
    private static final String TRANSFORMATION = "AES/CFB/PKCS5Padding";
    private static SecretKeySpec secretKeySpec;
    private static IvParameterSpec ivParameterSpec;

    public static void setKey(final String key) {
        try {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            keyBytes = MessageDigest.getInstance("SHA-256").digest(keyBytes);
            secretKeySpec = new SecretKeySpec(Arrays.copyOf(keyBytes, 16), "AES"); // Key must be 16, 24 or 32 bytes long (respectively for *AES-128*, *AES-192* or *AES-256*).
            ivParameterSpec = new IvParameterSpec(Arrays.copyOf(keyBytes, 16)); // IV length: must be 16 bytes long
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static byte[] encrypt(final byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return null;
    }


    public static String encrypt(final String string) {
        return Base64.getUrlEncoder().encodeToString(encrypt(string.getBytes(StandardCharsets.UTF_8)));
    }

    public static byte[] decrypt(final byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return null;
    }

    public static String decrypt(final String string) {
        byte[] bytes = decrypt(Base64.getUrlDecoder().decode(string));
        if (bytes == null) {
            return null;
        }
        return new String(bytes);
    }
}