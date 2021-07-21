package com.coodev.androidcollection.mvvm.databind;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.coodev.androidcollection.BR;
import com.coodev.androidcollection.entity.LoginInfo;

/**
 * 手动处理双向绑定
 * BaseObservable 提供了 notifyChange() 和 notifyPropertyChanged() 两个方法，
 * 前者会刷新所有的值域，后者则只更新对应 BR 的 flag，
 * 该 BR 的生成通过注释 @Bindable 生成，可以通过 BR notify 特定属性关联的视图
 */
public class TwoWayViewModel2 extends BaseObservable {

    /**
     * notifyPropertyChanged()只更新对应 BR 的 flag，该 BR 的生成通过注释 @Bindable 生成
     * 如果是 public 修饰符，则可以直接在成员变量上方加上 @Bindable 注解
     * 如果是 private 修饰符，则在成员变量的 get 方法上添加 @Bindable 注解
     */
    @Bindable
    public String name;

    private int age;

    public TwoWayViewModel2(String name, int age) {
        this.name = name;
        this.age = age;
    }

    /**
     * notifyPropertyChanged()只更新对应 BR 的 flag，该 BR 的生成通过注释 @Bindable 生成
     *
     * @return 用户名
     */
    @Bindable
    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
        // 局部更新
        notifyPropertyChanged(BR.name);
    }

    public void setInfo(String name, int age) {
        this.name = name;
        this.age = age;
        // 全部刷新
        notifyChange();
    }

}
