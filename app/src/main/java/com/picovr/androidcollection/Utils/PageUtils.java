package com.picovr.androidcollection.Utils;

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
}
