package biz.lungo.sensorreview;

import java.util.List;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {	
	private static final String MENU_ITEM_SENSORS_INFO = "Информация";	
	private static final String MENU_ITEM_CHOSE_SENSOR = "Выбрать сенсор";		
	private static final int MENU_ITEM_INFO_ID = 1;	
	private static final int MENU_ITEM_CHOSE_SENSOR_ID = 2;
	static TextView tvInfo;
	FragmentManager fragmentManager;	
	static Activity activity;
	SensorDialog sensorDialog;
	static SensorManager mSensorManager;	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        activity = this;
        tvInfo = (TextView) findViewById(R.id.tv_info);
        fragmentManager = getFragmentManager();
        sensorDialog = new SensorDialog();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); 
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ITEM_INFO_ID, 0, MENU_ITEM_SENSORS_INFO);
		menu.add(0, MENU_ITEM_CHOSE_SENSOR_ID, 0, MENU_ITEM_CHOSE_SENSOR);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch (item.getItemId()) {
		case MENU_ITEM_INFO_ID:
			processInfoItem();
			break;

		case MENU_ITEM_CHOSE_SENSOR_ID:
			sensorDialog.show(fragmentManager, "");
			break;
		}		
		return super.onMenuItemSelected(featureId, item);
	}

	private void processInfoItem() {
		List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		StringBuilder sb = new StringBuilder();
		for (Sensor sensor:sensorList){
			String name = sensor.getName();
			float power = sensor.getPower();
			float resolution = sensor.getResolution();
			int version = sensor.getVersion();
			String vendor = sensor.getVendor();
			float maximumRange = sensor.getMaximumRange();
			int minDelay = sensor.getMinDelay();
			sb.append("Name: " + name + "\n")
			.append("Power: " + power + "\n")
			.append("Resolution: " + resolution + "\n")
			.append("Version: " + version + "\n")
			.append("Vendor: " + vendor + "\n")
			.append("Maximum Range: " + maximumRange + "\n")
			.append("Minimum Delay: " + minDelay + "\n")
			.append("\n");			
			tvInfo.setText(sb.toString());
		}
	}
	
	
public static class SensorDialog extends DialogFragment{	
		String sensorNames[];
		int sensorTypes[];
		List<Sensor> sensorList;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {	
			ListView lv = new ListView(activity);
			sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
			sensorNames = new String[sensorList.size()];
			sensorTypes = new int[sensorList.size()];
			final SensorEventListener sEListener = new SensorEventListener() {
				
				@Override
				public void onSensorChanged(SensorEvent event) {
					String name = event.sensor.getName();
					float values[] = event.values;
					tvInfo.setText(name + "\n" + values[0] + "\n" + values[1] + "\n" + values[2] + "\n");
				}
				
				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					
				}
			};
			for (int i = 0; i < sensorNames.length; i++){
				sensorNames[i] = sensorList.get(i).getName();
				sensorTypes[i] = sensorList.get(i).getType();
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, 
											android.R.layout.simple_selectable_list_item, 
											sensorNames);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int i, long l) {
					Sensor sensor = mSensorManager.getDefaultSensor(sensorTypes[i]);
					mSensorManager.unregisterListener(sEListener);
					mSensorManager.registerListener(sEListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);					
					dismiss();
				}
			});
			return lv;
		}		
	}
}
