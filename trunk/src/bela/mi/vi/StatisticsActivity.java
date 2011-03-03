package bela.mi.vi;

import bela.mi.vi.data.MatchData;
import android.app.Activity;
import android.os.Bundle;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.*;

public class StatisticsActivity extends Activity {

	private MatchData mMatchData;
	private Integer mMatchId;
	private Cursor mMatchStatistics;
	private Integer mGamesCount;
	
	public static final String MATCH_ID = "match_id";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_activity);
        Bundle extras = getIntent().getExtras();
		if (extras == null)
			return;
		mMatchId = extras.getInt(MATCH_ID);
		mMatchData = new MatchData(this, mMatchId);
        mMatchStatistics = mMatchData.getMatchStatistics();
        mMatchStatistics.moveToFirst();
        
        TextView text;
        text = (TextView) findViewById(R.id.team1_sets);
        text.setText(mMatchData.getWinnerCount(MatchData.TEAM1).toString());
        text = (TextView) findViewById(R.id.team2_sets);
        text.setText(mMatchData.getWinnerCount(MatchData.TEAM2).toString());
        
        text = (TextView) findViewById(R.id.team1_points);
        text.setText(Integer.toString(mMatchStatistics.getInt(mMatchStatistics.getColumnIndex(MatchData.GAMES_TEAM1_POINTS))));
        text = (TextView) findViewById(R.id.team2_points);
        text.setText(Integer.toString(mMatchStatistics.getInt(mMatchStatistics.getColumnIndex(MatchData.GAMES_TEAM2_POINTS))));
        
        text = (TextView) findViewById(R.id.team1_declarations);
        text.setText(Integer.toString(mMatchStatistics.getInt(mMatchStatistics.getColumnIndex(MatchData.GAMES_TEAM1_DECLARATIONS))));
        text = (TextView) findViewById(R.id.team2_declarations);
        text.setText(Integer.toString(mMatchStatistics.getInt(mMatchStatistics.getColumnIndex(MatchData.GAMES_TEAM2_DECLARATIONS))));
        
        mGamesCount = mMatchStatistics.getInt(mMatchStatistics.getColumnIndex(MatchData.GAMES_COUNT));
        
        text = (TextView) findViewById(R.id.team1_all_tricks);
        text.setText(mMatchData.getMatchAllTricks(MatchData.TEAM1).toString());
        text = (TextView) findViewById(R.id.team2_all_tricks);
        text.setText(mMatchData.getMatchAllTricks(MatchData.TEAM2).toString());
        
        text = (TextView) findViewById(R.id.team1_chosen_trump);
        text.setText(getChosenTrump(MatchData.TEAM1));
        text = (TextView) findViewById(R.id.team2_chosen_trump);
        text.setText(getChosenTrump(MatchData.TEAM2));
        
        text = (TextView) findViewById(R.id.team1_passed_games);
        text.setText(getPassedGames(MatchData.TEAM1));
        text = (TextView) findViewById(R.id.team2_passed_games);
        text.setText(getPassedGames(MatchData.TEAM2));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_statistics_activity, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	    case R.id.help:
	    	HelpDialog help = new HelpDialog(this, getResources().getString(R.string.statistics_activity_help_message));
	    	help.show();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private String getChosenTrump(Integer team) {
		
        Integer chosenTrump = mMatchData.getMatchPassedGames(team) + mMatchData.getMatchFallenGames(team);
        Double chosenTrumpPercentage = 0.0;
        if (mGamesCount != 0)
        	chosenTrumpPercentage = ((double)chosenTrump / (double)mGamesCount) * 100;
        DecimalFormat decimalFormat = new DecimalFormat("0");
        return decimalFormat.format(chosenTrumpPercentage) + "% (" + chosenTrump.toString() + "/" + mGamesCount.toString() + ")";
	}
	
	private String getPassedGames(Integer team) {
		
		Integer passedGames = mMatchData.getMatchPassedGames(team);
        Integer chosenTrump = passedGames + mMatchData.getMatchFallenGames(team);
        Double passedGamesPercentage = 0.0;
        if (chosenTrump != 0)
        	passedGamesPercentage = ((double)passedGames / (double)chosenTrump) * 100;
        DecimalFormat decimalFormat = new DecimalFormat("0");
        return decimalFormat.format(passedGamesPercentage) + "% (" + passedGames.toString() + "/" + chosenTrump.toString() + ")";
	}
}
