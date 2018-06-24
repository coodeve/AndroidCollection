package com.picovr.androidcollection.mvp.model;

import com.picovr.androidcollection.mvp.base.CallBack;

import java.util.Map;

/**
 * 此类，为一点基本的通用功能的提取，自己可直接使用接口进行实现。
 * @author patrick.ding
 * @date 18/6/22
 */

public abstract class BaseModel<T> implements IBaseModel<T>{
    //数据请求参数
    protected String[] mParams;
    /**
     * 设置数据请求参数
     * @param args 参数数组
     */
    public  BaseModel params(String... args){
        mParams = args;
        return this;
    }

    /**
     *  执行Get网络请求，此类看需求由自己选择写与不写
     */
    protected void requestGetAPI(String url,CallBack<T> callback){
        //这里写具体的网络请求
    }

    /**
     *  执行Get网络请求，此类看需求由自己选择写与不写
     */
    protected void requestPostAPI(String url, Map params, CallBack<T> callback){
        //这里写具体的网络请求
    }


}
