package bela.mi.vi.data;

import android.content.Context;
import android.database.Cursor;

/**
 * This class handles match based database data.
 *  
 * @author Damir Mihaljinec
 */
public class MatchData extends Data {
	
	private Integer mMatchId;
	private Integer mCurrentSetId;
	
	private static final String connectedTables = Data.TABLE_MATCHES + " INNER JOIN " + Data.TABLE_SETS + " ON " + Data.TABLE_MATCHES + "." + Data.MATCHES_ID + "=" + Data.TABLE_SETS + "." + Data.SETS_MATCH + " INNER JOIN " + Data.TABLE_GAMES + " ON " + Data.TABLE_SETS + "." + Data.SETS_ID + "=" + Data.TABLE_GAMES + "." + Data.GAMES_SET;
	
	public MatchData(Context context, Integer matchId) {
		
		super(context);
		mMatchId = matchId;
	}
	
	public Cursor getSetsCursor() {
		
		// This query returns list of all sets where each row contains
		// set id, sum of team1 games in that set, sum of team2 games in that set
		String query = "SELECT " + Data.TABLE_SETS + "." + Data.SETS_ID + " AS " + Data.SETS_ID +
					   ", SUM(" + Data.TABLE_GAMES + "." + Data.GAMES_TEAM1_POINTS + ") AS " + Data.GAMES_TEAM1_POINTS +
					   ", SUM(" + Data.TABLE_GAMES + "." + Data.GAMES_TEAM2_POINTS + ") AS " + Data.GAMES_TEAM2_POINTS +
					   " FROM " + Data.TABLE_SETS + " INNER JOIN " + Data.TABLE_GAMES + " ON " + Data.TABLE_SETS +
					   "." + Data.SETS_ID + "=" + Data.TABLE_GAMES + "." + Data.GAMES_SET + " WHERE " + Data.TABLE_SETS + "." + Data.SETS_MATCH +
					   "=" + mMatchId.toString() + " GROUP BY " + Data.TABLE_SETS + "." + Data.SETS_ID;
		return super.rawQuery(query);
	}
	
	public Integer getSetPoints(Integer team) {
		
		mCurrentSetId = super.getActiveSet(mMatchId);
		if (mCurrentSetId == null)
			return 0;
		return super.getSetPoints(mCurrentSetId, team);
	}
	
	public Integer getSetPoints(Integer setId, Integer team) {
		
		return super.getSetPoints(setId, team);
	}
	
	public Integer getWinnerCount(Integer team) {
		
		return super.getWinnerCount(mMatchId, team);
	}
	
	public Integer getActiveSet() {
		
		return super.getActiveSet(mMatchId);
	}
	
	public Integer addGame(boolean allTricks, Integer team1Declarations, Integer team2Declarations, Integer team1Points, Integer team2Points) {
		
		mCurrentSetId = super.getActiveSet(mMatchId);
		if (mCurrentSetId == null)
			mCurrentSetId = super.addSet(mMatchId);
		Integer gameId = super.addGame(mCurrentSetId, allTricks, team1Declarations, team2Declarations, team1Points, team2Points);
		updateSetWinner();
		return gameId;
	}
	
	@Override
	public void removeGame(Integer gameId) {
		
		mCurrentSetId = super.getGameSet(gameId);
		super.removeGame(gameId);
		Cursor gamesCursor = getGamesCursor(mCurrentSetId);
		if (gamesCursor.getCount() == 0) {
			super.removeSet(mCurrentSetId);
			mCurrentSetId = null;
		}
		else {
			updateSetWinner();
		}
		gamesCursor.close();
	}
	
	@Override
	public void updateGame(Integer gameId, boolean allTricks, Integer team1Declarations, Integer team2Declarations, Integer team1Points, Integer team2Points) {
		super.updateGame(gameId, allTricks, team1Declarations, team2Declarations, team1Points, team2Points);
		mCurrentSetId = super.getGameSet(gameId);
		updateSetWinner();
	}
	
	public void removeAllGames() {
		
		mCurrentSetId = super.getActiveSet(mMatchId);
		if (mCurrentSetId != null) {
			super.removeAllGames(mCurrentSetId);
			super.removeSet(mCurrentSetId);
			mCurrentSetId = null;
		}
	}
	
