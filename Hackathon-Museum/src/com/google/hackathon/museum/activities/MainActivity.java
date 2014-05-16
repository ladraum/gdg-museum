package com.google.hackathon.museum.activities;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.google.hackathon.museum.R;
import com.google.hackathon.museum.delegate.EstimoteBeaconServiceDelegate;
import com.google.hackathon.museum.service.EstimoteBeaconService;

public class MainActivity extends Activity implements
		EstimoteBeaconServiceDelegate {

	private static final String TAG = "Hackathon";
	private EstimoteBeaconService estimoteBeaconService = null;
	private NumberFormat formatter = new DecimalFormat("#0.00");
	private TextView txtFirstUUID = null;
	private TextView txtFirstMajor = null;
	private TextView txtFirstMinor = null;
	private TextView txtFirstDistance = null;

	@Override
	public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
		if(!beacons.isEmpty()) {
			final Beacon beacon = beacons.iterator().next();
			final double distance = Utils.computeAccuracy(beacon);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MainActivity.this.txtFirstUUID.setText(String.valueOf(beacon.getProximityUUID()));
					MainActivity.this.txtFirstMajor.setText(String.valueOf(beacon.getMajor()));
					MainActivity.this.txtFirstMinor.setText(String.valueOf(beacon.getMinor()));
					MainActivity.this.txtFirstDistance.setText(formatter.format(distance) + "m");
				}
			});
		}
	}

	// Lifecycle callbacks

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		this.txtFirstUUID = (TextView) findViewById(R.id.txt_first_uuid);
		this.txtFirstMajor = (TextView) findViewById(R.id.txt_first_major);
		this.txtFirstMinor = (TextView) findViewById(R.id.txt_first_minor);
		this.txtFirstDistance = (TextView) findViewById(R.id.txt_first_distance);
		
		estimoteBeaconService = EstimoteBeaconService
				.getInstance(getApplicationContext());
		estimoteBeaconService.startRanging();
	}

	@Override
	protected void onStart() {
		super.onStart();
		estimoteBeaconService.connect();
		estimoteBeaconService.register(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		estimoteBeaconService.stop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
