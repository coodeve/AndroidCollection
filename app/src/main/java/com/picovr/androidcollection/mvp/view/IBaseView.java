package com.picovr.androidcollection.mvp.view;

import android.content.Context;

/**
 * @author patrick.ding
 * @date 18/6/21
 */

public interface IBaseView  {
    void showLoading();
    void hideLoading();
    void showError(String error);
    Context getContexts();
}
