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

		// Register to receive capture broadcast events
		IntentFilter captureFilter;
        captureFilter = new IntentFilter(this.getString(R.string.CAPTURE_EVENT));
        CaptureServiceReceiver captureReceiver = new CaptureServiceReceiver();
        registerReceiver(captureReceiver, captureFilter);
		
		// kick off LogCaptureService if it's not already running and bind to it
		this.doBindService();
		
	}
	
	public class CaptureServiceReceiver extends BroadcastReceiver
    {
      @Override
        public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
        {
    	  	Log.d("OUTPUT", "BROADCAST RECEIVED  Sensors: " + intent.getIntExtra(getString(R.string.SENSOR_1), -1) + ", " + intent.getIntExtra(getString(R.string.SENSOR_2), -1) + ", " + intent.getIntExtra(getString(R.string.SENSOR_3), -1) + "   TimeStamp:  " + intent.getIntExtra(getString(R.string.TIMESTAMP), -1) + "   DateString:  " + intent.getStringExtra(getString(R.string.DATE_STRING)));
    	  	
    	  	
    	  	// Here is the former code that updated the UI.  Will have to modify slightly to use data from intent...
    	  	
    	  	 /*
    	  	  * 
    	  	  
    	  	 editText.setText(line + "   " +  Long.toString(System.currentTimeMillis()));
    	  	  
			 ArrayList <SensorDescriptor> list = getList();
			 
			 for (int i = 0; i < list.size(); i++) {
				 
				 
				 SensorDescriptor descriptor = list.get(i);
				 Float currFloat = Float.parseFloat(components[i]);
				 
				 descriptor.setCurrMV(currFloat.floatValue());
				 
			 }
			 
			 
			 refreshList();
			 */
    	  	
    	  	
    	  	
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
			
			mBoundService.makeFTConnection(2154839, "phinominaltechnology", "Sk8ordie!");
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
	
	
	