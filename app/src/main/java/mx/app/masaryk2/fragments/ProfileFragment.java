package mx.app.masaryk2.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import mx.app.masaryk2.R;
import mx.app.masaryk2.activities.HomeActivity;
import mx.app.masaryk2.utils.Font;
import mx.app.masaryk2.utils.User;
import mx.app.masaryk2.utils.WebBridge;

public class ProfileFragment extends SectionFragment implements WebBridge.WebBridgeListener, ImageChooserListener {


	/*------------*/
	/* PROPERTIES */

    EditText txtEmail;
    EditText txtPassword;
    EditText txtConfirm;
    EditText txtFirstName;
    EditText txtLastName;
    EditText txtCity;
    EditText txtPhone;
    Button btBirthday;
    Button btAvatar;
    ImageView imgAvatar;
    TextView txtInfoName;
    TextView txtInfoCity;

    Boolean loaded;
    Calendar date;
    DatePickerDialog datePicker;
    SimpleDateFormat datePickerForm;
    SimpleDateFormat dateServerForm;

    String icFilePath;
    String imgPath;
    int icType;
    ImageChooserManager icManager;


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

        view         = (RelativeLayout)inflater.inflate(R.layout.fragment_profile, container, false);
        txtTitle     = (TextView)view.findViewById(R.id.txt_title);
        txtEmail     = (EditText)view.findViewById(R.id.txt_email);
        txtPassword  = (EditText)view.findViewById(R.id.txt_password);
        txtConfirm   = (EditText)view.findViewById(R.id.txt_confirm);
        txtFirstName = (EditText)view.findViewById(R.id.txt_first_name);
        txtLastName  = (EditText)view.findViewById(R.id.txt_last_name);
        txtCity      = (EditText)view.findViewById(R.id.txt_city);
        txtPhone     = (EditText)view.findViewById(R.id.txt_phone);
        btBirthday   = (Button)view.findViewById(R.id.bt_birthday);
        btAvatar     = (Button)view.findViewById(R.id.bt_avatar);
        imgAvatar    = (ImageView)view.findViewById(R.id.img_avatar);
        txtInfoName  = (TextView)view.findViewById(R.id.txt_info_name);
        txtInfoCity  = (TextView)view.findViewById(R.id.txt_info_city);


        txtEmail.setTypeface(Font.get(getActivity(), "source-sans-regular"));
        txtPassword.setTypeface(Font.get(getActivity(), "source-sans-regular"));
        txtConfirm.setTypeface(Font.get(getActivity(), "source-sans-regular"));
        txtFirstName.setTypeface(Font.get(getActivity(), "source-sans-regular"));
        txtLastName.setTypeface(Font.get(getActivity(), "source-sans-regular"));
        txtCity.setTypeface(Font.get(getActivity(), "source-sans-regular"));
        btBirthday.setTypeface(Font.get(getActivity(), "source-sans-regular"));
        txtPhone.setTypeface(Font.get(getActivity(), "source-sans-regular"));
        txtInfoName.setTypeface(Font.get(getActivity(), "source-sans-semibold"));
        txtInfoCity.setTypeface(Font.get(getActivity(), "source-sans-light"));
        ((Button)view.findViewById(R.id.bt_send)).setTypeface(Font.get(getActivity(), "source-sans-semibold"));


