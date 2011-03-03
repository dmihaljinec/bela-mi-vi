package bela.mi.vi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

/**
 * This dialog is a template for all help dialogs.
 * It creates a dialog with a standard look and displays
 * context specific message that was provided by object creator.
 *  
 * @author Damir Mihaljinec
 */
public class HelpDialog extends Dialog implements OnClickListener{

	private Button mCloseButton;
	private String mMessage;
	
	public HelpDialog(Context context, String message) {
		
		super(context);
		mMessage = message;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.help_dialog);
		TextView message = (TextView) findViewById(R.id.message);
		message.setText(mMessage);
		mCloseButton = (Button) findViewById(R.id.close);
		mCloseButton.setOnClickListener(this);
    }
	
	public void onClick(View v) {
		
		if (v == mCloseButton)
			dismiss();
	}
}
