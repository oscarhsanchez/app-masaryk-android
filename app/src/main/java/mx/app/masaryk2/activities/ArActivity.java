package mx.app.masaryk2.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.StartupConfiguration;

import java.io.IOException;

import mx.app.masaryk2.R;

public class ArActivity extends SectionActivity {


    final StartupConfiguration config = new StartupConfiguration("zLrgXqkEd2iB3oAAo3DjkbTJfsnWChVxgPRewWO5t6niIbN2dVp4LZU7j5Pt9B8jlQPFeJ0h4f85xNvoL3U5LR4nahOxiNnPIHGkCkSKKlA1YCotNZJtInMVermZGy4yoFQBWbKJSEUJSo39z4PbBxQKFvfYYJfGTpcDU3PtXNdTYWx0ZWRfXxErF0akR/a4lGXJ4y2I0FaRY0CEEL1Ek/6PLL6PA1P/Po6hD0/vQt5MaZo9IjVUi/XWR7OAs5T/OqdOV9++LI4Vui9OfQOCCBsULCNqtMV25MEEWj6qL49h95+M8R0lOFBOZjg+8NTRGHoEIUI7lfScqBcHWYANVgZgSEP/mv+lIVGzOFGClP3tgZfSLty8YZu66Mbowull0eYoW1Hr8whgo3MDnCDB//3IWAZJwhL2E5GliIWEvlLSDYT8Fkbb0sS6awMH4yL2Cx1QLo0b/0DI5/ohtlVCmoiv6DQRwI9TDith2vGdRIxTvVEdSmWi4eWfmChQEsABC8BIJjdDe65rQ0niLgg1MLJ45ZDdvasxh2WTZusyny/Z5hLYSCj2Jl4lfMJVa1pXVoXtsMVqm/WppAkdDokI8nJu3mnQdXLybTC8VRh61VQUT+4HJ1Z3zA7C2JFxEjMCsn1zKVGH0PENW07aaBcYZy4HTxzU+dF987BkmNsyn9I=");


    /*------------*/
	/* PROPERTIES */
    protected ArchitectView architectView;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up, R.anim.static_motion);
        setContentView(R.layout.activity_ar);

        if (ArchitectView.isDeviceSupported(this)) {
            architectView = (ArchitectView)findViewById(R.id.architect_view);
            architectView.onCreate(config);
        } else {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Tu dispositivo no sopora realidad aumentada.").setNeutralButton(R.string.bt_close, null).show();
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();
        try {
            architectView.load("ar/index.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (architectView != null) {
            architectView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (architectView != null) {
            architectView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (architectView != null) {
            architectView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (architectView != null) {
            architectView.onLowMemory();
        }
    }

    /*--------------*/
	/* CLICK EVENTS */

    public void clickBack(View v) {
        finish();
        overridePendingTransition(R.anim.static_motion, R.anim.slide_down);
    }


}
