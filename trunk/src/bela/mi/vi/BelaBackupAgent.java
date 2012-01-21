package bela.mi.vi;

import bela.mi.vi.data.Data;
import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class BelaBackupAgent extends BackupAgentHelper {

    public void onCreate() {
    	addHelper(Data.DB_NAME, new FileBackupHelper(this, "../databases/" + Data.DB_NAME));

        //addHelper("shared_prefs", new SharedPreferencesBackupHelper(this, "settings", "raw"));
    }
}
