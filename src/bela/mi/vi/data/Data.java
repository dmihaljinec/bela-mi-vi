package bela.mi.vi.data;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;

/**
 * This class handles SQLite database.
 *  
 * @author Damir Mihaljinec
 */
public class Data {
	
	private BelaOpenHandler belaOpenHandler;
	private SQLiteStatement winnerCount;
	private SQLiteStatement activeSet;
	
	public static final int TEAM1 = 1;
	public static final int TEAM2 = 2;
	public static final int GAME_POINTS = 162;
	public static final int BELA_DECLARATION = 20;
	public static final int ALL_TRICKS = 90;
	public static final int SET_LIMIT = 1001;
	
	public static final String DB_NAME = "Bela.db";
	protected static final Integer DB_VERSION = 1;
	protected static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS";
	protected static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS";
	protected static final String SQL_PRAGMA_FOREIGN_KEYS = "PRAGMA foreign_keys=ON";
	
	protected static final String TABLE_PLAYERS = "players";
	protected static final String TABLE_MATCHES = "matches";
	protected static final String TABLE_SETS = "sets";
	protected static final String TABLE_GAMES = "games";
	
	public static final String PLAYERS_ID = "_id";
	public static final String PLAYERS_NAME = "name";
	public static final String MATCHES_ID = "_id";
	public static final String MATCHES_DATE = "date";
	public static final String MATCHES_TIME = "time";
	public static final String MATCHES_TEAM1_PLAYER1 = "team1_player1_id";
	public static final String MATCHES_TEAM1_PLAYER2 = "team1_player2_id";
	public static final String MATCHES_TEAM2_PLAYER1 = "team2_player1_id";
	public static final String MATCHES_TEAM2_PLAYER2 = "team2_player2_id";
	public static final String MATCHES_SET_LIMIT = "set_limit";
	public static final String SETS_ID = "_id";
	public static final String SETS_MATCH = "match_id";
	public static final String SETS_WINNING_TEAM = "winning_team";
	public static final String GAMES_ID = "_id";
	public static final String GAMES_SET = "set_id";
	public static final String GAMES_ALL_TRICKS = "all_tricks";
	public static final String GAMES_TEAM1_DECLARATIONS = "team1_declarations";
	public static final String GAMES_TEAM2_DECLARATIONS = "team2_declarations";
	public static final String GAMES_TEAM1_POINTS = "team1_points";
	public static final String GAMES_TEAM2_POINTS = "team2_points";
	
	protected static final String SQL_CREATE_TABLE_PLAYERS = SQL_CREATE_TABLE + " " + TABLE_PLAYERS + " (" + PLAYERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PLAYERS_NAME + " TEXT NOT NULL UNIQUE)";
	protected static final String SQL_CREATE_TABLE_MATCHES = SQL_CREATE_TABLE + " " + TABLE_MATCHES + " (" + MATCHES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MATCHES_DATE + " TEXT NOT NULL, " + MATCHES_TIME + " TEXT NOT NULL, " + MATCHES_TEAM1_PLAYER1 + " INTEGER NOT NULL, " + MATCHES_TEAM1_PLAYER2 + " INTEGER NOT NULL, " + MATCHES_TEAM2_PLAYER1 + " INTEGER NOT NULL, " + MATCHES_TEAM2_PLAYER2 + " INTEGER NOT NULL, " + MATCHES_SET_LIMIT + " INTEGER DEFAULT 1001, FOREIGN KEY(" + MATCHES_TEAM1_PLAYER1 + ") REFERENCES " + TABLE_PLAYERS + "(" + PLAYERS_ID + "), FOREIGN KEY(" + MATCHES_TEAM1_PLAYER2 + ") REFERENCES " + TABLE_PLAYERS + "(" + PLAYERS_ID + "), FOREIGN KEY(" + MATCHES_TEAM2_PLAYER1 + ") REFERENCES " + TABLE_PLAYERS + "(" + PLAYERS_ID + "), FOREIGN KEY(" + MATCHES_TEAM2_PLAYER2 + ") REFERENCES " + TABLE_PLAYERS + "(" + PLAYERS_ID + "))";
	protected static final String SQL_CREATE_TABLE_SETS = SQL_CREATE_TABLE + " " + TABLE_SETS + " (" + SETS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SETS_MATCH + " INTEGER NOT NULL REFERENCES " + TABLE_MATCHES + "(" + MATCHES_ID + ") ON DELETE CASCADE, " + SETS_WINNING_TEAM + " INTEGER DEFAULT 0, FOREIGN KEY(" + SETS_MATCH + ") REFERENCES " + TABLE_MATCHES + "(" + MATCHES_ID + "))";
	protected static final String SQL_CREATE_TABLE_GAMES = SQL_CREATE_TABLE + " " + TABLE_GAMES + " (" + GAMES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + GAMES_SET + " INTEGER NOT NULL REFERENCES " + TABLE_SETS + "(" + SETS_ID + ") ON DELETE CASCADE, " + GAMES_ALL_TRICKS + " BOOLEAN DEFAULT 0, " + GAMES_TEAM1_DECLARATIONS + " INTEGER DEFAULT 0, " + GAMES_TEAM2_DECLARATIONS + " INTEGER DEFAULT 0, " + GAMES_TEAM1_POINTS + " INTEGER DEFAULT 0, " + GAMES_TEAM2_POINTS + " INTEGER DEFAULT 0, FOREIGN KEY(" + GAMES_SET + ") REFERENCES " + TABLE_SETS + "(" + SETS_ID + "))";
	
