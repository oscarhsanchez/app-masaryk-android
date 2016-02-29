package mx.app.masaryk2.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.craftar.CraftARActivity;
import com.craftar.CraftARItem;
import com.craftar.CraftARItemAR;
import com.craftar.CraftAROnDeviceCollection;
import com.craftar.CraftAROnDeviceCollectionManager;
import com.craftar.CraftARSDK;
import com.craftar.CraftARTracking;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import mx.app.masaryk2.R;

public class ArActivity extends CraftARActivity {


	/*------------*/
	/* PROPERTIES */

    CraftARTracking mTracking;
    CraftARSDK mCraftARSDK;
    CraftAROnDeviceCollectionManager mCollectionManager;
    CraftAROnDeviceCollection mCollection;

    private final String TAG = "OnDeviceARActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up, R.anim.static_motion);

    }

    @Override
    public void onPostCreate() {

        setContentView(R.layout.activity_ar);

        mCraftARSDK = CraftARSDK.Instance();
        mCraftARSDK.startCapture(this);

        mTracking = CraftARTracking.Instance();


    }

    @Override
    public void onCameraOpenFailed() {
        Toast.makeText(getApplicationContext(), "Camera error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPreviewStarted(int i, int i1) {
        mCollection =  CraftAROnDeviceCollectionManager.Instance().get("Masaryk2");
        /**
         * As the on-device collection is already in the device (we did it in the Splash Screen), we will add the collection items
         * to the tracking and start the AR experience.
         */
        loadCollection();
    }

    private void loadCollection() {
        // Get all item UUIDs in the collection

        Log.e("", "");

        for(String itemUUID: mCollection.listItems()){
            // Get the item and check that it is an AR item
            CraftARItem item = mCollection.getItem(itemUUID);
            if(item.isAR()){
                CraftARItemAR itemAR = (CraftARItemAR)item;
                // Add the item to the tracking
                Log.e(TAG, "Adding item " + item.getItemName() + " for tracking");
                mTracking.addItem(itemAR);
            }
        }
        // Start tracking this collection.
        mTracking.startTracking();
    }



    /*--------------*/
	/* CLICK EVENTS */

    public void clickBack(View v) {
        finish();
        overridePendingTransition(R.anim.static_motion, R.anim.slide_down);
    }

    @Override
    public void onBackPressed() {
        clickBack(null);
    }
}
