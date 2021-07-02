package com.coodev.androidcollection.Utils.log;

import android.content.Context;

import com.coodev.androidcollection.BuildConfig;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.DefaultFlattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.ConsolePrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * 使用一个第三方库 com.elvishew:xlog
 */
public class XLogUtils {

    /**
     * 日志过期时间：一周
     */
    private static final long LOG_VALID_TIME = 1000 * 60 * 60 * 24 * 7;

    /**
     * 初始化
     *
     * @param context context
     */
    public static void init(Context context) {

        String logDir = context.getExternalFilesDir(null).getAbsolutePath() + "/xlog/";
        // check
        checkLogLRU(logDir);
        // init
        LogConfiguration logConfig = new LogConfiguration.Builder()
                .logLevel(com.elvishew.xlog.LogLevel.ALL)
                .tag("assistanthmd")
                .build();
        Printer androidPrinter = new AndroidPrinter();
        Printer SystemPrinter = new ConsolePrinter();
        Printer filePrinter = new FilePrinter
                .Builder(logDir)
                .fileNameGenerator(new DateFileNameGenerator())
                .backupStrategy(new FileSizeBackupStrategy(1024 * 1024))
                .logFlattener(new DefaultFlattener())
                .build();

        if (BuildConfig.DEBUG) {
            XLog.init(logConfig, androidPrinter);
        } else {
            XLog.init(logConfig, filePrinter);
        }
    }

    /**
     * 检查缓存的日志文件
     *
     * @param logDir 日志文件地址
     */
    private static void checkLogLRU(String logDir) {
        File logDirFile = new File(logDir);
        if (!logDirFile.exists() || !logDirFile.isDirectory()) {
            return;
        }
        ArrayList<String> logFilePath = new ArrayList<>();
        File[] files = logDirFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return System.currentTimeMillis() - pathname.lastModified() > LOG_VALID_TIME;
            }
        });

        if (files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }
    }
}