	protected static final String SQL_SELECT_SETS_WINNER_COUNT = "SELECT COUNT(*) FROM " + TABLE_SETS + " WHERE " + SETS_MATCH + "=? AND " + SETS_WINNING_TEAM + "=?";
	protected static final String SQL_SELECT_SETS_ACTIVE_SET = "SELECT " + SETS_ID + " FROM " + TABLE_SETS + " WHERE " + SETS_MATCH + "=? AND " + SETS_WINNING_TEAM + "=0";
	protected static final String SQL_SELECT_GAMSE_POINTS_SUM = "SELECT SUM(?) FROM " + TABLE_GAMES + " WHERE " + GAMES_SET + "=?";
	
	public static final String TEAM1_SETS = "team1_sets";
	public static final String TEAM2_SETS = "team2_sets";
	public static final String TEAM1_PLAYER1 = "team1_player1";
	public static final String TEAM1_PLAYER2 = "team1_player2";
	public static final String TEAM2_PLAYER1 = "team2_player1";
	public static final String TEAM2_PLAYER2 = "team2_player2";
	public static final String GAMES_COUNT = "games_count";
	public static final String SETS_COUNT = "sets_count";
	public static final String SETS_WON = "sets_won";
	
	
	public Data(Context context) {
		
		belaOpenHandler = new BelaOpenHandler(context, Data.DB_NAME, Data.DB_VERSION);
		winnerCount = belaOpenHandler.getReadableDatabase().compileStatement(Data.SQL_SELECT_SETS_WINNER_COUNT);
		activeSet = belaOpenHandler.getReadableDatabase().compileStatement(Data.SQL_SELECT_SETS_ACTIVE_SET);
	}
	
