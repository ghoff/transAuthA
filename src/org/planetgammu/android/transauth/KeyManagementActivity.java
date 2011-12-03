package org.planetgammu.android.transauth;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class KeyManagementActivity extends Activity implements OnClickListener {

	private ListView mKeyList;
	private KeyListAdapter mKeyAdapter;
	private Button buttonAddKey;
	private Button buttonDone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.key_configuration);
		mKeyList = (ListView) findViewById(R.id.keyList);
		buttonAddKey = (Button) findViewById(R.id.addKey);
		buttonAddKey.setOnClickListener(this);
		buttonDone = (Button) findViewById(R.id.doneKey);
		buttonDone.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshList();
	}

	public void refreshList() {
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

		mKeyAdapter = new KeyListAdapter(this, R.layout.key_item, mIds);
		mKeyList.setAdapter(mKeyAdapter);
	}

	public void onClick(View view) {
		if (view == buttonAddKey)
		{
			startActivity(new Intent(this, KeyAddActivity.class));
		} else if (view == buttonDone) {
			finish();
		}
		
	}
}
