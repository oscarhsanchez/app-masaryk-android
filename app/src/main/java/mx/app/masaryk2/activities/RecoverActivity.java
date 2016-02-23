package mx.app.masaryk2.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import mx.app.masaryk2.R;
import mx.app.masaryk2.utils.Font;
import mx.app.masaryk2.utils.WebBridge;

public class RecoverActivity extends SectionActivity implements WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    EditText txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);

        txtEmail = (EditText)findViewById(R.id.txt_email);

        txtEmail.setTypeface(Font.get(this, "source-sans-regular"));
        ((TextView)findViewById(R.id.txt_section_title)).setTypeface(Font.get(this, "source-sans-semibold"));
        ((Button)findViewById(R.id.bt_send)).setTypeface(Font.get(this, "source-sans-semibold"));

        findViewById(R.id.bt_ar).setVisibility(View.GONE);

        setTitle("Recuperar");
    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickSend(View v) {

        if (!_isEmailValid(txtEmail.getText().toString())) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage(R.string.error_email).setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        _send();

    }



    /*------------------*/
	/* CUSTOM FUNCTIONS */

    protected void _send() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("email", txtEmail.getText());

        WebBridge.send("recover", params, "Enviando", this, this);
    }

    protected boolean _isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



    /*--------------------*/
	/* WEBBRIDGE LISTENER */

    @Override
    public void onWebBridgeSuccess(String url, JSONObject json) {

        boolean success = false;
        try {
            success = json.getBoolean("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (success) {

            txtEmail.setText("");
            String msg = "Por favor revisa tu correo para seguir las instrucciones para recuperar tu contraseña.";
            new AlertDialog.Builder(this).setMessage(msg).setNeutralButton(R.string.bt_close, null).show();

        } else {

            String error = "";
            try {
                error = json.getString("error_message");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage(error).setNeutralButton(R.string.bt_close, null).show();
        }

    }

    @Override
    public void onWebBridgeFailure(String url, String response) {

    }

}