	public Cursor getPlayersCursor() {
		
		// This query return list of all players sorted ascending by player name. Each row will contain
		// player id, player name, number of sets won and number of sets played
		// This query was created by Krunoslav Puljiæ
		final String query = "SELECT T2." + PLAYERS_ID + " AS " + PLAYERS_ID + ", T2." + PLAYERS_NAME +
			" AS " + PLAYERS_NAME + ", COALESCE(" + SETS_WON + ",0) AS " + SETS_WON +
			", " + SETS_COUNT + " FROM (SELECT " + TABLE_PLAYERS + "." + PLAYERS_ID + " AS " +
			PLAYERS_ID + ", "  + TABLE_PLAYERS + "." + PLAYERS_NAME + " AS " + PLAYERS_NAME +
			", COUNT(" + TABLE_SETS + "." + SETS_ID + ") AS " + SETS_COUNT + " FROM " +
			TABLE_PLAYERS + " LEFT JOIN " + TABLE_MATCHES + " ON (" + TABLE_PLAYERS +
			"." + PLAYERS_ID + "=" + TABLE_MATCHES + "." + MATCHES_TEAM2_PLAYER2 + 
			") OR (" + TABLE_PLAYERS + "." + PLAYERS_ID + "=" + TABLE_MATCHES + "." + 
			MATCHES_TEAM2_PLAYER1 + ") OR (" + TABLE_PLAYERS + "." + PLAYERS_ID + "=" +
			TABLE_MATCHES + "." + MATCHES_TEAM1_PLAYER2 + ") OR (" + TABLE_PLAYERS + "." +
			PLAYERS_ID + "=" + TABLE_MATCHES + "." + MATCHES_TEAM1_PLAYER1 + ") LEFT JOIN " +
			TABLE_SETS + " ON " + TABLE_MATCHES + "." + MATCHES_ID + "=" + TABLE_SETS + "." +
			SETS_MATCH + " GROUP BY " + TABLE_PLAYERS + "." + PLAYERS_ID + ", " + TABLE_PLAYERS +
			"." + PLAYERS_NAME + ") AS T2 LEFT JOIN (SELECT " + TABLE_PLAYERS + "." + PLAYERS_ID +
			" AS " + PLAYERS_ID + ", " + TABLE_PLAYERS + "." + PLAYERS_NAME + " AS " +
			PLAYERS_NAME + ", COUNT(" + TABLE_SETS + "." + SETS_ID + ") AS " + SETS_WON +
			" FROM " + TABLE_PLAYERS + ", " + TABLE_MATCHES + " INNER JOIN " + TABLE_SETS +
			" ON " + TABLE_MATCHES + "." + MATCHES_ID + "=" + TABLE_SETS + "." + SETS_MATCH +
			" WHERE (" + SETS_WINNING_TEAM + "=" + TEAM1 + " AND ((" + TABLE_PLAYERS + "." +
			PLAYERS_ID + "=" + TABLE_MATCHES + "." + MATCHES_TEAM1_PLAYER2 + ") OR (" +
			TABLE_PLAYERS + "." + PLAYERS_ID + "=" + TABLE_MATCHES + "." + MATCHES_TEAM1_PLAYER1 +
			"))) OR (" + SETS_WINNING_TEAM + "=" + TEAM2 + " AND ((" + TABLE_PLAYERS + "." +
			PLAYERS_ID + "=" + TABLE_MATCHES + "." + MATCHES_TEAM2_PLAYER2 + ") OR (" +
			TABLE_PLAYERS + "." + PLAYERS_ID + "=" + TABLE_MATCHES + "." + MATCHES_TEAM2_PLAYER1 +
			"))) GROUP BY " + TABLE_PLAYERS + "." + PLAYERS_ID + ", " + TABLE_PLAYERS + "." +
			PLAYERS_NAME + ") AS T1 ON T1._ID = T2._ID ORDER BY T2.NAME ASC";
		return belaOpenHandler.getReadableDatabase().rawQuery(query, null); 
	}
	
	public Integer addPlayer(String name) {
		
		ContentValues values = new ContentValues();
		values.put(Data.PLAYERS_NAME, name);
		return (int) belaOpenHandler.getWritableDatabase().insert(Data.TABLE_PLAYERS, null, values);
	}
	
	public boolean removePlayer(Integer id) {
		
		String whereClause = Data.PLAYERS_ID + "=" + id.toString();
		try {
			belaOpenHandler.getWritableDatabase().delete(Data.TABLE_PLAYERS, whereClause, null);
		}
		catch (SQLiteException e) {
			return false;
		}
		return true;
	}
	
	public boolean removeAllPlayers() {
		
		try {
			belaOpenHandler.getWritableDatabase().delete(Data.TABLE_PLAYERS, null, null);
		}
		catch (SQLiteException e) {
			return false;
		}
		return true;
	}
	
	public Cursor getMatchesCursor(Integer limit) {
		
		// This query returns list of all matches sorted descending by date first and then by time. Each row will contain
		// match id, match date, match time, number of sets won by team1, number of sets won by team2,
		// team1 player1 name, team1 player2 name, team2 player1 name, team2 player2 name
		final String query = "SELECT " + Data.MATCHES_ID + ", " + Data.MATCHES_DATE + ", " + Data.MATCHES_TIME +
			", (SELECT COUNT(*) FROM " + Data.TABLE_SETS + " WHERE " + Data.SETS_MATCH +
			"=" + Data.TABLE_MATCHES + "." + Data.MATCHES_ID + " AND " + Data.SETS_WINNING_TEAM +
			"=" + Data.TEAM1 + ") AS " + Data.TEAM1_SETS +
			", (SELECT COUNT(*) FROM " + Data.TABLE_SETS + " WHERE " + Data.SETS_MATCH +
			"=" + Data.TABLE_MATCHES + "." + Data.MATCHES_ID + " AND " + Data.SETS_WINNING_TEAM +
			"=" + Data.TEAM2 + ") AS " + Data.TEAM2_SETS +
			", (SELECT " + Data.PLAYERS_NAME + " FROM " + Data.TABLE_PLAYERS + " WHERE " +
			Data.PLAYERS_ID + "=" + Data.TABLE_MATCHES + "." + Data.MATCHES_TEAM1_PLAYER1 +
			") AS " + Data.TEAM1_PLAYER1 +
			", (SELECT " + Data.PLAYERS_NAME + " FROM " + Data.TABLE_PLAYERS + " WHERE " +
			Data.PLAYERS_ID + "=" + Data.TABLE_MATCHES + "." + Data.MATCHES_TEAM1_PLAYER2 +
			") AS " + Data.TEAM1_PLAYER2 +
			", (SELECT " + Data.PLAYERS_NAME + " FROM " + Data.TABLE_PLAYERS + " WHERE " +
			Data.PLAYERS_ID + "=" + Data.TABLE_MATCHES + "." + Data.MATCHES_TEAM2_PLAYER1 +
			") AS " + Data.TEAM2_PLAYER1 +
			", (SELECT " + Data.PLAYERS_NAME + " FROM " + Data.TABLE_PLAYERS + " WHERE " +
			Data.PLAYERS_ID + "=" + Data.TABLE_MATCHES + "." + Data.MATCHES_TEAM2_PLAYER2 +
			") AS " + Data.TEAM2_PLAYER2 +
			" FROM " + Data.TABLE_MATCHES + " ORDER BY " + Data.MATCHES_DATE + " DESC, " +
			Data.MATCHES_TIME + " DESC";
		
		if (limit != null)
			return belaOpenHandler.getReadableDatabase().rawQuery(query + " LIMIT " + limit.toString(), null);
		else
			return belaOpenHandler.getReadableDatabase().rawQuery(query, null);
	}
	
