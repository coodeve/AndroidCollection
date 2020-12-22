import android.app.Application;

import com.coodev.app_apt.annotation.ViewInjector;
import com.coodev.app_apt_runtime.ViewInjectInit;

public class RuntimeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ViewInjectInit.init(null);
    }
}
