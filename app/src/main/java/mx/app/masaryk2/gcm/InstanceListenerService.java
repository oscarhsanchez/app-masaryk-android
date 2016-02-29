package mx.app.masaryk2.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class InstanceListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify of changes
        Intent intent = new Intent(this, IntentService.class);
        startService(intent);
    }

}
