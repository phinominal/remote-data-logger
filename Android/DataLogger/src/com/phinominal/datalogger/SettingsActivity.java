package com.phinominal.datalogger;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class SettingsActivity extends Activity {

	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText tableIdEditText;
	
	ApplicationContext appContext;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.settings);
		
		appContext = ((ApplicationContext)getApplicationContext());
		
		userNameEditText = (EditText) this.findViewById(R.id.username_edit_text);
		passwordEditText = (EditText) this.findViewById(R.id.password_edit_text);
		tableIdEditText = (EditText) this.findViewById(R.id.table_id_edit_text);
		
		
	}
}
