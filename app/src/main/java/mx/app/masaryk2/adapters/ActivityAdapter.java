
package mx.app.masaryk2.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mx.app.masaryk2.R;

/**
 * Created by noisedan on 6/27/15.
 */
public class ActivityAdapter extends BaseAdapter {

    JSONArray data;
    Activity activity;
    LayoutInflater inflater = null;

    public ActivityAdapter(LayoutInflater i, JSONArray d, Activity a) {
        inflater = i;
        data     = d;
        activity = a;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length();
    }

    @Override
    public JSONObject getItem(int position) {
        try {
            return data.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.adapter_activity, null);
        }

        String title = "", description = "", type = "";
        try {
            JSONObject item = item = data.getJSONObject(position);
            title        = item.getString("title");
            description  = item.getString("description");
            type         = item.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((TextView) vi.findViewById(R.id.txt_title)).setText(title);
        ((TextView) vi.findViewById(R.id.txt_description)).setText(description);
        ((TextView) vi.findViewById(R.id.txt_type)).setText(type);

        return vi;

    }
}
