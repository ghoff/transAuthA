package org.planetgammu.android.transauth;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

public class KeyManagementActivity extends Activity {
	
	private ListView mKeyList;
	private KeyListAdapter mKeyAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] mIds = {};
		
		Cursor cursor = KeyData.getIds();
		try {
			if (!KeyData.cursorIsEmpty(cursor)) {
				int length = cursor.getCount();
				mIds = new String[length];
				for (int i = 0; i < length; i++) {
					cursor.moveToPosition(i);
					mIds[i] = cursor.getString(0);
				}
			}
		} finally {
			KeyData.tryCloseCursor(cursor);
		}
		
		setContentView(R.layout.key_configuration);
		mKeyList = (ListView) findViewById(R.id.key_list);
		mKeyAdapter = new KeyListAdapter(this, R.layout.key_item, mIds);
		mKeyList.setAdapter(mKeyAdapter);
	}
}
