package mx.app.masaryk2.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by noisedan on 7/11/15.
 */
public class SectionFragment extends Fragment {


	/*------------*/
	/* PROPERTIES */

    SectionFragmentListener listener;
    LayoutInflater inflater;
    Context context;
    RelativeLayout view;
    TextView txtTitle;


    public void setTitle(String t) {
        if (txtTitle != null) {
            txtTitle.setText(t);
        }
    }

    public interface SectionFragmentListener {
        public void onPushActivity(SectionFragment fragment, Intent intent);
    }

}
