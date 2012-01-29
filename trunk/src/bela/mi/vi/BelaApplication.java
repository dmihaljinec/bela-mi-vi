package bela.mi.vi;

import bela.mi.vi.data.Data;
import android.app.Application;

/**
 * Bela application
 * Counts number of data activities and when it reaches zero, closes database
 *  
 * @author Damir Mihaljinec
 */
public class BelaApplication extends Application {
	
	private long mActivities;
	
	public void createDataActivity() {
		
		mActivities++;
	}
	
	public void destroyDataActivity() {
		
		if (mActivities > 0) {
			mActivities--;
		}
		if (mActivities == 0) {
			Data data = new Data(getApplicationContext());
			data.finalClose();
		}
	}
}
