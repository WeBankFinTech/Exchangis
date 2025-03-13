/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.common.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.crypto.Cipher;

public class CryptoUtils {
    /**
     * Random seed
     */
    private static SecureRandom secureRandom = new SecureRandom();

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static final String RSA_ALGORITHM_NAME = "RSA";

    public static final int RSA_MAX_LATIN_LENGTH = 117;

    public static final int RSA_MAX_ENCRYPTED_TEXT_LENGTH = 128;

    private CryptoUtils(){
    }
    /**
     * Serialize the object to string
     * @param o Object
     * @return String
     * @throws Exception e
     */
    public static String object2String(Serializable o) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(o);
        oos.flush();
        oos.close();
        bos.close();
        return new String(new Base64().encode(bos.toByteArray()));
    }

    /**
     * Deserialize the string to object
     *
     * @param str String
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object string2Object(String str) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(new Base64().decode(str.getBytes()));
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object o = ois.readObject();
        bis.close();
        ois.close();
        return o;
    }

    /**
     * MD5 algorithm
     * @param source source string
     * @param salt salt
     * @param iterator iterator
     * @return value encrypted
     */
    public static String md5(String source, String salt, int iterator){
        StringBuilder token = new StringBuilder();
        try{
            MessageDigest digest = MessageDigest.getInstance("md5");
            if(StringUtils.isNotEmpty(salt)){
                digest.update(salt.getBytes(StandardCharsets.UTF_8));
            }
            byte[] result = digest.digest(source.getBytes());
            for(int i = 0; i < iterator - 1; i++){
                digest.reset();
                result = digest.digest(result);
            }
            for (byte aResult : result) {
                int temp = aResult & 0xFF;
                if (temp <= 0xF) {
                    token.append("0");
                }
                token.append(Integer.toHexString(temp));
            }
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return token.toString();
    }

    /**
     * Generate key pair
     * @return
     */
    public static Pair<RSAPrivateKey, RSAPublicKey> generateRSAKeyPair(){
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048, secureRandom);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            return new MutablePair<>((RSAPrivateKey) keyPair.getPrivate(), (RSAPublicKey) keyPair.getPublic());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypt by RSA algorithm (Hex string output)
     * @param source source data
     * @param publicKey public key
     * @return hex string
     * @throws Exception e
     */
    public static String encryptHexRSA(String source, String publicKey) throws Exception {
        byte[] encryptedBytes = rawEncryptRSA(source, publicKey);
        return Objects.nonNull(encryptedBytes) ? toHexString(encryptedBytes) : null;
    }


    /**
     * Encrypt by RSA algorithm (Default Base64 output)
     * @param source source data
     * @param publicKey public key
     * @return base64 string
     * @throws Exception e
     */
    private static String encryptRSA(String source, String publicKey) throws Exception{
        byte[] encryptedBytes = rawEncryptRSA(source, publicKey);
        return Objects.nonNull(encryptedBytes)? Base64.encodeBase64String(encryptedBytes) : null;
    }


    /**
     * Decrypt by RSA algorithm (default: Base64)
     * @param encrypted data encrypted
     * @param privateKey private key
     * @return
     * @throws Exception
     */
    public static String decryptRSA(String encrypted, String privateKey) throws Exception{
        if(StringUtils.isNotBlank(encrypted) && StringUtils.isNotBlank(privateKey)) {
            return decryptRSA(Base64.decodeBase64(encrypted.getBytes(StandardCharsets.US_ASCII)), privateKey);
        }
        return null;
    }

    public static String decryptHexRSA(String encryptedHex, String privateKey) throws Exception{
        if(StringUtils.isNotBlank(encryptedHex) && StringUtils.isNotBlank(privateKey)) {
            return decryptRSA(hexStringToBytes(encryptedHex), privateKey);
        }
        return null;
    }

    private static String decryptRSA(byte[] encryptedByte, String privateKey) throws Exception{
        byte[] privateKeyByte = Base64.decodeBase64(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_NAME);
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyByte));
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM_NAME);
        //Use privateKey to decrypt
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] decrypted = null;
        try{
            //Try to decrypt all the bytes encrypted
            decrypted = cipher.doFinal(encryptedByte);
        }catch (Throwable i){
            //Ignore
        }
        if(null == decrypted){
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int offset = 0;
            int sourceLen = encryptedByte.length;
            int left;
            while ((left = Math.min(sourceLen - offset, RSA_MAX_ENCRYPTED_TEXT_LENGTH)) > 0) {
                byte[] lainText = cipher.doFinal(encryptedByte, offset, left);
                output.write(lainText, 0, lainText.length);
                offset += left;
            }
            decrypted = output.toByteArray();
            //Necessary?
            output.close();
        }
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * Encrypt by RSA algorithm
     * @param source plain text
     * @param publicKey public key
     * @return encrypted bytes
     * @throws Exception e
     */
    private static byte[] rawEncryptRSA(String source, String publicKey) throws Exception{
        if(StringUtils.isNotBlank(source) && StringUtils.isNotBlank(publicKey)) {
            byte[] publicKeyByte = Base64.decodeBase64(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_NAME);
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyByte));
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM_NAME);
            //Use publicKey to encrypt
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            byte[] rawData = source.getBytes(StandardCharsets.UTF_8);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int offset = 0;
            int sourceLen = rawData.length;
            int left;
            while ((left = Math.min(sourceLen - offset, RSA_MAX_LATIN_LENGTH)) > 0) {
                byte[] encrypted = cipher.doFinal(rawData, offset, left);
                output.write(encrypted, 0, encrypted.length);
                offset += left;
            }
            byte[] encryptedData = output.toByteArray();
            //Necessary?
            output.close();
            return encryptedData;
        }
        return null;
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

    /**
     * Convert bytes to hex string
     * @param rawBytes raw bytes
     * @return hex string
     */
    private static String toHexString(byte[] rawBytes){
        char[] hexChars = new char[rawBytes.length * 2];
        for (int i = 0; i < rawBytes.length; i++){
            int v = rawBytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
