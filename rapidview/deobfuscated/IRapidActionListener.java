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
package com.tencent.rapidview.deobfuscated;

/**
 * @Class IRapidActionListener
 * @Desc 界面索取方如果需要执行交互动作需要实现该接口，并根据传入Key，Value决定执行怎么样的动作
 *
 * @author arlozhang
 * @date 2016.03.23
 */
public interface IRapidActionListener {

    void notify(String key, String value);

}
