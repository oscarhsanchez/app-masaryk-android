package mx.app.masaryk2.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mx.app.masaryk2.R;
import mx.app.masaryk2.activities.PromoDetailActivity;
import mx.app.masaryk2.adapters.PromoAdapter;
import mx.app.masaryk2.utils.FragmentInflatorFactory;
import mx.app.masaryk2.utils.WebBridge;


public class PromoFragment extends SectionFragment implements WebBridge.WebBridgeListener, PromoAdapter.OnItemClickListener {


	/*------------*/
	/* PROPERTIES */

    StaggeredGridLayoutManager layout;
    RecyclerView recyclerView;

    public PromoFragment() {
        // Required empty public constructor
    }

    public static PromoFragment newInstance(SectionFragmentListener listener) {
        PromoFragment f = new PromoFragment();
        f.listener = listener;
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view     = (RelativeLayout)FragmentInflatorFactory.inflatorFor(inflater, this).inflate(R.layout.fragment_promo, container, false);
        txtTitle = (TextView)view.findViewById(R.id.txt_title);
        layout   = new StaggeredGridLayoutManager(2, 1);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layout);

        setTitle("Promociones");
        WebBridge.send("promos", "Descargando", getActivity(), this);

        return view;

    }



    /*-------------------------*/
	/* RECLICLER VIEW LISTENER */

    @Override
    public void onItemClick(int position, JSONObject data) {
        Intent intent = new Intent(getActivity(), PromoDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("data", data.toString());
        startActivity(intent);
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

            JSONArray data = new JSONArray();
            try {
                data = json.getJSONArray("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            PromoAdapter rcAdapter = new PromoAdapter(getActivity(), data, this);
            recyclerView.setAdapter(rcAdapter);
        }

    }

    @Override
    public void onWebBridgeFailure(String url, String response) {

    }


}

