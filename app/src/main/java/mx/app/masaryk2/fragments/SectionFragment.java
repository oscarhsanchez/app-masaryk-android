package mx.app.masaryk2.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SectionFragment extends Fragment {


	/*------------*/
	/* PROPERTIES */

    SectionFragmentListener listener;
    RelativeLayout view;
    TextView txtTitle;

    protected Context context;

    public void setTitle(String t) {
        if (txtTitle != null) {
            txtTitle.setText(t);
        }
    }

    protected void _ar() {

    }

    public interface SectionFragmentListener {
        public void onPushActivity(SectionFragment fragment, Intent intent);
    }

}
