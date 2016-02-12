package mx.app.masaryk2.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.app.masaryk2.R;
import mx.app.masaryk2.utils.WebBridge;

/**
 * Created by noisedan on 9/29/15.
 */
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

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("email", txtEmail.getText());

        WebBridge.send("recover", params, "Enviando", this, this);
    }

    protected boolean _isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches()) return true;
        else return false;
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
