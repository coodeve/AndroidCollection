package com.picovr.androidcollection.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author patrick.ding
 * @since 18/10/15
 */
public class FilePathView extends LinearLayout {

    private static final String TAG = FilePathView.class.getSimpleName();
    /**
     * 最大显示item数量
     */
    private int ITEM_MAX_COUNT = 5;

    private int padding = 10;

    private int childViewID;

    /**
     * 存储路径
     */
    private List<String> pathStack = new ArrayList<>();

    private LayoutInflater mLayoutInflater;

    private PathItemClickListener pathItemClickListener;

    public FilePathView(Context context) {
        this(context, null);
    }

    public FilePathView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilePathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.HORIZONTAL);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setAnimator(LayoutTransition.APPEARING, layoutTransition.getAnimator(LayoutTransition.APPEARING));
        layoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, layoutTransition.getAnimator(LayoutTransition.CHANGE_APPEARING));
        layoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, layoutTransition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING));
        layoutTransition.setAnimator(LayoutTransition.CHANGING, layoutTransition.getAnimator(LayoutTransition.CHANGING));
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, layoutTransition.getAnimator(LayoutTransition.DISAPPEARING));
        this.setLayoutTransition(layoutTransition);


    }

    /**
     * 设置item最大显示数量，默认为5个
     *
     * @param maxCount [description]
     */
    public void setMaxItemCount(int maxCount) {
        ITEM_MAX_COUNT = maxCount;
    }

    public void setChildViewID(int res) {
        this.childViewID = res;
    }

    public View getChildView() {
        View child = mLayoutInflater.inflate(childViewID, null);
        return child;
    }


    /**
     * 路径跳转
     *
     * @param filePath
     */
    public void nextPath(String filePath) {
        String path = pi(filePath);
        Log.i(TAG, "nextPath# " + path);
        int index = pathStack.indexOf(path);
        if (index <= -1) {
            pathStack.add(path);
        } else {
            refreshPathData(index);
        }
        nextItem(pathStack.size());
    }

    public void setPathItemClickListener(PathItemClickListener pathItemClickListener) {
        this.pathItemClickListener = pathItemClickListener;
    }

    /**
     * 将选中位置之后的所有数据删除
     *
     * @param index
     */
    private void refreshPathData(int index) {
        int size = pathStack.size();
        for (int i = 0; i < size - 1 - index; i++) {
            pathStack.remove(pathStack.size() - 1);
        }
    }

    private void nextItem(int pathListSize) {
        int childCount = getChildCount();
        if (pathListSize <= ITEM_MAX_COUNT) {
            if (childCount < pathListSize) {
                addView(createChild(pathStack.get(pathListSize - 1)).getView());
            } else if (childCount > pathListSize) {
                removeViews(pathListSize, childCount - pathListSize);
            }
        }
        refreshChildData();
    }

    /**
     * 根据当前数据集合刷新childView数据和状态
     */
    private void refreshChildData() {
        int size = pathStack.size();
        int childSize = getChildCount();
        int offPosition = size - ITEM_MAX_COUNT > 0 ? size - ITEM_MAX_COUNT : 0;
        for (int i = 0; i < childSize; i++) {
            PathStatus pathStatus = (PathStatus) getChildAt(i);
            pathStatus.setPath(pathStack.get(i == 0 ? i : offPosition + i));
            if (i == 1) {
                if (childSize >= ITEM_MAX_COUNT) {
                    pathStatus.showOmit();
                } else if (i == childSize - 1) {
                    pathStatus.showCurrent();
                } else {
                    pathStatus.showNormal();
                }
                continue;
            }
            if (i == childSize - 1) {
                pathStatus.showCurrent();
                continue;
            }
            pathStatus.showNormal();

        }
    }

    private PathStatus createChild(String path) {
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        PathStatus pathStatus = new FilePathButton(getContext());
        pathStatus.setPath(path);
        pathStatus.getView().setLayoutParams(layoutParams);
        pathStatus.getView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PathStatus arg = (PathStatus) v;
                if (!arg.getOmit()) {
                    String path = ((PathStatus) v).getPath();

                    nextPath(path);
                    if (pathItemClickListener != null) {
                        pathItemClickListener.onPathItemClickListener(path);
                    }
                }
            }
        });
        return pathStatus;
    }

    public void clearAll() {
        removeAllViews();
        pathStack.clear();
    }

    public interface PathStatus {
        void setPath(String path);

        String getPath();

        void showNormal();

        void showCurrent();

        void showOmit();

        boolean getOmit();

        View getView();
    }

    public interface PathItemClickListener {
        void onPathItemClickListener(String path);
    }

    private static final String PATTERN = "(/)(/.+)";

    private static String pi(String line) {
        String result = null;
        Pattern r = Pattern.compile(PATTERN);
        Matcher m = r.matcher(line);
        if (m.find()) {
            result = m.group(2);
        } else {
            result = line;
        }
        return result;
    }
}
