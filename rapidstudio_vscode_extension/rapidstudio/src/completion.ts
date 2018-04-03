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

import {CompletionItemProvider,CompletionItem,CompletionItemKind,Position,CancellationToken,TextDocument, workspace} from 'vscode';
import { XLog } from './tool';



export class RapidCompletionManager{
    public static initCompletion(onSuccess : Function){
        const rootPath = workspace.rootPath;
        let path = require("path");
        let fs = require("fs");
        let workspace_file = rootPath + path.sep + "rapid_workspace.json";
        fs.readFile(workspace_file, 'utf8', function (err, data) {
            if (err) {
                XLog.error("An error occurred while setting auto completion: cannot read data from rapid_workspace.json.");
                return;
            }
            // Catch json exception
            let workspaceData = JSON.parse(data);
            try{
                
                if(workspaceData['completion'] && workspaceData['completion']['xml_tags']){
                    let _xmlTags =  workspaceData['completion']['xml_tags'];
                    _xmlTags.forEach(_xmlTag => {
                        xmlTags.push(_xmlTag);
                    });
                }
                if(workspaceData['completion'] && workspaceData['completion']['xml_attrs']){
                    let _xmlAttrs =  workspaceData['completion']['xml_attrs'];
                    _xmlAttrs.forEach(_xmlAttr => {
                        xmlAttrs.push(_xmlAttr);
                    });
                }
                if(workspaceData['completion'] && workspaceData['completion']['lua_funcs']){
                    let _luaFuncs=  workspaceData['completion']['lua_funcs'];
                    _luaFuncs.forEach(_luaFunc => {
                        luaFunctions.push(_luaFunc);
                    });
                }
            }catch(error){
                console.log(error);
                XLog.error("An error occurred while setting auto completion, the default setting has been used.");
                return;
            }
            onSuccess();
        });
    }
}


export class RapidXMLCompletionItemProvider implements CompletionItemProvider {
    private _completionItems: CompletionItem[];
    constructor (){
        this._completionItems = new Array<CompletionItem>();
        xmlTags.forEach(tag => {
            this._completionItems.push(new CompletionItem(tag,CompletionItemKind.Field));
        });
    }
    public provideCompletionItems(
        document: TextDocument, position: Position, token: CancellationToken): 
        CompletionItem[] {
            return this._completionItems;
    }
}

export class RapidXMLAttrsCompletionItemProvider implements CompletionItemProvider {
    private _completionItems: CompletionItem[];
    constructor (){
        this._completionItems = new Array<CompletionItem>();
        xmlAttrs.forEach(tag => {
            this._completionItems.push(new CompletionItem(tag,CompletionItemKind.Field));
        });
    }
    public provideCompletionItems(
        document: TextDocument, position: Position, token: CancellationToken): 
        CompletionItem[] {
            return this._completionItems;
    }

    public static getTriggerCharacters():String[]{
        return ['a', 'b', 'd', 'e', 'f', 't', 'u', 'w', 'r', 'i', 'l', 'n', 'o', 'g', 'L', 'c', 's'];
    }
}

export class RapidLuaCompletionItemProvider implements CompletionItemProvider {
    private _completionItems: CompletionItem[];
    constructor (){
        this._completionItems = new Array<CompletionItem>();
        luaFunctions.forEach(tag => {
            this._completionItems.push(new CompletionItem(tag,CompletionItemKind.Field));
        });
    }
    
    public provideCompletionItems(
        document: TextDocument, position: Position, token: CancellationToken): 
        CompletionItem[] {
            return this._completionItems;
    }
}



let xmlTags = [
    "RelativeLayout",
    "LinearLayout",
    "AbsoluteLayout",
    "TextView",
    "ImageView",
    "Progressbar",
    "ImageButton",
    "Button",
    "TXImageView",
    "TXAppiconView",
    "TXTab",
    "FrameLayout",
    "ScrollView",
    "InnerScrollView",
    "HorizontalScrollView",
    "ShaderView",
    "TXWebView",
    "HorizonScrollPicViewer",
    "ExpandableTextView",
    "ViewStub",
    "ListView",
    "DetailDownloadButton",
    "ContentItemDownloadButton",
    "TXDownloadProgressBar",
    "BannerView",
    "VideoView",
    "SpecialVideoView",
    "BookingButton",
    "GifView",
    "EditText"
]

