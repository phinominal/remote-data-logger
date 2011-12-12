package com.phinominal.datalogger;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.phinominal.datalogger.SensorArrayAdapter.FullViewHolder;

public class DataLogger extends ListActivity {
	
	public static String CLOUD_SYNC_STATE_CHANGE_STRING = "CLOUD_SYNC_STATE_CHANGED";

	public EditText editText;
	
	private TextView lastUpdateValueTextView;
	private TextView cloudSyncStatusTextView;
	private Button toggleCloudSyncingButton;
	private ProgressBar cloudSyncProgressBar;
	
	private LogCaptureService mBoundService;
	private boolean mIsBound = false;
	private Handler handler;
	private ApplicationContext appContext;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.data_logger);
		
		appContext = ((ApplicationContext)getApplicationContext());
		handler = new Handler();
		
		ListView lv = getListView();
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup) inflater.inflate(R.layout.data_logger_header, lv, false);
		lv.addHeaderView(header, null, false);
		
		lastUpdateValueTextView = (TextView) header.findViewById(R.id.last_update_text_view);
		cloudSyncStatusTextView = (TextView) header.findViewById(R.id.cloud_sync_status_text_view);
		cloudSyncProgressBar = (ProgressBar) header.findViewById(R.id.cloud_sync_progress_bar);
		
		toggleCloudSyncingButton = (Button) header.findViewById(R.id.toggle_cloud_sync_button);
		setupToggleCloudSyncStateButtonListener();
		
		this.refreshCloudSyncInterfaceState();
		
		ArrayAdapter<SensorDescriptor> adapter = new SensorArrayAdapter(this,
				appContext.getSelectedSensors(), true);
		setListAdapter(adapter);
		
		/*
		// Add EditText footer for debugging purposes
		editText = new EditText(this);
		editText.setHeight(80);
		editText.setWidth(200);
		getListView().addFooterView(editText);
		*/
		
		// Register to receive CAPTURE_EVENT broadcast events
		IntentFilter captureFilter;
        captureFilter = new IntentFilter(this.getString(R.string.CAPTURE_EVENT));
        CaptureServiceReceiver captureReceiver = new CaptureServiceReceiver();
        registerReceiver(captureReceiver, captureFilter);
        
        // Register to receive CAPTURE_EVENT broadcast events
		IntentFilter cloudSyncStateFilter;
		cloudSyncStateFilter = new IntentFilter(CLOUD_SYNC_STATE_CHANGE_STRING);
        CloudSyncStateChangeReceiver cloudSyncStateChangeReceiver = new CloudSyncStateChangeReceiver();
        registerReceiver(cloudSyncStateChangeReceiver, cloudSyncStateFilter);
        
		
		// kick off LogCaptureService if it's not already running and bind to it
		this.doBindService();
		
	}
	
	public class CaptureServiceReceiver extends BroadcastReceiver
    {
      @Override
        public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
        {
    	  	Log.d("OUTPUT", "BROADCAST RECEIVED  Sensors: " + intent.getIntExtra(getString(R.string.SENSOR_1), -1) + ", " + intent.getIntExtra(getString(R.string.SENSOR_2), -1) + ", " + intent.getIntExtra(getString(R.string.SENSOR_3), -1) + "   TimeStamp:  " + intent.getIntExtra(getString(R.string.TIMESTAMP), -1) + "   DateString:  " + intent.getStringExtra(getString(R.string.DATE_STRING)));
    	  	
    	  	LogEvent logEvent = new LogEvent(intent, context);
    	  	
    	  	if (logEvent.dateString != null) {
    	  		lastUpdateValueTextView.setText(logEvent.dateString);	
    	  	}
    	  	
    	  	ArrayList <SensorDescriptor> list = getList();
			 
			 for (int i = 0; i < list.size(); i++) {
				 
				 SensorDescriptor descriptor = list.get(i);
				 
				 int sensorValue = logEvent.getSensorValueAtIndex(i);
				 
				 descriptor.setCurrMV(sensorValue);
			 }
			 refreshList();    	  	
        }
    }
	
	public class CloudSyncStateChangeReceiver extends BroadcastReceiver
    {
      @Override
        public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
        {
    	  	Log.d("OUTPUT", "CloudSyncState Changed...");
    	  	refreshCloudSyncInterfaceState();
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
	        
			
			mBoundService.startCapture("/data/local/PB_ADC_C");	
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
	    appContext.persistState();
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
	
	/*
	 * 	private TextView lastUpdateValueTextView;
	private TextView cloudSyncStatusTextView;
	private Button toggleCloudSyncingButton;
	private ProgressBar cloudSyncProgressBar;
	 */
	
	private void setupToggleCloudSyncStateButtonListener() {
		toggleCloudSyncingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	ApplicationContext appContext = ((ApplicationContext)getApplicationContext());
            	Intent intent;
            	switch(appContext.currentCloudSyncState) {
	        		case CloudSyncStateNone: 
	        		case CloudSyncStateError:
	        			if (mBoundService != null) {
	        				
	        				// TODO check if tableId, username and password are set and act accordingly
	        				if (appContext.ftUsername != null && !appContext.ftUsername.equalsIgnoreCase("") && appContext.ftPassword != null && !appContext.ftPassword.equalsIgnoreCase("")) {
	        					mBoundService.makeFTConnection(appContext.ftTableId, appContext.ftUsername, appContext.ftPassword);
	        				} else {
	        					 android.content.Intent settingsIntent = new android.content.Intent(); 
	        					 settingsIntent.setClassName("com.phinominal.datalogger", "com.phinominal.datalogger.SettingsActivity");
	        			         startActivityForResult(new Intent(settingsIntent), 2);
	        				}

	        			} else {
	        				shortToast("Error: LogCaptureService not bound...");
	        			}
	        			break;
	        		case CloudSyncStateAuthenticating:  // Don't do anything if already trying to authenticate
	        			shortToast("Trying to authenticate...");
	        			break;
	        		case CloudSyncStateLogging:  // If logging, change to paused state
	        			appContext.currentCloudSyncState = ApplicationContext.CloudSyncState.CloudSyncStatePaused;
	        			intent = new Intent(CLOUD_SYNC_STATE_CHANGE_STRING);
	        			sendBroadcast(intent);
	        			break;
	        		case CloudSyncStatePaused:  // If paused, change to logging state
	        			appContext.currentCloudSyncState = ApplicationContext.CloudSyncState.CloudSyncStateLogging;
	        			intent = new Intent(CLOUD_SYNC_STATE_CHANGE_STRING);
	        			sendBroadcast(intent);
	        			break;
            	}
            }
        });
	}
	
	private void refreshCloudSyncInterfaceState() {
		
		handler.post(new Runnable() {
		    @Override
		    public void run() {
		      
		    	ApplicationContext appContext = ((ApplicationContext)getApplicationContext());
		    	switch(appContext.currentCloudSyncState) {
		    		case CloudSyncStateNone:
		    			toggleCloudSyncingButton.setEnabled(true);
		    			toggleCloudSyncingButton.setText("Start Cloud Sync");
		    			cloudSyncStatusTextView.setText("");
		    			cloudSyncProgressBar.setVisibility(ProgressBar.GONE);
		    			break;
		    		case CloudSyncStateAuthenticating:
		    			toggleCloudSyncingButton.setEnabled(false);
		    			cloudSyncStatusTextView.setText("Authenticating...");
		    			cloudSyncStatusTextView.invalidate();
		    			cloudSyncProgressBar.setVisibility(ProgressBar.VISIBLE);
		    			cloudSyncProgressBar.invalidate();
		    			
		    			break;
		    		case CloudSyncStateLogging:
		    			toggleCloudSyncingButton.setEnabled(true);
		    			toggleCloudSyncingButton.setText("Pause Cloud Sync");
		    			cloudSyncStatusTextView.setText("");
		    			cloudSyncProgressBar.setVisibility(ProgressBar.GONE);
		    			break;
		    		case CloudSyncStatePaused:
		    			toggleCloudSyncingButton.setEnabled(true);
		    			toggleCloudSyncingButton.setText("Resume Cloud Sync");
		    			cloudSyncStatusTextView.setText("");
		    			cloudSyncProgressBar.setVisibility(ProgressBar.GONE);
		    			break;
		    		case CloudSyncStateError:
		    			toggleCloudSyncingButton.setEnabled(true);
		    			toggleCloudSyncingButton.setText("Retry Cloud Sync");
		    			cloudSyncStatusTextView.setText("Auth Error");
		    			cloudSyncProgressBar.setVisibility(ProgressBar.GONE);
		    			break;
		    	}
		    	
		    }
		});
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
		appContext.persistState();
		android.os.Process.killProcess(android.os.Process.myPid());
		this.finish();}

	public boolean onPrepareOptionsMenu(Menu menu) {
		
		 //android.content.Intent intent = new android.content.Intent(); 
         //intent.setClassName("com.phinominal.datalogger", "com.phinominal.datalogger.SensorList");
         //startActivity(new Intent(intent));
		
		android.content.Intent settingsIntent = new android.content.Intent(); 
		settingsIntent.setClassName("com.phinominal.datalogger", "com.phinominal.datalogger.SettingsActivity");
        startActivityForResult(new Intent(settingsIntent), 2);
        
		return false;
	}
	
	
	private void shortToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

}
	
	
	