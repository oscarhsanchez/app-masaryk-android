package mx.app.masaryk2.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import mx.app.masaryk2.utils.FragmentInflatorFactory;
import mx.app.masaryk2.utils.WebBridge;

/**
 * Created by noisedan on 9/29/15.
 */
public class ProfileFragment extends SectionFragment implements WebBridge.WebBridgeListener {


	/*------------*/
	/* PROPERTIES */

    EditText txtEmail;
    EditText txtPassword;
    EditText txtConfirm;
    EditText txtFirstName;
    EditText txtLastName;
    EditText txtCity;
    Button btBirthday;

    Boolean loaded;
    Calendar date;
    DatePickerDialog datePicker;
    SimpleDateFormat datePickerForm;
    SimpleDateFormat dateServerForm;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(SectionFragment.SectionFragmentListener listener) {
        ProfileFragment f = new ProfileFragment();
        f.listener = listener;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.e("", "Perfil");

        view         = (RelativeLayout) FragmentInflatorFactory.inflatorFor(inflater, this).inflate(R.layout.fragment_profile, container, false);
        txtTitle     = (TextView)view.findViewById(R.id.txt_title);
        txtEmail     = (EditText)view.findViewById(R.id.txt_email);
        txtPassword  = (EditText)view.findViewById(R.id.txt_password);
        txtConfirm   = (EditText)view.findViewById(R.id.txt_confirm);
        txtFirstName = (EditText)view.findViewById(R.id.txt_first_name);
        txtLastName  = (EditText)view.findViewById(R.id.txt_last_name);
        txtCity      = (EditText)view.findViewById(R.id.txt_city);
        btBirthday   = (Button)view.findViewById(R.id.bt_birthday);


        ((Button)view.findViewById(R.id.bt_birthday)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });

        ((Button)view.findViewById(R.id.bt_send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _validate();
            }
        });


        datePickerForm = new SimpleDateFormat("MMMM d, yyyy", new Locale("es", "ES"));
        dateServerForm = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "ES"));

        Calendar newCalendar = Calendar.getInstance();
        datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date = Calendar.getInstance();
                date.set(year, monthOfYear, dayOfMonth);
                btBirthday.setText(datePickerForm.format(date.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        loaded = false;

        setTitle("Perfil");
        WebBridge.send("profile", "Descargando", getActivity(), this);

        return view;

    }



    /*------------------*/
	/* CUSTOM FUNCTIONS */

    protected void _validate() {

        if (!_isEmailValid(txtEmail.getText().toString())) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.txt_error).setMessage(R.string.error_email).setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        if (txtPassword.getText().length() < 6 && !txtPassword.getText().toString().isEmpty()) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.txt_error).setMessage("Tu contraseña debe tener al menos 6 caracteres").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        if (!txtPassword.getText().toString().equals(txtConfirm.getText().toString())) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.txt_error).setMessage("No coinciden los campos de contraseña").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        if (txtFirstName.getText().length() < 3) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.txt_error).setMessage("Indicanos tu nombre").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        if (txtLastName.getText().length() < 3) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.txt_error).setMessage("Indicanos tu apellido").setNeutralButton(R.string.bt_close, null).show();
            return;
        }

        _send();

    }



    /*----------------*/
	/* CUSTOM METHODS */

    protected void _send() {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("email",      txtEmail.getText());
        params.put("first_name", txtFirstName.getText());
        params.put("last_name",  txtLastName.getText());
        params.put("city",       txtCity.getText());

        if (!txtPassword.getText().toString().isEmpty()) {
            params.put("password",   txtPassword.getText());
            params.put("cpassword",  txtConfirm.getText());
        }

        if (date != null) {
            params.put("birthday", dateServerForm.format(date.getTime()));
        } else {
            params.put("birthday", "0000-00-00");
        }

        WebBridge.send("profile", params, "Enviando", getActivity(), this);
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

            if (loaded) {
                String msg = "¡Perfil actualizado!";
                new AlertDialog.Builder(getActivity()).setMessage(msg).setNeutralButton(R.string.bt_close, null).show();
            } else {

                loaded = true;

                try {
                    JSONObject data = json.getJSONObject("data");
                    txtEmail.setText(data.getString("email"));
                    txtFirstName.setText(data.getString("first_name"));
                    txtLastName.setText(data.getString("last_name"));
                    txtCity.setText(data.getString("city"));

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
        } else {

            String error = "";
            try {
                error = json.getString("error_message");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new AlertDialog.Builder(getActivity()).setTitle(R.string.txt_error).setMessage(error).setNeutralButton(R.string.bt_close, null).show();
        }

    }

    @Override
    public void onWebBridgeFailure(String url, String response) {
    }

}
