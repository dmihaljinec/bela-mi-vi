package bela.mi.vi;

import android.content.Context;

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
