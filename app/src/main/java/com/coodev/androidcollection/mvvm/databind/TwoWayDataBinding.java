package com.coodev.androidcollection.mvvm.databind;

import androidx.databinding.ObservableField;

import com.coodev.androidcollection.entity.LoginInfo;

/**
 * 使用{@link androidx.databinding.ObservableField}
 */
public class TwoWayDataBinding {
    private ObservableField<LoginInfo> mLoginInfoObservableField;

    public TwoWayDataBinding() {
        final LoginInfo loginInfo = new LoginInfo();
        mLoginInfoObservableField = new ObservableField<>(loginInfo);
    }

    // 自动调用
    public String getUserName() {
        return mLoginInfoObservableField.get().userName;
    }

    // 自动调用
    public void setUserName(String userName) {
        mLoginInfoObservableField.get().userName = userName;
    }
}