	public Cursor getMatchStatistics() {
		
		final String query = "SELECT SUM(" + Data.GAMES_TEAM1_POINTS + ") AS " + Data.GAMES_TEAM1_POINTS +
			", SUM(" + Data.GAMES_TEAM2_POINTS + ") AS " + Data.GAMES_TEAM2_POINTS +
			", SUM(" + Data.GAMES_TEAM1_DECLARATIONS + ") AS " + Data.GAMES_TEAM1_DECLARATIONS +
			", SUM(" + Data.GAMES_TEAM2_DECLARATIONS + ") AS " + Data.GAMES_TEAM2_DECLARATIONS +
			", COUNT(*) AS " + Data.GAMES_COUNT + " FROM " + connectedTables + " WHERE matches._id=" + mMatchId.toString();
		return super.rawQuery(query);
	}
	
	public Integer getMatchAllTricks(Integer team) {
		
		String query = "SELECT COUNT(*) FROM " + connectedTables + " WHERE " + Data.TABLE_MATCHES +
					   "." + Data.MATCHES_ID + "=" + mMatchId.toString() + " AND " + Data.TABLE_GAMES +
					   "." + Data.GAMES_ALL_TRICKS + "=1 AND " + Data.TABLE_GAMES + ".";
		if (team == Data.TEAM1)
			query += Data.GAMES_TEAM1_POINTS + ">0";
		else if (team == Data.TEAM2)
			query += Data.GAMES_TEAM2_POINTS + ">0";
		Cursor cursor = super.rawQuery(query);
		cursor.moveToFirst();
		Integer matchAllTricks = cursor.getInt(0);
		cursor.close();
		return matchAllTricks;
	}
	
	public Integer getMatchPassedGames(Integer team) {
		
		String query = "SELECT COUNT(*) FROM " + connectedTables + " WHERE " + Data.TABLE_MATCHES +
					   "." + Data.MATCHES_ID + "=" + mMatchId.toString() + " AND ((" + Data.TABLE_GAMES +
					   ".";
		if (team == Data.TEAM1)
			query += Data.GAMES_TEAM1_POINTS + ">" + Data.TABLE_GAMES + "." + Data.GAMES_TEAM2_POINTS +
					 " AND " + Data.TABLE_GAMES + "." + Data.GAMES_TEAM2_POINTS + ">0) OR (" +
					 Data.TABLE_GAMES + "." + Data.GAMES_TEAM2_POINTS + "=0 AND " + Data.TABLE_GAMES +
					 "." + Data.GAMES_ALL_TRICKS + "=1))";
		else if (team == Data.TEAM2)
			query += Data.GAMES_TEAM2_POINTS + ">" + Data.TABLE_GAMES + "." + Data.GAMES_TEAM1_POINTS +
					 " AND " + Data.TABLE_GAMES + "." + Data.GAMES_TEAM1_POINTS + ">0) OR (" +
					 Data.TABLE_GAMES + "." + Data.GAMES_TEAM1_POINTS + "=0 AND " + Data.TABLE_GAMES +
					 "." + Data.GAMES_ALL_TRICKS + "=1))";
		Cursor cursor = super.rawQuery(query);
		cursor.moveToFirst();
		Integer matchPassedGames = cursor.getInt(0);
		cursor.close();
		return matchPassedGames;
	}
	
	public Integer getMatchFallenGames(Integer team) {
		
		String query = "SELECT COUNT(*) FROM " + connectedTables + " WHERE " + Data.TABLE_MATCHES +
					   "." + Data.MATCHES_ID + "=" + mMatchId.toString() + " AND " + Data.TABLE_GAMES +
					   "." + Data.GAMES_ALL_TRICKS + "=0 AND " + Data.TABLE_GAMES + ".";
		if (team == Data.TEAM1)
			query += Data.GAMES_TEAM1_POINTS + "=0";
		else if (team == Data.TEAM2)
			query += Data.GAMES_TEAM2_POINTS + "=0";
		Cursor cursor = super.rawQuery(query);
		cursor.moveToFirst();
		Integer matchFallenGames = cursor.getInt(0);
		cursor.close();
		return matchFallenGames;
	}
	
	private void updateSetWinner() {
		
		Integer matchLimit = super.getMatchLimit(mMatchId);
		Integer team1 = super.getSetPoints(mCurrentSetId, Data.TEAM1);
		Integer team2 = super.getSetPoints(mCurrentSetId, Data.TEAM2);
		if (team1 < matchLimit && team2 < matchLimit) {
			super.setWinner(mCurrentSetId, 0);
			return;
		}
		if (team1 >= matchLimit && team1 > team2) {
			super.setWinner(mCurrentSetId, Data.TEAM1);
			mCurrentSetId = null;
		}
		else if (team2 >= matchLimit && team2 > team1) {
			super.setWinner(mCurrentSetId, Data.TEAM2);
			mCurrentSetId = null;
		}
		else
			super.setWinner(mCurrentSetId, 0);
	}
}