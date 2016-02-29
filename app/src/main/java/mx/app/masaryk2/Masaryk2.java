package mx.app.masaryk2;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.innoquant.moca.MOCA;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import mx.app.masaryk2.utils.ActivitySQL;

public class Masaryk2 extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(getBaseContext());
        MOCA.initializeSDK(this);
        Fabric.with(this, new Crashlytics());
        ActivitySQL.init(this);
    }

}
