package com.coodev.androidcollection.Utils.shell;

import android.os.Process;

/**
 * @author patrick.ding
 * @since 20/2/12
 */
public class ShellCmd {
    /**
     * 获取activity栈
     */
    public static final String COMMAND = "dumpsys activity -a | sed -n -e \"/Display #/p\" -e   \"/Stack #/p\" -e \"/Running activities/,/Run #/p\"";

    /**
     * 获取系统剩余内存
     */
    public static final String DUMP_SYSTEM_MEMINFO = "cat /proc/meminfo";

    /**
     * 获取pid
     */
    public static final String PROC_SELF = "ps -e | grep 包名";

    /**
     * 应用使用内存
     */
    public static final String DUMP_APP_MEMINFO = " cat /proc/" + Process.myPid() + "/status";
}
