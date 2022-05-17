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

import android.widget.Toast;

import com.tencent.rapidview.data.Var;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class ToastAction
 * @Desc 弹出toast的Action
 *
 * @author arlozhang
 * @date 2016.03.24
 */
public class ToastAction extends ActionObject{

    public ToastAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }


    @Override
    public boolean run() {
        Var value = mMapAttribute.get("value");

        if( value == null ){
            value = new Var("");
        }

        if( mRapidView == null ){
            return false;
        }

        Toast.makeText(mRapidView.getView().getContext(), value.getString(), Toast.LENGTH_SHORT).show();

        return true;
    }
}
