package com.phinominal.datalogger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.phinominal.datalogger.SensorArrayAdapter.FullViewHolder;

public class DataLogger extends ListActivity {

	public EditText editText;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.data_logger);
		
		ApplicationContext appContext = ((ApplicationContext)getApplicationContext());
		
		ArrayAdapter<SensorDescriptor> adapter = new SensorArrayAdapter(this,
				appContext.getSelectedSensors(), true);
		setListAdapter(adapter);
		

		// Add EditText footer for debugging purposes
		editText = new EditText(this);
		editText.setHeight(80);
		editText.setWidth(200);
		getListView().addFooterView(editText);

		
		
		// TODO  Clearly needs a different timing mechanism then a countdown timer!!!!, or at least it needs to restart it upon finish!!!
		new CountDownTimer(30000000, 1000) {

		     public void onTick(long millisUntilFinished) {
		    	 Log.d("LOG", "Printing contents of last dir");
		    	 try {
		    		 InputStream inStream = new FileInputStream("/data/local/PROPBRIDGE_ADC_LAST"); 
		    		 
		    		 if (inStream != null) {
		    			 InputStreamReader inputreader = new InputStreamReader(inStream);
		    			 BufferedReader buffreader = new BufferedReader(inputreader);
		    			 
		    			 String line = buffreader.readLine();
		    			 
		    			 if (line != null) {
		    				 String [] components = line.split(" ");
		    				 
		    				 editText.setText(line + "   " +  Long.toString(System.currentTimeMillis()));
		    				 ArrayList <SensorDescriptor> list = getList();
		    				 
		    				 for (int i = 0; i < list.size(); i++) {
		    					 
		    					 
		    					 SensorDescriptor descriptor = list.get(i);
		    					 Float currFloat = Float.parseFloat(components[i]);
		    					 
		    					 descriptor.setCurrMV(currFloat.floatValue());
		    					 
		    				 }
		    				 
		    				 
		    				 refreshList();
		    				 
		    			 } 
		    			/* If we were reading the full file instead...
		    			 while ((line = buffreader.readLine()) != null) {
		    				 Log.d("LOG", line);
		    			 }
		    			 */
		    		 } 
		    	 } catch (Exception e) {
		    		 editText.setText("No file present at ADC_LAST  " + Long.toString(System.currentTimeMillis()));
		    		 Log.d("Exception", e.toString());
		    	 }
		     }

		     public void onFinish() {
		         //mTextField.setText("done!");
		     }
		  }.start();

	}
	
	private ArrayList <SensorDescriptor> getList() {
		return (ArrayList <SensorDescriptor>) ((SensorArrayAdapter) getListAdapter()).getList();
	}
	
	private void refreshList() {
		((SensorArrayAdapter) getListAdapter()).notifyDataSetChanged();
	}
	
	
	private FullViewHolder viewHolderForPosition(int position) {
		
		View clickedSensorView = getListView().getChildAt(position);
		
		return (FullViewHolder)clickedSensorView.getTag();
		
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    ApplicationContext appContext = ((ApplicationContext)getApplicationContext());
		
		ArrayAdapter<SensorDescriptor> adapter = new SensorArrayAdapter(this,
				appContext.getSelectedSensors(), true);
		setListAdapter(adapter);
	}

	// menu crap: just removes the application
	public void nuketheapp(){
		ApplicationContext appContext = ((ApplicationContext)getApplicationContext());
		appContext.persistSensorState();
		android.os.Process.killProcess(android.os.Process.myPid());
		this.finish();}

	public boolean onPrepareOptionsMenu(Menu menu) {
		
		 android.content.Intent intent = new android.content.Intent(); 
         intent.setClassName("com.phinominal.datalogger", "com.phinominal.datalogger.SensorList");
         startActivity(new Intent(intent));
		return false;
	}

}
	
	
	