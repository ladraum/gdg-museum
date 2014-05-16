package com.google.hackathon.museum.activities;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.google.hackathon.museum.R;
import com.google.hackathon.museum.delegate.EstimoteApiGatewayDelegate;
import com.google.hackathon.museum.delegate.EstimoteBeaconServiceDelegate;
import com.google.hackathon.museum.model.Art;
import com.google.hackathon.museum.service.EstimoteApiGateway;
import com.google.hackathon.museum.service.EstimoteBeaconService;

public class MainActivity extends Activity implements
		EstimoteBeaconServiceDelegate, EstimoteApiGatewayDelegate {
	
	private Map<String, Integer> imagePath = new HashMap<String, Integer>();

	private static final String TAG = "Hackathon";
	private NumberFormat formatter = new DecimalFormat("#0.00");
	
	private EstimoteBeaconService estimoteBeaconService = null;
	private EstimoteApiGateway estimoteApiGateway = null;
	
	private TextView txtBeaconsInRange = null;
	private TextView txtFirstUUID = null;
	private TextView txtFirstMajor = null;
	private TextView txtFirstMinor = null;
	private TextView txtFirstDistance = null;
	
	private Beacon nearbyBeacon = null;
	private Beacon lastBeacon = null;
	private boolean showingImage = false;
	
	@Override
	public void didGetCloserToBeacon(Beacon beacon) {
		if(!showingImage && !beacon.equals(this.lastBeacon)) {
			this.lastBeacon = beacon;
			estimoteApiGateway.findProductByNearbyBeacon(this.lastBeacon);
		}
	}

	@Override
	public void onBeaconsDiscovered(final Region region, final List<Beacon> beacons) {
		if(!beacons.isEmpty()) {
			final Beacon beacon = beacons.iterator().next();
			
			if (!beacon.equals(this.nearbyBeacon)) {
				this.nearbyBeacon = beacon;
			}
			
			final double distance = Utils.computeAccuracy(beacon);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MainActivity.this.txtBeaconsInRange.setText(String.valueOf(beacons.size()));
					MainActivity.this.txtFirstUUID.setText(String.valueOf(beacon.getProximityUUID()));
					MainActivity.this.txtFirstMajor.setText(String.valueOf(beacon.getMajor()));
					MainActivity.this.txtFirstMinor.setText(String.valueOf(beacon.getMinor()));
					MainActivity.this.txtFirstDistance.setText(formatter.format(distance) + "m");
				}
			});
		}
	}
	
	@Override
	public void didMuseumProductFound(Art museumProduct) {
		Log.i(TAG, "Welcome to: " + museumProduct.getImagePath());
		
		if(!showingImage) {
			LayoutInflater inflater = getLayoutInflater();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View modal = inflater.inflate(R.layout.show_image, null);
			ImageView modelImage = (ImageView)modal.findViewById(R.id.model_image);
			modelImage.setImageResource(imagePath.get(museumProduct.getImagePath()));

			builder.setView(modal)
				.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					showingImage = false;
				}
			});
			
			modelImage.setImageResource(imagePath.get(museumProduct.getImagePath()));
				
			AlertDialog dialog = builder.create();
			dialog.show();
			showingImage = true;
		}
				
	}
	
	// Lifecycle callbacks

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		this.txtBeaconsInRange = (TextView) findViewById(R.id.txt_beacons_in_range);
		this.txtFirstUUID = (TextView) findViewById(R.id.txt_first_uuid);
		this.txtFirstMajor = (TextView) findViewById(R.id.txt_first_major);
		this.txtFirstMinor = (TextView) findViewById(R.id.txt_first_minor);
		this.txtFirstDistance = (TextView) findViewById(R.id.txt_first_distance);
		
		this.imagePath.put("whale.png", R.drawable.whale);
		this.imagePath.put("tsubasa.png", R.drawable.tsubasa);

		estimoteBeaconService = EstimoteBeaconService.getInstance(getApplicationContext());
		estimoteBeaconService.startRanging();
	}

	@Override
	protected void onStart() {
		super.onStart();
		estimoteBeaconService.connect();
		estimoteBeaconService.register(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		estimoteApiGateway = EstimoteApiGateway.getinstance();
		estimoteApiGateway.registerDelegate(this);
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
