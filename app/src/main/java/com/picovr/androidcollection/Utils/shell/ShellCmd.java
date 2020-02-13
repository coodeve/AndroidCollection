package com.picovr.androidcollection.Utils.shell;

/**
 * @author patrick.ding
 * @since 20/2/12
 */
public class ShellCmd {
    /**
     * 获取activity栈
     */
    public static final String COMMAND = "dumpsys activity -a | sed -n -e \"/Display #/p\" -e   \"/Stack #/p\" -e \"/Running activities/,/Run #/p\"";


}
