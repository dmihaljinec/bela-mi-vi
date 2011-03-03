package bela.mi.vi.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BelaOpenHandler extends SQLiteOpenHelper {
	
	public BelaOpenHandler(Context context, String dbName, int dbVersion) {
		
        super(context, dbName, null, dbVersion);
    }
	
	@Override
    public void onCreate(SQLiteDatabase db) {

		db.execSQL(Data.SQL_CREATE_TABLE_PLAYERS);
		db.execSQL(Data.SQL_CREATE_TABLE_MATCHES);
		db.execSQL(Data.SQL_CREATE_TABLE_SETS);
		db.execSQL(Data.SQL_CREATE_TABLE_GAMES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
    	if (oldVersion == newVersion)
    		return;
    	
    	db.execSQL(Data.SQL_DROP_TABLE + " " + Data.TABLE_PLAYERS);
    	db.execSQL(Data.SQL_DROP_TABLE + " " + Data.TABLE_MATCHES);
    	db.execSQL(Data.SQL_DROP_TABLE + " " + Data.TABLE_SETS);
    	db.execSQL(Data.SQL_DROP_TABLE + " " + Data.TABLE_GAMES);
        onCreate(db);
    }
    
    @Override
    public void onOpen(SQLiteDatabase db) {
    	
      super.onOpen(db);
      if (!db.isReadOnly()) {
		// Enable foreign key constraints
		db.execSQL(Data.SQL_PRAGMA_FOREIGN_KEYS);
      }
    }
}