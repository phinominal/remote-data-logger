package com.phinominal.datalogger;

import android.content.Intent;
import android.content.res.Resources;

public class LogEvent {
	
	
	public int sensor1;
	public int sensor2;
	public int sensor3;
	public int sensor4;
	public int sensor5;
	public int sensor6;
	public int timeStamp;
	public String dateString;
	
	
	public LogEvent(int sensor1, int sensor2, int sensor3, int timeStamp, String dateString) {
		this.sensor1 = sensor1;
		this.sensor2 = sensor2;
		this.sensor3 = sensor3;
		this.timeStamp = timeStamp;
		this.dateString = dateString;
	}
	
	public void addValuesToIntent(Intent intent) {
		intent.putExtra(Resources.getSystem().getString(R.string.SENSOR_1), sensor1);
		intent.putExtra(Resources.getSystem().getString(R.string.SENSOR_2), sensor2);
		intent.putExtra(Resources.getSystem().getString(R.string.SENSOR_3), sensor3);
		intent.putExtra(Resources.getSystem().getString(R.string.SENSOR_4), sensor4);
		intent.putExtra(Resources.getSystem().getString(R.string.SENSOR_5), sensor5);
		intent.putExtra(Resources.getSystem().getString(R.string.SENSOR_6), sensor6);
		intent.putExtra(Resources.getSystem().getString(R.string.TIMESTAMP), timeStamp);
		intent.putExtra(Resources.getSystem().getString(R.string.DATE_STRING), dateString);
	}
}

