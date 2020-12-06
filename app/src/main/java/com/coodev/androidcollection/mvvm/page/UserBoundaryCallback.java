package com.coodev.androidcollection.mvvm.page;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;

import com.coodev.androidcollection.dao.room.User;

class UserBoundaryCallback extends PagedList.BoundaryCallback<User> {
    @Override
    public void onZeroItemsLoaded() {
        // 无数据时获取数据
        getData();
    }

    @Override
    public void onItemAtFrontLoaded(@NonNull User itemAtFront) {

    }


    @Override
    public void onItemAtEndLoaded(@NonNull User itemAtEnd) {
        getNextData();
    }

    /**
     * 加载下一页数据
     */
    private void getNextData() {

    }

    /**
     * 获取数据
     */
    private void getData() {

    }

    /**
     * 将上述两个数据源加入到数据库中，
     * 由于数据绑定的原因，数据可以进刷新
     */
    private void insertData(){

    }

}
