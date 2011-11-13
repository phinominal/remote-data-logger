package com.phinominal.datalogger;

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
		
		
		Intent intent = new Intent(CAPTURE_EVENT);
        intent.putExtra(TIMESTAMP, new Date().getTime());
        sendBroadcast(intent);
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
