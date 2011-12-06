package com.phinominal.datalogger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;



public class ApplicationContext extends Application {
	
	private static final String PREFS_NAME = "AppContextPrefs";
	private static final String FT_USERNAME = "ftUsername";
	private static final String FT_PASSWORD = "ftPassword";
	private static final String FT_TABLE_ID = "ftTableId";
	
	public static enum CloudSyncState {
	    CloudSyncStateNone, CloudSyncStateAuthenticating, CloudSyncStateLogging, CloudSyncStatePaused,
	    CloudSyncStateError
	}
	
	public CloudSyncState currentCloudSyncState;
	
	public ArrayList <SensorDescriptor> sensors = null;
	
	private static String filename = "sensor_list_object";
	
	
	public String ftUsername = "";//"phinominaltechnology";
	public String ftPassword = "";//"Sk8ordie!";
	public long ftTableId = 2154839;
	
	@SuppressWarnings("unchecked")
	public void onCreate () {
		
		currentCloudSyncState = CloudSyncState.CloudSyncStateNone;
		
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try
		{
			fis = openFileInput(filename); 
			in = new ObjectInputStream(fis);
			sensors = (ArrayList <SensorDescriptor>) in.readObject();
			in.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		catch(ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}
		
		if (sensors == null) {
			sensors = new ArrayList<SensorDescriptor>();
			sensors.add(new SensorDescriptor("Sensor 1", 7000, 50000, "", "", 0, true));
			sensors.add(new SensorDescriptor("Sensor 2", 0, 232, "", "", 1, true));
			sensors.add(new SensorDescriptor("Sensor 3", 0, 4000, "", "", 2, true));
			sensors.add(new SensorDescriptor("Sensor 4", 0, 4000, "", "", 3, true));
			sensors.add(new SensorDescriptor("Sensor 5", 0, 4000, "", "", 4, true));
			sensors.add(new SensorDescriptor("Sensor 6", 0, 4000, "", "", 5, true));
		}
		
		// Get preference values
		
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    this.ftUsername = settings.getString(ApplicationContext.FT_USERNAME, "");
	    this.ftPassword = settings.getString(ApplicationContext.FT_PASSWORD, "");
	    this.ftTableId = settings.getLong(ApplicationContext.FT_TABLE_ID, 0);
	}
	
	
	public void persistState () {
		
		if (this.sensors != null) {
			
			FileOutputStream fos = null;
			ObjectOutputStream out = null;
			try
			{
				fos =  openFileOutput(filename, Context.MODE_WORLD_READABLE); 
				out = new ObjectOutputStream(fos);
				out.writeObject(sensors);
				out.close();
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}	
		}
		
	      // We need an Editor object to make preference changes.
	      // All objects are from android.context.Context
	      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString(ApplicationContext.FT_USERNAME, this.ftUsername);
	      editor.putString(ApplicationContext.FT_PASSWORD, this.ftPassword);
	      editor.putLong(ApplicationContext.FT_TABLE_ID, this.ftTableId);

	      // Commit the edits!
	      editor.commit();
	}
	
	public ArrayList <SensorDescriptor> getSelectedSensors() {
		ArrayList <SensorDescriptor> selectedSensors = new ArrayList<SensorDescriptor>();
		
		for (int i = 0; i < sensors.size(); i++) {
			SensorDescriptor tempDescriptor = sensors.get(i);
			if (tempDescriptor.isSelected()) {
				selectedSensors.add(tempDescriptor);
			}
		}
		
		return selectedSensors;
	}
	
	
	

}
