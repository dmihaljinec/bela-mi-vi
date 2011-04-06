package bela.mi.vi;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.text.util.Linkify;

/**
 * This is About dialog.
 *  
 * @author Damir Mihaljinec
 */
public class AboutDialog extends Dialog implements OnClickListener{

	private Button mCloseButton;
	private String mVersion;
	
	public AboutDialog(Context context) {
		
		super(context);
		mVersion = context.getResources().getString(R.string.version);
		try{
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			mVersion += " " + info.versionName; 
		}
		catch (NameNotFoundException  e){
			
		}
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about_dialog);
		
		TextView version = (TextView) findViewById(R.id.version);
		version.setText(mVersion);
		
		TextView web = (TextView) findViewById(R.id.web);
		Linkify.addLinks(web, Linkify.ALL);
		
		mCloseButton = (Button) findViewById(R.id.close);
		mCloseButton.setOnClickListener(this);
    }
	
	public void onClick(View v) {
		
		if (v == mCloseButton)
			dismiss();
	}
}
