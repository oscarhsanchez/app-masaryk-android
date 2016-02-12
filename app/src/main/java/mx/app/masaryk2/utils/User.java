package mx.app.masaryk2.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by noisedan on 7/14/15.
 */
public class User {

    static public void set(String key, String value, Activity a) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(a).edit();
        e.putString("profile_" + key, value);
        e.commit();
    }

    static public String get(String key, Activity a) {

        Map<String,?> data = PreferenceManager.getDefaultSharedPreferences(a).getAll();
        Object result = data.get("profile_" + key);
        return result != null ? result.toString() : "";

    }

    static public void clear(Activity a) {

        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(a).edit();
        Map<String,?> data = PreferenceManager.getDefaultSharedPreferences(a).getAll();

        for (Map.Entry<String,?> entry : data.entrySet()) {
            e.remove(entry.getKey());
        }

        e.commit();
    }

    static public void logged(Boolean value, Activity a) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(a).edit();
        e.putString("profile_logged", value ? "true" : "false");
        e.commit();
    }

    static public boolean logged(Activity a) {
        Map<String,?> data = PreferenceManager.getDefaultSharedPreferences(a).getAll();
        String isLogged = (String)data.get("profile_logged");
        return isLogged == null ? false : isLogged.equals("true");
    }

    static public String getToken(Activity a) {
        Map<String,?> data = PreferenceManager.getDefaultSharedPreferences(a).getAll();
        String strToken = (String)data.get("token");
        return strToken;
    }

    static public void mode(int value, Activity a) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(a).edit();
        e.putString("profile_mode", Integer.toString(value));
        e.commit();
    }

    static public int mode(Activity a) {
        Map<String,?> data = PreferenceManager.getDefaultSharedPreferences(a).getAll();
        String mode = (String)data.get("profile_mode");
        return mode == null ? 0 : Integer.parseInt(mode);
    }

}
