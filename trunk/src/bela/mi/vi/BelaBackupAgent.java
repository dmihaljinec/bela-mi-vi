package bela.mi.vi;

import bela.mi.vi.data.Data;
import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;

/**
 * Backup for database and shared preferences.
 * 
 * @author Damir Mihaljinec
 */
public class BelaBackupAgent extends BackupAgentHelper {

	private final static String PREFS = "shared_prefs";
	
    public void onCreate() {
    	
    	addHelper(Data.DB_NAME, new FileBackupHelper(this, "../databases/" + Data.DB_NAME));
        addHelper(PREFS, new SharedPreferencesBackupHelper(this, "bela.mi.vi_preferences"));
    }
}
