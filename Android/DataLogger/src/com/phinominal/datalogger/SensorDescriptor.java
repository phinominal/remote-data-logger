package com.phinominal.datalogger;

import java.io.Serializable;

public class SensorDescriptor implements Serializable {
	
	static final long serialVersionUID = 1;
	
	private String name;
	private boolean selected;
	private float currMV;
	private int rangeMin;
	private int rangeMax;
	private String rangeMinLabel;
	private String rangeMaxLabel;
	private int tag;
	
	public SensorDescriptor(String name, int rangeMin, int rangeMax, String rangeMinLabel, String rangeMaxLabel, int tag) {
		this.name = name;
		this.rangeMin = rangeMin;
		this.rangeMax = rangeMax;
		this.rangeMinLabel = rangeMinLabel;
		this.rangeMaxLabel = rangeMaxLabel;
		this.tag = tag;
	}
	
	public float getMappedMV() {
		
		//Y = (X-A)/(B-A) * (D-C) + C
	
		return (((int)currMV) - rangeMin ) / (rangeMax - rangeMin) * (100 - 0) + 0; 
	}
	
	
	// getters & setters
	
	public int getRangeMin() {
		return rangeMin;
	}

	public void setRangeMin(int rangeMin) {
		this.rangeMin = rangeMin;
	}

	public int getRangeMax() {
		return rangeMax;
	}

	public void setRangeMax(int rangeMax) {
		this.rangeMax = rangeMax;
	}

	public String getRangeMinLabel() {
		return rangeMinLabel;
	}

	public void setRangeMinLabel(String rangeMinLabel) {
		this.rangeMinLabel = rangeMinLabel;
	}

	public String getRangeMaxLabel() {
		return rangeMaxLabel;
	}

	public void setRangeMaxLabel(String rangeMaxLabel) {
		this.rangeMaxLabel = rangeMaxLabel;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public float getCurrMV() {
		return currMV;
	}

	public void setCurrMV(float currMV) {
		this.currMV = currMV;
	}

	public SensorDescriptor(String name) {
		this.name = name;
		selected = false;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
}
