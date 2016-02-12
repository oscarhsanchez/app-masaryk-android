package mx.app.masaryk2.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Locale;

import mx.app.masaryk2.R;


/**
 * Created by noisedan on 9/29/15.
 */
public class ActivityDetailActivity extends SectionActivity implements OnMapReadyCallback {


	/*------------*/
	/* PROPERTIES */

    TextView txtTitleActivity;
    TextView txtDescription;
    TextView txtAddress;
    ImageView imgActivity;

    JSONObject data;
    GoogleMap mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_detail);

        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);

        txtTitleActivity = (TextView)findViewById(R.id.txt_title_activity);
        txtDescription   = (TextView)findViewById(R.id.txt_description);
        txtAddress       = (TextView)findViewById(R.id.txt_address);
        imgActivity      = (ImageView)findViewById(R.id.img_activity);

        if (getIntent().hasExtra("data")) {
            try {

                data = new JSONObject( getIntent().getStringExtra("data") );

                int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                if (code == ConnectionResult.SUCCESS) {


                    ((MapFragment) getFragmentManager().findFragmentById(R.id.map_view)).getMapAsync(this);

                } else if (code == ConnectionResult.SERVICE_MISSING || code == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED || code == ConnectionResult.SERVICE_DISABLED) {
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(code, this, 1);
                    dialog.show();
                }

                txtTitleActivity.setText( data.getString("title") );
                txtDescription.setText( data.getString("description") );
                txtAddress.setText( data.getString("address") );

                String url = data.getString("thumb");

                Log.e("URL", url);

                Picasso.with(this).load(url).into(imgActivity);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickWaze(View v) {
        try {
            String url = "waze://?q=" + data.getDouble("lat") + "," + data.getDouble("lng");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity( intent );
        } catch ( Exception exception  ) {
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
            startActivity(intent);
        }
    }

    public void clickMaps(View v) {

        try {
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", data.getString("lat"), data.getString("lng"), data.getString("title"));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        } catch ( Exception exception  ) {

        }

    }



    /*---------------*/
	/* MAPS LISTENER */

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapView = googleMap;

        try {
            double lat   = data.getDouble("lat");
            double lng   = data.getDouble("lng");
            String title = data.getString("title");
            LatLng point = new LatLng(lat, lng);

            MarkerOptions o = new MarkerOptions().position(point).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
            mapView.addMarker(o);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
