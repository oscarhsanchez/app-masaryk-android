package mx.app.masaryk2.utils;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ActivitySQL extends SQLiteOpenHelper {

	// All Static variables
    private static final int DATABASE_VERSION  = 1;
    private static final String DATABASE_NAME  = "events.sqlite";
    private static final String TABLE_SCHEDULE = "events";

    private static final String TABLE_SCHEDULE_SCHEMA = "CREATE TABLE " + TABLE_SCHEDULE +
													"(\"id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE, " +
													"\"event\" INTEGER, \"title\" TEXT, \"date\" INTEGER)";


    private static ActivitySQL database;
    //private static Context context;
	
    public ActivitySQL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_SCHEDULE_SCHEMA);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	
	static public void init(Context context) {
		
		if (ActivitySQL.database != null) return;
		
		ActivitySQL.database = new ActivitySQL(context);
		ActivitySQL.database.close();

	}

	static public HashMap<String, Object> get (int eventID) {

		SQLiteDatabase db = ActivitySQL.database.getWritableDatabase();
		HashMap<String, Object> data = new HashMap<String, Object>();

		String query  = "SELECT id, event, title, date FROM " + TABLE_SCHEDULE + " WHERE event = ?;";
		Cursor cursor = db.rawQuery(query, new String[] {Integer.toString(eventID)});

		if (cursor == null) return null;

		if (cursor.moveToFirst()) {

			data.put("id",    cursor.getInt(0));
			data.put("event", cursor.getString(1));
			data.put("title", cursor.getString(2));
			data.put("date",  cursor.getString(3));

            cursor.close();
			return data;
		}

        cursor.close();
		return null;
	}

	static public int add (int eventID, String title, String date) {
		
		SQLiteDatabase db = ActivitySQL.database.getWritableDatabase();
				
		ContentValues pquery = new ContentValues();
		pquery.put("event", eventID);
		pquery.put("title", title);
	    pquery.put("date", 	date);
		
	    return (int)db.insert(TABLE_SCHEDULE, null, pquery);	
		
	}
	
	static public Boolean exists (int eventID) {
		
		SQLiteDatabase db = ActivitySQL.database.getWritableDatabase();
			
		String query  = "SELECT id FROM " + TABLE_SCHEDULE + " WHERE event = ?;";
		Cursor cursor = db.rawQuery(query, new String[] {Integer.toString(eventID)});
        Boolean exist = cursor != null && cursor.moveToFirst();

        if (cursor != null) {
            cursor.close();
        }

		return exist;

	}
	
	static public void remove (int eventID) {
		SQLiteDatabase db = ActivitySQL.database.getWritableDatabase();
	    db.delete(TABLE_SCHEDULE, "event="+eventID, null);
	}
}
