package com.coodev.androidcollection.mvvm.databind;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.coodev.androidcollection.BR;
import com.coodev.androidcollection.entity.LoginInfo;

/**
 * 手动处理双向绑定
 * BaseObservable 提供了 notifyChange() 和 notifyPropertyChanged() 两个方法，
 * 前者会刷新所有的值域，后者则只更新对应 BR 的 flag，
 * 该 BR 的生成通过注释 @Bindable 生成
 */
public class TwoWayViewModel extends BaseObservable {

    //如果是 public 修饰符，则可以直接在成员变量上方加上 @Bindable 注解
    //如果是 private 修饰符，则在成员变量的 get 方法上添加 @Bindable 注解
    private LoginInfo mLoginInfo;

    public TwoWayViewModel() {
        mLoginInfo = new LoginInfo();
        mLoginInfo.userName = "Patrick";
    }

    /**
     * notifyPropertyChanged()只更新对应 BR 的 flag，该 BR 的生成通过注释 @Bindable 生成
     *
     * @return 用户名
     */
    @Bindable
    public String getUserName() {
        return mLoginInfo.userName;
    }

    /**
     * 会被自定调用
     *
     * @param userName
     */
    public void setUserName(String userName) {
        if (userName != null && !userName.equals(mLoginInfo.userName)) {
            mLoginInfo.userName = userName;
            notifyPropertyChanged(BR.userName);
        }
    }
}
