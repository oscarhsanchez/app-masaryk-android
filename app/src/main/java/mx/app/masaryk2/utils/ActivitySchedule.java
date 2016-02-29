package mx.app.masaryk2.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import mx.app.masaryk2.activities.PaymentActivity;
import mx.app.masaryk2.alarms.AlarmReceiver;

public class ActivitySchedule {

    public interface ActivityScheduleListener {
        public void onScheduled(Boolean status);
    }

    static public void schedule(JSONObject item, Context context, ActivityScheduleListener listener) {

        int eventID = 0;
        String title = "", date = "";
        Boolean payment = false;

        try {
            eventID = item.getInt("id");
            title   = item.getString("title");
            date    = item.getString("date_from");
            payment = item.getInt("type_id") == 2;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (exists(eventID)) {
            unregister(eventID, context, listener);
        } else {
            register(eventID, title, date, payment, context, listener);
        }

    }

    static public boolean exists(int eventID) {
        return ActivitySQL.exists(eventID);
    }

    static void register(final int eventID, final String title, final String date, final Boolean payment, final Context context, final ActivityScheduleListener listener) {

        String msg = "";
        if (payment) {
            msg = "Esta actividad tiene costo. Si ya hiciste el pago ¿te gustaría agendarlo? En caso contrario también puedes pasar a la pasarela de pago.";
        } else {
            msg = "¿Quieres agregar este evento a tu calendario?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setCancelable(false);

        builder.setPositiveButton("Agendar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        ActivitySQL.add(eventID, title, date);
                        AlarmReceiver.add(eventID, date, context);

                        HashMap<String, Object> params = new HashMap<>();
                        params.put("activity_id", eventID);
                        WebBridge.send("activity-register", params, context);

                        listener.onScheduled(true);

                    }
                });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        if (payment) {
            builder.setNeutralButton("Pagar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(context, PaymentActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            context.startActivity(intent);
                        }
                    });
        }

        AlertDialog alert = builder.create();
        alert.show();

    }

    static void unregister(final int eventID, final Context context, final ActivityScheduleListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("¿Quieres eliminar el evento de tu agenda?").setCancelable(false);

        builder.setPositiveButton("Si, por favor",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        ActivitySQL.remove(eventID);
                        AlarmReceiver.remove(eventID, context);

                        listener.onScheduled(false);

                        HashMap<String, Object> params = new HashMap<>();
                        params.put("activity_id", eventID);
                        WebBridge.send("activity-unregister", params, context);

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

}
