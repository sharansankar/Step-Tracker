package ca.uwaterloo.lab4_203_35;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mapper.IMapperListener;
import mapper.InterceptPoint;
import mapper.MapLoader;
import mapper.Mapper;
import mapper.PedometerMap;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Build;
import java.lang.Math; 



public class MainActivity extends Activity {
	static int step = 0; 
	static boolean buttonClick = false; 
	
	static Mapper mv;
	static PedometerMap map;
	@Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      if (savedInstanceState == null) {
          getFragmentManager().beginTransaction()
                  .add(R.id.container, new PlaceholderFragment())
                  .commit();
      }
      
     
      
   /*   MapLoader loader = new MapLoader();
     map = loader.loadMap(getExternalFilesDir(null), "E2-3344.svg");
     
      mv = new Mapper(getBaseContext(), 800, 500, 20,20);
		registerForContextMenu(mv); 
		*/ 
	
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		mv.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item) || mv.onContextItemSelected(item);
	}
	

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      
      
      return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();
      if (id == R.id.action_settings) {
          return true;
      }
      return super.onOptionsItemSelected(item);
  }
  
  /**
   * A placeholder fragment containing a simple view.
   */
 public static class PlaceholderFragment extends Fragment {

      public PlaceholderFragment() {
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
              Bundle savedInstanceState) {
          View rootView = inflater.inflate(R.layout.fragment_main, container, false);
          
          
          LinearLayout layout1 = (LinearLayout) rootView.findViewById(R.id.layout1);
          
          
          
          LineGraphView graph; 
          graph = new LineGraphView(getActivity().getApplicationContext(),100,Arrays.asList("x", "y", "z"));
          graph.setVisibility(View.VISIBLE);
          
          layout1.addView(graph);
          
          TextView accelX = (TextView)(rootView.findViewById(R.id.accelX));
          TextView accelY = ( TextView)(rootView.findViewById(R.id.accelY));
          TextView accelZ = ( TextView)(rootView.findViewById(R.id.accelZ));
          TextView steps = ( TextView)(rootView.findViewById(R.id.stepCounter));
          TextView destinationLabel = (TextView)(rootView.findViewById(R.id.travelling));
          ImageView compass = (ImageView)(rootView.findViewById(R.id.compass)); 
          
          
          TextView N = (TextView)(rootView.findViewById(R.id.North));
          TextView E = ( TextView)(rootView.findViewById(R.id.East));
          
          
             
        //Accelerometer
          //declaring instance of sensorManger
            SensorManager sensorManager2 = (SensorManager) rootView.getContext().getSystemService(SENSOR_SERVICE); 
         //created an object called "light sensor" and set to the type of light sensor
            Sensor accelSensor = sensorManager2.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //delcared event listener and set tv1 to be the outputted textview 
            Sensor orSensor = sensorManager2.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            SensorEventListener orientObject = new RotSensorEventListener(compass);
            sensorManager2.registerListener(orientObject, orSensor,SensorManager.SENSOR_DELAY_FASTEST);
           
            
            
            
       //registering the lightsensor with the sensor manager    
            
                        
            
            //Button Zeroing 
           
           
            
           
            MapLoader loader = new MapLoader();
            map = loader.loadMap(rootView.getContext().getExternalFilesDir(null), "E2-3344.svg");
             
             mv = new Mapper(getActivity().getApplicationContext(), 1800, 1000, 30, 27);
       		registerForContextMenu(mv);
       		mv.setMap(map);
            
           layout1.addView(mv);
           mv.setVisibility(View.VISIBLE);
           
          /* PositionListener listener = new PositionListener(mv); 
           mv.addListener(listener);
           Log.d("j","this is a test log output asdkasdkjhaskdakfjhkdjfhasdkjhfa"); 
   	    */
           PositionListener listener = new PositionListener(mv,orientObject, destinationLabel );
           mv.addListener(listener);
           
           final SensorEventListener accelObject = new AccelSensorEventListener(N,E,accelX,accelY, accelZ, graph,steps, step, (RotSensorEventListener) orientObject, listener);
           sensorManager2.registerListener(accelObject, accelSensor,SensorManager.SENSOR_DELAY_FASTEST);
          
           Button button = (Button) (rootView.findViewById(R.id.resetButton));
           
           button.setOnClickListener(new View.OnClickListener() {
               public void onClick(View v) {
                   // Perform action on click
               
               	((AccelSensorEventListener) accelObject).Reset(); 

               }
           });
           
           return rootView; 
      }
  }
}
class AccelSensorEventListener implements SensorEventListener {
    
