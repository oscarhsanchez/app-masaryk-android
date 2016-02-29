package mx.app.masaryk2.gcm;

import mx.app.masaryk2.R;
import mx.app.masaryk2.utils.WebBridge;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import android.content.Intent;

import java.io.IOException;
import java.util.HashMap;

public class IntentService extends android.app.IntentService {

	private static final String TAG = "Masaryk2";

	public IntentService() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		InstanceID instanceID = InstanceID.getInstance(this);
		String senderId = getResources().getString(R.string.gcm_sender_id);

		try {

			String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
			HashMap<String, Object> params = new HashMap<>();
			params.put("token",  token);
			params.put("device", "Android");
			WebBridge.send("notification", params, this);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}