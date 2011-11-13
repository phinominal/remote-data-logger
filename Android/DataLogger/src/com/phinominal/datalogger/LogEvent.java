package com.phinominal.datalogger;

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
}

