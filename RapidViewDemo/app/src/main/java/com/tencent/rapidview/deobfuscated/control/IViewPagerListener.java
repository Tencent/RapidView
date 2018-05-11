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
package com.tencent.rapidview.deobfuscated.control;

/**
 * @Class IViewPagerListener
 * @Desc viewPager的消息通知
 *
 * @author arlozhang
 * @date 2017.07.03
 */
public interface IViewPagerListener {

    /**
     * 模拟系统的pause和resume在翻页的时候也发出pause和resume消息，同时和系统的pause和resume合在一起
     */
    void onPause(int pos, String tag);

    void onResume(int pos, String tag);

    /**
     * 提供一个首次反动到当前页面的标记，便于页面进行加载
     */
    void onPageSelected(int pos, String tag, Boolean first);
}
