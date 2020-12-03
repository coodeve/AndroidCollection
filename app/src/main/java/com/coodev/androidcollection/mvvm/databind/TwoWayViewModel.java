package com.coodev.androidcollection.mvvm.databind;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.coodev.androidcollection.BR;
import com.coodev.androidcollection.entity.LoginInfo;

/**
 * 手动处理双向绑定
 */
public class TwoWayViewModel extends BaseObservable {
    private LoginInfo mLoginInfo;

    public TwoWayViewModel() {
        mLoginInfo = new LoginInfo();
    }

    @Bindable
    public String getUserName() {
        return mLoginInfo.userName;
    }

    /**
     * 会被自定调用
     * @param userName
     */
    public void setUserName(String userName) {
        if (userName != null && !userName.equals(mLoginInfo.userName)) {
            mLoginInfo.userName = userName;
            notifyPropertyChanged(BR.userName);
        }
    }
}
