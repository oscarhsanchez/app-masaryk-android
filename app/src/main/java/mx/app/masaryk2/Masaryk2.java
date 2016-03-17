package mx.app.masaryk2;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.innoquant.moca.MOCA;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import mx.app.masaryk2.utils.ActivitySQL;

public class Masaryk2 extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "ZdM23ytTNz9n0Ow9nHwlcmW7o";
    private static final String TWITTER_SECRET = "ktRRcL3k1guEg6EnGmPIadxaq5YY1sKLRUG4wauxVpYXtcAr8m";


    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(getBaseContext());
        MOCA.initializeSDK(this);

        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        //, new Twitter(authConfig), new TweetComposer()
        Fabric.with(this, new Crashlytics());

        ActivitySQL.init(this);
    }

}
