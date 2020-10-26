package com.coodev.androidcollection.Utils.ui;

/**
 * @author patrick.ding
 * @since 19/2/13
 */
public class PageUtils {
    /**
     * @param size  当前数据集大小
     * @param limit 每页数据的大小
     * @return 需要请求第几页
     */
    public static int calculatePageIndex(int size, int limit) {
        int pageIndex = 1;
        int flag = size % limit;
        if (flag != 0) {
            pageIndex = (int) Math.ceil((double) size / (double) limit);
        } else {
            pageIndex = size / limit + 1;
        }
        return pageIndex;
    }

    /**
     * 判断是否是最后一行
     * 用于GridLayout类似的布局进行判断
     *
     * @param currentItemPosition 当前item位置
     * @param totalItems          总共items
     * @param spanCount           分为几列
     * @return
     */
    private static boolean isLastRow(int currentItemPosition, int totalItems, int spanCount) {
        boolean result = false;
        int rowCount = 0;

        if (0 == totalItems % spanCount) {
            rowCount = totalItems / spanCount;
        } else {
            rowCount = totalItems / spanCount + 1;
        }
        if ((currentItemPosition + 1) > (rowCount - 1) * spanCount)
            result = true;

        return result;
    }

}
