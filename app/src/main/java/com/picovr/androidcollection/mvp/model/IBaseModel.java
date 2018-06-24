package com.picovr.androidcollection.mvp.model;

import com.picovr.androidcollection.mvp.base.CallBack;

/**
 * model和数据交互的接口类
 * @author patrick.ding
 * @date 18/6/21
 */

public interface IBaseModel<T> {
    /**
     * 动作执行接口，具体的有子类进行实现。
     * @param callBack
     */
    void execute(CallBack<T> callBack);

}
