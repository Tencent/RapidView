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
package com.tencent.rapidview.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.tencent.rapidview.parser.ButtonParser;
import com.tencent.rapidview.parser.RapidParserObject;

/**
 * @Class RapidButton
 * @Desc 光子界面Button
 *
 * @author arlozhang
 * @date 2016.02.18
 */
public class RapidButton extends RapidViewObject {

    public RapidButton(){}

    @Override
    protected RapidParserObject createParser(){
        return new ButtonParser();
    }

    @Override
    protected View createView(Context context){
        return new Button(context);
    }
}
