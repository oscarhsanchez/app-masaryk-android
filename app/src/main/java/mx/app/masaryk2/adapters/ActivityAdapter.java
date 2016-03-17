
package mx.app.masaryk2.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

import mx.app.masaryk2.R;
import mx.app.masaryk2.utils.ActivitySQL;
import mx.app.masaryk2.utils.ActivitySchedule;
import mx.app.masaryk2.utils.Font;

public class ActivityAdapter extends BaseAdapter {

    JSONArray data;
    Activity activity;
    LayoutInflater inflater = null;


    public ActivityAdapter(LayoutInflater i, JSONArray d, Activity a) {
        inflater = i;
        data = d;
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

            vi = inflater.inflate(R.layout.adapter_activity, parent, false);

            ((TextView) vi.findViewById(R.id.txt_title)).setTypeface(Font.get(activity, "source-sans-semibold"));
            ((TextView) vi.findViewById(R.id.txt_description)).setTypeface(Font.get(activity, "source-sans-light"));
            ((TextView) vi.findViewById(R.id.txt_date)).setTypeface(Font.get(activity, "source-sans-regular"));
            ((TextView) vi.findViewById(R.id.txt_time)).setTypeface(Font.get(activity, "source-sans-regular"));
            ((Button) vi.findViewById(R.id.bt_schedule)).setTypeface(Font.get(activity, "source-sans-regular"));

            vi.findViewById(R.id.bt_schedule).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position    = Integer.parseInt(v.getTag().toString());
                    final Button bt = (Button)v;
                    ActivitySchedule.schedule(getItem(position), activity, new ActivitySchedule.ActivityScheduleListener() {
                        @Override
                        public void onScheduled(Boolean status) {
                            bt.setText( status ? "Eliminar" : "Agendar" );
                        }
                    });
                }
            });

        }

        int activity = 0, status = 0;
        String title = "", description = "", type = "", date_from = "", date_to = "";
        try {
            JSONObject item = data.getJSONObject(position);
            activity    = item.getInt("id");
            status      = item.getInt("status");
            title       = item.getString("title");
            description = item.getString("description");
            type        = item.getString("type");
            date_from   = item.getString("date_from");
            date_to     = item.getString("date_to");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Locale locale = new Locale("es-MX");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
        Date datef = new Date();
        Date datet = new Date();

        try {
            datef = format.parse(date_from);
            datet = format.parse(date_to);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        SimpleDateFormat formatd = new SimpleDateFormat("EE'.-' F 'de' MM", locale);
        SimpleDateFormat formatt = new SimpleDateFormat("HH:mm a", locale);


        ((TextView) vi.findViewById(R.id.txt_title)).setText(title);
        ((TextView) vi.findViewById(R.id.txt_description)).setText(description);
        ((TextView) vi.findViewById(R.id.txt_date)).setText(formatd.format(datef));
        ((TextView) vi.findViewById(R.id.txt_time)).setText(formatt.format(datef) + " - " + formatt.format(datet));
        ((ImageView) vi.findViewById(R.id.img_type)).setImageResource(type.equalsIgnoreCase("paga") ? R.drawable.icon_activity_pay : R.drawable.icon_activity_free);
        vi.findViewById(R.id.bt_schedule).setTag(position);

        if (ActivitySchedule.exists(activity)) {
            ((Button) vi.findViewById(R.id.bt_schedule)).setText("Eliminar");
        } else {
            ((Button) vi.findViewById(R.id.bt_schedule)).setText("Agendar");
        }

        if (status == 1) {
            ((TextView) vi.findViewById(R.id.txt_status)).setText("- Abierto -");
            vi.setAlpha(1);
        } else if (status == 2) {
            ((TextView) vi.findViewById(R.id.txt_status)).setText("- Pocos lugares -");
            vi.setAlpha(1);
        } else {
            ((TextView) vi.findViewById(R.id.txt_status)).setText("- Lleno -");
            vi.setAlpha(0.5f);
        }

        return vi;

    }

}
