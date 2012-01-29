package bela.mi.vi;

import android.app.backup.BackupManager;
import android.content.Context;

/**
 * Backward compatible BackupManager instance
 * 
 * @author Damir Mihaljinec
 */
public class WrapBackupManager {
	
	private BackupManager mInstance;

	static {
	    try {
	        Class.forName("android.app.backup.BackupManager");
	    }
	    catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public static void checkAvailable() {
		
	}

	public void dataChanged() {
	    
		mInstance.dataChanged();
	}

	public WrapBackupManager(Context context) {
		
	    mInstance = new BackupManager(context);
	}
}
