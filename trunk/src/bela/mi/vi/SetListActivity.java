package bela.mi.vi;

import bela.mi.vi.data.MatchData;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This activity handles list of sets.
 *  
 * @author Damir Mihaljinec
 */
public class SetListActivity extends Activity {
	
	private MatchData mMatchData;
	private Cursor mSets;
	private ListView mSetList;
	private Integer mMatchId;
	
	public static final String MATCH_ID = "match_id";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        Bundle extras = getIntent().getExtras();
        if (extras == null)
			return;
		mMatchId = extras.getInt(MATCH_ID);
		mMatchData = new MatchData(this, mMatchId);
		
		mSetList = (ListView) findViewById(R.id.game_list);
		
		mSetList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) mSetList.getAdapter().getItem(position);
				int _id = cursor.getInt(cursor.getColumnIndex(MatchData.SETS_ID));
				Intent gameIntent = new Intent(SetListActivity.this, GameActivity.class);
				gameIntent.putExtra(GameActivity.MATCH_ID, mMatchId);
				gameIntent.putExtra(GameActivity.SET_ID, _id);
				if ((position + 1) == mSets.getCount())
					gameIntent.putExtra(GameActivity.EDITABLE, true);
		    	startActivity(gameIntent);
			}
		});
		
		mSets = mMatchData.getSetsCursor();
		startManagingCursor(mSets);
		String[] from = new String[] { MatchData.GAMES_TEAM1_POINTS, MatchData.GAMES_TEAM2_POINTS };
		int[] to = new int[] { R.id.points1, R.id.points2 };
		mSetList.setAdapter(new SimpleCursorAdapter(this, R.layout.game_list_row, mSets, from, to));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_set_list_activity, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	    case R.id.help:
	    	HelpDialog help = new HelpDialog(this, getResources().getString(R.string.set_list_activity_help_message));
	    	help.show();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