	float[] smoothed = {0,0,0}; 
	int state = 0;
	 
	TextView outputx;
	TextView outputy;
	TextView outputz;
	TextView stepCount; 
	TextView northLabel; 
	TextView eastLabel; 
	LineGraphView graph1;
	PositionListener position; 
	static int stepCounter ;
	static RotSensorEventListener orient; 
	static double north = 0; 
	static double east = 0; 
    public AccelSensorEventListener(TextView n,TextView e,TextView x, TextView y, TextView z, LineGraphView graph, TextView s, int step, RotSensorEventListener orientObject, PositionListener position ){
      outputx = x;
      outputy=y; 
      outputz=z; 
      northLabel = n; 
      eastLabel = e; 
      graph1= graph; 
      stepCount= s; 
      stepCounter= step ;
      orient= orientObject; 
      this.position = position; 
    }
    
    public void reset(){
    	stepCounter=0;
    }
     
     public void onAccuracyChanged(Sensor s, int i) {
    	 
    	 
     }
     
     public void onSensorChanged(SensorEvent se) {
       if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
    	 
    	   //  output.setText(4); 
    	   //output.setText((int) se.values[0]);
    	  
    	   // the variable se.values is an array of type int[] or double[].
         // the first value (se.values[0]) contains the value
         // of the light sensor. store it somewhere useful.
       lowPassFilter(se.values); 	
       
       } 
       
                   
   }
     public void lowPassFilter(float[] val){
    	 smoothed[0]+= (val[0]-smoothed[0])/25; // divided by c 
    	 smoothed[1] += (val[1]-smoothed[1])/25; 
    	 smoothed[2] += (val[2]-smoothed[2])/25; 
    	// System.out.println(smoothed[2]); 
    	 graph1.addPoint(smoothed);//add array of points 
    	 outputx.setText( "x=" + Float.toString(smoothed[0]));
    	 outputy.setText("y= " + Float.toString(smoothed[1]));
    	 outputz.setText("z= " + Float.toString(smoothed[2]));
    	 
    	stateSystem(smoothed[2]); 
    	 
		
     }
     private void stateSystem(float z){
    	 if(state== 0 && z >= 0 && z<= 0.6){
    		 //State1(smoothed[0], smoothed[1], smoothed[2]);
    		 state = 1; 
    	 }
    	 else if(state ==1 &&z> 0.6 && z<= 1.4){
    		 state = 2; 
    		 //step++;
        	 //stepCount.setText("Number of steps = " + step); 
    	 } 
    	 else if(state == 2 && z>0.6  && z<=1.4){
    		 state=3; 
    		// step++;
        	// stepCount.setText("Number of steps = " + step); 
    	 }
    	 else if(state ==3 && z>0 && z<=0.6){
    		 state = 4;  
    	 }
    	 else if (state==4 && z>-0.4 && z<= 0 ){
    		 state =5 ; 
    	 }
    	 else if (state==5 && z>-0.4 && z<= 0.1 ){
    		 stepCounter++;
    		 stepCount.setText("Number of steps = " + stepCounter); 
    		 float angle =  orient.get(); 
        	 north += Math.cos(Math.toRadians(angle)); 
        	 east += Math.sin(Math.toRadians(angle)); 
        	 northLabel.setText("North= " + north); 
        	 eastLabel.setText("East = " + east); 
     		 state =0 ; 
     		position.movedLocation(orient.get()); 
     	 }
    	 //stepCount.setText("Current State= " + state); 
    	 
    	
     }
     public void Reset(){
    	 stepCount.setText("Number of steps = 0");
    	 stepCounter=0; 
    	 north =0; 
    	 east = 0; 
    	 northLabel.setText("North= " + north); 
    	 eastLabel.setText("East = " + east); 
     } 
}


