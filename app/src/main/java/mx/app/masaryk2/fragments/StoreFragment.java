package mx.app.masaryk2.fragments;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mx.app.masaryk2.R;
import mx.app.masaryk2.activities.StoreDetailActivity;
import mx.app.masaryk2.adapters.StoreAdapter;
import mx.app.masaryk2.utils.Font;
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
    Button btCancel;
    String searchText = "";
    RelativeLayout rlFilter;
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

        view      = (RelativeLayout)inflater.inflate(R.layout.fragment_store, container, false);
        txtTitle  = (TextView)view.findViewById(R.id.txt_title);
        listView  = (ListView)view.findViewById(R.id.list_view);
        txtSearch = (EditText)view.findViewById(R.id.txt_search);
        btCancel  = (Button)view.findViewById(R.id.bt_cancel);
        rlFilter  = (RelativeLayout)view.findViewById(R.id.rl_filter);

        listView.setOnItemClickListener(this);

        txtSearch.setTypeface(Font.get(getActivity(), "source-sans-regular"));
        btCancel.setTypeface(Font.get(getActivity(), "source-sans-semibold"));

        setTitle("Locales");

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
        if (result == ConnectionResult.SUCCESS) {
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view)).getMapAsync(this);
        } else {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result, 0).show();
            }
        }

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearch.setText(searchText);
                txtSearch.clearFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        view.findViewById(R.id.bt_ar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _ar();
            }
        });

        KeyboardVisibilityEvent.setEventListener(getActivity(), this);
        view.getViewTreeObserver().addOnGlobalLayoutListener(this);
        txtSearch.addTextChangedListener(this);
        txtSearch.setOnEditorActionListener(this);

        return view;

    }



    /*----------------*/
	/* CUSTOM METHODS */

    protected void _map() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        mapView.clear();

        for (int i=0; i<dataFilter.length(); i++) {

            try {
                JSONObject d = dataFilter.getJSONObject(i);

                String title        = d.getString("title");
                String address      = d.getString("address");
                LatLng latlng       = new LatLng(d.getDouble("lat"), d.getDouble("lng"));
                BitmapDescriptor b  = BitmapDescriptorFactory.fromResource(R.drawable.map_pin);
                MarkerOptions m     = new MarkerOptions().position(latlng).title(title).icon(b).snippet(address);

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
                StoreAdapter adapter = new StoreAdapter(getActivity().getLayoutInflater(), data, getActivity());
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
        JSONObject item = new JSONObject();
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

        float height = view.getHeight() - rlFilter.getHeight() - rlFilter.getY();
        float width  = btCancel.getWidth() + btCancel.getX();
        searchText   = txtSearch.getText().toString();

        ValueAnimator vals1 = ValueAnimator.ofFloat(listView.getHeight(), b ? height : (height * hValue));
        ValueAnimator vals2 = ValueAnimator.ofFloat(txtSearch.getWidth(), b ? btCancel.getX() - 20 : width);

        vals1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                listView.getLayoutParams().height = value.intValue();
                listView.requestLayout();
            }
        });

        vals2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                txtSearch.getLayoutParams().width = value.intValue();
                txtSearch.requestLayout();
            }
        });

        if (!b) {
            _map();
        }

        vals1.setDuration(250);
        vals2.setDuration(250);

        vals1.start();
        vals2.start();

    }



    /*---------------*/
	/* GLOBAL LAYOUT */

    @Override
    @SuppressWarnings("deprecation")
    public void onGlobalLayout() {

        float height = view.getHeight() - rlFilter.getHeight() - rlFilter.getY();
        SupportMapFragment map = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_view);
        View viewMap = map.getView();

        if (viewMap != null) {
            RelativeLayout.LayoutParams p1 = (RelativeLayout.LayoutParams) map.getView().getLayoutParams();
            RelativeLayout.LayoutParams p2 = (RelativeLayout.LayoutParams) listView.getLayoutParams();

            p1.height = Math.round(height * (1 - hValue));
            p2.height = Math.round(height * hValue);

            map.getView().setLayoutParams(p1);
            listView.setLayoutParams(p2);
        }

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
                if (title.contains(s.toString())) {
                    dataFilter.put(data.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        StoreAdapter adapter = new StoreAdapter(getActivity().getLayoutInflater(), dataFilter, getActivity());
        listView.setAdapter(adapter);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == KeyEvent.KEYCODE_ENTER) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            handled = true;
        }
        return handled;
    }
}

