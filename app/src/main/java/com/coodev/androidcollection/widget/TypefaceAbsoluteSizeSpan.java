package com.coodev.androidcollection.widget;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;

import androidx.annotation.NonNull;

/**
 * 设置不同的字体，用于SpannableString
 */
class TypefaceAbsoluteSizeSpan extends AbsoluteSizeSpan {

    private final float baselineOffset;
    private final Typeface mUITypeface;

    public TypefaceAbsoluteSizeSpan(int size, float baselineOffset, Typeface typeface) {
        super(size);
        this.baselineOffset = baselineOffset;
        mUITypeface = typeface;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setTypeface(mUITypeface);
        textPaint.baselineShift += (int) (baselineOffset);
        textPaint.baselineShift += 1;//字体位置修正
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint textPaint) {
        super.updateMeasureState(textPaint);
        textPaint.setTypeface(mUITypeface);
        textPaint.baselineShift += (int) (baselineOffset);
        textPaint.baselineShift += 1;//字体位置修正
    }
}