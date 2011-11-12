package bela.mi.vi;

import bela.mi.vi.data.MatchData;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity handles match details.
 * It uses GameList to manage total score and sets.
 *  
 * @author Damir Mihaljinec
 */
public class MatchActivity extends Activity {
	
	private Integer mMatchId;
	private MatchData mMatchData;
	private GameList mGameList;
	private String mDifference;
	private Integer mActiveSet;
	
	public static final String MATCH_ID = "match_id";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_activity);
        Bundle extras = getIntent().getExtras();
		if (extras == null)
			return;
		mMatchId = extras.getInt(MATCH_ID);
		mMatchData = new MatchData(this, mMatchId);
		mGameList = new GameList(this, mMatchData, mMatchId, null, true, true);
		mGameList.setOnDeleteListItemListener(new GameList.OnDeleteListItemListener() {
			
			@Override
			public void onDeleteListItem() {
				
				setSetsResult();
				setGamesResult();
			}
		});
		mDifference = getResources().getString(R.string.difference);
		setGamesResult();
		setSetsResult();
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
		if (mActiveSet != mMatchData.getActiveSet()){
			mActiveSet = mMatchData.getActiveSet();
			mGameList.reset();
		}
		setSetsResult();
		setGamesResult();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_match_activity, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	    case R.id.list_of_sets:
	    	Intent setsIntent = new Intent(MatchActivity.this, SetListActivity.class);
	    	setsIntent.putExtra(SetListActivity.MATCH_ID, mMatchId);
	    	startActivity(setsIntent);
	        return true;
	    case R.id.new_game:
	    	mGameList.newGame();
	    	return true;
	    case R.id.match_statistics:
	    	Intent statisticsIntent = new Intent(MatchActivity.this, StatisticsActivity.class);
	    	statisticsIntent.putExtra(StatisticsActivity.MATCH_ID, mMatchId);
	    	startActivity(statisticsIntent);
	    	return true;
	    case R.id.help:
	    	HelpDialog help = new HelpDialog(this, getResources().getString(R.string.match_activity_help_message));
	    	help.show();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		boolean returnValue = mGameList.onContextItemSelected(item);
		if (returnValue == false)
			return super.onContextItemSelected(item);
		return returnValue;
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
		case GameList.NEW_GAME:
		case GameList.EDIT_GAME:
			if (resultCode == Activity.RESULT_OK) {
				mGameList.resultOk();
				if (mMatchData.getActiveSet() == null) {
					mGameList.reset();
					String score = mMatchData.getSetPoints(mActiveSet, MatchData.TEAM1).toString() + " - " +
								   mMatchData.getSetPoints(mActiveSet, MatchData.TEAM2).toString();
					//Toast toast = Toast.makeText(this, score, Toast.LENGTH_SHORT);
					Toast toast = Toast.makeText(this, score, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					toast.show();
				}
				mActiveSet = mMatchData.getActiveSet();
			}
			else if (resultCode == Activity.RESULT_CANCELED) {
				mGameList.resultCanceled();
			}
			break;
		default:
		}
	}
	
	private void setSetsResult() {
		
		Integer team1Sets = mMatchData.getWinnerCount(MatchData.TEAM1);
		Integer team2Sets = mMatchData.getWinnerCount(MatchData.TEAM2);
		TextView setsResult = (TextView) findViewById(R.id.score);
		setsResult.setText(team1Sets.toString() + " - " + team2Sets.toString());
	}
	
	private void setGamesResult() {
		
		Integer team1 = mMatchData.getSetPoints(MatchData.TEAM1);
		Integer team2 = mMatchData.getSetPoints(MatchData.TEAM2);
		TextView gamesResult = (TextView) findViewById(R.id.set_score);
		gamesResult.setText(team1.toString() + " - " + team2.toString());
		TextView difference = (TextView) findViewById(R.id.difference);
		difference.setText(mDifference + ": " + Integer.toString(Math.abs(team1 - team2)));
	}
}