package bela.mi.vi;

import bela.mi.vi.data.MatchData;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RadioButton;
import android.text.TextWatcher;
import android.text.Editable;

public class NewGameActivity extends Activity implements OnClickListener {
	
	private Integer mMatchId;
	private MatchData mMatchData;
	private Integer mTeam1Declarations = 0;
	private Integer mTeam2Declarations = 0;
	private Integer mGamePoints = MatchData.GAME_POINTS;
	private Integer mDeclarationTeam = 0;
	
	// Widgets
	private TextView mGamePointsTextView;
	private TextView mGamePointsTeam1TextView;
	private TextView mGamePointsTeam2TextView;
	private TextView mGamePointsAllTricksTextView;
	private RadioButton mTeam1RadioButton;
	private RadioButton mTeam2RadioButton;
	private Button mAdd20;
	private Button mAdd50;
	private CheckBox mBelaCheckBox;
	private CheckBox mAllTricksCheckBox;
	private EditText mPointsTeam1EditText;
	private EditText mPointsTeam2EditText;
	private Button mOkButton;
	private Button mCancelButton;
	
	public static final String MATCH_ID = "match_id";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game_activity);
        Bundle extras = getIntent().getExtras();
        if (extras == null)
			return;
        
		mMatchId = extras.getInt(MATCH_ID);
		mMatchData = new MatchData(this, mMatchId);
		
		mGamePointsTextView = (TextView) findViewById(R.id.game_points);
		mGamePointsTeam1TextView = (TextView) findViewById(R.id.game_points_team1);
		mGamePointsTeam2TextView = (TextView) findViewById(R.id.game_points_team2);
		mGamePointsAllTricksTextView = (TextView) findViewById(R.id.game_points_all_tricks);
		mTeam1RadioButton = (RadioButton) findViewById(R.id.team1_rb);
		mTeam1RadioButton.setOnClickListener(new DeclarationsTeamOnClickListener(MatchData.TEAM1));
		mTeam2RadioButton = (RadioButton) findViewById(R.id.team2_rb);
		mTeam2RadioButton.setOnClickListener(new DeclarationsTeamOnClickListener(MatchData.TEAM2));
		mAdd20 = (Button) findViewById(R.id.add20);
		mAdd20.setOnClickListener(new AddOnClickListener(20));
		mAdd50 = (Button) findViewById(R.id.add50);
		mAdd50.setOnClickListener(new AddOnClickListener(50));
		mBelaCheckBox = (CheckBox) findViewById(R.id.bela_cb);
		mBelaCheckBox.setOnClickListener(new BelaOnClickListener());
		mAllTricksCheckBox = (CheckBox) findViewById(R.id.all_tricks_cb);
		mAllTricksCheckBox.setOnClickListener(new AllTricksOnClickListener());
		mPointsTeam1EditText = (EditText) findViewById(R.id.team1_et);
		mPointsTeam2EditText = (EditText) findViewById(R.id.team2_et);
		mPointsTeam1EditText.addTextChangedListener(new PointsTextWatcher(mPointsTeam2EditText));
		mPointsTeam2EditText.addTextChangedListener(new PointsTextWatcher(mPointsTeam1EditText));
		mOkButton = (Button) findViewById(R.id.ok);
		mOkButton.setOnClickListener(this);
		mCancelButton = (Button) findViewById(R.id.cancel);
		mCancelButton.setOnClickListener(this);
		setGamePoints();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_new_game_activity, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	    case R.id.help:
	    	HelpDialog help = new HelpDialog(this, getResources().getString(R.string.new_game_activity_help_message));
	    	help.show();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private class AllTricksOnClickListener implements CheckBox.OnClickListener {
		
		public void onClick(View view) {
			
			if (mAllTricksCheckBox.isChecked() == true)
				mGamePointsAllTricksTextView.setTextColor(getResources().getColor(R.color.list_blue));
			else
				mGamePointsAllTricksTextView.setTextColor(getResources().getColor(R.color.grey));
			setGamePoints();
			resetPoints();
		}
	}

	private class BelaOnClickListener implements CheckBox.OnClickListener {
		
		public void onClick(View view) {
			
			addBela();
			setGamePoints();
			resetPoints();
		}
	}
	
	private class AddOnClickListener implements Button.OnClickListener {
		
		private Integer mAdd;
		
		public AddOnClickListener(Integer add) {
			
			mAdd = add;
		}
		
		public void onClick(View view) {
			
			addDeclarations(mAdd);
			setGamePoints();
			resetPoints();
		}
	}
	
	private class DeclarationsTeamOnClickListener implements RadioButton.OnClickListener {
		
		private Integer mTeam;
		
		public DeclarationsTeamOnClickListener(Integer team) {
			
			mTeam = team;
		}
		
		public void onClick(View view) {
			
			if (mDeclarationTeam == 0)
				enableDeclaration(true);
			
			if (mDeclarationTeam != mTeam) {
				mDeclarationTeam = mTeam;
				setBelaCheckBoxLabel(mTeam);
				resetDeclarations();
				resetGamePoints();
				resetPoints();
			}
		}
	}
	
	private class PointsTextWatcher implements TextWatcher {
		
		private EditText mSecondary;
		
		public PointsTextWatcher(EditText editText) {
			
			mSecondary = editText;
		}
		
		public void afterTextChanged(Editable primary) {
			
			
			Integer primaryNumber = 0;
			Integer secondaryNumber = 0;
			if (primary.toString().contentEquals("") == true) {
				mOkButton.setEnabled(false);
				return;
			}
			primaryNumber = Integer.parseInt(primary.toString());
			if (mSecondary.getText().toString().contentEquals("") == true) {
				if (primaryNumber == mGamePoints) {
					mSecondary.setText(secondaryNumber.toString());
					mOkButton.setEnabled(true);
					return;
				}
			}
			else {
				secondaryNumber = Integer.parseInt(mSecondary.getText().toString());
				if (primaryNumber + secondaryNumber == mGamePoints) {
					mOkButton.setEnabled(true);
					return;
				}
			}
			
			mSecondary.setText("");

			if (primaryNumber > mGamePoints || primaryNumber == 1 || primaryNumber == (mGamePoints - 1)) {
				mOkButton.setEnabled(false);
				return;
			}
			secondaryNumber = mGamePoints - primaryNumber;
			if (mAllTricksCheckBox.isChecked() == true && ((primaryNumber > 0) && (primaryNumber < mGamePoints))) {
				mOkButton.setEnabled(false);
			}
			else {
				mSecondary.setText(secondaryNumber.toString());
				mOkButton.setEnabled(true);
			}
		}
		
		public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        public void onTextChanged(CharSequence s, int start, int before, int count) { }
	}
	
	public void onClick(View view) {
		
		if (view == mOkButton) {
			Integer team1Points = Integer.parseInt(mPointsTeam1EditText.getText().toString());
			Integer team2Points = Integer.parseInt(mPointsTeam2EditText.getText().toString());
			mMatchData.addGame(mAllTricksCheckBox.isChecked(), mTeam1Declarations, mTeam2Declarations, team1Points, team2Points);
			setResult(Activity.RESULT_OK);
			finish();
		}
		else if (view == mCancelButton) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
	}
	
	private void setBelaCheckBoxLabel(Integer team) {
		
		if (team == MatchData.TEAM1)
			mBelaCheckBox.setText(getResources().getString(R.string.team2) + " " + getResources().getString(R.string.bela));
		else if (team == MatchData.TEAM2)
			mBelaCheckBox.setText(getResources().getString(R.string.team1) + " " + getResources().getString(R.string.bela));
		else
			mBelaCheckBox.setText(getResources().getString(R.string.bela));
	}
	
	private void addDeclarations(Integer points) {
		
		if (mDeclarationTeam == MatchData.TEAM1)
			mTeam1Declarations += points;
		else if (mDeclarationTeam == MatchData.TEAM2)
			mTeam2Declarations += points;
	}
	
	private void addBela() {
		
		if (mBelaCheckBox.isChecked() == true) {
			if (mDeclarationTeam == MatchData.TEAM1) {
				mTeam2Declarations = MatchData.BELA_DECLARATION;
			}
			else if (mDeclarationTeam == MatchData.TEAM2) {
				mTeam1Declarations = MatchData.BELA_DECLARATION;
			}
		}
		else {
			if (mDeclarationTeam == MatchData.TEAM1) {
				mTeam2Declarations = 0;
			}
			else if (mDeclarationTeam == MatchData.TEAM2) {
				mTeam1Declarations = 0;
			}
		}
	}
	
	private void resetPoints() {
		
		mPointsTeam1EditText.setText("");
		mPointsTeam2EditText.setText("");
		mOkButton.setEnabled(false);
	}
	
	private void resetGamePoints() {
		
		mTeam1Declarations = 0;
		mTeam2Declarations = 0;
		mGamePoints = MatchData.GAME_POINTS;
		setGamePoints();
	}
	
	private void resetDeclarations() {
		
		mBelaCheckBox.setChecked(false);
	}
	
	private void setGamePoints() {

		mGamePoints = MatchData.GAME_POINTS + mTeam1Declarations + mTeam2Declarations;
		if (mAllTricksCheckBox.isChecked() == true)
			mGamePoints += MatchData.ALL_TRICKS;
		mGamePointsTextView.setText(mGamePoints.toString());
		mGamePointsTeam1TextView.setText(getResources().getString(R.string.team1) + ": " + mTeam1Declarations.toString());
		mGamePointsTeam2TextView.setText(getResources().getString(R.string.team2) + ": " + mTeam2Declarations.toString());
		
	}
	
	private void enableDeclaration(boolean enable) {
		
		mAdd20.setEnabled(enable);
		mAdd50.setEnabled(enable);
		mBelaCheckBox.setEnabled(enable);
	}
}