class MakePath{
	List <PointF> pathPoints; 
	PointF destination ; 
	PointF startpoint; 
	Mapper mv; 
	public MakePath(Mapper mv ,PointF start, PointF end){
		pathPoints = new ArrayList<PointF>(); 
		startpoint= start; 
		destination= end; 
		this.mv = mv; 
		pathCalculation(startpoint); 
	}
	public void pathCalculation(PointF current){
		System.out.println( "Current: " + current + " destination = " + destination); 
		
		pathPoints.clear(); 
		
		//add current location to 
		if (pathPoints.isEmpty()){
			pathPoints.add(current); 
		}
		//determine the current point region
		//region 1 
	//	System.out.println("intersections = " + mv.calculateIntersections(current, destination).isEmpty());
	//	System.out.println("pathpoints are " + pathPoints);
		
		if ((mv.calculateIntersections(current, destination).isEmpty())){
			
			pathPoints.add(destination); 
			mv.setUserPath(pathPoints); 
			return; 
			
		}
		else{
		//region 1
		if ((3 < current.x && current.x <7.3 && current.y < 18) || (10 < current.x && current.x <14 && current.y < 18)  || (17 < current.x && current.x < 21.75 && current.y < 18) || (17 < current.x && current.x < 22.35 && current.y < 18&& current.y > 7) ){
			getToBaseline(current); 
			System.out.println("start is in region 1");
		}
		//region 2 
		else if (current.x < 5 && current.y < 22 && current.y > 18 ){
			getToBaseline(current);
			System.out.println("start is in region 2");
			
		} 
		//region 3
		else if (19 < current.x && current.x <24 && current.y < 7) {
			System.out.println("start is in region 3");
			getToBaseline3(current); 
			
		}
		//region 4
		else if (20 < current.x && current.x <24 && current.y > 19 && current.y<20) {
			System.out.println("start is in region 4");
			getToBaseline3(current);
		}
		//region 5 
		else if (current.x > 16 && current.x <19  && current.y > 19){
			System.out.println("start is in region 5");
			getToBaseline(current);
			
		} 
		//region 6 
		else if (current.x <19 && current.y > 18 && current.y<19){
			System.out.println("start is in region 6");
			getToBaseline(current);
			
		}
		else{
			//invalid starting point
			
		}
		
		
		if (mv.calculateIntersections(pathPoints.get(pathPoints.size() -1) , destination).isEmpty()){
			pathPoints.add(destination); 
			
			
		}
		else{
			//get from region 6 to final destination; 
			//determine where the destination point lies 
			//region 1
			if ((3 < destination.x && destination.x <7.3 && destination.y < 18) || (10 < destination.x && destination.x <14 && destination.y < 18)  || (17 < destination.x && destination.x < 21.75 && destination.y < 18) || (17 < destination.x && destination.x < 22.5 && destination.y < 18&& destination.y > 7) ){
				 
				System.out.println("destination is in region 1");
				getToDest(pathPoints.get((pathPoints.size()-1)));
			}
			//region 2 
			else if (destination.x < 5 && destination.y < 22 && destination.y > 17.7 ){
				 
				System.out.println("destination is in region 2");
				getToDest(pathPoints.get((pathPoints.size()-1)));
			} 
			//region 3
			else if (19 < destination.x && destination.x <24 && destination.y < 7.5 && destination.y > 4.5) {
				
				System.out.println("destination is in region 3");
				getToDest3(pathPoints.get((pathPoints.size()-1)));
			}
			//region 4
			else if (20 < destination.x && destination.x <22 && destination.y > 19 && destination.y<20) {
				
				System.out.println("destination is in region 4");
				getToDest3(pathPoints.get((pathPoints.size()-1)));
			}
			//region 5 
			else if (destination.x > 19 && destination.x <21  && destination.y > 19 && destination.y < 21){
				 
				System.out.println("destination is in region 5");
				getToDest(pathPoints.get((pathPoints.size()-1)));
			} 
			//region 6 
			else if (destination.x <19 && destination.y > 18 && destination.y<19){
				System.out.println("destination is in region 6");
				getToDest(pathPoints.get((pathPoints.size()-1))); 
				
			}
			else{
				//invalid destination point
		
			} 
		 
		}
		mv.setUserPath(pathPoints);
		}
	}
	//get to dest when not final point is in regions 4 or 3
	void getToDest3(PointF current){
		PointF thisCurrent = new PointF(18, current.y); 
		
		pathPoints.add(thisCurrent);
		
		thisCurrent.y =  destination.y ; 
		pathPoints.add(thisCurrent);
		thisCurrent.x = thisCurrent.x + destination.x- current.x;
		pathPoints.add(thisCurrent);
	}

