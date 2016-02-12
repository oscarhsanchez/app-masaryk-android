package mx.app.masaryk2.utils;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mx.app.masaryk2.R;
import mx.app.masaryk2.dialogs.ProgressDialog;


public class WebBridge {

    private ProgressDialog progress;
    private WebBridgeListener callback;
    private AsyncHttpClient client;
    private String url;


    static public ArrayList<WebBridge> instances = new ArrayList<WebBridge>();

    static public String url(String url, Activity a) {
        if (url.indexOf("http://") == 0) return url;

        int mode = User.mode(a);
        String u = "http://masaryk.avanna.tech/api/" + url;

        Log.e("REQUEST URL", u);
        return u;
    }


    static private void addVersion(Map<String,Object> params, Activity activity) {
        String app_version = "N/A";
        try {
            app_version = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (Exception e) {}

        params.put("app_version", app_version);
        params.put("app_os", "android");
    }


    static public WebBridge send(String url, HashMap<String,Object> params, String message, Activity activity, WebBridgeListener callback) {
        WebBridge wb = WebBridge.getInstance(activity, message, callback);
        if (wb != null) {
            wb.send(WebBridge.url(url, activity), params);
        }
        return wb;
    }


    static public WebBridge send(String url, HashMap<String,Object> params,  Activity activity, WebBridgeListener callback) {
        WebBridge wb = WebBridge.getInstance(activity, null, callback);
        if (wb != null) {
            wb.send(WebBridge.url(url, activity), params);
        }
        return wb;
    }


    static public WebBridge send(String url, String message, Activity activity, WebBridgeListener callback) {
        WebBridge wb = WebBridge.getInstance(activity, message, callback);
        if (wb != null) {
            wb.send(WebBridge.url(url, activity), null);
        }
        return wb;
    }


    static public WebBridge send(String url, HashMap<String,Object> params, Activity activity) {
        WebBridge wb = WebBridge.getInstance(activity, null, null);
        if (wb != null) {
            wb.send(WebBridge.url(url, activity), params);
        }
        return wb;
    }

    static WebBridge getInstance(Activity activity, String message, WebBridgeListener callback) {

        if (WebBridge.haveNetworkConnection(activity) == false) {
            Toast.makeText(activity, activity.getResources().getString(R.string.error_connectivity), Toast.LENGTH_LONG).show();
            return null;
        }

        WebBridge wb = new WebBridge();
        wb.callback  = callback;
        wb.client    = new AsyncHttpClient();
        wb.client.setUserAgent("GIRORM Android Client 1.0");
        wb.client.setTimeout(60000);


        PersistentCookieStore cookie = new PersistentCookieStore(activity);
        wb.client.setCookieStore(cookie);


        if (message != null) wb.progress = ProgressDialog.show(activity, message, true, false, null);


        WebBridge.instances.add(wb);
        return wb;

    }


    protected void send(String url, HashMap<String,Object> p) {

        this.url = url;

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = null;
                try {
                    response = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                success(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = "";
                try {
                    response = new String(responseBody, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("Status", "" + statusCode);
                Log.e("Throw", error.toString());
                Log.e("Error", response);
                //Log.e("Header", headers.toString());
                failure(response);
            }
        };

        if (p != null) {
            RequestParams params = new RequestParams();

            for (Map.Entry<String,Object> entry : p.entrySet()) {
                String k = entry.getKey();
                Object v = entry.getValue();
                if (v instanceof File) {
                    File f = (File)v;
                    try {
                        params.put(k, f, "image/jpeg");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    params.put(k, v);
                }
            }

            Log.e("PARAMS:", p.toString());
            client.post(url, params, handler);
        } else {
            client.get(url, handler);
        }

    }


    public void failure (String response) {
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
        if (callback != null) {
            callback.onWebBridgeFailure(url, response);
        }
    }


    public void success (String response) {

        Log.e("RESPONSE", response.toString());

        if (progress != null) {
            progress.dismiss();
            progress = null;
        }

        if (callback != null) {
            JSONObject json = null;
            try {
                json = new JSONObject(response);
            } catch (Exception e) {}
            callback.onWebBridgeSuccess(url, json);
        }

        WebBridge.instances.remove(this);

    }


    static public boolean haveNetworkConnection(Activity a) {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) {
                    haveConnectedWifi = true;
                }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) {
                    haveConnectedMobile = true;
                }
        }

        return haveConnectedWifi || haveConnectedMobile;
    }


    public interface WebBridgeListener {
        void onWebBridgeSuccess(String url, JSONObject json);
        void onWebBridgeFailure(String url, String response);
    }

}
