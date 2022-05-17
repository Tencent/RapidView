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
package com.tencent.rapidview.action;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.parser.RapidParserObject;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class TaskAction
 * @Desc 调用task的动作
 *
 * @author arlozhang
 * @date 2016.08.04
 */
public class TaskAction extends ActionObject{

    public TaskAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run() {
        Var task = mMapAttribute.get("tid");
        RapidParserObject parser = getParser();

        if( parser == null || task == null ){
            return false;
        }

        parser.run(task.getString());

        return true;
    }
}
