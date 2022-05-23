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
package com.tencent.rapidview.data;

import com.tencent.rapidview.deobfuscated.IVar;
import com.tencent.rapidview.utils.RapidStringUtils;

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * @Class Var
 * @Desc RapidView变量类型
 *
 * @author arlozhang
 * @date 2016.12.08
 */
public class Var implements IVar {

    public enum TYPE{
        enum_null,
        enum_boolean,
        enum_int,
        enum_long,
        enum_float,
        enum_double,
        enum_string,
        enum_array,
        enum_object,
        enum_int_array,
        enum_boolean_array,
        enum_float_array,
        enum_double_array,
    }

    private TYPE      mCurrentType = TYPE.enum_null;

    private boolean   mBoolean = false;

    private int       mInt    = 0;

    private long      mLong   = 0;

    private float     mFloat  = 0;

    private double    mDouble = 0;

    private String    mString = "";

    private Object[]  mArray  = null;

    private int[]     mIntArray = null;

    private boolean[] mBooleanArray = null;

    private float[]   mFloatArray = null;

    private double[]  mDoubleArray = null;

    private Object   mObject = null;


    public Var(){}

    public Var(boolean value){
        set(value);
    }

    public Var(int value){
        set(value);
    }

    public Var(long value){
        set(value);
    }

    public Var(float value){
        set(value);
    }

    public Var(double value){
        set(value);
    }

    public Var(String value){
        set(value);
    }

    public Var(Object[] value){
        int[] a = new int[6];
        set(value);
    }

    public Var(Object value){
        set(value);
    }

    public Var(LuaValue value){
        if( value == null || value.isnil() ){
            return;
        }

        if( value.isboolean() ){
            set(value.toboolean());
            return;
        }

        if( value.isstring() || value.isnumber() ){
            set(value.toString());
            return;
        }

        if( value.isuserdata() ){
            set(value.touserdata());
            return;
        }

        set(value);
    }

    public void setNull(){
        mCurrentType = TYPE.enum_null;
    }

    public boolean isNull(){
        return mCurrentType == TYPE.enum_null;
    }

    public TYPE getType(){
        return mCurrentType;
    }

    public void set(boolean value){
        mBoolean = value;
        mCurrentType = TYPE.enum_boolean;
    }

    public void set(Boolean value){
        mBoolean = value;
        mCurrentType = TYPE.enum_boolean;
    }

    public void set(int value){
        mInt = value;
        mCurrentType = TYPE.enum_int;
    }

    public void set(Integer value){
        mInt = value;
        mCurrentType = TYPE.enum_int;
    }

    public void set(long value){
        mLong = value;
        mCurrentType = TYPE.enum_long;
    }

    public void set(Long value){
        mLong = value;
        mCurrentType = TYPE.enum_long;
    }

    public void setLong(long value){
        mLong = value;
        mCurrentType = TYPE.enum_long;
    }

    public void set(float value){
        mFloat = value;
        mCurrentType = TYPE.enum_float;
    }

    public void set(Float value){
        mFloat = value;
        mCurrentType = TYPE.enum_float;
    }

    public void setFloat(float value){
        mFloat = value;
        mCurrentType = TYPE.enum_float;
    }

    public void set(double value){
        mDouble = value;
        mCurrentType = TYPE.enum_double;
    }

    public void set(Double value){
        mDouble = value;
        mCurrentType = TYPE.enum_double;
    }

    public void set(String value){
        if( value == null ){
            value = "";
        }

        mString = value;
        mCurrentType = TYPE.enum_string;
    }

    public void set(Object[] value){
        mArray = value;

        mCurrentType = TYPE.enum_array;
    }

    public void set(Object value){
        mObject = value;
        mCurrentType = TYPE.enum_object;
    }

    @Override
    public boolean getBoolean(){

        switch (mCurrentType){
            case enum_boolean:
                return mBoolean;
            case enum_int:
                if( mInt == 0 ){
                    return false;
                }

                return true;
            case enum_long:
                if( mLong == 0 ){
                    return false;
                }

                return true;
            case enum_float:
                if( mFloat == 0 ){
                    return false;
                }

                return true;
            case enum_double:
                if( mDouble == 0 ){
                    return false;
                }

                return true;
            case enum_string:
                return RapidStringUtils.stringToBoolean(mString);
            case enum_object:
                if( mObject != null && mObject instanceof Boolean ) {
                    return ((Boolean) mObject).booleanValue();
                }
        }

        return false;
    }

