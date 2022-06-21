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

package com.webank.wedatasphere.exchangis.datax.common;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author devendeng
 * For cryptor
 */
public class CryptoUtils {
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
}
