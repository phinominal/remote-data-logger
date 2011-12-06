package com.phinominal.datalogger;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.phinominal.datalogger.SensorArrayAdapter.ViewHolder;

public class SensorList extends ListActivity {

	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.sensor_list);
		
		ApplicationContext appContext = ((ApplicationContext)getApplicationContext());
		
		ArrayAdapter<SensorDescriptor> adapter = new SensorArrayAdapter(this,
				appContext.sensors);
		setListAdapter(adapter);
	}

	// Doesn't work for some reason
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked
		SensorDescriptor clickedSensor = (SensorDescriptor)this.getListAdapter().getItem(position);
		View clickedSensorView = getListView().getChildAt(position);
		
		clickedSensor.setSelected(!clickedSensor.isSelected());
		ViewHolder viewHolder = (ViewHolder)clickedSensorView.getTag();
		viewHolder.checkbox.setChecked(clickedSensor.isSelected());
	}
	

	// menu crap: just removes the application
	public void nuketheapp(){
		ApplicationContext appContext = ((ApplicationContext)getApplicationContext());
		appContext.persistState();
		android.os.Process.killProcess(android.os.Process.myPid());
		this.finish();}
	public boolean onPrepareOptionsMenu(Menu menu) {nuketheapp();return true;}

}