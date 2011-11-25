package org.planetgammu.android.transauth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;

public class AndroidTransauthActivity extends Activity implements OnClickListener, 
	OnKeyListener, DialogInterface.OnClickListener {

	private Button buttonValidate;
	private Button buttonGetBarcode;
	private Button buttonGetDemo;
	private EditText editCode;
	private boolean codeVerified;
	private static final int SCAN_REQUEST = 31338;
	private static final String ZXING_MARKET = 
		"market://search?q=pname:com.google.zxing.client.android";
	private static final String ZXING_DIRECT = 
		"http://zxing.googlecode.com/files/BarcodeScanner3.72.apk";

	/* Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		buttonValidate = (Button)findViewById(R.id.buttonValidate);
		buttonGetBarcode = (Button)findViewById(R.id.buttonGetBarcode);
		buttonGetDemo = (Button)findViewById(R.id.buttonGetDemo);
		buttonValidate.setOnClickListener(this);
		buttonGetBarcode.setOnClickListener(this);
		buttonGetDemo.setOnClickListener(this);
		editCode = (EditText)findViewById(R.id.editCode);
		editCode.setOnKeyListener(this);
		codeVerified = false;
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

		if (codeVerified == true) return;
		data = editCode.getText().toString();
		try {
			result = Decode.doit(data, Decode.demoKey);
			data = String.format(getString(R.string.stringResults),
					result[0],(float)(result[1])/100,result[2]);
			codeVerified = true;
		}
		catch(Exception e) {
			data = e.getMessage();
		}
		editCode.setText(data);
	}
	public void onClick(View view) {
		if (view == buttonValidate) {
			validateCode();
		} else if (view == buttonGetDemo) {
			editCode.setText(Decode.demoMessage);
			codeVerified = false;
		} else if (view == buttonGetBarcode) {
			Intent intentScan = new Intent("com.google.zxing.client.android.SCAN");
			intentScan.putExtra("SCAN_MODE", "QR_CODE_MODE");
			intentScan.putExtra("SAVE_HISTORY", false);
			try { startActivityForResult(intentScan, SCAN_REQUEST); }
			catch (ActivityNotFoundException e) { showDownloadDialog(); }
			codeVerified = false;
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == SCAN_REQUEST && resultCode == Activity.RESULT_OK) {
			String contents = intent.getStringExtra("SCAN_RESULT");
			//parseSecret(Uri.parse(contents));
			editCode.setText(contents);
		}
	}
	/**
	 * Prompt to download ZXing from Market. If Market app is not installed, such 
	 * as on a development phone, open the HTTPS URI for the ZXing apk.
	 */
	private void showDownloadDialog() {
		new AlertDialog.Builder(this)
			.setTitle(R.string.install_dialog_title)
			.setMessage(R.string.install_dialog_message)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(R.string.install_button,	this)
			.setNegativeButton(R.string.cancel, null)
			.show();
		}
	public void onClick(DialogInterface dialog, int whichButton) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ZXING_MARKET));
		try { startActivity(intent); }
		catch (ActivityNotFoundException e) { // if no Market app
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ZXING_DIRECT));
			startActivity(intent);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuItem:
			startActivity(new Intent(this, PrefsActivity.class));
			break;
		}
		return true;
	}
}
