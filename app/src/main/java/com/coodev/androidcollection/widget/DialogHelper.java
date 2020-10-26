package com.coodev.androidcollection.widget;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;

public class DialogHelper {

    private static void createDialogFromButton(Context context,int layout,int anim){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(anim);
        window.setContentView(layout);
    }

    private static void createTimePickerDialog(Context context, TimePickerDialog.OnTimeSetListener listener, int hourOfDay, int minute,
                                               boolean is24HourView){
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, listener, hourOfDay, minute, is24HourView);
        timePickerDialog.show();
    }

}
