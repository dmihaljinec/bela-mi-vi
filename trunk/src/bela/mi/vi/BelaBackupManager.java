package bela.mi.vi;

import android.content.Context;

/**
 * Backup manager that creates instance of android.app.backup.BackupManager if available.
 * 
 * @author Damir Mihaljinec
 */
public class BelaBackupManager {
	
	private static Boolean mBackupManagerAvailable = null;

    public static void dataChanged(Context context) {

        if (mBackupManagerAvailable == null) {
        	try {
        		WrapBackupManager.checkAvailable();
        		mBackupManagerAvailable = true;
            } catch (Throwable t) {
            	mBackupManagerAvailable = false;
            }
        }

        if (mBackupManagerAvailable == true) {
            WrapBackupManager wrapBackupManager = new WrapBackupManager(context);
            wrapBackupManager.dataChanged();
        }
    }
}
