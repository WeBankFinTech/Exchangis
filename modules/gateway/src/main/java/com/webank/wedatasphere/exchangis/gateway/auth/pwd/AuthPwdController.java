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

package com.webank.wedatasphere.exchangis.gateway.auth.pwd;

import com.webank.wedatasphere.exchangis.common.util.AESUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Map;

/**
 * @author davidhua
 * 2019/4/30
 */
@RequestMapping("/pwd")
@Controller
public class AuthPwdController {
    @Value("${auth.script.secret-key}")
    private String secretKeyPath;

    private static final String ENCRYPT_PWD_KEY = "src_pwd";
    private static final Logger LOG = LoggerFactory.getLogger(AuthPwdController.class);

    @RequestMapping(value = "/encrypt", method = RequestMethod.POST)
    public ResponseEntity<String> encrypt(@RequestBody Map<String, String> map){
        String srcPwd = map.get(ENCRYPT_PWD_KEY);
        if(StringUtils.isBlank(srcPwd)){
            return ResponseEntity.status(403).body("");
        }
        String encryptPwd = "";
        try {
            String keyContent = FileCopyUtils.copyToString(new BufferedReader
                    (new InputStreamReader(new FileInputStream(secretKeyPath))));
            if(StringUtils.isNotBlank(keyContent)){
                encryptPwd =  AESUtils.encrypt(srcPwd, Base64.getDecoder().decode(
                        keyContent));
            }else{
                return ResponseEntity.status(500).body("");
            }
        }catch(Exception e){
            LOG.error(e.getMessage());
            return ResponseEntity.status(500).body("");
        }
        return ResponseEntity.ok(encryptPwd);
    }
}
