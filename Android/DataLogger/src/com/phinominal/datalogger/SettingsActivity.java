package com.phinominal.datalogger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity {

	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText tableIdEditText;
	private Button createTableButton;
	private Button saveButton;
	private Button cancelButton;
	
	ApplicationContext appContext;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.settings);
		
		appContext = ((ApplicationContext)getApplicationContext());
		
		userNameEditText = (EditText) this.findViewById(R.id.username_edit_text);
		passwordEditText = (EditText) this.findViewById(R.id.password_edit_text);
		tableIdEditText = (EditText) this.findViewById(R.id.table_id_edit_text);
		createTableButton = (Button) this.findViewById(R.id.create_table_button);
		saveButton = (Button) this.findViewById(R.id.save_button);
		cancelButton = (Button) this.findViewById(R.id.cancel_button);
		
		userNameEditText.setText(appContext.ftUsername);
		passwordEditText.setText(appContext.ftPassword);
		tableIdEditText.setText(Long.toString(appContext.ftTableId));
		
		saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	appContext.ftUsername = userNameEditText.getText().toString();
            	appContext.ftPassword = passwordEditText.getText().toString();
            	appContext.ftTableId = Long.parseLong(tableIdEditText.getText().toString());
            	
            	finish();
            }
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}
