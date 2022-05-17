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

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class InvalidateAction
 * @Desc 无效化界面动作
 *
 * @author arlozhang
 * @date 2018.01.05
 */
public class InvalidateAction extends ActionObject{

    public InvalidateAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run(){

        mRapidView.getView().invalidate();
        mRapidView.getView().requestLayout();

        return true;
    }
}
