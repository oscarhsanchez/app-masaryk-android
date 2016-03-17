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
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mx.app.masaryk2.R;
import mx.app.masaryk2.dialogs.ProgressDialog;
import mx.app.masaryk2.utils.ActivitySchedule;
import mx.app.masaryk2.utils.Font;


public class ActivityDetailActivity extends SectionActivity {


	/*------------*/
	/* PROPERTIES */

    TextView txtTitle;
    TextView txtAddress;
    TextView txtDescription;
    TextView txtDate;
    TextView txtTime;
    TextView txtStatus;
    ImageView imgGallery;
    Button btSchedule;

    JSONObject data;

    SocialAuthAdapter socialAdapter;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_detail);

        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);

        socialAdapter = new SocialAuthAdapter(new ResponseListener());
        socialAdapter.addCallBack(Provider.TWITTER, "http://sutsix.mx/");

        txtTitle         = (TextView)findViewById(R.id.txt_title);
        txtAddress       = (TextView)findViewById(R.id.txt_address);
        txtDescription   = (TextView)findViewById(R.id.txt_description);
        txtDate          = (TextView)findViewById(R.id.txt_date);
        txtTime          = (TextView)findViewById(R.id.txt_time);
        txtStatus        = (TextView)findViewById(R.id.txt_status);
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

                data = new JSONObject( getIntent().getStringExtra("data"));

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
                txtDescription.setText(data.getString("description"));

                String url = data.getJSONObject("full").getString("src");
                Picasso.with(this).load(url).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(imgGallery);

                btSchedule.setText( ActivitySchedule.exists( data.getInt("id") ) ? "Eliminar" : "Agendar" );

                int status = data.getInt("status");
                if (status == 1) {
                    txtStatus.setText("- Abierto -");
                } else if (status == 2) {
                    txtStatus.setText("- Pocos lugares -");
                } else {
                    txtStatus.setText("- Lleno -");
                }

                /*

                    tooltipView.showWithTitle("Haz clic aquí y busca\nlos puntos RA para encontrar ofertas", image: UIImage(named: "icon_tooltip"), anchorToView: topbar.ba);

                 */


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickSchedule(View v) {
        ActivitySchedule.schedule(data, this, new ActivitySchedule.ActivityScheduleListener() {
            @Override
            public void onScheduled(Boolean status) {
                btSchedule.setText(status ? "Eliminar" : "Agendar");
            }
        });
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

    public void clickShare(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Selecciona una opción");

        builder.setPositiveButton("Twitter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                _twitter();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Facebook", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                _facebook();
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
            startActivity(intent);
        } catch (Exception exception) {
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

    protected void _facebook() {

        String url = "";
        try {
            url = data.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Uri uri = Uri.parse(url);
        ShareLinkContent content = new ShareLinkContent.Builder().setContentUrl(uri) .build();
        ShareDialog.show(this, content);
    }

    protected void _twitter() {
        progress = ProgressDialog.show(ActivityDetailActivity.this, "Obteniendo permisos", true, false, null);
        socialAdapter.authorize(ActivityDetailActivity.this, Provider.TWITTER);
        //TweetComposer.Builder builder = new TweetComposer.Builder(this).text(url);
        //builder.show();
    }


    private final class ResponseListener implements DialogListener {

        public void onComplete(Bundle values) {

            progress.dismiss();
            progress = null;

            String url = "";
            try {
                url = data.getString("url");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                progress = ProgressDialog.show(ActivityDetailActivity.this, "Publicando", true, false, null);
                socialAdapter.updateStatus(url, new PostStatusListener(), true);
            } catch (Exception e) { e.printStackTrace(); }
        }

        @Override
        public void onBack() {
            if (progress != null) {
                progress.dismiss();
                progress = null;
            }
        }
        @Override
        public void onCancel() {
            if (progress != null) {
                progress.dismiss();
                progress = null;
            }
        }
        @Override
        public void onError(SocialAuthError arg0) {
            // TODO Auto-generated method stub
            progress.dismiss();
            progress = null;
            Toast.makeText(ActivityDetailActivity.this, "Error desconocido", Toast.LENGTH_LONG).show();
        }
    }

    // To get status of message after authentication
    private final class PostStatusListener implements SocialAuthListener<Integer> {

        @Override
        public void onError(SocialAuthError e) {
            progress.dismiss();
            progress = null;
            Toast.makeText(ActivityDetailActivity.this, "Error desconocido", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onExecute(String provider, Integer t) {
            // TODO Auto-generated method stub
            Integer status = t;

            progress.dismiss();
            progress = null;

            if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204) {
                Toast.makeText(ActivityDetailActivity.this, "Publicado", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ActivityDetailActivity.this, "Error desconocido", Toast.LENGTH_LONG).show();
            }
        }
    }

}
