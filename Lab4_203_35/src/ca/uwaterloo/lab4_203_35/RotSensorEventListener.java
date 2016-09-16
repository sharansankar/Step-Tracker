package ca.uwaterloo.lab4_203_35;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.ImageView;
import android.widget.TextView;

public class RotSensorEventListener implements SensorEventListener {
	public static float smoothedDeg = 0;
	 
	ImageView compass ;
	public RotSensorEventListener(ImageView compass){
		
		this.compass = compass; 
	} 
	public void onAccuracyChanged(Sensor s, int i) {
   	
   	 
    }
    
    public void onSensorChanged(SensorEvent se) {
      if (se.sensor.getType() == Sensor.TYPE_ORIENTATION) {
    	  lowPassFilter(se.values[0]); 
    	  compass.setRotation(360f - se.values[0]); 
   	   
    	 // north.setText(Float.toString(smoothedDeg));
    	  
    	  //  output.setText(4); 
   	   //output.setText((int) se.values[0]);
   	  
   	   // the variable se.values is an array of type int[] or double[].
        // the first value (se.values[0]) contains the value
        // of the light sensor. store it somewhere useful.
      	
      } 
}
    private void lowPassFilter(float val){
   	 smoothedDeg+= (val-smoothedDeg)/30; // divided by c 
   	 
    }
    public float get(){
    	return smoothedDeg; 
    }
   
}
