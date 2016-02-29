package mx.app.masaryk2.activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import mx.app.masaryk2.R;

public class SectionActivity extends FragmentActivity {


	/*------------*/
	/* PROPERTIES */

    public void setTitle(String t) {
        if (findViewById(R.id.txt_title) != null) {
            ((TextView)findViewById(R.id.txt_title)).setText(t);
        }
    }


	/*--------------*/
	/* CLICK EVENTS */

    public void clickBack(View v) {
        finish();
        overridePendingTransition(R.anim.slide_right_from, R.anim.slide_right);
    }

    public void clickAr(View v) {
        Intent intent = new Intent(this, ArActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        clickBack(null);
    }


}
