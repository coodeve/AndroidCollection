package com.picovr.androidcollection.mvp.model;

import com.picovr.androidcollection.mvp.base.OnLoadListener;

/**
 * @author patrick.ding
 * @date 18/6/21
 */

public interface IBaseModel {
    <T> void requestData(OnLoadListener<T> onLoadListener);

}
