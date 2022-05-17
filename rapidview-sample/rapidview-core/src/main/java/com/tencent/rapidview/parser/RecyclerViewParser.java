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
package com.tencent.rapidview.parser;

import android.support.v7.widget.LinearLayoutManager;

import com.tencent.rapidview.control.NormalRecyclerView;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.utils.RapidStringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RecyclerViewParser
 * @Desc RecyclerView的解析器
 *
 * @author arlozhang
 * @date 2017.09.14
 */
public class RecyclerViewParser extends ViewGroupParser {

    private static Map<String, IFunction> mRecyclerViewClassMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mRecyclerViewClassMap.put("layoutmanager", initlayoutmanager.class.newInstance());
            mRecyclerViewClassMap.put("maxflingcount", initmaxflingcount.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public RecyclerViewParser(){}

    @Override
    protected void loadFinish(){
        ((NormalRecyclerView)mRapidView.getView()).getAdapter().setLimitLevel(mLimitLevel);
        ((NormalRecyclerView)mRapidView.getView()).getAdapter().setRapidID(mRapidID);
        ((NormalRecyclerView)mRapidView.getView()).getAdapter().setActionListener(getActionListener());
    }

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mRecyclerViewClassMap.get(key);

        return clazz;
    }

    private static class initlayoutmanager implements IFunction {
        public initlayoutmanager(){}

        public void run(RapidParserObject object, Object view, Var value) {
            List<String> list = RapidStringUtils.stringToList(value.getString());
            if( list.size() == 1 ){
                String type = list.get(0);
                if( type.compareToIgnoreCase("linearlayoutmanager") == 0 ){
                    ((NormalRecyclerView)view).setLinearLayoutManager(LinearLayoutManager.VERTICAL, false);
                }
                else if( type.compareToIgnoreCase("gridlayoutmanager") == 0 ){
                    ((NormalRecyclerView)view).setGridLayoutManager(3);
                }
            }
            else if( list.size() == 2 ){
                String type = list.get(0);
                String param1 = list.get(1);

                if( type.compareToIgnoreCase("linearlayoutmanager") == 0 ){
                    int orientation = LinearLayoutManager.VERTICAL;
                    if( param1.compareToIgnoreCase("horizontal") == 0 ){
                        orientation = LinearLayoutManager.HORIZONTAL;
                    }
                    else if( param1.compareToIgnoreCase("INVALID_OFFSET") == 0 ){
                        orientation = LinearLayoutManager.INVALID_OFFSET;
                    }

                    ((NormalRecyclerView)view).setLinearLayoutManager(orientation, false);
                }
                else if( type.compareToIgnoreCase("gridlayoutmanager") == 0 ){
                    ((NormalRecyclerView)view).setGridLayoutManager(Integer.parseInt(param1));
                }
            }
            else if( list.size() >= 3 ){
                String type = list.get(0);
                String param1 = list.get(1);
                String param2 = list.get(2);

                if( type.compareToIgnoreCase("linearlayoutmanager") == 0 ){
                    int orientation = LinearLayoutManager.VERTICAL;
                    boolean reverseLayout = RapidStringUtils.stringToBoolean(param2);

                    if( param1.compareToIgnoreCase("horizontal") == 0 ){
                        orientation = LinearLayoutManager.HORIZONTAL;
                    }
                    else if( param1.compareToIgnoreCase("INVALID_OFFSET") == 0 ){
                        orientation = LinearLayoutManager.INVALID_OFFSET;
                    }

                    ((NormalRecyclerView)view).setLinearLayoutManager(orientation, reverseLayout);
                }
                else if( type.compareToIgnoreCase("gridlayoutmanager") == 0 ){
                    ((NormalRecyclerView)view).setGridLayoutManager(Integer.parseInt(param1));
                }
            }
        }
    }

    private static class initmaxflingcount implements IFunction {
        public initmaxflingcount() {
        }

        public void run(RapidParserObject object, Object view, Var value) {
            ((NormalRecyclerView)view).setMaxFlingCount(value.getInt());
        }
    }
}
