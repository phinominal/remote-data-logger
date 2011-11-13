package com.phinominal.datalogger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LogCaptureService extends Service {
	
	public static String CAPTURE_EVENT = "com.phinominal.datalogger.custom.intent.action.CAPTURE_EVENT";
	
	public static String TIMESTAMP = "com.phinominal.datalogger.logcaptureservice.TIMESTAMP";
	public static String DATE_STRING = "com.phinominal.datalogger.logcaptureservice.DATE_STRING";
	public static String SENSOR_1 = "com.phinominal.datalogger.logcaptureservice.SENSOR_1";
	public static String SENSOR_2 = "com.phinominal.datalogger.logcaptureservice.SENSOR_2";
	public static String SENSOR_3 = "com.phinominal.datalogger.logcaptureservice.SENSOR_3";
	public static String SENSOR_4 = "com.phinominal.datalogger.logcaptureservice.SENSOR_4";
	public static String SENSOR_5 = "com.phinominal.datalogger.logcaptureservice.SENSOR_5";
	public static String SENSOR_6 = "com.phinominal.datalogger.logcaptureservice.SENSOR_6";
	
	
	
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
	
	public void publicMethod() {
		Log.d("LOG", "Public method called!!!!");
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
	   				 String [] components = line.split(",");
	   				 
	   				 int [] sensorValues = new int[6];
	   				 int timeStamp = -1;
	   				 String timeString = "";
	   				 int sensorCount = 0;
	   				 int timeCount = 0;
	   				 int i = 0;
	   				 boolean parsingSensors = false;
	   				 boolean parsingTime = false;
	   				 
	   				Intent intent = new Intent(CAPTURE_EVENT);
	   				
	   				 
	   				 while (i < components.length) {
	   					 String currComponent = components[i].trim();
	   					 
	   					 if (currComponent.equalsIgnoreCase("S")) {
	   						 parsingSensors = true;
	   					 } else if (currComponent.equalsIgnoreCase("T")) {
	   						 parsingSensors = false;
	   						 parsingTime = true;
	   					 } else if (parsingSensors) {
	   						 sensorValues[sensorCount] = Integer.parseInt(currComponent);
	   						 intent.putExtra(sensorStringForSensorNumber(sensorCount), sensorValues[sensorCount]);
	   						 sensorCount++;
	   					 } else if (parsingTime) {
	   						 if (timeCount == 0) {
	   							 timeStamp = Integer.parseInt(currComponent);
	   							intent.putExtra(TIMESTAMP, timeStamp);
	   							 timeCount++;
	   						 } else {
	   							 intent.putExtra(DATE_STRING, currComponent);
	   							 timeString = currComponent;
	   						 }
	   					 }
	   					 
	   					 i++;
	   				 }	   				 
	   				 
	   				 sendBroadcast(intent);
	   				  
	   				 //Log.d("CaptureInput", line);
	   				 
	   				 
	   			 }
	   		 } 
	   	 } catch (Exception e) {
	   		 
	   		 Log.d("Exception", e.toString());
	   	 }
	}
	
	private String sensorStringForSensorNumber(int i) {
		String sensorString = "";
		switch (i) {
			case 0:
				sensorString = SENSOR_1;
				break;
			case 1:
				sensorString = SENSOR_2;
				break;
			case 2:
				sensorString = SENSOR_3;
				break;
			case 3:
				sensorString = SENSOR_4;
				break;
			case 4:
				sensorString = SENSOR_5;
				break;
			case 5:
				sensorString = SENSOR_6;
				break;
			default:
					
		}
		return sensorString;
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
        Toast.makeText(this, "LogCaptureService started", Toast.LENGTH_SHORT).show();
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
