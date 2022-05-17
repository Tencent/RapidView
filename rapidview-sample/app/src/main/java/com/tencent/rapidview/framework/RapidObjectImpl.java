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
package com.tencent.rapidview.framework;

import android.content.Context;

import com.tencent.rapidview.deobfuscated.IRapidActionListener;
import com.tencent.rapidview.animation.RapidAnimationCenter;
import com.tencent.rapidview.data.DataExpressionsParser;
import com.tencent.rapidview.data.RapidDataBinder;
import com.tencent.rapidview.data.Var;
import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.lua.RapidLuaEnvironment;
import com.tencent.rapidview.param.ParamsObject;
import com.tencent.rapidview.parser.ViewStubParser;
import com.tencent.rapidview.task.RapidTaskCenter;
import com.tencent.rapidview.utils.RapidStringUtils;
import com.tencent.rapidview.utils.RapidXmlLoader;
import com.tencent.rapidview.utils.XLog;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Class RapidObjectImpl
 * @Desc 由于对外接口包含对外调用和内部递归两种功能，不应放在一起，
 *       因此抽出共同的实现逻辑部分，增加子类易读性和安全。
 *
 * @author arlozhang
 * @date 2016.04.12
 */
public abstract class RapidObjectImpl {

    public class CONCURRENT_LOAD_STATE {

        public volatile boolean mCalledLoad = false;

        public volatile boolean mCalledInitialize = false;

        public volatile boolean mInitialized = false;

        public volatile boolean mWaited = false;

        public List<IRapidView> mPreloadList = new ArrayList<IRapidView>();
    }

    protected IRapidView loadView(Context parent, IRapidView rapidView, ParamsObject paramsObject, IRapidActionListener listener){

        if( parent == null || rapidView == null || paramsObject == null ){
            return null;
        }


        rapidView.load(parent, paramsObject, listener);

        return rapidView;
    }

    protected IRapidView initXml(Context context,
                                 String  rapidID,
                                 boolean limitLevel,
                                 String  xmlName,
                                 Map<String, String> envMap,
                                 RapidLuaEnvironment luaEnv,
                                 RapidTaskCenter taskCenter,
                                 RapidAnimationCenter animationCenter,
                                 RapidDataBinder binder,
                                 CONCURRENT_LOAD_STATE concState){
        Document doc = null;
        IRapidView[] arrayView = null;

        if( context == null || RapidStringUtils.isEmpty(xmlName) ){
            if( context == null ){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "初始化失败（context为空）");
            }

            if( RapidStringUtils.isEmpty(xmlName) ){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "初始化失败（xmlName为空）");
            }

