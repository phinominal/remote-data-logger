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
		if (true /*sensors == null*/) {
			sensors = new ArrayList<SensorDescriptor>();
			sensors.add(new SensorDescriptor("Soil Humidity", 7000, 50000, "Dry", "Wet", 0));
			sensors.add(new SensorDescriptor("Temperature", 0, 232, "Dark", "Light", 1));
			sensors.add(new SensorDescriptor("Photon Density", 0, 4000, "Cold", "Hot", 2));
			sensors.add(new SensorDescriptor("Infrared", 0, 4000, "Low", "High", 3));
			sensors.add(new SensorDescriptor("Photo Diode", 0, 4000, "Close", "Far", 5));
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
