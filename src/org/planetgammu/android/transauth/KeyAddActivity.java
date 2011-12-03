package org.planetgammu.android.transauth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class KeyAddActivity extends Activity implements OnClickListener {

	private EditText mIdEntry;
	private EditText mKeyEntry;
	private Button mSubmitButton;
	private Button mCancelButton;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_key);

		mIdEntry = (EditText) findViewById(R.id.id_value);
		mKeyEntry = (EditText) findViewById(R.id.key_value);
		mSubmitButton = (Button) findViewById(R.id.submit_button);
		mCancelButton = (Button) findViewById(R.id.cancel_button);
		
		mSubmitButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);
	}

	public void onClick(View view) {
		if (view == mSubmitButton) {
			String id = mIdEntry.getText().toString();
			String key = mKeyEntry.getText().toString();
			finish();
			KeyData.insert(Integer.parseInt(id), key);
		} else if (view == mCancelButton) {
			finish();
		}
	}
}
