package com.coodev.androidcollection.Utils.ipc;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.facade.service.DegradeService;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.launcher.ARouter;
import com.coodev.androidcollection.BuildConfig;

import java.util.Map;

/**
 * {@link ARouter}使用
 * 目前测试使用
 * {@link com.coodev.androidcollection.test.butterknife.ButterKnifeActivity}
 * {@link com.coodev.androidcollection.test.butterknife.ViewInjectorActivity}
 */

public class ARouterUtils {

    public static void init(Application application) {
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(application);
    }

    /**
     * 简单跳转
     * 测试
     * url配置:
     * <activity android:name=".activity.SchemeFilterActivity">
     * <!-- Scheme -->
     * <intent-filter>
     * <data
     * android:host="m.aliyun.com"
     * android:scheme="arouter"/>
     * <p>
     * <action android:name="android.intent.action.VIEW"/>
     * <p>
     * <category android:name="android.intent.category.DEFAULT"/>
     * <category android:name="android.intent.category.BROWSABLE"/>
     * </intent-filter>
     * </activity>
     *
     * @param route 可以是route参数,也可以是url(需要在AndroidManifest.xml中配置)
     */
    public static void skip(String route) {
        ARouter.getInstance().build(route).navigation();
    }

    /**
     * 添加监听
     *
     * @param route
     * @param context
     * @param callback
     */
    public static void skip(String route, Context context, NavigationCallback callback) {
        ARouter.getInstance().build(route).navigation(context, callback);
    }

    /**
     * 携带参数跳
     * 测试
     *
     * @param route
     * @param params
     */
    public static void skip(String route, Map<String, String> params) {
        ARouter.getInstance().build(route)
                .withString(null, null)
                .navigation();
    }

    // 比较经典的应用就是在跳转过程中处理登陆事件，这样就不需要在目标页重复做登陆检查
    // 拦截器会在跳转之间执行，多个拦截器会按优先级顺序依次执行
    @Interceptor(priority = 8, name = "测试用拦截器")
    public class TestInterceptor implements IInterceptor {
        @Override
        public void process(Postcard postcard, InterceptorCallback callback) {
            callback.onContinue(postcard);  // 处理完成，交还控制权
            // callback.onInterrupt(new RuntimeException("我觉得有点异常"));      // 觉得有问题，中断路由流程

            // 以上两种至少需要调用其中一种，否则不会继续路由
        }

        @Override
        public void init(Context context) {
            // 拦截器的初始化，会在sdk初始化的时候调用该方法，仅会调用一次
        }
    }

    // 降级策略
    // 实现DegradeService接口，并加上一个Path内容任意的注解即可
    @Route(path = "/xxx/xxx")
    public class DegradeServiceImpl implements DegradeService {
        @Override
        public void onLost(Context context, Postcard postcard) {
            // do something.
        }

        @Override
        public void init(Context context) {

        }
    }

}
