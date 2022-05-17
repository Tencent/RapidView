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

import org.luaj.vm2.LuaValue;

/**
 * @Class IVar
 * @Desc Var的非混淆接口
 *
 * @author arlozhang
 * @date 2017.09.18
 */
public interface IVar {

    boolean getBoolean();

    int getInt();

    long getLong();

    float getFloat();

    double getDouble();

    String getString();

    Object getObject();

    Object getArrayItem(int index);

    int getArrayLenth();

    LuaValue getLuaValue();

    void createIntArray(int length);

    void createBooleanArray(int length);

    void createFloatArray(int length);

    void createDoubleArray(int length);

    int[] getIntArray();

    boolean[] getBooleanArray();

    float[] getFloatArray();

    double[] getDoubleArray();

    int getIntArrayItem(int index);

    boolean getBooleanArrayItem(int index);

    float getFloatArrayItem(int index);

    double getDoubleArrayItem(int index);
}
