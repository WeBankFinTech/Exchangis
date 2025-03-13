package com.webank.wedatasphere.exchangis.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.FileCopyUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtils {
    public static byte[] generateKey(String seed){
        KeyGenerator generator = null;
        SecureRandom secureRandom;
        try {
            generator = KeyGenerator.getInstance("AES");
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (StringUtils.isNotBlank(seed)){
            secureRandom.setSeed(seed.getBytes());
        }
        generator.init(128, secureRandom);
        SecretKey secretKey = generator.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * Generate aes key
     * @param keystorePath key store path
     * @param seed random seed
     */
    public static void generateKey(String keystorePath, String seed){
        try {
            String base64Str = Base64.getEncoder()
                    .encodeToString(generateKey(seed));
            FileCopyUtils.copy(base64Str, new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(keystorePath))));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String encrypt(String src, String password){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            keyGenerator.init(128, random);
            SecretKey secretKey = keyGenerator.generateKey();
            return encrypt(src, secretKey.getEncoded());
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String encrypt(String src, byte[] keyRaw){
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(keyRaw, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher
                    .doFinal(src.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(encrypted), StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String decrypt(String src, String password){
        try{
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            keyGenerator.init(128, random);
            SecretKey secretKey = keyGenerator.generateKey();
            return decrypt(src, secretKey.getEncoded());
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String decrypt(String src, byte[] keyRaw){
        try{
            SecretKeySpec keySpec = new SecretKeySpec(keyRaw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(src);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        }catch(Exception ex){
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    public static String decryptHex(String srcHex, byte[] keyRaw) {
        try{
            SecretKeySpec keySpec = new SecretKeySpec(keyRaw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = hexStringToBytes(srcHex);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        }catch(Exception ex){
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private static byte[] hexStringToBytes(String hexString){
        int len = hexString.length();
        if(len % 2 == 1){
            hexString = "0" + hexString;
        }
        byte[] output = new byte[len / 2];
        int pos = 0;
        for(int i = 0 ; i < len / 2; i ++){
            pos = i * 2;
            output[i] = (byte) (Integer.parseInt(hexString.substring(pos, pos + 2), 16));
        }
        return output;
    }

}
