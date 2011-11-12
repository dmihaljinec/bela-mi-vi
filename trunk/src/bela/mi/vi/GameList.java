package bela.mi.vi;

import bela.mi.vi.data.MatchData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnCreateContextMenuListener;

/**
 * This class handles game list view operations.
 * It can initiate a new game activity and delete selected game.
 *  
 * @author Damir Mihaljinec
 */
public class GameList {

	private boolean mHasHeader = false;
	private boolean mCanDelete = false;
	private Context mContext;
	private Activity mActivity;
	private MatchData mMatchData;
	private Integer mMatchId;
	private Integer mSetId;
	private Cursor mGames;
	private Integer mRemoveGameId;
	private ListView mGameList;
	private OnDeleteListItemListener mDeleteListItemListener;
	
	public final static int NEW_GAME = 1;
	public final static int EDIT_GAME = 2;
	private static final Integer HEADER_POSITION = 0;
	
	public GameList(Context context, MatchData matchData, Integer matchId, Integer setId, boolean addable, boolean deletable) {
		
		mContext = context;
		mHasHeader = addable;
		mCanDelete = deletable;
		mMatchData = matchData;
		mMatchId = matchId;
		mSetId = setId;
		mActivity = (Activity) mContext;
		mGameList = (ListView) mActivity.findViewById(R.id.game_list);
		
		setupListView();
	}

	public void setupListView() {
		
        LayoutInflater inflater = mActivity.getLayoutInflater();
        
        if (mHasHeader == true){
			View header = (View) inflater.inflate(R.layout.list_header, null);
			TextView headerText = (TextView) header.findViewById(R.id.header);
			headerText.setText(mContext.getResources().getString(R.string.new_game));
			mGameList.addHeaderView(header);
			mGameList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (position == HEADER_POSITION) {
						newGame();
					}
				}
			});
        }
			
		setListAdapter();
		
		if (mCanDelete == true) {
			mActivity.registerForContextMenu(mGameList);
			mGameList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
					AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
					if (mHasHeader == true && info.position == HEADER_POSITION)
						// do nothing
						return;
					// Cursor Adapter
					MenuInflater inflater = mActivity.getMenuInflater();
					inflater.inflate(R.menu.context_menu_game_list, menu);
					Cursor cursor = (Cursor) mGameList.getAdapter().getItem(info.position);
					String gameResult = cursor.getString(cursor.getColumnIndex(MatchData.GAMES_TEAM1_POINTS)) + " - " + cursor.getString(cursor.getColumnIndex(MatchData.GAMES_TEAM2_POINTS));
					menu.setHeaderTitle(gameResult);
				}		
			});
		}
	}
	
	public void setListAdapter() {
		
		Integer setId;
		if (mSetId == null)
			setId = mMatchData.getActiveSet();
		else
			setId = mSetId;
		if (setId != null) {
			mGames = mMatchData.getGamesCursor(setId);
			mActivity.startManagingCursor(mGames);
		}
		String[] from = new String[] { MatchData.GAMES_TEAM1_POINTS, MatchData.GAMES_TEAM2_POINTS,
									   MatchData.GAMES_TEAM1_DECLARATIONS, MatchData.GAMES_TEAM2_DECLARATIONS };
		int[] to = new int[] { R.id.points1, R.id.points2 };
		mGameList.setAdapter(new SimpleCursorAdapter(mContext, R.layout.game_list_row, mGames, from, to));
	}
	
	// Callback from Activity when Context Menu is selected 
	public boolean onContextItemSelected(MenuItem item) {
	
		if (mCanDelete == false)
			return false;
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Cursor cursor = (Cursor) mGameList.getAdapter().getItem(info.position);
		int gameId = cursor.getInt(cursor.getColumnIndex(MatchData.GAMES_ID));
		switch (item.getItemId()) {
		case R.id.remove_game:
			mRemoveGameId = gameId;
			confirmDeleteDialog();
			return true;
		case R.id.edit_game:
			editGame(gameId);
			return true;
			
		default:
			return false;
		}
	}
	
	public void requery() {
		
		if (mGames != null)
			mGames.requery();
	}
	
	public void reset() {
		
		mGames = null;
		setListAdapter();
	}
	
	public void resultOk() {
		
		if (mGames == null) {
			setListAdapter();
			return;
		}
		mGames.requery();
	}
	
	public void resultCanceled() {
		
	}
	
	public void newGame() {
		
		Intent gameIntent = new Intent(mActivity, NewGameActivity.class);
		gameIntent.putExtra(NewGameActivity.MATCH_ID, mMatchId);
		mActivity.startActivityForResult(gameIntent, NEW_GAME);
	}
	
	public void editGame(final int gameId) {
		Intent gameIntent = new Intent(mActivity, NewGameActivity.class);
		gameIntent.putExtra(NewGameActivity.MATCH_ID, mMatchId);
		gameIntent.putExtra(NewGameActivity.GAME_ID, gameId);
		mActivity.startActivityForResult(gameIntent, EDIT_GAME);
	}
	
	public void setOnDeleteListItemListener(OnDeleteListItemListener listener) {
		
		mDeleteListItemListener = listener;
	}
	
	private void confirmDeleteDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(mContext.getResources().getString(R.string.game_delete_conformation));
		builder.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mMatchData.removeGame(mRemoveGameId);
				mGames.requery();
				if (mGames.getCount() == 0)
					mGames = null;
				if (mDeleteListItemListener != null)
					mDeleteListItemListener.onDeleteListItem();
			}
		});
		builder.setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.setTitle(R.string.remove_game);
		alert.show();
	}
	
	public interface OnDeleteListItemListener {
		
		public void onDeleteListItem();
	}
}
