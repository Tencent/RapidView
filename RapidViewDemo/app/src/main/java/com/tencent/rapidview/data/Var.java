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
public class Var implements IVar{

    public enum TYPE{
        enum_null,
        enum_boolean,
        enum_int,
        enum_long,
        enum_float,
        enum_double,
        enum_string,
        enum_array,
        enum_object
    }

    private TYPE     mCurrentType = TYPE.enum_null;

    private boolean  mBoolean = false;

    private int      mInt    = 0;

    private long     mLong   = 0;

    private float    mFloat  = 0;

    private double   mDouble = 0;

    private String   mString = "";

    private Object[] mArray  = null;

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

    public void set(int value){
        mInt = value;
        mCurrentType = TYPE.enum_int;
    }

    public void set(long value){
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

    public void setFloat(float value){
        mFloat = value;
        mCurrentType = TYPE.enum_float;
    }

    public void set(double value){
        mDouble = value;
        mCurrentType = TYPE.enum_double;
    }

    public void set(String value){
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
        }

        return false;
    }

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
            default:
                return 0;
        }

        return 0;
    }

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
            default:
                return 0;
        }

        return 0;
    }

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
            default:
                return 0;
        }

        return 0;
    }

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
            default:
                return 0;
        }

        return 0;
    }

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

    public int getArrayLenth(){
        if( mCurrentType != TYPE.enum_array ){
            return -1;
        }

        if( mArray == null ){
            return 0;
        }

        return mArray.length;
    }

    public Object getArrayItem(int index){
        if( mCurrentType != TYPE.enum_array ||
            mArray == null ||
            index < 0 ||
            index >= mArray.length ){
            return null;
        }

        return mArray[index];
    }

    public Object[] getArray(){
        if( mCurrentType != TYPE.enum_array ){
            return null;
        }

        return mArray;
    }

    public LuaValue getLuaValue(){
        switch (mCurrentType){
            case enum_object:
                return LuaValue.userdataOf(mObject);
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
