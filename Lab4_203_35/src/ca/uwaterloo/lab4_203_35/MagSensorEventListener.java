package ca.uwaterloo.lab4_203_35;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.ImageView;

public class MagSensorEventListener implements SensorEventListener {
	ImageView compass; 
	public MagSensorEventListener(ImageView compass){
		this.compass = compass; 
		
	} 
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

}
