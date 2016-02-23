
package mx.app.masaryk2.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mx.app.masaryk2.R;
import mx.app.masaryk2.utils.Font;

public class StoreAdapter extends BaseAdapter {

    JSONArray data;
    Activity activity;
    LayoutInflater inflater = null;

    public StoreAdapter(LayoutInflater i, JSONArray d, Activity a) {
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
            vi = inflater.inflate(R.layout.adapter_store, parent, false);
            ((TextView) vi.findViewById(R.id.txt_title)).setTypeface(Font.get(activity, "source-sans-regular"));
            ((TextView) vi.findViewById(R.id.txt_address)).setTypeface(Font.get(activity, "source-sans-light"));
        }

        String title = "", address = "", url = "";
        try {
            JSONObject item  = data.getJSONObject(position);
            JSONObject thumb = item.getJSONObject("thumb");
            title   = item.getString("title");
            address = item.getString("address");
            url     = thumb.getString("src");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((TextView) vi.findViewById(R.id.txt_title)).setText(title);
        ((TextView) vi.findViewById(R.id.txt_address)).setText(address);

        Picasso.with(activity).load(url).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into((ImageView)vi.findViewById(R.id.img_store));

        return vi;

    }
}
