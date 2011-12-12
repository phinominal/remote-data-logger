package com.phinominal.datalogger;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SensorArrayAdapter extends ArrayAdapter <SensorDescriptor> {

	private final List<SensorDescriptor> list;
	private final Activity context;
	private boolean provideFullSensorViews = false;
	
	// super lame that I can't figure out how to invoke another constructor
	public SensorArrayAdapter(Activity context, List<SensorDescriptor> list) {
		super(context, R.layout.sensor_descriptor_row, list);
		this.provideFullSensorViews = false;
		this.context = context;
		this.list = list;
	}
	
	public SensorArrayAdapter(Activity context, List<SensorDescriptor> list, boolean provideFullSensorViews) { 
		super(context, R.layout.sensor_descriptor_row, list);
		this.provideFullSensorViews = provideFullSensorViews;
		this.context = context;
		this.list = list;
	}

	public static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
		protected ImageView sensorIcon;
	}
	
	public static class FullViewHolder {
		protected TextView sensorNameTextView;
		protected ProgressBar mvProgressBar;
		protected Button playPauseButton;
		protected TextView mvTextView;
		protected Button moreInfoButton;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (this.provideFullSensorViews) {
			return this.getFullSensorView(position, convertView, parent);
		} else {
			return this.getSimpleSensorChooserView(position, convertView, parent);
		}
	}
	
	private View getSimpleSensorChooserView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.sensor_descriptor_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) view.findViewById(R.id.label);
			viewHolder.sensorIcon = (ImageView)view.findViewById(R.id.sensor_icon);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							SensorDescriptor element = (SensorDescriptor) viewHolder.checkbox
									.getTag();
							element.setSelected(buttonView.isChecked());

						}
					});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		SensorDescriptor sensorDescriptor = list.get(position);  
		holder.text.setText(list.get(position).getName());
		holder.checkbox.setChecked(list.get(position).isSelected());
		int tag = sensorDescriptor.getTag();
		if (tag == 0) {
			holder.sensorIcon.setImageResource(R.drawable.soil);
		} else if (tag == 1) {
			holder.sensorIcon.setImageResource(R.drawable.photo_diode1);
		} else if (tag == 2) {
			holder.sensorIcon.setImageResource(R.drawable.thermistor);
		} else if (tag == 3) {
			holder.sensorIcon.setImageResource(R.drawable.infrared);
		} else if (tag == 5) {
			holder.sensorIcon.setImageResource(R.drawable.soil_diode2);
		}
		//holder.sensorIcon.setImageResource(R.drawable.city_logo);
		return view;
	}

	
	private View getFullSensorView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.full_sensor_descriptor_row, null);
			
			final FullViewHolder viewHolder = new FullViewHolder();
			viewHolder.sensorNameTextView = (TextView)view.findViewById(R.id.sensor_name_textview);
			viewHolder.mvProgressBar = (ProgressBar)view.findViewById(R.id.mv_progress_bar);
			viewHolder.mvTextView = (TextView) view.findViewById(R.id.mv_textview);
			viewHolder.playPauseButton = (Button)view.findViewById(R.id.play_pause_button);
			viewHolder.moreInfoButton = (Button)view.findViewById(R.id.more_info_button);
			
		
			view.setTag(viewHolder);
			
		} else {
			view = convertView;	
		}
		
		// Customize the row for the specific sensor and its current values
		// TODO:  THIS LOGIC IS WACK RIGHT NOW!!!  All of the mapping logic should be done inside the SensorDescriptor class!!! 
		
		FullViewHolder holder = (FullViewHolder) view.getTag();
		
		
		SensorDescriptor sensor = list.get(position); // use AppContext instead of list as backing store here and get
		
		holder.mvProgressBar.setMax(sensor.getRangeMax());
		if (sensor.getTag() == 0) {
			holder.mvProgressBar.setMax(14000);
			holder.mvProgressBar.setProgress((int)sensor.getCurrMV() - 7000);
		} else {
			holder.mvProgressBar.setMax(sensor.getRangeMax());
			holder.mvProgressBar.setProgress((int)sensor.getCurrMV());
		}
		holder.sensorNameTextView.setText(sensor.getName());
		//float mv = sensor.getCurrMV() / 5000;
		
		holder.mvTextView.setText(Float.toString(sensor.getCurrMV()));
		//holder.mvProgressBar.setProgress((int)sensor.getMappedMV());
		
		if (sensor.getCurrMV() == -1) {
			view.setEnabled(false);
			holder.mvTextView.setEnabled(false);
			holder.sensorNameTextView.setEnabled(false);
		} else {
			view.setEnabled(true);
			holder.mvTextView.setEnabled(true);
			holder.sensorNameTextView.setEnabled(true);
		}
		
		
		return view;
	}

	
	public List<SensorDescriptor> getList() {
		return list;
	}

}
