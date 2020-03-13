package com.picovr.androidcollection.ui.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.picovr.androidcollection.R;
import com.picovr.androidcollection.widget.webview.WebHelper;

/**
 * <pre>
 *   @author  : lucien.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2018/12/04 14:28
 *   desc    : 基础弹框(从底部弹框)
 * </pre>
 */
public abstract class BaseBottomDialog extends DialogFragment {
    private View mRootView;
    private DialogInterface.OnDismissListener listener;

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(listener != null){
            listener.onDismiss(dialog);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        if (getDialog() == null) {
            return;
        }

        //取消点击外面和返回消失
        if (!canceledOnTouchOutside()) {
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                    return false;
                }
            });
        }

        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            // 背景色透明
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.getDecorView().setPadding(10, 10, 10, 10);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(wlp);
            window.setWindowAnimations(R.style.BottomDialogStyle);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.NoticeDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutResID = specifyLayout(savedInstanceState);
        //对话框的布局
        mRootView = inflater.inflate(layoutResID, container,false);

        initView(mRootView);
        return mRootView;
    }

    /**
     * 指定布局id
     * @param savedInstanceState
     * @return
     */
    protected abstract int specifyLayout(Bundle savedInstanceState);

    /**
     * 初始化布局
     * @param root
     */
    protected abstract void initView(View root);

    /**
     * 找到控件ID
     */
    protected <T extends View> T findViewById(int id) {
        if (mRootView == null) {
            return null;
        }
        return (T) mRootView.findViewById(id);
    }

    /**
     * 点击外面可取消
     * @return
     */
    protected abstract boolean canceledOnTouchOutside();

    public void show(FragmentActivity mFragmentActivity){
        show(mFragmentActivity.getSupportFragmentManager(), getClass().getSimpleName());
    }

}
