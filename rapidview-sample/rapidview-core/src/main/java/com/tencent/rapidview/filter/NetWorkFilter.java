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
package com.tencent.rapidview.filter;

import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.utils.NetworkUtil;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @Class NetWorkFilter
 * @Desc 网络情况的过滤器
 *
 * @author arlozhang
 * @date 2016.08.04
 */
public class NetWorkFilter extends FilterObject{

    public NetWorkFilter(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean pass(){
        Var type = mMapAttribute.get("type");
        Var style = mMapAttribute.get("style");

        if( style == null ){
            return false;
        }

        if( type == null ){
            type = new Var("");
        }

        if( type.getString().compareToIgnoreCase("unequal") == 0 ){
            return isUnequal(style.getString());
        }

        return isEqual(style.getString());
    }

    private boolean isUnequal(String style){

        if( style.compareToIgnoreCase("active") == 0 ){

            if( !NetworkUtil.isNetworkActive() ){
                return true;
            }

            return false;
        }
        else if( style.compareToIgnoreCase("wifi") == 0 ){
            if( !NetworkUtil.isWifi() ){
                return true;
            }

            return false;
        }
        else if( style.compareToIgnoreCase("2g") == 0 ){
            if( !NetworkUtil.is2G() ){
                return true;
            }

            return false;
        }
        else if( style.compareToIgnoreCase("3g") == 0 ){
            if( !NetworkUtil.is3G() ){
                return true;
            }

            return false;
        }
        else if( style.compareToIgnoreCase("4g") == 0 ){
            if( !NetworkUtil.is4G() ){
                return true;
            }

            return false;
        }
        else if( style.compareToIgnoreCase("wap") == 0 ){
            if( !NetworkUtil.isWap() ){
                return true;
            }

            return false;
        }

        return false;
    }

    private boolean isEqual(String style){

        if( style.compareToIgnoreCase("active") == 0 ){

            if( NetworkUtil.isNetworkActive() ){
                return true;
            }

            return false;
        }
        else if( style.compareToIgnoreCase("wifi") == 0 ){
            if( NetworkUtil.isWifi() ){
                return true;
            }

            return false;
        }
        else if( style.compareToIgnoreCase("2g") == 0 ){
            if( NetworkUtil.is2G() ){
                return true;
            }

            return false;
        }
        else if( style.compareToIgnoreCase("3g") == 0 ){
            if( NetworkUtil.is3G() ){
                return true;
            }

            return false;
        }
        else if( style.compareToIgnoreCase("4g") == 0 ){
            if( NetworkUtil.is4G() ){
                return true;
            }

            return false;
        }
        else if( style.compareToIgnoreCase("wap") == 0 ){
            if( NetworkUtil.isWap() ){
                return true;
            }

            return false;
        }

        return false;
    }
}
