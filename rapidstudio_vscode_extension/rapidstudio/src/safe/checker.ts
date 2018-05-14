/***************************************************************************************************
 Tencent is pleased to support the open source community by making RapidView available.
 Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 Licensed under the MITLicense (the "License"); you may not use this file except in compliance
 withthe License. You mayobtain a copy of the License at
 http://opensource.org/licenses/MIT
 Unless required by applicable law or agreed to in writing, software distributed under the License is
 distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 implied. See the License for the specific language governing permissions and limitations under the
 License.
 ***************************************************************************************************/

import {XLog} from "../tool"
import { toUnicode, encode, decode } from "punycode";

export class RapidChecker{
    public static assertSafeFilePath(path : string ){
        try {
            // a safe file path should include blank or chinese symbol 
            if(this.containsValidChar(path)){
                throw new Error("Path cannot contains chinese symbol or other unicode: " + path);
            }
    
            if(path.indexOf(" ")>=0){
                throw new Error("Path cannot contains space: " + path);
            }
        } catch (error) {
            XLog.error((<Error>error).message);
            throw error;
        }
        
    }

    private static containsValidChar(str:string){ 
        for (var i=0; i < str.length; i++) {
            var el = str.charCodeAt(i);
            if(el > 255){
                return true;
            }
        }
        return false;
    }
}