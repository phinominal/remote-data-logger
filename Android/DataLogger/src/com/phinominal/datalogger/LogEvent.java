package com.phinominal.datalogger;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

public class LogEvent {
	
	
	public int sensor1 = -1;
	public int sensor2 = -1;
	public int sensor3 = -1;
	public int sensor4 = -1;
	public int sensor5 = -1;
	public int sensor6 = -1;
	public int timeStamp;
	public String dateString;
	
	
	public LogEvent(int sensor1, int sensor2, int sensor3, int timeStamp, String dateString) {
		this.sensor1 = sensor1;
		this.sensor2 = sensor2;
		this.sensor3 = sensor3;
		this.timeStamp = timeStamp;
		this.dateString = dateString;
	}
	
	public LogEvent(Intent intent, Context context) {
		this.sensor1 = intent.getIntExtra(context.getString(R.string.SENSOR_1), -1);
		this.sensor2 = intent.getIntExtra(context.getString(R.string.SENSOR_2), -1);
		this.sensor3 = intent.getIntExtra(context.getString(R.string.SENSOR_3), -1); 
		this.timeStamp = intent.getIntExtra(context.getString(R.string.TIMESTAMP), -1);
		this.dateString = intent.getStringExtra(context.getString(R.string.DATE_STRING));
	}
	
	public void addValuesToIntent(Intent intent, Context context) {
		intent.putExtra(context.getString(R.string.SENSOR_1), sensor1);
		intent.putExtra(context.getString(R.string.SENSOR_2), sensor2);
		intent.putExtra(context.getString(R.string.SENSOR_3), sensor3);
		intent.putExtra(context.getString(R.string.SENSOR_4), sensor4);
		intent.putExtra(context.getString(R.string.SENSOR_5), sensor5);
		intent.putExtra(context.getString(R.string.SENSOR_6), sensor6);
		intent.putExtra(context.getString(R.string.TIMESTAMP), timeStamp);
		intent.putExtra(context.getString(R.string.DATE_STRING), dateString);
	}
	
}

