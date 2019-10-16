package th.co.apps360.eat360;

import android.support.multidex.MultiDexApplication;

//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;

/**
 * Created by dan on 2/17/17.
 */

public class Eat360Application extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
    }

}
