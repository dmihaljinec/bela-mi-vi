package bela.mi.vi;

import bela.mi.vi.data.Data;
import android.app.Activity;
import android.os.Bundle;

/**
 * This class handles closing data on activity on Destroy.
 *  
 * @author Damir Mihaljinec
 */
abstract class DataActivity extends Activity {

	protected Data mData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		BelaApplication app = ((BelaApplication)getApplicationContext());
		app.createDataActivity();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroy() {
		
		if (mData != null) {
			mData.close();
			mData = null;
		}
		BelaApplication app = ((BelaApplication)getApplicationContext());
		app.destroyDataActivity();
		super.onDestroy();
	}
}
