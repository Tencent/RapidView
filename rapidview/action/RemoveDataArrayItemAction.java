package com.tencent.rapidview.action;

import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class RemoveDataArrayItemAction
 * @Desc 当数据池中存在数组时，约定key以后缀数字表达，如data1,data2,data3,data4,data5。当调用本action时，会
 * 删除对应的数据，如data3。然后由后面的数据补位，例如：data1:10,data2:20,data3:30,data4:40,data5:50。当删除
 * data3时，数据变成：data1:10,data2:20,data3:40,data4:50。
 *
 * @author arlozhang
 * @date 2016.08.10
 */
public class RemoveDataArrayItemAction extends ActionObject{

    Map<String, Var> mMapData = null;

    public RemoveDataArrayItemAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run(){
        Var name   = mMapAttribute.get("name");
        Var index  = mMapAttribute.get("index");
        Var map    = mMapAttribute.get("data");
        int    nIndex;
        Var   value;

        RapidDataBinder binder = getBinder();

        if( binder == null || name == null || index == null ){
            return false;
        }

        if( map != null ){

            Object obj = null;
            if( map.getObject() instanceof Map ){
                obj = map.getObject();
            }
            else{
                obj = binder.getObject(map.getString());
            }

            if( obj != null && obj instanceof Map ){
                mMapData = (Map<String, Var>) obj;
            }
        }

        removeData(binder, name.getString() + index.getString());

        nIndex = index.getInt() + 1;

        value = getData(binder, name + Integer.toString(nIndex));

        while( !value.isNull() ){

            updateData(binder, name + Integer.toString(nIndex - 1), value);

            nIndex++;

            value = getData(binder, name + Integer.toString(nIndex));
        }

        removeData(binder, name + Integer.toString(nIndex - 1));

        return true;
    }

    private Var getData(RapidDataBinder binder, String key){
        Var ret;

        if( mMapData != null ){
            ret = mMapData.get(key);
            if( ret == null ){
                ret = new Var();
            }

            return ret;
        }

        return binder.getData(key);
    }

    private void removeData(RapidDataBinder binder, String key){
        if( mMapData != null ){
            mMapData.remove(key);
            return;
        }

        binder.removeData(key);
    }

    private void updateData(RapidDataBinder binder, String key, Var value){
        if( mMapData != null ){
            mMapData.put(key, value);
            return;
        }

        binder.update(key, value);
    }
}
