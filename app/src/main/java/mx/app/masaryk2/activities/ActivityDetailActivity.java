package mx.app.masaryk2.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mx.app.masaryk2.R;
import mx.app.masaryk2.utils.Font;


public class ActivityDetailActivity extends SectionActivity implements OnMapReadyCallback {


	/*------------*/
	/* PROPERTIES */

    TextView txtTitle;
    TextView txtAddress;
    TextView txtDescription;
    TextView txtDate;
    TextView txtTime;
    ImageView imgGallery;
    Button btSchedule;

    JSONObject data;
    GoogleMap mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_detail);

        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);

        txtTitle         = (TextView)findViewById(R.id.txt_title);
        txtAddress       = (TextView)findViewById(R.id.txt_address);
        txtDescription   = (TextView)findViewById(R.id.txt_description);
        txtDate          = (TextView)findViewById(R.id.txt_date);
        txtTime          = (TextView)findViewById(R.id.txt_time);
        imgGallery       = (ImageView)findViewById(R.id.img_gallery);
        btSchedule       = (Button)findViewById(R.id.bt_schedule);

        txtTitle.setTypeface(Font.get(this, "source-sans-semibold"));
        txtAddress.setTypeface(Font.get(this, "source-sans-light"));
        txtDescription.setTypeface(Font.get(this, "source-sans-light"));
        txtDate.setTypeface(Font.get(this, "source-sans-regular"));
        txtTime.setTypeface(Font.get(this, "source-sans-regular"));
        btSchedule.setTypeface(Font.get(this, "source-sans-regular"));

        if (getIntent().hasExtra("data")) {
            try {

                data = new JSONObject( getIntent().getStringExtra("data") );

                GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
                int result = googleAPI.isGooglePlayServicesAvailable(this);
                if (result == ConnectionResult.SUCCESS) {
                    ((MapFragment) getFragmentManager().findFragmentById(R.id.map_view)).getMapAsync(this);
                } else {
                    if(googleAPI.isUserResolvableError(result)) {
                        googleAPI.getErrorDialog(this, result, 0).show();
                    }
                }

                String date_from = data.getString("date_from");
                String date_to   = data.getString("date_to");
                Locale locale    = new Locale("es-MX");
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
                txtDate.setText(formatd.format(datef));
                txtTime.setText(formatt.format(datef) + " - " + formatt.format(datet));

                txtTitle.setText( data.getString("title") );
                txtAddress.setText(data.getString("address"));
                txtDescription.setText( data.getString("description") );

                String url = data.getJSONObject("full").getString("src");
                Picasso.with(this).load(url).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(imgGallery);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickSchedule(View v) {

    }


    public void clickWaze(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Quieres abrir Waze?").setCancelable(false);

        builder.setPositiveButton("Abrir",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        _waze();
                    }
                });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void clickMaps(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Quieres abrir Google Maps?").setCancelable(false);

        builder.setPositiveButton("Abrir",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        _maps();
                    }
                });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }



    /*----------------*/
	/* CUSTOM METHODS */

    protected void _waze() {
        try {
            String url = "waze://?q=" + data.getDouble("lat") + "," + data.getDouble("lng");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity( intent );
        } catch ( Exception exception  ) {
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
            startActivity(intent);
        }
    }

    protected void _maps() {
        try {
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f", data.getString("lat"), data.getString("lng"), data.getString("title"));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        } catch ( Exception e  ) {
            e.printStackTrace();
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
            BitmapDescriptor b = BitmapDescriptorFactory.fromResource(R.drawable.map_pin);

            MarkerOptions o = new MarkerOptions().position(point).title(title).icon(b);

            mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
            mapView.addMarker(o);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
