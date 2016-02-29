package mx.app.masaryk2.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import mx.app.masaryk2.R;
import mx.app.masaryk2.utils.Font;
import mx.app.masaryk2.utils.User;
import mx.app.masaryk2.utils.WebBridge;


public class LoginActivity extends Activity implements WebBridge.WebBridgeListener, FacebookCallback<LoginResult> {


	/*------------*/
	/* PROPERTIES */

    EditText txtEmail;
    EditText txtPassword;
    CallbackManager callback = CallbackManager.Factory.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail    = (EditText)findViewById(R.id.txt_email);
        txtPassword = (EditText)findViewById(R.id.txt_password);

        txtEmail.setTypeface(Font.get(this, "source-sans-regular"));
        txtPassword.setTypeface(Font.get(this, "source-sans-regular"));

        ((Button)findViewById(R.id.bt_recover)).setTypeface(Font.get(this, "source-sans-semibold"));
        ((Button)findViewById(R.id.bt_send)).setTypeface(Font.get(this, "source-sans-semibold"));
        ((Button)findViewById(R.id.bt_register)).setTypeface(Font.get(this, "source-sans-semibold"));
        ((Button)findViewById(R.id.bt_facebook)).setTypeface(Font.get(this, "source-sans-semibold"));

        //txtEmail.setText("daniel.fer@avanna.com.mx");
        //txtPassword.setText("123456");

        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().registerCallback(callback, this);

        if (User.logged(this)) {
            _login();
        }

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickSend(View v) {

        ArrayList<String> errors = new ArrayList<>();
        if (!_isEmailValid(txtEmail.getText().toString())) errors.add(getString(R.string.error_email));
        if (txtPassword.getText().length() < 6) errors.add(getString(R.string.error_password));

        if (errors.size() != 0) {
            String msg = "";
            for (String s : errors) {
                msg += "- " + s + "\n";
            }
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage(msg.trim()).setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        _send();

    }

    public void clickRegister(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 2);
    }

    public void clickFacebook(View v) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_birthday", "user_hometown"));
    }

    public void clickRecover(View v) {
        Intent intent = new Intent(this, RecoverActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 2);
    }



    /*------------------*/
	/* CUSTOM FUNCTIONS */

    protected void _send() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("email", txtEmail.getText());
        params.put("password", txtPassword.getText());

        WebBridge.send("login", params, "Enviando", this, this);

    }

    protected boolean _isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    protected void _login() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 1);
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

            if (url.contains("login")) {
                txtPassword.setText("");
                String token = "";

                try {
                    token = json.getJSONObject("data").getString("remember_token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                User.set("renew_token", token, this);
                User.logged(true, this);

                _login();

            } else if (url.contains("facebook")) {

                Intent intent = new Intent(this, RegisterActivity.class);
                try {
                    intent.putExtra("data", json.getJSONObject("data").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 2);
            }

        } else {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Usuario o contraseña incorrecta").setNeutralButton(R.string.bt_close, null).show();
        }
    }

    @Override
    public void onWebBridgeFailure(String url, String response) {

    }



    /*-------------------*/
	/* FACEBOOK LISTENER */

    @Override
    public void onSuccess(LoginResult loginResult) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", loginResult.getAccessToken().getToken());
        WebBridge.send("facebook", params, "Enviando", this, this);
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onError(FacebookException e) {
    }



	/*-------------------*/
	/* OVERRIDE ACTIVITY */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        callback.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode != RESULT_OK) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

}
