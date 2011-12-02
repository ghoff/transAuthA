package org.planetgammu.android.transauth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class KeyData {
	
	static final String ID_COLUMN = "id";
	static final String KEY_COLUMN = "key";
	static final String DBNAME = "keys";
	static final int DBVERSION = 1;
	private static final String PATH = "database";
	private static SQLiteDatabase DATABASE = null;
	
	private KeyData() {
		/* not used normally */
	}
	
	static void initialize(Context context)
	{
		if (DATABASE != null)
			return;
		
		KeyOpenHelper KeyOpen = new KeyOpenHelper(context);
		DATABASE = KeyOpen.getWritableDatabase();
	}
	
	static Cursor getIds() {
		return DATABASE.query(DBNAME, new String[] {ID_COLUMN}, null, null, null, null, null);
	}
	
	static Cursor getKeyById(int keyId) {
		return DATABASE.query(DBNAME, new String[] {KEY_COLUMN}, ID_COLUMN + "= ?", 
				new String[] {Integer.toString(keyId)}, null, null, null);
	}
	
	static void delete(int keyId) {
		DATABASE.delete(DBNAME, ID_COLUMN + "=?", new String[] {Integer.toString(keyId)});
	}
	
	static void insert(int keyId, String key) {
		ContentValues values = new ContentValues();
		values.put(ID_COLUMN, Integer.toString(keyId));
		values.put(KEY_COLUMN, key);
		DATABASE.insert(DBNAME, null, values);
	}
	
	static boolean keyExists(int keyId) {
		Cursor cursor = getKeyById(keyId);
		try {
			return !cursorIsEmpty(cursor);
		} finally {
			tryCloseCursor(cursor);
		}
	}
	
	static boolean cursorIsEmpty(Cursor c) {
		return c == null || c.getCount() == 0;
	}
	static void tryCloseCursor(Cursor c) {
		if (c != null && !c.isClosed()) {
			c.close();
		}
	}


	public static class KeyOpenHelper extends SQLiteOpenHelper {

		KeyOpenHelper(Context context) {
			super(context, PATH, null, DBVERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			String dbcreate = String.format(
					"CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT NOT NULL)",
					DBNAME, ID_COLUMN, KEY_COLUMN);
			db.execSQL(dbcreate);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
