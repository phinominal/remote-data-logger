package com.phinominal.datalogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.Application;
import android.content.Context;



public class ApplicationContext extends Application {
	
	public ArrayList <SensorDescriptor> sensors = null;
	
	private static String filename = "sensor_list_object";
	
	@SuppressWarnings("unchecked")
	public void onCreate () {
		
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
		
		// currently creating the sensor array every time...
		if (sensors == null) {
			sensors = new ArrayList<SensorDescriptor>();
			sensors.add(new SensorDescriptor("Sensor 1", 7000, 50000, "", "", 0, true));
			sensors.add(new SensorDescriptor("Sensor 2", 0, 232, "", "", 1, true));
			sensors.add(new SensorDescriptor("Sensor 3", 0, 4000, "", "", 2, true));
			sensors.add(new SensorDescriptor("Sensor 4", 0, 4000, "", "", 3, true));
			sensors.add(new SensorDescriptor("Sensor 5", 0, 4000, "", "", 4, true));
			sensors.add(new SensorDescriptor("Sensor 6", 0, 4000, "", "", 5, true));
		}
	}
	
	
	public void persistSensorState () {
		
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
