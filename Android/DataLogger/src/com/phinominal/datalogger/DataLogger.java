package com.phinominal.datalogger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.phinominal.datalogger.SensorArrayAdapter.FullViewHolder;
import com.phinominal.ftclient.FtClient;

public class DataLogger extends ListActivity {

	public EditText editText;
	
	private LogCaptureService mBoundService;
	private boolean mIsBound = false;
	
	// **** TEMP Fusion Tables vars (these should be in a separate class...)
	
	private FtClient ftclient;
	private long tableid = 2147942; //123456;
	private String username = "phinominaltechnology";
	private String password = "Sk8ordie!";
	
	// *********************************************************************
	
	
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

		
		
		IntentFilter captureFilter;
        captureFilter = new IntentFilter(LogCaptureService.CAPTURE_EVENT);
        CaptureServiceReceiver captureReceiver = new CaptureServiceReceiver();
        registerReceiver(captureReceiver, captureFilter);
		
		
		/*
		try {
			// Initialize FTClient
			String token = ClientLogin.authorize(username, password);
			ftclient = new FtClient(token);
		} catch (Exception e) {
			editText.setText("FT ClientLogin Failed  " + Long.toString(System.currentTimeMillis()));
			Log.d("OUTPUT", "FT ClientLogin Failed  " + Long.toString(System.currentTimeMillis()));
   		 	Log.d("Exception", e.toString());
   	 }
   	 */
		
		
		this.doBindService();
		
		// TODO  Clearly needs a different timing mechanism then a countdown timer!!!!, or at least it needs to restart it upon finish!!!
		new CountDownTimer(30000000, 1500) {

		     public void onTick(long millisUntilFinished) {
		    	 
		    	 Log.d("LOG", "Printing contents of last dir");
		    	 try {
		    		 InputStream inStream = new FileInputStream("/data/local/PB_ADC_C"); 
		    		 
		    		 if (inStream != null) {
		    			 InputStreamReader inputreader = new InputStreamReader(inStream);
		    			 BufferedReader buffreader = new BufferedReader(inputreader);
		    			 
		    			 String line = buffreader.readLine();
		    			 
		    			 if (line != null) {
		    				 String [] components = line.split(" ");
		    				 
		    				 editText.setText(line + "   " +  Long.toString(System.currentTimeMillis()));
		    				 /*
		    				 Thread thread = new Thread()
		    				 {
		    				     @Override
		    				     public void run() {
		    				         try {
		    				        	 
		    				        	 
		    				        	// Generate INSERT statement
		 		    			        StringBuilder insert = new StringBuilder();
		 		    			        insert.append("INSERT INTO ");
		 		    			        insert.append(tableid);
		 		    			        insert.append(" (Severity, Location, Address, Timestamp) VALUES ");
		 		    			        insert.append("(");
		 		    			        insert.append(new Date().getTime());
		 		    			        insert.append(", '");
		 		    			        insert.append("String Location");
		 		    			        insert.append("', '");
		 		    			        insert.append("Address description");
		 		    			        insert.append("', ");
		 		    			        insert.append(new Date().getTime());
		 		    			        insert.append(")");

		 		    			        // Save the data to Fusion Tables
		 		    			        ftclient.query(insert.toString());
		    				         } catch (Exception e) {
		    				        	 editText.setText("FusionTables insertion failed  " + Long.toString(System.currentTimeMillis()));
				    		    		 Log.d("Exception", e.toString());
		    				         }
		    				     }
		    				 };
		    				 
		    				 thread.start();
		    				   */ 				 
		    				 /*
		    				 ArrayList <SensorDescriptor> list = getList();
		    				 
		    				 for (int i = 0; i < list.size(); i++) {
		    					 
		    					 
		    					 SensorDescriptor descriptor = list.get(i);
		    					 Float currFloat = Float.parseFloat(components[i]);
		    					 
		    					 descriptor.setCurrMV(currFloat.floatValue());
		    					 
		    				 }
		    				 
		    				 
		    				 refreshList();
		    				 
		    				 */
		    				 
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
		  };/*.start();*/

	}
	
	public class CaptureServiceReceiver extends BroadcastReceiver
    {
      @Override
        public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
        {
    	  	Log.d("OUTPUT", "BROADCAST RECEIVED  Sensors: " + intent.getIntExtra(LogCaptureService.SENSOR_1, -1) + ", " + intent.getIntExtra(LogCaptureService.SENSOR_2, -1) + ", " + intent.getIntExtra(LogCaptureService.SENSOR_3, -1) + "   TimeStamp:  " + intent.getIntExtra(LogCaptureService.TIMESTAMP, -1) + "   DateString:  " + intent.getStringExtra(LogCaptureService.DATE_STRING));
    	  	
        }
    }

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        mBoundService = ((LogCaptureService.LogCaptureBinder)service).getService();
	        
			
			mBoundService.startCapture();	
			mBoundService.publicMethod();

	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        mBoundService = null;
	    }
	};

	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
	    bindService(new Intent(this, LogCaptureService.class), mConnection, Context.BIND_AUTO_CREATE);
	   
	    mIsBound = true;
	}

	void doUnbindService() {
	    if (mIsBound) {
	        // Detach our existing connection.
	        unbindService(mConnection);
	        mIsBound = false;
	    }
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
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
	
	
	