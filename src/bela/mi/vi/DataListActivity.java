package bela.mi.vi;

import android.app.ListActivity;
import android.os.Bundle;
import bela.mi.vi.data.Data;

/**
 * This class handles closing data on activity on Destroy.
 *  
 * @author Damir Mihaljinec
 */
abstract class DataListActivity extends ListActivity {

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
