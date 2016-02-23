package mx.app.masaryk2.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

public class User {

    static public void set(String key, String value, Activity a) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(a).edit();
        e.putString("profile_" + key, value);
        e.apply();
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

        e.apply();
    }

    static public void logged(Boolean value, Activity a) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(a).edit();
        e.putString("profile_logged", value ? "true" : "false");
        e.apply();
    }

    static public boolean logged(Activity a) {
        Map<String,?> data = PreferenceManager.getDefaultSharedPreferences(a).getAll();
        String isLogged = (String)data.get("profile_logged");
        return isLogged != null && isLogged.equals("true");
    }

    static public String getToken(Activity a) {
        Map<String,?> data = PreferenceManager.getDefaultSharedPreferences(a).getAll();
        return (String)data.get("token");
    }

    static public int mode(Activity a) {
        Map<String,?> data = PreferenceManager.getDefaultSharedPreferences(a).getAll();
        String mode = (String)data.get("profile_mode");
        return mode == null ? 0 : Integer.parseInt(mode);
    }

}