            return null;
        }

        doc = RapidXmlLoader.self().getDocument(context, xmlName, rapidID, limitLevel);

        if( doc == null ){
            XLog.d(RapidConfig.RAPID_ERROR_TAG, "初始化失败（XMLDOC为空）：" + xmlName);
            return null;
        }

        try{
            Element root = doc.getDocumentElement();
            if( root == null ){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "初始化失败（XML没有根节点）：" + xmlName);
                return null;
            }

            if( isMergeTag(root) ){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "根节点禁止使用merge标签：" + xmlName);
                return null;
            }

            arrayView = initElement(context, rapidID, limitLevel, root, envMap, luaEnv, null, taskCenter, animationCenter, binder, concState);
            if( arrayView[0] == null ){
                XLog.d(RapidConfig.RAPID_ERROR_TAG, "初始化的对象为空：" + xmlName);
                return null;
            }


            arrayView[0].getParser().getTaskCenter().setRapidView(arrayView[0]);
            arrayView[0].getParser().getXmlLuaCenter().setRapidView(arrayView[0]);

        }catch (Exception e){
            e.printStackTrace();
        }

        return arrayView[0];
    }


    protected boolean isSpecialTag(Element element){
        String tagName;

        if( element == null ){
            return false;
        }

        tagName = element.getTagName();

        if( tagName.compareToIgnoreCase("merge") != 0 &&
            tagName.compareToIgnoreCase("include") != 0 &&
            tagName.compareToIgnoreCase("viewstub") != 0 ){
            return false;
        }

        return true;
    }

    protected boolean isMergeTag(Element element){
        String tagName;

        if( element == null ){
            return false;
        }

        tagName = element.getTagName();

        if( tagName.compareToIgnoreCase("merge") != 0 ){
            return false;
        }

        return true;
    }

    protected IRapidView[] initElement(Context context,
                                       String  rapidID,
                                       boolean limitLevel,
                                       Element element,
                                       Map<String, String> envMap,
                                       RapidLuaEnvironment luaEnv,
                                       Map<String, IRapidView> brotherMap,
                                       RapidTaskCenter taskCenter,
                                       RapidAnimationCenter animationCenter,
                                       RapidDataBinder binder,
                                       CONCURRENT_LOAD_STATE concState){
        IRapidView[] arrayObj;

        if( isSpecialTag(element) ){
            arrayObj = initSpecialTag(context, rapidID, limitLevel, element, envMap, luaEnv, brotherMap, taskCenter, animationCenter, binder, concState);

            return arrayObj;
        }

        arrayObj = new IRapidView[1];

        arrayObj[0] = initNormalTag(context, rapidID, limitLevel, element, envMap, luaEnv, brotherMap, taskCenter, animationCenter, binder, concState);

        return arrayObj;
    }

    protected IRapidView[] initSpecialTag(Context context,
                                          String  rapidID,
                                          boolean limitLevel,
                                          Element element,
                                          Map<String, String> envMap,
                                          RapidLuaEnvironment luaEnv,
                                          Map<String, IRapidView> brotherMap,
                                          RapidTaskCenter taskCenter,
                                          RapidAnimationCenter animationCenter,
                                          RapidDataBinder binder,
                                          CONCURRENT_LOAD_STATE concState){

        String tagName = element.getTagName();

        if( tagName.compareToIgnoreCase("merge") == 0 ){
            return initMerge(context, rapidID, limitLevel, element, envMap, luaEnv, brotherMap, taskCenter, animationCenter, binder, concState);
        }

        if( tagName.compareToIgnoreCase("include") == 0 ){
            return initInclude(context, rapidID, limitLevel, element, envMap, luaEnv, taskCenter, animationCenter, binder, concState);
        }

        if( tagName.compareToIgnoreCase("viewstub") == 0 ){
            return initViewStub(context, rapidID, limitLevel, element, envMap, luaEnv, brotherMap, taskCenter, animationCenter, binder, concState);
        }

        return null;
    }

    protected IRapidView initNormalTag(Context context,
                                       String  rapidID,
                                       boolean limitLevel,
                                       Element element,
                                       Map<String, String> envMap,
                                       RapidLuaEnvironment luaEnv,
                                       Map<String, IRapidView> brotherMap,
                                       RapidTaskCenter taskCenter,
                                       RapidAnimationCenter animationCenter,
                                       RapidDataBinder binder,
                                       CONCURRENT_LOAD_STATE concState){
        Object obj;
        Class  clazz;

        try{
            if( taskCenter != null && addTask(element, taskCenter, envMap) ) {
                return null;
            }

            if( addScript(element, luaEnv, envMap) ){
                return null;
            }

            if( addPreCompile(element, luaEnv) ){
                return null;
            }


            if( addAnimation(element, animationCenter) ){
                return null;
            }

            clazz = RapidChooser.getInstance().getDisposalClass(element, limitLevel);
            if( clazz == null ){
                return null;
            }

            obj = clazz.newInstance();

            if( !( obj instanceof IRapidView) ){
                return null;
            }

            initControl(context, rapidID, limitLevel, (IRapidView)obj, element, envMap, luaEnv, brotherMap, taskCenter, animationCenter, binder, concState);

            ((IRapidView) obj).getParser().mControlName = element.getTagName().toLowerCase();

        }catch (Exception e){
            e.printStackTrace();
            obj = null;
        }

        return (IRapidView)obj;
    }

    protected boolean initControl( Context context,
                                   String  rapidID,
                                   boolean limitLevel,
                                   IRapidView view,
                                   Element element,
                                   Map<String, String> envMap,
                                   RapidLuaEnvironment luaEnv,
                                   Map<String, IRapidView> brotherMap,
                                   RapidTaskCenter taskCenter,
                                   RapidAnimationCenter animationCenter,
                                   RapidDataBinder binder,
                                   CONCURRENT_LOAD_STATE concState){

        if( view == null || element == null || envMap == null || taskCenter == null ){
            return false;
        }

        return view.initialize(context, rapidID, limitLevel, element, envMap, luaEnv, brotherMap, taskCenter, animationCenter, binder, concState);
    }

    protected IRapidView[] initMerge(Context context,
                                     String  rapidID,
                                     boolean limitLevel,
                                     Element element,
                                     Map<String, String> envMap,
                                     RapidLuaEnvironment luaEnv,
                                     Map<String, IRapidView> brotherMap,
                                     RapidTaskCenter taskCenter,
                                     RapidAnimationCenter animationCenter,
                                     RapidDataBinder binder,
                                     CONCURRENT_LOAD_STATE concState){
        IRapidView[] arrayView;
        NodeList listChild;
        List<IRapidView> listView = new ArrayList<IRapidView>();


        if( context == null ||element == null || envMap == null){
            return null;
        }

        listChild = element.getChildNodes();

        for( int i = 0; i < listChild.getLength(); i++ ){
            Element childElement;
            IRapidView rapidView;

            Node node = listChild.item(i);

            if( node.getNodeType() != Node.ELEMENT_NODE ){
                continue;
            }

            childElement = (Element)node;

            if( isSpecialTag(childElement) ){
                IRapidView[] arrayChildView = initSpecialTag(context, rapidID, limitLevel, childElement, envMap, luaEnv, brotherMap, taskCenter, animationCenter, binder, concState);

                for( int j = 0 ; j < arrayChildView.length; j++ ){
                    listView.add(arrayChildView[j]);
                }

                continue;
            }

            rapidView = initNormalTag(context, rapidID, limitLevel, childElement, envMap, luaEnv, brotherMap, taskCenter, animationCenter, binder, concState);

            if( rapidView != null ){
                listView.add(rapidView);
            }
        }

        arrayView = new IRapidView[listView.size()];

        for( int i = 0; i < listView.size(); i++ ){
            arrayView[i] = listView.get(i);
        }

        return arrayView;
    }

    protected IRapidView[] initInclude(Context context,
                                        String  rapidID,
                                        boolean limitLevel,
                                        Element element,
                                        Map<String, String> envMap,
                                        RapidLuaEnvironment luaEnv,
                                        RapidTaskCenter taskCenter,
                                        RapidAnimationCenter animationCenter,
                                        RapidDataBinder binder,
                                        CONCURRENT_LOAD_STATE concState){

        IRapidView[] arrayView;
        List<IRapidView> listView = new ArrayList<IRapidView>();
        String strXmlList;
        String strEnvList;
        String strBinder;
        String strLuaEnv;
        NamedNodeMap mapAttrs;
        List<String> listXml;
        List<Map<String, String>> listMapEnv;
        Map<String, String> mapAttributes = new ConcurrentHashMap<String, String>();
        DataExpressionsParser parser = new DataExpressionsParser();
        RapidLuaEnvironment luaEnvironment = luaEnv;

        if( context == null ||element == null ){
            return null;
        }

        mapAttrs = element.getAttributes();
        for( int i = 0; i < mapAttrs.getLength(); i++){
            mapAttributes.put(mapAttrs.item(i).getNodeName().toLowerCase(),
                    mapAttrs.item(i).getNodeValue());
        }

        strXmlList = mapAttributes.get("layout");
        if( strXmlList == null ){

            strXmlList = mapAttributes.get("xml");

            if( strXmlList == null ){
                strXmlList = "";
            }
        }

        if( parser.isDataExpression(strXmlList) ){
            strXmlList = parser.get(binder, envMap, null, null, strXmlList).getString();
        }

        strEnvList = mapAttributes.get("environment");
        if( strEnvList == null ){
            strEnvList = "";
        }

        if( parser.isDataExpression(strEnvList) ){
            Var list = parser.get(binder, envMap, null, null, strEnvList);
            if( list != null ){
                strEnvList = list.getString();
            }
        }

        strBinder = mapAttributes.get("binder");
        if( strBinder != null && strBinder.compareToIgnoreCase("new") == 0 ){
            binder = new RapidDataBinder(new ConcurrentHashMap<String, Var>());
        }

        strLuaEnv = mapAttributes.get("luaenvironment");
        if( strLuaEnv != null && strLuaEnv.compareToIgnoreCase("new") == 0 ){
            luaEnvironment = new RapidLuaEnvironment(null, rapidID, limitLevel);
        }

        listXml = RapidStringUtils.stringToList(strXmlList);
        listMapEnv = RapidStringUtils.stringToListMap(strEnvList);

        for( int i = 0; i < listXml.size(); i++ ){

            IRapidView xmlView = initXml( context,
                                          rapidID,
                                          limitLevel,
                                          listXml.get(i),
                    listMapEnv.size() > i ? listMapEnv.get(i) : new ConcurrentHashMap<String, String>(),
                                          luaEnvironment,
                                          taskCenter,
                                          animationCenter,
                                          binder,
                                          concState);

            if( xmlView == null ){
                continue;
            }

            listView.add(xmlView);
        }

        arrayView = new IRapidView[listView.size()];

        for( int i = 0; i < listView.size(); i++ ){
            arrayView[i] = listView.get(i);
        }

        return arrayView;
    }

    protected IRapidView[] initViewStub(Context context,
                                        String  rapidID,
                                        boolean limitLevel,
                                        Element element,
                                        Map<String, String> envMap,
                                        RapidLuaEnvironment luaEnv,
                                        Map<String, IRapidView> brotherMap,
                                        RapidTaskCenter taskCenter,
                                        RapidAnimationCenter animationCenter,
                                        RapidDataBinder binder,
                                        CONCURRENT_LOAD_STATE concState){
        IRapidView[]  arrayView = new IRapidView[1];
        String         strLayout;
        NamedNodeMap   mapAttrs;
        IRapidView xmlView;
        IRapidView stubView;
        Map<String, String>    mapAttributes = new ConcurrentHashMap<String, String>();
        DataExpressionsParser  parser = new DataExpressionsParser();

        if( context == null ||element == null ){
            return null;
        }

        mapAttrs = element.getAttributes();

        for( int i = 0; i < mapAttrs.getLength(); i++){
            mapAttributes.put(mapAttrs.item(i).getNodeName().toLowerCase(),
                    mapAttrs.item(i).getNodeValue());
        }

        strLayout = mapAttributes.get("layout");

        if( parser.isDataExpression(strLayout) ){
            Var layout = parser.get(binder, envMap, null, null, strLayout);
            if( layout != null ){
                strLayout = layout.getString();
            }
        }

        xmlView = initXml( context,
                           rapidID,
                           limitLevel,
                           strLayout,
                           new ConcurrentHashMap<String, String>(),
                           luaEnv,
                           taskCenter,
                           animationCenter,
                           binder,
                           concState);

        stubView = initNormalTag(context, rapidID, limitLevel, element, envMap, luaEnv, brotherMap, taskCenter, animationCenter, binder, concState);
        if( stubView == null ){
            return null;
        }

        if( !(stubView.getParser() instanceof ViewStubParser) ){
            return null;
        }

        ((ViewStubParser) stubView.getParser()).setReplaceView(xmlView);

        arrayView[0] = stubView;

        return arrayView;
    }

    protected boolean addTask(Element element, RapidTaskCenter taskCenter, Map<String, String> envMap){
        if( element.getTagName().compareToIgnoreCase("task") != 0 ||
                taskCenter == null ){
            return false;
        }

        taskCenter.setEnvironment(envMap);

        taskCenter.add(element);

        return true;
    }

    protected boolean addScript(Element element, RapidLuaEnvironment luaEnv, Map<String, String> envMap){
        if( element.getTagName().compareToIgnoreCase("script") != 0 ){
            return false;
        }

        luaEnv.getXmlLuaCenter().add(element, envMap);

        return true;
    }


    protected boolean addAnimation(Element element, RapidAnimationCenter animationCenter){

        if( animationCenter == null || !animationCenter.isAnimation(element) ){
            return false;
        }

        animationCenter.add(element);

        return true;
    }

    protected boolean addPreCompile(Element element, RapidLuaEnvironment luaEnv){
        String file = null;

        if( element.getTagName().compareToIgnoreCase("precompile") != 0 ){
            return false;
        }

        file = element.getAttribute("file");


        if( file != null ){
            luaEnv.initClosure(file);
        }

        return true;
    }
}
