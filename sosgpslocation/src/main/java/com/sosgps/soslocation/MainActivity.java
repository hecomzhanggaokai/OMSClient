package com.sosgps.soslocation;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sosgps.R;

public class MainActivity extends Activity {
	
	protected static final String TAG = "MainActivity";
	private Button button;
	private TextView text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		button = (Button)findViewById(R.id.doLocation);
		text = (TextView)findViewById(R.id.showLocation);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SOSLocationConfigEntity locationEntity = (SOSLocationConfigEntity) SOSLocationEntityFactory
						.prepareEntity(
								MainActivity.this,
								com.sosgps.soslocation.SOSCurrentParameter.MANUAL_VISITE_CONFIG);
				locationEntity.setGpsEnable(0);
				locationEntity.setNetworkEnable(0);
				locationEntity.setCellEnable(1);
				SOSLocationManager sosLocationManager = SOSLocationBuilder.build(
						MainActivity.this, locationEntity);
				sosLocationManager.start(new SOSLocationManagerListener() {

					@Override
					public void onLocationChanged(Location location) {
						text.setText(location.toString());
					}
				});
			}
		});
	}

}
