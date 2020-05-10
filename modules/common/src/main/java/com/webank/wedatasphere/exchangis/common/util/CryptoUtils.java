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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.crypto.Cipher;

/**
 * @author devendeng
 * For cryptor
 */
public class CryptoUtils {
    /**
     * Random seed
     */
    private static SecureRandom secureRandom = new SecureRandom();

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
            keyPairGen.initialize(1024, secureRandom);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            return new MutablePair<>((RSAPrivateKey) keyPair.getPrivate(), (RSAPublicKey) keyPair.getPublic());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypt by RSA algorithm
     * @param source source data
     * @param publicKey public key
     * @return
     * @throws Exception
     */
    public static String encryptRSA(String source, String publicKey) throws Exception{
        byte[] publicKeyByte = Base64.decodeBase64(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey rsaPublicKey = (RSAPublicKey)keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyByte));
        Cipher cipher = Cipher.getInstance("RSA");
        //Use publicKey to encrypt
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] encrypted = cipher.doFinal(source.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(encrypted);
    }

    /**
     * Decrypt by RSA algorithm
     * @param encrypted data encrypted
     * @param privateKey private key
     * @return
     * @throws Exception
     */
    public static String decryptRSA(String encrypted, String privateKey) throws Exception{
        byte[] privateKeyByte = Base64.decodeBase64(privateKey);
        byte[] encryptedByte = Base64.decodeBase64(encrypted.getBytes(StandardCharsets.UTF_8));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyByte));
        Cipher cipher = Cipher.getInstance("RSA");
        //Use privateKey to decrypt
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] decrypted = cipher.doFinal(encryptedByte);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

}
