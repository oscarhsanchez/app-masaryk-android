package mx.app.masaryk2.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mx.app.masaryk2.R;
import mx.app.masaryk2.activities.ArActivity;

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

        view.findViewById(R.id.bt_ar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ArActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

    }



    public interface SectionFragmentListener {
        public void onPushActivity(SectionFragment fragment, Intent intent);
    }

}
