package bela.mi.vi;

import bela.mi.vi.data.MatchData;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * This activity handles list of games.
 * Based on intents input it's possible to delete games from a list.
 *  
 * @author Damir Mihaljinec
 */
public class GameActivity extends Activity {
	
	private MatchData mMatchData;
	private int mMatchId = 0;
	private int mSetId = 0;
	private GameList mGameList;
	
	public static final String MATCH_ID = "match_id";
	public static final String SET_ID = "set_id";
	public static final String EDITABLE = "editable";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        Bundle extras = getIntent().getExtras();
		if (extras == null)
			return;
		mMatchId = extras.getInt(MATCH_ID);
		mSetId = extras.getInt(SET_ID);
		boolean editable = extras.getBoolean(EDITABLE);
		mMatchData = new MatchData(this, mMatchId);
		
		mGameList = new GameList(this, mMatchData, mMatchId, mSetId, false, editable);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_game_activity, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	    case R.id.help:
	    	HelpDialog help = new HelpDialog(this, getResources().getString(R.string.game_activity_help_message));
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
}
