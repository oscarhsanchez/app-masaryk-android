package mx.app.masaryk2.activities;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import mx.app.masaryk2.R;

/**
 * Created by noisedan on 9/29/15.
 */
public class PromoDetailActivity extends SectionActivity {


	/*------------*/
	/* PROPERTIES */

    JSONObject data;
    ImageView imgPromo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_detail);

        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);

        imgPromo = (ImageView)findViewById(R.id.img_promo);

        if (getIntent().hasExtra("data")) {
            try {

                data = new JSONObject( getIntent().getStringExtra("data") );
                String url = data.getJSONObject("full").getString("src");

                Picasso.with(this).load(url).into(imgPromo);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
