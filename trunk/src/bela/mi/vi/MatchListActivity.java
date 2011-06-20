package bela.mi.vi;

import bela.mi.vi.data.Data;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.graphics.*;

/**
 * This activity handles list of matches.
 * It can initiate a new match activity and remove single or all matches.
 *  
 * @author Damir Mihaljinec
 */
public class MatchListActivity extends ListActivity{
	
	private Data mData;
	private Cursor mMatches;
	private Integer mRemoveMatchId;
	private static final Integer mHeader = 1;
	private static final Integer LIST_ITEM_LIMIT = 10;
	private Integer mListItemLimit;
	private Integer mTotalMatchCount;
	private View mFooter;
	private static final int NEW_MATCH = 1;
	
	public void onAttachedToWindow() {
	
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		mData = new Data(this);
		
		setTitle(getResources().getString(R.string.match_list_activity_title));
		
		// Add header
		View header = (View)getLayoutInflater().inflate(R.layout.list_header, null);
		TextView headerText = (TextView) header.findViewById(R.id.header);;
		headerText.setText(getResources().getString(R.string.new_match));
		getListView().addHeaderView(header);
		
		// Add footer
		mFooter = (View)getLayoutInflater().inflate(R.layout.match_list_row, null);
		TextView footerText = (TextView) mFooter.findViewById(R.id.score);
		footerText.setText(getResources().getString(R.string.list_footer));
		getListView().addFooterView(mFooter);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mListItemLimit = Integer.valueOf(prefs.getString("matchesListPref", LIST_ITEM_LIMIT.toString()));
		if (mListItemLimit < 1)
			mListItemLimit = LIST_ITEM_LIMIT;
		
		// Add cursor adapter
		addCursorAdapter(mListItemLimit);
		registerForContextMenu(getListView());
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Integer listItemLimit = Integer.valueOf(prefs.getString("matchesListPref", LIST_ITEM_LIMIT.toString()));
		if (listItemLimit < 1)
			listItemLimit = LIST_ITEM_LIMIT;
		if (mListItemLimit != listItemLimit){
			mListItemLimit = listItemLimit;
			addCursorAdapter(mListItemLimit);
		}
		mMatches.requery();
		mTotalMatchCount = mData.getMatchesCursor(null).getCount();
		showFooter();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		
		// Header
		if (position == 0) {
			newMatch();
			return;
		}
		
		// Footer
		if (position > mMatches.getCount()) {
			addCursorAdapter(mMatches.getCount() + mListItemLimit);
			showFooter();
			return;
		}
		
		// Cursor Adapter
		Cursor cursor = (Cursor) getListAdapter().getItem(position - mHeader);
		int _id = cursor.getInt(cursor.getColumnIndex(Data.MATCHES_ID));
		Intent matchIntent = new Intent(MatchListActivity.this, MatchActivity.class);
		matchIntent.putExtra(MatchActivity.MATCH_ID, _id);
    	startActivity(matchIntent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_match_list_activity, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	    case R.id.list_of_players:
	    	Intent managePlayersIntent = new Intent(MatchListActivity.this, PlayerListActivity.class);
	    	startActivity(managePlayersIntent);
	    	return true;
	    case R.id.new_match:
	    	newMatch();
	        return true;
	    case R.id.remove_all_matches:
	    	confirmDeleteAllDialog();
	    	return true;
	    case R.id.help:
	    	HelpDialog help = new HelpDialog(this, getResources().getString(R.string.match_list_activity_help_message));
	    	help.show();
	    	return true;
	    case R.id.settings:
	    	Intent settingsIntent = new Intent(MatchListActivity.this, SettingsActivity.class);
	    	startActivity(settingsIntent);
	    	return true;
	    case R.id.about:
	    	AboutDialog about = new AboutDialog(this);
	    	about.show();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		
		// Header
		if (info.position == 0)
			// do nothing
			return;
		
		// Cursor Adapter
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_match_list_activity, menu);
		Cursor cursor = (Cursor)this.getListAdapter().getItem(info.position - mHeader);
		String match = cursor.getString(cursor.getColumnIndex(Data.MATCHES_DATE))
					 + " " + cursor.getString(cursor.getColumnIndex(Data.MATCHES_TIME));
		menu.setHeaderTitle(match);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.remove_match:
			Cursor cursor = (Cursor) getListAdapter().getItem(info.position - mHeader);
			mRemoveMatchId = cursor.getInt(cursor.getColumnIndex(Data.MATCHES_ID));
			confirmDeleteDialog();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case NEW_MATCH:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				if (extras == null)
					return;
				int matchId = extras.getInt(NewMatchActivity.MATCH_ID);
				Intent matchIntent = new Intent(MatchListActivity.this, MatchActivity.class);
				matchIntent.putExtra(MatchActivity.MATCH_ID, matchId);
		    	startActivity(matchIntent);
			}
			break;
		default:		
		}
	}
	
	public void confirmDeleteAllDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(this.getResources().getString(R.string.match_delete_all_conformation));
		builder.setPositiveButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mData.removeAllMatches();
				mMatches.requery();
				mTotalMatchCount = 0;
				showFooter();
			}
		});
		builder.setNegativeButton(this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.setTitle(R.string.remove_all_matches);
		alert.show();
	}
	
	public void confirmDeleteDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(getResources().getString(R.string.match_delete_conformation));
		builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mData.removeMatch(mRemoveMatchId);
				mMatches.requery();
				mTotalMatchCount--;
				showFooter();
			}
		});
		builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.setTitle(R.string.remove_match);
		alert.show();
	}
	
	public void notEnoughPlayersDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(getResources().getString(R.string.not_enough_players));
		builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) { }
		});
		AlertDialog alert = builder.create();
		alert.setTitle(R.string.warning);
		alert.show();
	}
	
	private void newMatch() {
		
		if (mData.getPlayersCursor().getCount() < 4) {
			notEnoughPlayersDialog();
		}
		else {
			Intent newMatchIntent = new Intent(MatchListActivity.this, NewMatchActivity.class);
			startActivityForResult(newMatchIntent, NEW_MATCH);
		}
	}
	
	private class MatchListViewBinder implements SimpleCursorAdapter.ViewBinder {
		
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			
			TextView textView = (TextView) view;
			switch(textView.getId()) {
			case R.id.date:
				textView.setText(cursor.getString(cursor.getColumnIndex(Data.MATCHES_DATE)));
				return true;
			case R.id.time:
				textView.setText(cursor.getString(cursor.getColumnIndex(Data.MATCHES_TIME)));
				return true;
			case R.id.score:
				String sets = Integer.toString(cursor.getInt(cursor.getColumnIndex(Data.TEAM1_SETS))) + " : " +
							  Integer.toString(cursor.getInt(cursor.getColumnIndex(Data.TEAM2_SETS)));
				textView.setText(sets);
				return true;
			case R.id.players:
				String players = cursor.getString(cursor.getColumnIndex(Data.TEAM1_PLAYER1)) + "/" + cursor.getString(cursor.getColumnIndex(Data.TEAM1_PLAYER2)) + " vs " +
						  		 cursor.getString(cursor.getColumnIndex(Data.TEAM2_PLAYER1)) + "/" + cursor.getString(cursor.getColumnIndex(Data.TEAM2_PLAYER2));
				textView.setText(players);
				return true;
			default:
				return false;
			}
		}
	}
	
	public void addCursorAdapter(Integer limit) {
		
		mMatches = mData.getMatchesCursor(limit);
		startManagingCursor(mMatches);
		String[] from = new String[] { Data.MATCHES_DATE, Data.MATCHES_TIME, Data.TEAM1_SETS, Data.TEAM2_SETS,
									   Data.TEAM1_PLAYER1, Data.TEAM1_PLAYER2, Data.TEAM2_PLAYER1, Data.TEAM2_PLAYER2 };
		int[] to = new int[] { R.id.date, R.id.time, R.id.score, R.id.players };
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.match_list_row, mMatches, from, to);
		adapter.setViewBinder(new MatchListViewBinder());
		setListAdapter(adapter);
	}
	
	private void showFooter() {
	
		if (mMatches.getCount() < mTotalMatchCount) {
			if (getListView().getFooterViewsCount() == 0)
				getListView().addFooterView(mFooter);
		}
		else {
			if (getListView().getFooterViewsCount() > 0)
				getListView().removeFooterView(mFooter);
		}
	}
}
