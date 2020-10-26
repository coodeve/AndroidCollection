package com.coodev.androidcollection.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Author EnvisionBoundary
 * Created at  2018/6/28.
 */

public class NormalDialog {
    private final Context context;
    private CharSequence ok;
    private CharSequence cancel;
    private final AlertDialog.Builder builder;
    private int okColor = -1;
    private int cancelColor = -1;
    private boolean canceledOnTouchOutside = true;
    public AlertDialog alertDialog;
    private boolean mCancelVisible = true;
    private boolean alertDialogHeightHalfWindow = false;
    private boolean mDismissPositiveButton = true;
    private float height = 1f;
    private View view;
    private DialogInterface.OnCancelListener onCancelListener;
    public static final String OK = "ok";
    public static final String CANCEL = "cancel";


    public NormalDialog(Context context) {
        this.context = context;
        builder = new AlertDialog.Builder(context);
        ok = OK;
        cancel = CANCEL;
    }

    public NormalDialog setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public NormalDialog setMessage(String message) {
        builder.setMessage(message);
        return this;
    }

    public NormalDialog setMessage(View v) {
        builder.setView(v);
        view = v;
        return this;
    }

    public NormalDialog setOk(String ok) {
        this.ok = ok;
        return this;
    }

    public NormalDialog setCancel(String cancel) {
        this.cancel = cancel;
        return this;
    }

    public NormalDialog setCancelVisiable(boolean visible) {
        this.mCancelVisible = visible;
        return this;
    }

    public NormalDialog setOkColor(int color) {
        this.okColor = color;
        return this;
    }

    public NormalDialog setCancelColor(int color) {
        this.cancelColor = color;
        return this;
    }

    private NormalDialog setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return this;
    }

    public NormalDialog setAlertDialogHeight(boolean flag) {
        this.alertDialogHeightHalfWindow = flag;
        return this;
    }

    public NormalDialog setAlertDialogHeight(float height) {
        this.height = height;
        return this;
    }

    public void setPositiveButtonDismiss(boolean flag) {
        this.mDismissPositiveButton = flag;
    }

    public void show(final DialogListener onClickListener) {
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (onClickListener != null) {
                    onClickListener.confirm();
                }
                if (mDismissPositiveButton) {
                    dialogInterface.dismiss();
                } else {
                    preventDismissDialog();
                }
            }
        });
        if (mCancelVisible) {
            builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (onClickListener != null) {
                        onClickListener.cancel();
                    }
//                    dialogInterface.dismiss();
                }
            });
        }
        alertDialog = builder.create();
        alertDialog.setCancelable(canceledOnTouchOutside);
        alertDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        //alertDialog.setOnCancelListener(onCancelListener);
        alertDialog.show();
        if (okColor != -1) {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(okColor);
        }
        if (cancelColor != -1) {
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(cancelColor);
        }

        if (alertDialogHeightHalfWindow) {
            setDialogHeight(alertDialog, 0.4f);
        }

        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setTextSize(15f);
            mMessageView.setTextColor(Color.GRAY);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void showEditDialog() {
        builder.setPositiveButton(ok, null);
        if (mCancelVisible) {
            builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
        alertDialog = builder.create();
        alertDialog.setCancelable(canceledOnTouchOutside);
        alertDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        alertDialog.show();
        if (okColor != -1) {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(okColor);
        }
        if (cancelColor != -1) {
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(cancelColor);
        }

        if (alertDialogHeightHalfWindow) {
            setDialogHeight(alertDialog, 0.4f);
        }
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setTextSize(15f);
            mMessageView.setTextColor(Color.GRAY);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }

    public void changePositiveText(String ok) {
        if (alertDialog != null) {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(ok);
        }
    }

    public void changePositiveText(int ok) {
        if (alertDialog != null) {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(ok);
        }
    }


    /**
     * 设置 dialog的高度
     *
     * @param dialog
     */
    private void setDialogHeight(AlertDialog dialog) {
        WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (display.getHeight() * 0.4);//设置为当前屏幕高度的0.4
        dialog.getWindow().setAttributes(p);     //设置生效
    }

    public NormalDialog setDialogHeight(AlertDialog dialog, float height) {
        WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = m.getDefaultDisplay();
        WindowManager.LayoutParams p = alertDialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (display.getHeight() * height);
        alertDialog.getWindow().setAttributes(p);     //设置生效
        return this;
    }


    /**
     * 通过反射 阻止关闭对话框
     */
    private void preventDismissDialog() {
        try {
            Field field = alertDialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //设置mShowing值，欺骗android系统
            field.set(alertDialog, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public interface DialogListener {
        void confirm();
        void cancel();
    }

}
