package com.phinominal.datalogger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.phinominal.ftclient.ClientLogin;
import com.phinominal.ftclient.FtClient;

public class LogCaptureService extends Service {
	
	private long tableid = 0;
	private FtClient ftclient = null;
	
	private int bufferSize = 10;
	private ArrayList <LogEvent> logBuffer = new ArrayList <LogEvent>();
	
	
	private int updateIntervalMillis = 1000;
	
	
	private Handler mHandler = new Handler();   
	private Runnable mUpdateTimeTask = new Runnable() {
 	   public void run() {
 		   captureLast();
 	       mHandler.postDelayed(this, updateIntervalMillis);
 	   }
 	};
 
	
	public void startCapture() {
		mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.post(mUpdateTimeTask);
	}
	
	private void captureLast() {
		//Log.d("LOG", "Printing contents of last directory...");
		
		try {
	   		 InputStream inStream = new FileInputStream("/data/local/PB_ADC_C"); 
	   		 
	   		 if (inStream != null) {
	   			 InputStreamReader inputreader = new InputStreamReader(inStream);
	   			 BufferedReader buffreader = new BufferedReader(inputreader);
	   			 
	   			 String line = buffreader.readLine();
	   			 
	   			 
	   			 if (line != null) {
	   				 
	   				 
	   				 LogEvent logEvent = getLogEventFromCaptureLine(line);
	   				 
	   				 if (logEvent != null) {
	   					 
	   					 
	   					Intent intent = new Intent(Resources.getSystem().getString(R.string.CAPTURE_EVENT));
	   					logEvent.addValuesToIntent(intent);
	   					
	   					sendBroadcast(intent);
	   					
	   					logBuffer.add(logEvent);
	   					
	   					if (logBuffer.size() >= bufferSize) {
	   						Log.d("LOG", "Publishing buffer");
	   						publishBuffer();
	   					}

	   				 }
	   				
	   			 }
	   		 } 
	   	 } catch (Exception e) {
	   		 
	   		 Log.d("Exception", e.toString());
	   	 }
	}
	
	
	private LogEvent getLogEventFromCaptureLine(String line) {
		
		String [] components = line.split(",");
			 
			 int [] sensorValues = new int[6];
			 int timeStamp = -1;
			 String timeString = "";
			 int sensorCount = 0;
			 int timeCount = 0;
			 int i = 0;
			 boolean parsingSensors = false;
			 boolean parsingTime = false;
			 
			 while (i < components.length) {
				 String currComponent = components[i].trim();
				 
				 if (currComponent.equalsIgnoreCase("S")) {
					 parsingSensors = true;
				 } else if (currComponent.equalsIgnoreCase("T")) {
					 parsingSensors = false;
					 parsingTime = true;
				 } else if (parsingSensors) {
					 sensorValues[sensorCount] = Integer.parseInt(currComponent);
					 
					 sensorCount++;
				 } else if (parsingTime) {
					 if (timeCount == 0) {
						 timeStamp = Integer.parseInt(currComponent);
						 timeCount++;
					 } else {
						 timeString = currComponent;
					 }
				 }
				 
				 i++;
			 }	 
			 
			 LogEvent logEvent = new LogEvent(sensorValues[0], sensorValues[1], sensorValues[2], timeStamp, timeString);
			 return logEvent;
	}
	
	
	private void publishBuffer() {
		
		Thread thread = new Thread()
			 {
			     @Override
			     public void run() {
			         try {
			        	 		        	 
			        	StringBuilder batchInsert = new StringBuilder();
			        					
						for (int i = 0; i < logBuffer.size(); i++) {
							
							LogEvent logEvent = logBuffer.get(i);
							
							
							StringBuilder insert = new StringBuilder();
					        insert.append("INSERT INTO ");
					        insert.append(tableid);
					        insert.append(" (Sensor1, Sensor2, Sensor3, TimeStamp, DateString) VALUES ");
					        
							insert.append("(");
						        insert.append(logEvent.sensor1);
						        insert.append(", ");
						        insert.append(logEvent.sensor2);
						        insert.append(", ");
						        insert.append(logEvent.sensor3);
						        insert.append(", ");
						        insert.append(logEvent.timeStamp);
						        insert.append(", '");
						        insert.append(logEvent.dateString);
						        insert.append("'); ");
						     
						     batchInsert.append(insert);
						}
	
						logBuffer.clear();
						
	   			        // Save the data to Fusion Tables
	   			        Log.d("Logging query", batchInsert.toString());
	   			        ftclient.query(batchInsert.toString());
			         } catch (Exception e) {
			        	 Log.d("FT Posting Exception", e.toString());
			         }
			     } 
			 };
			 
			 thread.start();
	}
	
	public void makeFTConnection(long tableid, String username, String password) {
		this.tableid = tableid;
		try {
			// Initialize FTClient
			String token = ClientLogin.authorize(username, password);
			ftclient = new FtClient(token);
			
		} catch (Exception e) {
			Log.d("OUTPUT", "FT ClientLogin Failed  " + Long.toString(System.currentTimeMillis()));
   		 	Log.d("Exception", e.toString());
   	 }
	}
	

    @Override
    public void onCreate() {
    	Log.d("SERVICE", "ON SERVICE Create");
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
        handleStart(intent);
    }
    
    
    @Override
    public void onDestroy() {
        
        // Tell the user we stopped.
        Toast.makeText(this, "LogCaptureService ended", Toast.LENGTH_SHORT).show();
    }

    
    
    // **********************************************************************
    // Code that allows clients to bind to this service
    // **********************************************************************
    
    
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LogCaptureBinder();
	
	
	
	/**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LogCaptureBinder extends Binder {
        LogCaptureService getService() {
            return LogCaptureService.this;
        }
    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent arg0) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		stopSelf();
		return false;
	}
	
	private void handleStart(Intent intent) {
		Log.d("SERVICE", "ON SERVICE START");
	}
	

}
