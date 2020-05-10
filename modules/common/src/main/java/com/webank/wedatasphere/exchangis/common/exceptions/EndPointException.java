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

package com.webank.wedatasphere.exchangis.common.exceptions;


/**
 * Map inner exception to ui message
 * @author davidhua
 * 2019/1/16
 */
public class EndPointException extends RuntimeException{
    private String uiMessage;
    private Object[] args;
    public EndPointException(String uiMessage, String message, Throwable throwable){
        super(message, throwable);
        this.uiMessage = uiMessage;
    }
    public EndPointException(String uiMessage, Throwable throwable,Object...args){
        super(throwable);
        if(args != null && args.length > 0){
            this.args = args;
        }
        this.uiMessage = uiMessage;
    }
    public String getUiMessage(){
        return this.uiMessage;
    }
    public Object[] getArgs(){
        return this.args;
    }
}
