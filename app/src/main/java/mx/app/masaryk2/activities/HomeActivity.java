package mx.app.masaryk2.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import mx.app.masaryk2.R;
import mx.app.masaryk2.fragments.ActivityFragment;
import mx.app.masaryk2.fragments.ProfileFragment;
import mx.app.masaryk2.fragments.PromoFragment;
import mx.app.masaryk2.fragments.SectionFragment;
import mx.app.masaryk2.fragments.StoreFragment;
import mx.app.masaryk2.utils.Font;
import mx.app.masaryk2.utils.User;
import mx.app.masaryk2.utils.WebBridge;


public class HomeActivity extends FragmentActivity implements SectionFragment.SectionFragmentListener, WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    Button[] tabs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        overridePendingTransition(R.anim.fade_in, R.anim.static_motion);

        tabs = new Button[4];
        tabs[0] = (Button)findViewById(R.id.bt_tab_bottom_1);
        tabs[1] = (Button)findViewById(R.id.bt_tab_bottom_2);
        tabs[2] = (Button)findViewById(R.id.bt_tab_bottom_3);
        tabs[3] = (Button)findViewById(R.id.bt_tab_bottom_4);

        for (int i=0; i<tabs.length; i++) {
            tabs[i].setTypeface(Font.get(this, "source-sans-regular"));
        }

        WebBridge.send("verify", "Validando", this, this);

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickTab(View v) {
        int position = Integer.parseInt(v.getTag().toString());
        update(position);
    }



    /*------------------*/
	/* CUSTOM FUNCTIONS */

    public void update(int index) {

        for (int i=0; i<tabs.length; i++) {
            if (i == index) {
                int img = getResources().getIdentifier("bt_menu_bottom_" + (i+1) +"_on", "drawable", getPackageName());
                tabs[i].setTextColor(Color.parseColor("#5b5b5f"));
                tabs[i].setCompoundDrawablesWithIntrinsicBounds(0, img, 0, 0);
                tabs[i].setBackgroundColor(Color.parseColor("#edeef0"));
            } else {
                int img = getResources().getIdentifier("bt_menu_bottom_" + (i+1) +"_off", "drawable", getPackageName());
                tabs[i].setTextColor(Color.parseColor("#FFFFFF"));
                tabs[i].setCompoundDrawablesWithIntrinsicBounds(0, img, 0, 0);
                tabs[i].setBackgroundColor(Color.parseColor("#cd506c"));
            }
        }

        FragmentManager manager = getSupportFragmentManager();
        SectionFragment section;
        if (index == 0) {
            section = PromoFragment.newInstance(HomeActivity.this);
        } else if (index == 1) {
            section = StoreFragment.newInstance(HomeActivity.this);
        } else if (index == 2) {
            section = ActivityFragment.newInstance(HomeActivity.this);
        } else {
            section = ProfileFragment.newInstance(HomeActivity.this);
        }

        FragmentTransaction transition = manager.beginTransaction();
        //transition.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transition.replace(R.id.fl_tabs, section).commit();

    }



    /*-------------------*/
	/* FRAGMENT LISTENER */

    @Override
    public void onPushActivity(SectionFragment fragment, Intent intent) {
        startActivityForResult(intent, 1);
    }



    /*--------------------*/
	/* WEBBRIDGE LISTENER */

    @Override
    public void onWebBridgeSuccess(String url, JSONObject json) {

        boolean success = false;
        String service  = "";

        try {
            success = json.getBoolean("success");
            service = json.getString("service");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!success) {

            if (service.equals("verify")) {

                HashMap<String, Object> params = new HashMap<>();
                params.put("renew_token", User.getToken(this));
                WebBridge.send("renew", params, "Enviando", this, this);

            } else if (service.equals("renew")) {

                User.set("renew_token", "", this);
                User.logged(false, this);
                setResult(Activity.RESULT_OK);
                finish();
                overridePendingTransition(R.anim.static_motion, R.anim.fade_out);

            }

        } else {
            update(1);
        }
    }

    @Override
    public void onWebBridgeFailure(String url, String response) {

    }
}
