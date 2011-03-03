package bela.mi.vi;

import bela.mi.vi.data.Data;
import android.app.Activity;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.AdapterView;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.text.TextWatcher;
import android.text.Editable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewMatchActivity extends Activity implements OnClickListener {

	private Data mData;
	private Cursor mPlayers;
	private String mDate;
	private String mTime;
	private boolean mLimitOk = true;
	
	private Button mOkButton;
	private Button mCancelButton;
	private Spinner mPlayersSpinner[];
	
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_match_activity);
		
		mData = new Data(this);
		mPlayers = mData.getPlayersCursor();
		startManagingCursor(mPlayers);
		String[] from = new String[] { Data.PLAYERS_NAME };
		int[] to = new int[] { android.R.id.text1 };
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, mPlayers, from, to);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		PlayerOnItemSelectedListener playerListener = new PlayerOnItemSelectedListener();
		mPlayersSpinner = new Spinner[4];
		Integer ids[] = new Integer[] { R.id.team1_player1, R.id.team1_player2,
									    R.id.team2_player1, R.id.team2_player2 };
		for (int i = 0; i < 4; ++i) {
			mPlayersSpinner[i] = (Spinner) findViewById(ids[i]);
			mPlayersSpinner[i].setAdapter(adapter);
			mPlayersSpinner[i].setOnItemSelectedListener(playerListener);
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat (DATE_FORMAT);
		String date = sdf.format(Calendar.getInstance().getTime());
		mDate = date.substring(0, 10);
		mTime = date.substring(11, 16);
		TextView textView = (TextView) findViewById(R.id.date);
		textView.setText(mDate);
		textView = (TextView) findViewById(R.id.time);
		textView.setText(mTime);
		EditText limit = (EditText) findViewById(R.id.limit);
		limit.setText(Integer.toString(Data.SET_LIMIT));
		limit.addTextChangedListener(new LimitTextWatcher());
		mOkButton = (Button) findViewById(R.id.ok);
		mOkButton.setOnClickListener(this);
		mCancelButton = (Button) findViewById(R.id.cancel);
		mCancelButton.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_new_match_activity, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	    case R.id.help:
	    	HelpDialog help = new HelpDialog(this, getResources().getString(R.string.new_match_activity_help_message));
	    	help.show();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	public void onClick(View view) {
		
		if (view == mOkButton) {
			if (addMatch() == true) {
				setResult(Activity.RESULT_OK);
        		finish();
			}
		}
		else if (view == mCancelButton) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
	}
	
	private void enableOkButton() {
		
		if (mLimitOk == false) {
			mOkButton.setEnabled(false);
			return;
		}
		Map<String, Integer> players = new HashMap<String, Integer>();
		for (int i = 0; i < 4; ++i) {
			Cursor cursor = (Cursor) mPlayersSpinner[i].getSelectedItem();
			players.put(cursor.getString(cursor.getColumnIndex(Data.PLAYERS_NAME)), 1);
		}
		if (players.size() != 4) {
			mOkButton.setEnabled(false);
			return;
		}
		mOkButton.setEnabled(true);
	}

	private class PlayerOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			
			enableOkButton();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parentView) { }
	}

	private class LimitTextWatcher implements TextWatcher {
		
		public void afterTextChanged(Editable limit) {
			
			if (limit.toString().contentEquals("") == true)
				mLimitOk = false;
			else
				mLimitOk = true;
			enableOkButton();
		}
		
		public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        public void onTextChanged(CharSequence s, int start, int before, int count) { }
	}
	
	private boolean addMatch() {
		
		Integer[] spinners = new Integer[] { R.id.team1_player1, R.id.team1_player2,
											 R.id.team2_player1, R.id.team2_player2 };
		Integer[] ids = new Integer[4];
		Spinner spinner;
		Cursor cursor;
		for (int i = 0; i < 4; ++i) {
			spinner = (Spinner) findViewById(spinners[i]);
			cursor = (Cursor) spinner.getSelectedItem();
			ids[i] = cursor.getInt(cursor.getColumnIndex(Data.PLAYERS_ID));
		}
		EditText limit = (EditText) findViewById(R.id.limit);
    	Integer setLimit = Data.SET_LIMIT;
    	if (limit.getText().toString().equals("") == false) {
    		setLimit = Integer.valueOf(limit.getText().toString());
    	}
        mData.addMatch(mDate, mTime, ids[0], ids[1], ids[2], ids[3], setLimit);
        return true;
	}
}
