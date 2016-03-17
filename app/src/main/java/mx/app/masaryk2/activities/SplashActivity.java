package mx.app.masaryk2.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import mx.app.masaryk2.R;
import mx.app.masaryk2.dialogs.ProgressDialog;


public class SplashActivity extends SectionActivity {


	/*------------*/
	/* PROPERTIES */

    ImageView imgSplash;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        overridePendingTransition(R.anim.fade_in, R.anim.static_motion);
        imgSplash = (ImageView)findViewById(R.id.img_splash);

        if (getIntent().hasExtra("url")) {
            try {

                progress = ProgressDialog.show(this, "Descargando", true, false, null);
                String url = getIntent().getStringExtra("url");

                Log.e("", "URL: " + url);

                Picasso.with(this).load(url).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(imgSplash, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        end(3000);
                    }
                    @Override
                    public void onError() {
                        end(0);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void end(int time) {

        progress.dismiss();
        progress = null;

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                this.cancel();
                finish();
                overridePendingTransition(R.anim.static_motion, R.anim.fade_out);
            }
        };
        timer.schedule(timerTask, time);
    }

}
