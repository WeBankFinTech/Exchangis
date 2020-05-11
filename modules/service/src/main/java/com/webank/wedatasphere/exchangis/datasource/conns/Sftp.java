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

package com.webank.wedatasphere.exchangis.datasource.conns;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * @author davidhua
 * 2019/5/20
 */
public class Sftp {
    private static final String STRICT_HOST_KEY = "StrictHostKeyChecking";

    public static SftpConnection login(String host, int port, String username, String prvKeyPath,
                                       String password, int timeout){
        JSch jSch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        try{
            if(StringUtils.isNotBlank(prvKeyPath)){
                jSch.addIdentity(prvKeyPath);
            }
            session = jSch.getSession(username, host,port);
            if(null == session){
                throw new RuntimeException("Login sftp server failed");
            }
            session.setPassword(password);
            Properties config = new Properties();
            config.put(STRICT_HOST_KEY, "no");
            config.put("PreferredAuthentications", "publickey,password");
            session.setConfig(config);
            session.setTimeout(timeout);
            session.connect();
            channelSftp = (ChannelSftp)session.openChannel("sftp");
            channelSftp.connect();
            return new SftpConnection(channelSftp, session);
        }catch(Exception e){
            if(null != session && session.isConnected()){
                session.disconnect();
            }
            if(null != channelSftp && channelSftp.isConnected()){
                channelSftp.disconnect();
            }
            throw new RuntimeException(e);
        }
    }


    public static class SftpConnection{
        private ChannelSftp channelSftp;
        private Session session;
        SftpConnection(ChannelSftp channelSftp,  Session session){
            this.channelSftp = channelSftp;
            this.session = session;
        }

        public void disconnect(){
            if(channelSftp.isConnected()){
                channelSftp.disconnect();
            }
            if(session.isConnected()){
                session.disconnect();
            }
        }

        public ChannelSftp getChannel(){
            return channelSftp;
        }

        public Session getSession(){
            return session;
        }

    }
}