let xmlAttrs = [
    "disposal",
    "task",
    "hook",
    "datafilter",
    "networkfilter",
    "packagefilter",
    "viewchangeaction",
    "updatedataaction",
    "outeraction",
    "toastaction",
    "tmastaction",
    "reportaction",
    "addviewaction",
    "dialogaction",
    "backaction",
    "taskaction",
    "videoaction",
    "removedataarrayitemaction",
    "copyobjectaction",
    "jumpappdetailaction",
    "imagebrowseraction",
    "luaaction",
    "integeroperationaction",
    "interrput",
    "datachange",
    "loadfinish",
    "datainitialize",
    "viewshow",
    "viewscrollexposure",
    "url1",
    "url2",
    "url3",
    "url4",
    "url5",
    "action1",
    "action2",
    "action3",
    "action4",
    "action5",
    "title1",
    "title2",
    "title3",
    "title4",
    "title5",
    "imagebackgroundcolor",
    "id",
    "interval",
    "scaletype",
    "appid",
    "recommendid",
    "ordered",
    "actionurl",
    "scene",
    "slotid",
    "sourcesceneslotid",
    "actionid",
    "status",
    "gradientdrawable",
    "packagename",
    "appname",
    "iconurl",
    "verifytype",
    "description",
    "versioncode",
    "filesize",
    "apkurl",
    "apkid",
    "report",
    "averagerating",
    "click",
    "activity",
    "downloadcount",
    "md5",
    "signature",
    "flag",
    "channelid",
    "versionname",
    "applink",
    "sourcemodeltype",
    "appsimpledetail",
    "downloadtext",
    "opentext",
    "sourcescene",
    "modeltype",
    "buttontype",
    "selectall",
    "selection",
    "extendselection",
    "cutflag",
    "snapshot",
    "url",
    "auto",
    "maxframe",
    "repeatmode",
    "wifiauto",
    "tryplay",
    "data",
    "imagehorizontalheight",
    "imageverticalheight",
    "imageheight",
    "edgewidth",
    "fling",
    "fullscroll",
    "scrollto",
    "smoothscrollingenabled",
    "smoothscrollby",
    "smoothscrollto",
    "background",
    "image",
    "resizeimage",
    "backgroundcolor",
    "scaletype",
    "frameanimation",
    "startframeanimation",
    "stopframeanimation",
    "oneshotframeanimation",
    "visibleframeanimation",
    "startoffsetframeanimation",
    "adjustviewbounds",
    "maxheight",
    "minheight",
    "maxwidth",
    "minwidth",
    "gravity",
    "horizontalgravity",
    "verticalgravity",
    "baselinealigned",
    "weightsum",
    "orientation",
    "cachecolorhint",
    "dividerheight",
    "footerdividers",
    "headerdividers",
    "itemscanfocus",
    "selectionafterheaderview",
    "choicemode",
    "drawselectornntop",
    "fastscroll",
    "filtertext",
    "scrollbarstyle",
    "scrollingcache",
    "smoothscrollbar",
    "stackfrombottom",
    "textfilter",
    "transcriptmode",
    "progressbackgroundcolor",
    "progresscolor",
    "indeterminate",
    "progress",
    "scrolltochild",
    "notifychildscroll",
    "lineargradient",
    "snapshoturl",
    "videoid",
    "videomargin",
    "ems",
    "maxems",
    "minems",
    "singleline",
    "ellipsize",
    "text",
    "textstyle",
    "textsize",
    "textcolor",
    "line",
    "maxlines",
    "linespacingextra",
    "linespacingmultiplier",
    "scalex",
    "scaley",
    "textscalex",
    "freezestext",
    "maxheight",
    "minheight",
    "maxwidth",
    "minwidth",
    "autolink",
    "buffertype",
    "cursorvisible",
    "hint",
    "imeactionid",
    "imeactionlabel",
    "imeoptions",
    "includefontpadding",
    "inputtype",
    "rawinputtype",
    "coverview",
    "speedtextview",
    "progresstextview",
    "queuecolor",
    "pausecolor",
    "downloadcolor",
    "default",
    "blururl",
    "imageshape",
    "reset",
    "pause",
    "start",
    "preparedseek",
    "seek",
    "stop",
    "play",
    "wifiautoplay",
    "autoplay",
    "descendantfocusability",
    "backgroundresource",
    "backgrounddrawable",
    "clickable",
    "contentdescription",
    "contextclickable",
    "drawingcachebackgroundcolor",
    "drawingcacheenabled",
    "drawingcachequality",
    "duplicateparentstateenabled",
    "duplicateparentstate",
    "enabled",
    "focusable",
    "focusableintouchmode",
    "hapticfeedbackenabled",
    "fadingedge",
    "horizontalfadingedgeenabled",
    "horizontalscrollbarenabled",
    "keepscreenon",
    "longclickable",
    "minimumheight",
    "minimumwidth",
    "padding",
    "saveenabled",
    "scrollcontainer",
    "scrollbarfadingenabled",
    "selected",
    "soundeffectsenabled",
    "verticalfadingedgeenabled",
    "verticalscrollbarenabled",
    "visibility",
    "willnotcachedrawing",
    "willnotdraw",
    "longclick",
    "keyevent",
    "createcontextmenu",
    "focuschange",
    "touch",
    "animation",
    "startanimation",
    "clearanimation",
    "realid",
    "scrollexposure",
    "layoutgravity",
    "weight",
    "margin",
    "marginleft",
    "margintop",
    "marginbottom",
    "marginright",
    "alignleft",
    "aligntop",
    "alignright",
    "alignbottom",
    "leftof",
    "above",
    "rightof",
    "below",
    "centervertical",
    "centerhorizontal",
    "centerinparent",
    "alignparenttop",
    "alignparentright",
    "alignparentleft",
    "alignparentbottom",
    "height",
    "width",
    "fill_parent",
    "match_parent",
    "wrap_content",
    "true",
    "false",
    "no_gravity",
    "top",
    "bottom",
    "left",
    "right",
    "center_vertical",
    "fill_vertical",
    "center_horizontal",
    "fill_horizontal",
    "center",
    "fill",
    "alphaanimation",
    "animationset",
    "rotateanimation",
    "scaleanimation",
    "translateanimation",
    "animationlist",
    "fromalpha",
    "toalpha",
    "cancel",
    "initialize",
    "restrictduration",
    "scalecurrentduration",
    "detachwallpaper",
    "duration",
    "fillafter",
    "fillbefore",
    "fillenabled",
    "repeatcount",
    "repeatmode",
    "startoffset",
    "starttime",
    "zadjustment",
    "startnow",
    "interpolator",
    "animationend",
    "animationrepeat",
    "animationstart",
    "addanimation",
    "shareinterpolator",
    "addframe",
    "oneshot",
    "visible",
    "fromdegrees",
    "todegrees",
    "pivotxtype",
    "pivotxvalue",
    "pivotytype",
    "pivotyvalue",
    "fromx",
    "tox",
    "fromy",
    "toy",
    "fromxtype",
    "fromxvalue",
    "toxtype",
    "toxvalue",
    "fromytype",
    "fromyvalue",
    "toytype",
    "toyvalue",
    "invisible",
    "gone",
    "event",
    "focus",
    "userview",
    "function",
    "file",
    "view",
    "parent",
    "data",
    "object",
    "origin",
    "target",
    "doublebutton",
    "style",
    "title",
    "content",
    "buttontext",
    "buttonclick",
    "leftbuttontext",
    "rightbuttontext",
    "leftbuttonclick",
    "rightbuttonclick",
    "urls",
    "index",
    "value",
    "operation",
    "add",
    "subtract",
    "multiply",
    "divide",
    "pageid",
    "sourceslot",
    "key",
    "name",
    "param",
    "call",
    "tmast",
    "reference",
    "type",
    "unequal",
    "active",
    "wifi",
    "2g",
    "3g",
    "4g",
    "wap",
    "package",
    "uninstalled",
    "integer",
    "float",
    "greater",
    "greaterequal",
    "equal",
    "unequal",
    "less",
    "lessequal",
    "horizontal",
    "vertical"
]

let luaFunctions = [
    "getBytesFromBitmap",
    "Log",
    "create",
    "decode",
    "encode",
    "getAnimationCenter",
    "getEnv",
    "getGlobals",
    "getJavaInterface",
    "isLimitLevel",
    "getContext",
    "getPhotonID",
    "getLayoutParams",
    "getUiHandler",
    "addView",
    "removeView",
    "update",
    "bind",
    "unregister",
    "get",
    "removeData",
    "getObject",
    "getArrayByte",
    "isNil",
    "getString",
    "getLength",
    "shareImageToWX",
    "shareTextToWX",
    "request",
    "takePicture",
    "choosePicture",
    "getBitmapFromBytes",
    "createParams",
    "getID",
    "getView",
    "getParser",
    "load",
    "initialize",
    "addView",
    "setArrayView",
    "setEnvironment",
    "getEnv",
    "add",
    "run",
    "notify",
    "getActionRunner",
    "getFilterRunner",
    "setParentView",
    "getParentView",
    "setIndexInParent",
    "getIndexInParent",
    "getUIHandler",
    "update",
    "getParams",
    "getID",
    "getChildView",
    "getBinder",
    "getListener",
    "getTaskCenter",
]

