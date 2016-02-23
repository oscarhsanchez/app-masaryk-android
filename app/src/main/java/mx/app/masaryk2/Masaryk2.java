package mx.app.masaryk2;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.innoquant.moca.MOCA;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by noisedan on 2/10/16.
 */
public class Masaryk2 extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(getBaseContext());
        MOCA.initializeSDK(this);
        Fabric.with(this, new Crashlytics());
    }

}