    @Override
    public int getInt(){

        switch (mCurrentType){
            case enum_int:
                return mInt;
            case enum_long:
                return 0;
            case enum_boolean:
                if( mBoolean ){
                    return 1;
                }
                return 0;
            case enum_float:
                return (int)mFloat;
            case enum_double:
                return (int)mDouble;
            case enum_string:
                try{
                    return mString.isEmpty() ? 0 : Integer.parseInt(mString);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case enum_object:
                if( mObject != null && mObject instanceof Integer ){
                    return ((Integer) mObject).intValue();
                }
            default:
                return 0;
        }

        return 0;
    }

    @Override
    public long getLong(){

        switch (mCurrentType){
            case enum_int:
                return mInt;
            case enum_long:
                return mLong;
            case enum_boolean:
                if( mBoolean ){
                    return 1;
                }
                return 0;
            case enum_float:
                return (int)mFloat;
            case enum_double:
                return (int)mDouble;
            case enum_string:
                try{
                    return Long.parseLong(mString);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case enum_object:
                if( mObject != null && mObject instanceof Long ) {
                    return ((Long) mObject).longValue();
                }
            default:
                return 0;
        }

        return 0;
    }

    @Override
    public float getFloat(){

        switch (mCurrentType){
            case enum_float:
                return mFloat;
            case enum_boolean:
                if( mBoolean ){
                    return 1;
                }
                return 0;
            case enum_int:
                return mInt;
            case enum_long:
                return 0;
            case enum_double:
                return (float)mDouble;
            case enum_string:
                try{
                    return Float.parseFloat(mString);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case enum_object:
                if( mObject != null && mObject instanceof Float ) {
                    return ((Float) mObject).floatValue();
                }
            default:
                return 0;
        }

        return 0;
    }

    @Override
    public double getDouble(){

        switch (mCurrentType){
            case enum_double:
                return mDouble;
            case enum_boolean:
                if( mBoolean ){
                    return 1;
                }
                return 0;
            case enum_int:
                return mInt;
            case enum_long:
                return 0;
            case enum_float:
                return (double)mFloat;
            case enum_string:
                try{
                    return Double.parseDouble(mString);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case enum_object:
                if( mObject != null && mObject instanceof Double ) {
                    return ((Double) mObject).doubleValue();
                }
            default:
                return 0;
        }

        return 0;
    }

    @Override
    public String getString(){

        switch (mCurrentType){
            case enum_string:
                return mString == null ? "" : mString;
            case enum_boolean:
                return Boolean.toString(mBoolean);
            case enum_int:
                return Integer.toString(mInt);
            case enum_long:
                return Long.toString(mLong);
            case enum_float:
                return Float.toString(mFloat);
            case enum_double:
                return Double.toString(mDouble);
            case enum_object:
                return mObject == null ? "" : mObject.toString();
        }

        return "";
    }

    @Override
    public Object getObject(){

        switch (mCurrentType){
            case enum_object:
                return mObject;
            case enum_boolean:
                return mBoolean;
            case enum_int:
                return mInt;
            case enum_long:
                return mLong;
            case enum_float:
                return mFloat;
            case enum_double:
                return mDouble;
            case enum_array:
                return mArray;
            case enum_string:
                return mString;
        }

        return null;
    }

    @Override
    public int getArrayLenth(){

        switch (mCurrentType){
            case enum_array:
                return mArray.length;
            case enum_int_array:
                return mIntArray.length;
            case enum_boolean_array:
                return mBooleanArray.length;
            case enum_float_array:
                return mFloatArray.length;
            case enum_double_array:
                return mDoubleArray.length;
        }

        return -1;
    }

    @Override
    public Object getArrayItem(int index){
        if( mCurrentType != TYPE.enum_array ||
                mArray == null ||
                index < 0 ||
                index >= mArray.length ){
            return null;
        }

        return mArray[index];
    }

    @Override
    public void createIntArray(int length){
        mIntArray = new int[length];
        mCurrentType = TYPE.enum_int_array;
    }

    @Override
    public void createBooleanArray(int length){
        mBooleanArray = new boolean[length];
        mCurrentType = TYPE.enum_boolean_array;
    }

    @Override
    public void createFloatArray(int length){
        mFloatArray = new float[length];
        mCurrentType = TYPE.enum_float_array;
    }

    @Override
    public void createDoubleArray(int length){
        mDoubleArray = new double[length];
        mCurrentType = TYPE.enum_double_array;
    }

    @Override
    public int[] getIntArray(){
        return mIntArray;
    }

    @Override
    public boolean[] getBooleanArray(){
        return mBooleanArray;
    }

    @Override
    public float[] getFloatArray(){
        return mFloatArray;
    }

    @Override
    public double[] getDoubleArray(){
        return mDoubleArray;
    }

    @Override
    public int getIntArrayItem(int index){
        return mIntArray[index];
    }

    @Override
    public boolean getBooleanArrayItem(int index){
        return mBooleanArray[index];
    }

    @Override
    public float getFloatArrayItem(int index){
        return mFloatArray[index];
    }

    @Override
    public double getDoubleArrayItem(int index){
        return mDoubleArray[index];
    }

    public Object[] getArray(){
        if( mCurrentType != TYPE.enum_array ){
            return null;
        }

        return mArray;
    }

    @Override
    public LuaValue getLuaValue(){
        switch (mCurrentType){
            case enum_object:
                if( mObject instanceof Object[] ||
                        mObject instanceof byte[]   ||
                        mObject instanceof int[]    ||
                        mObject instanceof boolean[]||
                        mObject instanceof float[]  ||
                        mObject instanceof double[] ){
                    return LuaValue.userdataOf(mObject);
                }
                else {
                    return CoerceJavaToLua.coerce(mObject);
                }
            case enum_boolean:
                return LuaBoolean.valueOf(mBoolean);
            case enum_int:
                return LuaInteger.valueOf(mInt);
            case enum_long:
                return LuaInteger.valueOf(mLong);
            case enum_float:
                return LuaDouble.valueOf(mFloat);
            case enum_double:
                return LuaDouble.valueOf(mDouble);
            case enum_array:
                return arrayToTable(mArray);
            case enum_string:
                return LuaString.valueOf(mString);
        }

        return null;
    }


    private static LuaTable arrayToTable(Object[] arrayObj){
        LuaTable table = new LuaTable();

        for( int i = 0; i < arrayObj.length; i++ ){
            table.set( LuaInteger.valueOf( i + 1), CoerceJavaToLua.coerce(arrayObj[i]));
        }

        return table;
    }
}