	public Integer getMatchLimit(Integer matchId) {
		
		String[] matchesColumns = new String[] { Data.MATCHES_SET_LIMIT };
		String matchesSelection = Data.MATCHES_ID + "=" + matchId.toString();
		Cursor cursor = belaOpenHandler.getReadableDatabase().query(Data.TABLE_MATCHES, matchesColumns, matchesSelection, null, null, null, null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}
	
	protected Cursor rawQuery(String sqlQuery) {
		
		return belaOpenHandler.getReadableDatabase().rawQuery(sqlQuery, null);
	}
	
	public Integer addMatch(String date, String time, Integer team1Player1Id, Integer team1Player2Id, Integer team2Player1Id, Integer team2Player2Id, Integer setLimit) {
		
		ContentValues values = new ContentValues();
		values.put(Data.MATCHES_DATE, date);
		values.put(Data.MATCHES_TIME, time);
		values.put(Data.MATCHES_TEAM1_PLAYER1, team1Player1Id);
		values.put(Data.MATCHES_TEAM1_PLAYER2, team1Player2Id);
		values.put(Data.MATCHES_TEAM2_PLAYER1, team2Player1Id);
		values.put(Data.MATCHES_TEAM2_PLAYER2, team2Player2Id);
		values.put(Data.MATCHES_SET_LIMIT, setLimit); 
		return (int) belaOpenHandler.getWritableDatabase().insert(Data.TABLE_MATCHES, null, values);
	}
	
	public void removeMatch(Integer id) {
		
		String whereClause = Data.MATCHES_ID + "=" + id.toString();
		belaOpenHandler.getWritableDatabase().delete(Data.TABLE_MATCHES, whereClause, null);
	}
	
	public void removeAllMatches() {
		
		belaOpenHandler.getWritableDatabase().delete(Data.TABLE_MATCHES, null, null);
	}

	protected Integer addSet(Integer matchId) {
		
		ContentValues values = new ContentValues();
		values.put(Data.SETS_MATCH, matchId);
		return (int) belaOpenHandler.getWritableDatabase().insert(Data.TABLE_SETS, null, values);
	}
	
	protected Integer getWinnerCount(Integer matchId, Integer team) {
		
		winnerCount.bindLong(1, matchId);
		winnerCount.bindLong(2, team);
		return (int) winnerCount.simpleQueryForLong();
	}
	
	protected Integer getActiveSet(Integer matchId) {
		
		activeSet.bindLong(1, matchId);
		try {
			return (int) activeSet.simpleQueryForLong();
		}
		catch (SQLiteDoneException e) {
			return null;
		}
	}
	
	protected void setWinner(Integer setId, Integer team) {
		
		ContentValues values = new ContentValues();
		values.put(Data.SETS_WINNING_TEAM, team);
		String whereClause = Data.SETS_ID + "=" + setId.toString();
		belaOpenHandler.getWritableDatabase().update(Data.TABLE_SETS, values, whereClause, null);
	}
	
	public Integer getSetPoints(Integer setId, Integer team) {
		
		String column = "Sum(";
		String gamesSelection = Data.GAMES_SET + "=" + setId.toString();
		switch(team){
		case TEAM1:
			column += Data.GAMES_TEAM1_POINTS + ") AS points";
			break;
		case TEAM2:
			column += Data.GAMES_TEAM2_POINTS + ") AS points";
			break;
		default:
			return 0;
		}
		String[] gamesColumns = new String[] { column };
		Cursor c = belaOpenHandler.getReadableDatabase().query(Data.TABLE_GAMES, gamesColumns, gamesSelection, null, null, null, null);
		c.moveToFirst();
		return c.getInt(0);
	}
	
	protected void removeSet(Integer setId) {
		
		String whereClause = Data.SETS_ID + "=" + setId.toString();
		belaOpenHandler.getWritableDatabase().delete(Data.TABLE_SETS, whereClause, null);
	}
	
	public void removeAllSets(Integer matchId) {
		
		String whereClause = Data.SETS_MATCH + "=" + matchId.toString();
		belaOpenHandler.getWritableDatabase().delete(Data.TABLE_SETS, whereClause, null);
	}
	
	public Cursor getGamesCursor(Integer setId) {
		
		String[] gamesColumns = new String[] { Data.GAMES_ID, Data.GAMES_TEAM1_POINTS, Data.GAMES_TEAM2_POINTS,
											   Data.GAMES_TEAM1_DECLARATIONS, Data.GAMES_TEAM2_DECLARATIONS };
		String gamesSelection = Data.GAMES_SET + "=" + setId.toString();
		return belaOpenHandler.getReadableDatabase().query(Data.TABLE_GAMES, gamesColumns, gamesSelection, null, null, null, null);
	}
	
	protected Integer getGameSet(Integer gameId) {
		
		String[] gamesColumns = new String[] { Data.GAMES_SET };
		String gamesSelection = Data.GAMES_ID + "=" + gameId.toString();
		Cursor cursor = belaOpenHandler.getReadableDatabase().query(Data.TABLE_GAMES, gamesColumns, gamesSelection, null, null, null, null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	protected Integer addGame(Integer setId, boolean allTricks, Integer team1Declarations, Integer team2Declarations, Integer team1Points, Integer team2Points) {
		
		ContentValues values = new ContentValues();
		values.put(Data.GAMES_SET, setId);
		values.put(Data.GAMES_ALL_TRICKS, allTricks);
		values.put(Data.GAMES_TEAM1_DECLARATIONS, team1Declarations);
		values.put(Data.GAMES_TEAM2_DECLARATIONS, team2Declarations);
		values.put(Data.GAMES_TEAM1_POINTS, team1Points);
		values.put(Data.GAMES_TEAM2_POINTS, team2Points);
		return (int) belaOpenHandler.getWritableDatabase().insert(Data.TABLE_GAMES, null, values);
	}
	
	public Cursor getGame(Integer gameId) {
		String[] gamesColumns = new String[] { Data.GAMES_ID, Data.GAMES_TEAM1_POINTS, Data.GAMES_TEAM2_POINTS,
				   Data.GAMES_TEAM1_DECLARATIONS, Data.GAMES_TEAM2_DECLARATIONS, Data.GAMES_ALL_TRICKS };
		
		String gamesSelection = Data.GAMES_ID + "=" + gameId.toString();
		return belaOpenHandler.getReadableDatabase().query(Data.TABLE_GAMES, gamesColumns, gamesSelection, null, null, null, null);
	}
	
	public void updateGame(Integer gameId, boolean allTricks, Integer team1Declarations, Integer team2Declarations, Integer team1Points, Integer team2Points) {
		
		ContentValues values = new ContentValues();
		values.put(Data.GAMES_ALL_TRICKS, allTricks);
		values.put(Data.GAMES_TEAM1_DECLARATIONS, team1Declarations);
		values.put(Data.GAMES_TEAM2_DECLARATIONS, team2Declarations);
		values.put(Data.GAMES_TEAM1_POINTS, team1Points);
		values.put(Data.GAMES_TEAM2_POINTS, team2Points);
		String whereClause = Data.GAMES_ID + "=" + gameId.toString();
		belaOpenHandler.getWritableDatabase().update(Data.TABLE_GAMES, values, whereClause, null);
	}
	
	protected void removeGame(Integer gameId) {
		
		String whereClause = Data.GAMES_ID + "=" + gameId.toString();
		belaOpenHandler.getWritableDatabase().delete(Data.TABLE_GAMES, whereClause, null);
	}
	
	protected void removeAllGames(Integer setId) {
		
		String whereClause = Data.GAMES_SET + "=" + setId.toString();
		belaOpenHandler.getWritableDatabase().delete(Data.TABLE_GAMES, whereClause, null);
	}
}