package mx.app.masaryk2.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import mx.app.masaryk2.R;


public class PaymentActivity extends SectionActivity {


    /*------------*/
	/* PROPERTIES */
    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        overridePendingTransition(R.anim.slide_up, R.anim.static_motion);

        webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                Log.e("", "shouldOverrideUrlLoading: " + url);
                Log.e("", "getHost: " + uri.getHost());
                Log.e("", "getScheme: " + uri.getScheme());
                if (uri.getScheme().equals("masaryk")) {
                    Log.e("", "DETECTED");
                    Log.e("", "------------------------");
                    return false;
                }
                Log.e("", "------------------------");
                return true;
            }
        });

        webView.loadUrl("https://www.pasoporkm.com/masaryk.html?event=e7311b0ba6d1b1af3be179047c98401c&api_key=1b19545a931f6506fc3906e69079bd92be853d20ee06b1b2f8aae3dbec082f51");

    }


    /*--------------*/
	/* CLICK EVENTS */

    public void clickBack(View v) {
        finish();
        overridePendingTransition(R.anim.static_motion, R.anim.slide_down);
    }

}
