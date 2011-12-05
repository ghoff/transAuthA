package org.planetgammu.android.transauth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Displays the list of key IDs.
 */
public class KeyListAdapter extends ArrayAdapter<String> {
	private KeyManagementActivity mContext;

	public KeyListAdapter(Context context, int userRowId, String[] items) {
		super(context, userRowId, items);
		mContext = (KeyManagementActivity) context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = mContext.getLayoutInflater();
		String currentKey = getItem(position);
		if (currentKey == null)
			return null;
		View row = inflater.inflate(R.layout.key_item, null);
		TextView idView = (TextView) row.findViewById(R.id.keyId);
		Button delButton = (Button) row.findViewById(R.id.keyDelete);
		ButtonClickListener bcl = new ButtonClickListener(mContext, row);
		delButton.setOnClickListener(bcl);
		idView.setText(currentKey);

		return row;
	}

	private class ButtonClickListener implements OnClickListener {
		private View mRow;
		private Context mContext;

		public ButtonClickListener(Context context, View row) {
			mRow = row;
			mContext = context;
		}

		public void onClick(View v) {
			TextView idView = (TextView) mRow.findViewById(R.id.keyId);
			final String sId = (String) idView.getText();
			new AlertDialog.Builder(mContext)
				.setTitle(R.string.delete_message)
				.setMessage(sId)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							KeyData.delete(Integer.parseInt(sId));
							((KeyManagementActivity) mContext).refreshList();
						}
					})
				.setNegativeButton(R.string.cancel, null)
				.show();
		}
	}
}
