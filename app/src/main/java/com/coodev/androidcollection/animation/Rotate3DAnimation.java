package com.coodev.androidcollection.animation;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * @author patrick.ding
 * @since 18/11/28
 */
public class Rotate3DAnimation extends Animation {

    private float mFromDegrees;
    private float mToDegrees;
    private float mCenterX;
    private float mCenterY;
    private float mDepthZ;
    private boolean mReverse;
    private Camera mCamera;
    float scale = 1;    // <------- 像素密度

    public Rotate3DAnimation(Context context, float fromDegrees, float toDegrees, float centerX, float centerY, float depthZ, boolean reverse) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mDepthZ = depthZ;
        mReverse = reverse;
        // 获取手机像素密度 （即dp与px的比例）
        scale = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        // 按百分比计算要转到的角度
        float degrees = fromDegrees + (mToDegrees - fromDegrees) * interpolatedTime;
        // 中心点
        final float centerX = mCenterX;
        final float centerY = mCenterY;
        // 相机
        final Camera camera = mCamera;
        final Matrix matrix = t.getMatrix();

        camera.save();

        if (mReverse) {
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
        } else {
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
        }

        camera.rotateY(degrees);// Y轴旋转
        camera.getMatrix(matrix);
        camera.restore();

        // 修正失真，主要修改 MPERSP_0 和 MPERSP_1
        float[] mValues = new float[9];
        matrix.getValues(mValues);                //获取数值
        mValues[6] = mValues[6] / scale;            //数值修正
        mValues[7] = mValues[7] / scale;            //数值修正
        matrix.setValues(mValues);                //重新赋值

        matrix.preTranslate(-centerX, -centerY);// 使用pre将旋转中心移动到和Camera位置相同。
        matrix.postTranslate(centerX, centerY);// 使用post将图片(View)移动到原来的位置


        super.applyTransformation(interpolatedTime, t);
    }
}