	void getToDest(PointF current){
		PointF thisCurrent = new PointF(destination.x, current.y);
		PointF newpoint = new PointF(destination.x, current.y); 
		pathPoints.add(newpoint);
		pathPoints.add(thisCurrent);
		
		thisCurrent.x= destination.x; 
		thisCurrent.y =  destination.y; 
		pathPoints.add(thisCurrent);
	//	System.out.println("the points in the gettodest method are: " + pathPoints); 
		
}	

	void getToBaseline3(PointF current){
		PointF regionCurrent = new PointF(18,current.y); 
		pathPoints.add(regionCurrent); 
		
		getToBaseline(regionCurrent); 
	}
	
	//adds y points to get to region 6 
	void getToBaseline(PointF current){
		PointF thisCurrent = new PointF(current.x, 18); 
		
		
		pathPoints.add(thisCurrent);
	//	System.out.println("pathPoints to baseline are = " + pathPoints); 
		
	} 
		
} 

class PositionListener implements IMapperListener {
	
	PointF loc ;
	PointF dest; 
	Mapper mv; 
	RotSensorEventListener orientObject;
	float angle; 
	float initializedAngle; 
	static MakePath path;
	TextView destLabel; 
	//listener for change in the position of the user 
	
	public PositionListener(Mapper mv, SensorEventListener orientObject2, TextView label){
		 orientObject = (RotSensorEventListener) orientObject2; 
		loc = new PointF(0, 0); 
		dest = new PointF(0, 0); 
		System.out.println("mv and loc " + mv + " "+ loc);
		this.destLabel = label ;
		this.mv = mv; 
		
		
		
//		Log.d("loc ", "" + loc); 
		
		
		
	} 
	public void movedLocation(float angle ){
		this.angle= angle;
		loc.x+= Math.sin(Math.toRadians(this.angle - initializedAngle));
		loc.y -=  Math.cos(Math.toRadians(this.angle- initializedAngle));
		path.pathCalculation(loc); 
		if (loc.x > 0.9*dest.x && loc.x < 1.1*dest.x && loc.y >0.9*dest.y && loc.y < 1.1*dest.y ){
			destLabel.setText("you have arrived!"); 
			destLabel.setTextColor(Color.GREEN); 
			
		}else{
			
			
		}
	}
	@Override
	public void locationChanged(Mapper source, PointF loc) {
		// TODO Auto-generated method stub
		//System.out.println("Location X: " + loc.x + ", Location Y: " + loc.y);
		
		System.out.println("the current location is " + loc); 
		this.loc= loc ;
		initializedAngle = orientObject.get(); 
		 
			//MakePath path = new MakePath(source, this.loc, this.dest); 
		//Log.d("LocationChanged","this new location is" + loc.x + "and " + loc.y); 
		if (this.loc.x!=0 && this.loc.y != 0 & this.dest.x != 0 && this.dest.y!= 0){
			 path.pathCalculation(loc);  
		}  
	}

	@Override
	public void DestinationChanged(Mapper source, PointF dest) {
		// TODO Auto-generated method stub
		//System.out.println("Dest Location X: " + dest.x + ", Dest Location Y: " + dest.y);
		 this.dest = dest;
		// Log.d("LocationChanged","this new location is" + loc.x + "and " + loc.y);
		if (loc.x!=0 && loc.y != 0 & dest.x != 0 && dest.y!= 0){
				 path = new MakePath(this.mv, this.loc, this.dest); 
				
			} 
	}
	
	
}





