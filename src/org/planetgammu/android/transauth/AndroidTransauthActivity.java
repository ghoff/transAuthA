package org.planetgammu.android.transauth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;

public class AndroidTransauthActivity extends Activity implements OnClickListener, 
	OnKeyListener {

	private Button buttonValidate;
	private Button buttonGetBarcode;
	private Button buttonGetDemo;
	private Button buttonKeys;
	private EditText editCode;
	private boolean codeVerified;
	private static final int SCAN_REQUEST = 31338;
	private static final String ZXING_MARKET = 
		"market://search?q=pname:com.google.zxing.client.android";
	private static final String ZXING_DIRECT = 
		"http://zxing.googlecode.com/files/BarcodeScanner3.72.apk";

	/* Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		buttonValidate = (Button)findViewById(R.id.buttonValidate);
		buttonGetBarcode = (Button)findViewById(R.id.buttonGetBarcode);
		buttonGetDemo = (Button)findViewById(R.id.buttonGetDemo);
		buttonKeys = (Button)findViewById(R.id.buttonKeys);
		buttonValidate.setOnClickListener(this);
		buttonGetBarcode.setOnClickListener(this);
		buttonGetDemo.setOnClickListener(this);
		buttonKeys.setOnClickListener(this);
		editCode = (EditText)findViewById(R.id.editCode);
		editCode.setOnKeyListener(this);
		codeVerified = false;
		KeyData.initialize(this);
	}
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_ENTER) {
				validateCode();
				return true;
			} else {
				codeVerified = false;
			}
		}
		return false;
	}
	private void validateCode() {
		long[] result;
		String data;
		Cursor cursor = null;

		if (codeVerified == true) return;
		data = editCode.getText().toString();
		try {
			byte[] messageBinary = Base32String.decode(data);
			Authutil auth = new Authutil();
			String keyId = auth.parseMessage(messageBinary);
			
			cursor = KeyData.getKeyById(Integer.parseInt(keyId));
			if (KeyData.cursorIsEmpty(cursor) && (cursor.getCount() != 1))
				throw new Exception(String.format("Key %s not loaded", keyId));
			
			cursor.moveToFirst();
			String key = cursor.getString(0);
			result = auth.decryptDecodeMessage(key);

			data = String.format(getString(R.string.stringResults),
					result[0],(float)(result[1])/100,result[2]);
			codeVerified = true;
		}
		catch(Exception e) {
			data = e.getMessage();
		} finally {
			KeyData.tryCloseCursor(cursor);
		}
		editCode.setText(data);
	}
	public void onClick(View view) {
		if (view == buttonValidate) {
			validateCode();
		} else if (view == buttonGetDemo) {
			//TODO remove demo button
			editCode.setText(Decode.demoMessage);
			if (!KeyData.keyExists(2))
				KeyData.insert(2, Decode.demoKey);
			codeVerified = false;
		} else if (view == buttonKeys) {
			startActivity(new Intent(this, KeyManagementActivity.class));
		} else if (view == buttonGetBarcode) {
			Intent intentScan = new Intent("com.google.zxing.client.android.SCAN");
			intentScan.putExtra("SCAN_MODE", "QR_CODE_MODE");
			intentScan.putExtra("SAVE_HISTORY", false);
			try { startActivityForResult(intentScan, SCAN_REQUEST); }
			catch (ActivityNotFoundException e) { showDialog(SCAN_REQUEST); }
			codeVerified = false;
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == SCAN_REQUEST && resultCode == Activity.RESULT_OK) {
			String contents = intent.getStringExtra("SCAN_RESULT");
			editCode.setText(contents);
		}
	}
	/**
	 * Prompt to download ZXing from Market. If Market app is not installed, such 
	 * as on a development phone, open the HTTPS URI for the ZXing apk.
	 * @return 
	 */
	protected Dialog onCreateDialog(int dialogNo) {
		if (dialogNo != SCAN_REQUEST)
			return null;
		Dialog dialog = new AlertDialog.Builder(this)
			.setTitle(R.string.install_dialog_title)
			.setMessage(R.string.install_dialog_message)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(R.string.install_button,	new DialogClick())
			.setNegativeButton(R.string.cancel, null)
			.create();
		return dialog;
		}
	
	class DialogClick implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int whichButton) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ZXING_MARKET));
			try { startActivity(intent); }
			catch (ActivityNotFoundException e) { // if no Market app
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ZXING_DIRECT));
				startActivity(intent);
			}
		}
	}
}
