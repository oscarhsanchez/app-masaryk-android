package mx.app.masaryk2.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mx.app.masaryk2.R;
import mx.app.masaryk2.activities.ActivityDetailActivity;
import mx.app.masaryk2.adapters.ActivityAdapter;
import mx.app.masaryk2.utils.ActivitySchedule;
import mx.app.masaryk2.utils.WebBridge;



public class ActivityFragment extends SectionFragment implements WebBridge.WebBridgeListener, AdapterView.OnItemClickListener {


	/*------------*/
	/* PROPERTIES */

    JSONArray data;
    ListView listView;

    public ActivityFragment() {
        // Required empty public constructor
    }

    public static ActivityFragment newInstance(SectionFragmentListener listener) {
        ActivityFragment f = new ActivityFragment();
        f.listener = listener;
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view     = (RelativeLayout)inflater.inflate(R.layout.fragment_activity, container, false);
        txtTitle = (TextView)view.findViewById(R.id.txt_title);
        listView = (ListView)view.findViewById(R.id.list_view);

        listView.setOnItemClickListener(this);

        view.findViewById(R.id.bt_ar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _ar();
            }
        });

        _ar();

        setTitle("Actividades");
        WebBridge.send("activities", "Descargando", getActivity(), this);

        return view;

    }



    /*--------------------*/
	/* WEBBRIDGE LISTENER */

    @Override
    public void onWebBridgeSuccess(String url, JSONObject json) {

        boolean success = false;
        try {
            success = json.getBoolean("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (success) {

            data = new JSONArray();
            try {
                data = json.getJSONArray("data");
                ActivityAdapter adapter = new ActivityAdapter(getActivity().getLayoutInflater(), data, getActivity());
                listView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        
    }

    @Override
    public void onWebBridgeFailure(String url, String response) {

    }



    /*---------------------*/
	/* ITEM CLICK LISTENER */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JSONObject item = new JSONObject();
        try {
            item = data.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getActivity(), ActivityDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("data", item.toString());
        startActivityForResult(intent, 5);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent d) {
        if (requestCode == 5) {
            if (data != null) {
                for (int i=0; i<listView.getChildCount(); i++) {
                    int pos   = i - listView.getFirstVisiblePosition();
                    View v    = listView.getChildAt(pos);
                    Button bt = (Button)v.findViewById(R.id.bt_schedule);
                    try {
                        bt.setText( ActivitySchedule.exists(data.getJSONObject(pos).getInt("id")) ? "Eliminar" : "Agendar" );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}

