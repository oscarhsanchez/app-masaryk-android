package mx.app.masaryk2.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.app.masaryk2.R;
import mx.app.masaryk2.utils.User;
import mx.app.masaryk2.utils.WebBridge;

/**
 * Created by noisedan on 9/29/15.
 */
public class RegisterActivity extends SectionActivity implements WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    EditText txtEmail;
    EditText txtPassword;
    EditText txtConfirm;
    EditText txtFirstName;
    EditText txtLastName;
    EditText txtCity;
    Button btBirthday;

    private Calendar date;
    private DatePickerDialog datePicker;
    private SimpleDateFormat datePickerForm;
    private SimpleDateFormat dateServerForm;

    JSONObject data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);

        txtEmail     = (EditText)findViewById(R.id.txt_email);
        txtPassword  = (EditText)findViewById(R.id.txt_password);
        txtConfirm   = (EditText)findViewById(R.id.txt_confirm);
        txtFirstName = (EditText)findViewById(R.id.txt_first_name);
        txtLastName  = (EditText)findViewById(R.id.txt_last_name);
        txtCity      = (EditText)findViewById(R.id.txt_city);
        btBirthday   = (Button)findViewById(R.id.bt_birthday);

        datePickerForm = new SimpleDateFormat("MMMM d, yyyy", new Locale("es", "ES"));
        dateServerForm = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "ES"));

        Calendar newCalendar = Calendar.getInstance();
        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date = Calendar.getInstance();
                date.set(year, monthOfYear, dayOfMonth);
                btBirthday.setText(datePickerForm.format(date.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        if (getIntent().hasExtra("data")) {
            try {
                data = new JSONObject( getIntent().getStringExtra("data") );
                txtEmail.setText(data.getString("email"));
                txtFirstName.setText(data.getString("first_name"));
                txtLastName.setText(data.getString("last_name"));
                txtCity.setText(data.getString("hometown"));

                if (!data.getString("birthday").toString().equals("0000-00-00")) {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    date = Calendar.getInstance();
                    date.setTime(format.parse(data.getString("birthday")));
                    btBirthday.setText(datePickerForm.format(date.getTime()));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setTitle("Registro");

        /*
        txtEmail.setText("test@avanna.com.mx");
        txtFirstName.setText("Test");
        txtLastName.setText("Test");
        txtCity.setText("Mexico");
        txtPassword.setText("123456");
        txtConfirm.setText("123456");
        */

    }



	/*--------------*/
	/* CLICK EVENTS */

    public void clickBirthday(View v) {
        datePicker.show();
    }

    public void clickSend(View v) {

        if (!_isEmailValid(txtEmail.getText().toString())) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage(R.string.error_email).setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        if (txtPassword.getText().length() < 6) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Tu contraseña debe tener al menos 6 caracteres").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        if (!txtPassword.getText().toString().equals(txtConfirm.getText().toString())) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("No coinciden los campos de contraseña").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        if (txtFirstName.getText().length() < 3) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Indicanos tu nombre").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        if (txtLastName.getText().length() < 3) {
            new AlertDialog.Builder(this).setTitle(R.string.txt_error).setMessage("Indicanos tu apellido").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        _send();

    }



    /*------------------*/
	/* CUSTOM FUNCTIONS */

    protected void _send() {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("email",      txtEmail.getText());
        params.put("first_name", txtFirstName.getText());
        params.put("last_name",  txtLastName.getText());
        params.put("password",   txtPassword.getText());
        params.put("cpassword",  txtConfirm.getText());
        params.put("city",       txtCity.getText());

        if (date != null) {
            params.put("birthday", dateServerForm.format(date.getTime()));
        } else {
            params.put("birthday", "0000-00-00");
        }

        try {
            if (data != null) {
                params.put("token", data.getString("token"));
                params.put("fb_id", data.getString("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebBridge.send("register", params, "Enviando", this, this);

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

            String token = "";
            try {
                token = json.getJSONObject("data").getString("remember_token");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            User.set("renew_token", token, this);
            User.logged(true, this);

            setResult(Activity.RESULT_OK);
            finish();

            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, 1);

        } else {

            String error = "";
            try {
                JSONArray errrors = json.getJSONArray("error_message");
                for(int i=0; i<errrors.length(); i++) {
                    error += errrors.getString(i) + "\n";
                }
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