        view.findViewById(R.id.bt_birthday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });

        view.findViewById(R.id.bt_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _validate();
            }
        });
        view.findViewById(R.id.bt_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _avatar();
            }
        });
        view.findViewById(R.id.bt_ar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _ar();
            }
        });
        view.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _logout();
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
                btBirthday.setTextColor(Color.parseColor("#000000"));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        loaded = false;

        _ar();

        setTitle("Perfil");
        WebBridge.send("profile", "Descargando", getActivity(), this);

        return view;

    }



    /*----------------*/
	/* CUSTOM METHODS */

    protected void restartImageChooser() {
        icManager = new ImageChooserManager(this, icType, true);
        icManager.setImageChooserListener(this);
        icManager.reinitialize(icFilePath);
    }

    protected void showImageChooser(int type) {

        icType    = type;
        icManager = new ImageChooserManager(this, icType, true);
        icManager.setImageChooserListener(this);
        //icManager.clearOldFiles();

        try {
            icFilePath = icManager.choose();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void _logout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("¿Quieres cerrar tu sesión?").setCancelable(false);

        builder.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        WebBridge.send("logout", "Cerrando", getActivity(), ProfileFragment.this);
                    }
                });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    protected void _avatar() {

        final Dialog alert = new Dialog(getActivity());
        alert.setTitle(getResources().getString(R.string.txt_select_option));
        alert.setContentView(getActivity().getLayoutInflater().inflate(R.layout.dialog_alert_photo, view, false));
        alert.findViewById(R.id.bt_select_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                showImageChooser(ChooserType.REQUEST_CAPTURE_PICTURE);
            }
        });
        alert.findViewById(R.id.bt_select_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                showImageChooser(ChooserType.REQUEST_PICK_PICTURE);
            }
        });
        alert.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
            }
        });
        alert.show();

    }

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

    protected void _profile(JSONObject json) {
        try {
            JSONObject data = json.getJSONObject("data");
            txtEmail.setText(data.getString("email"));
            txtFirstName.setText(data.getString("first_name"));
            txtLastName.setText(data.getString("last_name"));
            txtCity.setText(data.getString("city"));
            txtPhone.setText(data.getString("phone"));
            txtInfoName.setText( data.getString("first_name") + " " + data.getString("last_name"));
            txtInfoCity.setText(data.getString("city"));

            if (!data.getString("birthday").equals("0000-00-00")) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                date = Calendar.getInstance();
                date.setTime(format.parse(data.getString("birthday")));
                btBirthday.setText(datePickerForm.format(date.getTime()));
                btBirthday.setTextColor(Color.parseColor("#000000"));
            } else {
                btBirthday.setTextColor(Color.parseColor("#BBBBBB"));
            }

            Picasso.with(getActivity()).load(data.getString("avatar")).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).fit().centerInside().into(imgAvatar);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   protected void _send() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("email",      txtEmail.getText());
        params.put("first_name", txtFirstName.getText());
        params.put("last_name",  txtLastName.getText());
        params.put("city",       txtCity.getText());
        params.put("phone",      txtPhone.getText());

        if (!txtPassword.getText().toString().isEmpty()) {
            params.put("password",   txtPassword.getText());
            params.put("cpassword",  txtConfirm.getText());
        }

        if (date != null) {
            params.put("birthday", dateServerForm.format(date.getTime()));
        } else {
            params.put("birthday", "0000-00-00");
        }

        if (imgPath != null) {

            File file = new File(imgPath);
            params.put("image", file);
            Log.e("", "FILE PATH: " + file.getAbsolutePath());
            Log.e("", "FILE EXISTS: " + file.exists());

            params.put("image", file);
        }

        WebBridge.send("profile", params, "Enviando", getActivity(), this);
    }

    protected boolean _isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



    /*--------------------*/
	/* WEBBRIDGE LISTENER */

    @Override
    public void onWebBridgeSuccess(String url, JSONObject json) {

        boolean success = false;
        String service  = "";

        try {
            success = json.getBoolean("success");
            service = json.getString("service");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (success) {

            if (service.equals("profile")) {
                if (loaded) {
                    String msg = "¡Perfil actualizado!";
                    new AlertDialog.Builder(getActivity()).setMessage(msg).setNeutralButton(R.string.bt_close, null).show();
                } else {
                    loaded = true;
                }
                _profile(json);

            } else if (service.equals("logout")) {

                User.set("renew_token", "", getActivity());
                User.logged(false, getActivity());

                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.static_motion, R.anim.fade_out);

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




    /*------------------------*/
	/* IMAGE CHOOSER LISTENER */


    @Override
    public void onImageChosen(final ChosenImage image) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.e("IMG", "Chosen Image: O - " + image.getFilePathOriginal());
                Log.e("IMG", "Chosen Image: T - " + image.getFileThumbnail());
                Log.e("IMG", "Chosen Image: Ts - " + image.getFileThumbnailSmall());
                if (image != null) {
                    Picasso.with(getActivity()).load(Uri.fromFile(new File(image.getFileThumbnail()))).fit().centerInside().into(imgAvatar);
                    imgPath = image.getFilePathOriginal();
                } else {
                    Log.i("IMG", "Chosen Image: Is null");
                }
            }
        });
    }

    @Override
    public void onError(String s) {

    }

    @Override
    public void onImagesChosen(ChosenImages chosenImages) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == HomeActivity.RESULT_OK && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (icManager == null) {
                restartImageChooser();
            }
            icManager.submit(requestCode, data);
        }
    }


}
