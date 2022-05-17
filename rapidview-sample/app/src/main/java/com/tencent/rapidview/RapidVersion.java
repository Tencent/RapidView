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
package com.tencent.rapidview;

/**
 * @Class RapidVersion
 * @Desc 光子皮肤引擎版本号，非常重要，修改引擎一定要增加版本号！
 *       由于CMS限制，GRAY_ID禁止使用空格
 *
 * @author arlozhang
 * @date 2016.04.21
 */
public class RapidVersion {

    /**RAPID_ENGINE_VERSION
    * 光子皮肤引擎版本号，当更新了在XML中有体现的功能时，如增加XML参数，增加ACTION等，都需要增加引擎版本号，以确
    * 保CMS下发皮肤文件时，不会把含有不支持的功能的新XML配置发到老的版本中，引起不能解析的异常。在配置XML时，
    * 需要在CMS中指定该文件最低支持的光子皮肤引擎版本号，即为功能开发后，引擎版本号增加后的那一版版本号。**/

    public final static int RAPID_ENGINE_VERSION = 1;

    /**RAPID_GRAY_ID
     * 灰度标识，当有多个团队同时开发皮肤引擎时。可能同时存在相同的引擎版本对应不同的功能。例如，主干的光子皮肤
     * 引擎版本号为7，两个团队基于主干拉了两个svn分支，A团队开发了XXX功能，增加引擎版本号，为8。发布灰度。B团队开
     * 发了YYY功能，增加引擎版本号也为8，发布灰度。此时两个8版本对应于不同的功能。
     *
     * 为了解决这个问题，出现了灰度标识。当发布灰度时，无需增加引擎版本号，只需要配置唯一灰度标识。如A团队可以配
     * 置a_gray_xxx。B团队可以配置b_gray_yyy。此时。在CMS配置视图时，同时配置灰度版唯一灰度标识后。本地针对
     * 同名视图会优先拉取基于当前「引擎版本」的配置了「灰度标识」的视图。当功能合入主干后，删除灰度标识，增加引擎
     * 版本号，此时发布的主线包为单线程发布，就不会出现这个问题了。**/

    public final static String RAPID_GRAY_ID = "";
}
