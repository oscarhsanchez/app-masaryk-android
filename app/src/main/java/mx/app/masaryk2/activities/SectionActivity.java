package mx.app.masaryk2.activities;

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
    }

    @Override
    public void onBackPressed() {
        clickBack(null);
    }


}
