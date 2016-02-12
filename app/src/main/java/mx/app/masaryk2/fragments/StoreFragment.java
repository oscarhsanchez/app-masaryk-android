package mx.app.masaryk2.fragments;


import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mx.app.masaryk2.R;
import mx.app.masaryk2.activities.ActivityDetailActivity;
import mx.app.masaryk2.activities.StoreDetailActivity;
import mx.app.masaryk2.adapters.StoreAdatpter;
import mx.app.masaryk2.utils.FragmentInflatorFactory;
import mx.app.masaryk2.utils.WebBridge;



public class StoreFragment extends SectionFragment implements WebBridge.WebBridgeListener, OnMapReadyCallback, AdapterView.OnItemClickListener, KeyboardVisibilityEventListener, ViewTreeObserver.OnGlobalLayoutListener, TextWatcher, TextView.OnEditorActionListener {


	/*------------*/
	/* PROPERTIES */

    ListView listView;
    GoogleMap mapView;
    LatLngBounds bounds;
    JSONArray data;
    JSONArray dataFilter;
    EditText txtSearch;
    float hValue = 0.35f;

    protected Handler handler = new Handler();
    protected final Runnable runnable = new Runnable() {
        public void run() {
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
            mapView.moveCamera(cu);
            mapView.animateCamera(cu);
        }
    };


    public StoreFragment() {
        // Required empty public constructor
    }

    public static StoreFragment newInstance(SectionFragmentListener listener) {
        StoreFragment f = new StoreFragment();
        f.listener = listener;
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view      = (RelativeLayout)FragmentInflatorFactory.inflatorFor(inflater, this).inflate(R.layout.fragment_store, container, false);
        txtTitle  = (TextView)view.findViewById(R.id.txt_title);
        listView  = (ListView)view.findViewById(R.id.list_view);
        txtSearch = (EditText)view.findViewById(R.id.txt_search);

        listView.setOnItemClickListener(this);

        setTitle("Locales");

        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (code == ConnectionResult.SUCCESS) {

            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view)).getMapAsync(this);

        } else if (code == ConnectionResult.SERVICE_MISSING || code == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED || code == ConnectionResult.SERVICE_DISABLED) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(code, getActivity(), 1);
            dialog.show();
        }

        KeyboardVisibilityEvent.setEventListener(getActivity(), this);
        view.getViewTreeObserver().addOnGlobalLayoutListener(this);
        txtSearch.addTextChangedListener(this);
        txtSearch.setOnEditorActionListener(this);

        return view;

    }

    protected void _map() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        mapView.clear();

        for (int i=0; i<dataFilter.length(); i++) {

            try {
                JSONObject d = dataFilter.getJSONObject(i);

                String title        = d.getString("title");
                String address      = d.getString("address");
                LatLng latlng       = new LatLng(d.getDouble("lat"), d.getDouble("lng"));
                //BitmapDescriptor b  = BitmapDescriptorFactory.fromResource(R.mipmap.pin); .icon(b)
                MarkerOptions m     = new MarkerOptions().position(latlng).title(title).snippet(address);

                mapView.addMarker(m);
                builder.include(latlng);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (dataFilter.length() > 0) {
            bounds = builder.build();
            handler.postDelayed(runnable, 1000);
        }

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
                dataFilter = data;
                StoreAdatpter adapter = new StoreAdatpter(getActivity().getLayoutInflater(), data, getActivity());
                listView.setAdapter(adapter);
                _map();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onWebBridgeFailure(String url, String response) {

    }



    /*---------------*/
	/* MAPS LISTENER */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapView = googleMap;
        WebBridge.send("stores", "Descargando", getActivity(), this);
    }



    /*--------------------*/
	/* LIST VIEW LISTENER */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JSONObject item = null;
        try {
            item = data.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("data", item.toString());
        startActivity(intent);
    }



    /*-------------------*/
	/* KEYBOARD LISTENER */

    @Override
    public void onVisibilityChanged(boolean b) {

        float height = view.getHeight() - txtSearch.getHeight() - txtSearch.getY();
        ValueAnimator vals = ValueAnimator.ofFloat(listView.getHeight(), b ? height: (height * hValue));

        vals.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                listView.getLayoutParams().height = value.intValue();
                listView.requestLayout();
            }
        });

        if (!b) {
            _map();
        }

        vals.setDuration(250);
        vals.start();

    }



    /*---------------*/
	/* GLOBAL LAYOUT */

    @Override
    public void onGlobalLayout() {

        float height = view.getHeight() - txtSearch.getHeight() - txtSearch.getY();
        SupportMapFragment map = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_view);

        RelativeLayout.LayoutParams p1 = (RelativeLayout.LayoutParams)map.getView().getLayoutParams();
        RelativeLayout.LayoutParams p2 = (RelativeLayout.LayoutParams)listView.getLayoutParams();

        p1.height = (int)Math.round(height * (1-hValue));
        p2.height = (int)Math.round(height * hValue);

        map.getView().setLayoutParams(p1);
        listView.setLayoutParams(p2);

        if (Build.VERSION.SDK_INT<16) {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        } else {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }

    }



    /*--------------------*/
	/* EDIT TEXT LISTENER */

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        dataFilter = new JSONArray();
        for (int i=0; i<data.length(); i++) {
            try {
                String title = data.getJSONObject(i).getString("title").toLowerCase();
                if (title.contains(s.toString().toString())) {
                    dataFilter.put(data.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        StoreAdatpter adapter = new StoreAdatpter(getActivity().getLayoutInflater(), dataFilter, getActivity());
        listView.setAdapter(adapter);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        Log.e("KEYCODE_ENTER", "" + KeyEvent.KEYCODE_ENTER);
        Log.e("actionId", "" + actionId);
        if (actionId == KeyEvent.KEYCODE_ENTER) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            handled = true;
        }
        return handled;
    }
}

