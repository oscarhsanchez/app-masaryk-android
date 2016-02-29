package mx.app.masaryk2.alarms;

import java.util.Calendar;
import java.util.HashMap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import mx.app.masaryk2.activities.LoginActivity;
import mx.app.masaryk2.utils.ActivitySQL;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {		

		// TODO Auto-generated method stub
		int eventID = intent.getIntExtra("event", 999999);

		HashMap<String, Object> event = ActivitySQL.get(eventID);
		if (event == null) {
			return;
		}

		String title = event.get("title").toString();

		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(android.R.drawable.stat_notify_more)
		        .setContentTitle("Camina Masaryk")
		        .setTicker(title)
		        .setContentText("Test")
		        .setAutoCancel(true)
		        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		
		Intent home = new Intent(context, LoginActivity.class);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(LoginActivity.class);
		stackBuilder.addNextIntent(home);
		
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// mId allows you to update the notification later on.
		mNotificationManager.notify(eventID, mBuilder.build());

		
	}
	
	public static void add (int eventID, String dt, Context context) {
		
		 String datetime[] = dt.split(" ");
		 String date[]	   = datetime[0].split("-");
		 String time[]	   = datetime[1].split(":");

		 Calendar calendar = Calendar.getInstance();
		 calendar.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]) - 10, 0);
		 Log.e("MSG", calendar.getTime().toString());
		 
		 Intent intent = new Intent(context, AlarmReceiver.class);
		 intent.putExtra("event", eventID);

		 PendingIntent sender = PendingIntent.getBroadcast(context.getApplicationContext(), eventID, intent, 0);

		 AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		 am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);			 
		
	}
	
	public static void remove (int eventID, Context context) {

		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra("event", eventID);
		PendingIntent.getBroadcast(context, eventID, intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();

	}

}
