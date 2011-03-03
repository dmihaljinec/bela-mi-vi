package bela.mi.vi;

import java.text.DecimalFormat;

import bela.mi.vi.data.Data;
import android.app.ListActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.content.DialogInterface;
import android.text.InputType;

public class PlayerListActivity extends ListActivity {

	private Data mData;
	private Cursor mPlayers;
	private Integer mRemovePlayerId;
	private Integer mHeader = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		mData = new Data(this);
		
		// Add header
		View header = (View)getLayoutInflater().inflate(R.layout.list_header, null);
		TextView headerText = (TextView) header.findViewById(R.id.header);;
		headerText.setText(getResources().getString(R.string.new_player));
		getListView().addHeaderView(header);
		
		//mPlayers = mData.getPlayersCursor();
		mPlayers = mData.getPlayersCursorEx();
		startManagingCursor(mPlayers);
		String[] from = new String[] { Data.PLAYERS_ID, Data.PLAYERS_NAME };
		int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_two_line_row, mPlayers, from, to);
		adapter.setViewBinder(new PlayerListViewBinder(getResources().getString(R.string.winning_rate)));
		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}
	
	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		
		super.onListItemClick(list, view, position, id);
		
		// Header
		if (position == 0) {
			newPlayerDialog();
			return;
		}
		
		Cursor cursor = (Cursor)this.getListAdapter().getItem(position - mHeader);
		cursor.getInt(cursor.getColumnIndex(Data.PLAYERS_ID));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_player_list_activity, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	    case R.id.new_player:
	        newPlayerDialog();
	        return true;
	    case R.id.remove_all_players:
	    	confirmDeleteAllDialog();
	    	return true;
	    case R.id.help:
	    	HelpDialog help = new HelpDialog(this, getResources().getString(R.string.player_list_activity_help_message));
	    	help.show();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		if (info.position == 0)
			return;
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_player_list_activity, menu);
		Cursor cursor = (Cursor) getListAdapter().getItem(info.position - mHeader);
		String player = cursor.getString(cursor.getColumnIndex(Data.PLAYERS_NAME));
		menu.setHeaderTitle(player);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.remove_player:
			Cursor cursor = (Cursor) getListAdapter().getItem(info.position - mHeader);
			mRemovePlayerId = cursor.getInt(cursor.getColumnIndex(Data.PLAYERS_ID));
			confirmDeleteDialog();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	public void newPlayerDialog() {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		input.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		alert.setTitle(getResources().getString(R.string.enter_player_name));
		alert.setIcon(android.R.drawable.ic_menu_add);
		alert.setView(input);
		alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String name = input.getText().toString().trim();
				mData.addPlayer(name);
				mPlayers.requery();
			}
		}); 
		alert.setNegativeButton(this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();
	}
	
	public void confirmDeleteAllDialog() {
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(this.getResources().getString(R.string.remove_all_players));
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.setMessage(this.getResources().getString(R.string.player_delete_all_conformation));
		alert.setPositiveButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mData.removeAllPlayers();
				mPlayers.requery();
			}
		}); 
		alert.setNegativeButton(this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();
	}
	
	public void confirmDeleteDialog() {
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(this.getResources().getString(R.string.remove_player));
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.setMessage(this.getResources().getString(R.string.player_delete_conformation));
		alert.setPositiveButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mData.removePlayer(mRemovePlayerId);
				mPlayers.requery();
			}
		}); 
		alert.setNegativeButton(this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();
	}
	
	private class PlayerListViewBinder implements SimpleCursorAdapter.ViewBinder {
		
		private String mWinningRate;
		
		public PlayerListViewBinder(String winningRate) {
			
			mWinningRate = winningRate;
		}
		
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			
			TextView tv = (TextView) view;
			// TODO handle cursor indices more error prone
			Integer setsCount, setsWon;
			switch(tv.getId()) {
			case android.R.id.text1:
				tv.setText(cursor.getString(cursor.getColumnIndex(Data.PLAYERS_NAME)));
				return true;
			case android.R.id.text2:
				setsCount = cursor.getInt(cursor.getColumnIndex(Data.SETS_COUNT));
				setsWon = cursor.getInt(cursor.getColumnIndex(Data.SETS_WON));
				Double winningRate = 0.0;
		        if (setsCount != 0)
		        	winningRate = ((double)setsWon / (double)setsCount) * 100;
		        DecimalFormat decimalFormat = new DecimalFormat("0");
				String rate = mWinningRate + ": " + decimalFormat.format(winningRate) + "% (" + setsWon.toString() + "/" + setsCount.toString() + ")";
				tv.setText(rate);
				return true;
			default:
				return false;
			}
		}
	}
}
