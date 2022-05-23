package com.tencent.rapidview.deobfuscated.utils;

/**
 * @Class IRapidFeedsCacheQueue
 * @Desc feeds列表页无感滑动接口定义
 *
 * @author arlozhang
 * @date 2018.09.14
 */
public interface IRapidFeedsCacheQueue {

    interface IRequestInterface {
        boolean request(Object lastStub, IRapidFeedsCacheQueue finishInterface);
    }

    interface IResponseInterface{
        void onGetResponse(Object response);
    }

    void start(IRequestInterface reqInterface);

    void finish(Boolean succeed, Boolean isFinish, Object resp, Object stub);

    void get(IResponseInterface respInterface);
}
