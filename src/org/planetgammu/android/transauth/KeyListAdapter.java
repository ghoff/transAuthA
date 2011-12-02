package org.planetgammu.android.transauth;

import android.content.Context;
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
		View row;

		LayoutInflater inflater = mContext.getLayoutInflater();
		String currentKey = getItem(position);
		if (currentKey == null)
			return null;
		row = inflater.inflate(R.layout.key_item, null);
		TextView idView = (TextView) row.findViewById(R.id.keyId);
		Button delButton = (Button) row.findViewById(R.id.keyDelete);
		ButtonClickListener bcl = new ButtonClickListener(row);
		delButton.setOnClickListener(bcl);
		idView.setText(currentKey);
	 
		return row;
	}
	
	public class ButtonClickListener implements OnClickListener {
		private View mRow;
		
		public ButtonClickListener(View row) {
			mRow = row;
		}

		@Override
		public void onClick(View v)
		{			
			TextView idView = (TextView) mRow.findViewById(R.id.keyId);
			String sId = (String) idView.getText();
			KeyData.delete(Integer.parseInt(sId));
		}
	}
}
