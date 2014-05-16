package com.google.hackathon.museum.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.os.RemoteException;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.google.hackathon.museum.delegate.EstimoteBeaconServiceDelegate;

public class EstimoteBeaconService {

	private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);
	private BeaconManager beaconManager = null;
	private static EstimoteBeaconService instance;
	private static final String TAG = "Hackathon";
	private EstimoteBeaconServiceDelegate delegate;
	private int pingLimit = 5;
	
	private HashMap<Beacon, Integer> pingCount = new HashMap<Beacon, Integer>();
	
	private Context context;

	private EstimoteBeaconService(Context context) {
		com.estimote.sdk.utils.L.enableDebugLogging(true);
		this.context = context;
		this.beaconManager = new BeaconManager(this.context);
	}

	public static EstimoteBeaconService getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new EstimoteBeaconService(applicationContext);
		}

		return instance;
	}
	
	public void register(EstimoteBeaconServiceDelegate delegate) {
		this.delegate = delegate;
	}
	
	public void startRanging() {
		beaconManager.setRangingListener(new BeaconManager.RangingListener() {
			@Override
			public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
				List<Beacon> sortedBeacons = new ArrayList<Beacon>(beacons);
				if(EstimoteBeaconService.instance.delegate != null) {
					Collections.sort(sortedBeacons, new Comparator<Beacon>() {

						public int compare(Beacon first, Beacon second) {
							double firstDistance = Utils.computeAccuracy(first);
							double secondDistance = Utils.computeAccuracy(second);
							if(firstDistance < 0) {
								// negative numbers should be on the final of list
								return 1;
							}
							
							if(firstDistance < secondDistance) {
								return -1;
							} else {
								return 1;
							}
						}
					});
					EstimoteBeaconService.instance.delegate.onBeaconsDiscovered(region, sortedBeacons);
				}
				
				Beacon beacon = sortedBeacons.iterator().next();
					if (pingCount.containsKey(beacon)) {
						int pingAmount = pingCount.get(beacon);
						double distance = Utils.computeAccuracy(beacon);
						Log.i(TAG, "Beacon=["+beacon.getMinor()+"] with pingAmount=" + pingAmount + " and distance=" + distance);
						if (pingAmount >= pingLimit) {
							if(EstimoteBeaconService.instance.delegate != null) {
								EstimoteBeaconService.instance.delegate.didGetCloserToBeacon(beacon);
							}
						}
						
						if(distance > 0 && distance < 1) {
							
							if (pingAmount < pingLimit) {
								pingCount.clear();
								pingCount.put(beacon, (pingAmount + 1));
							}
						} else {
							if (pingAmount > 0) {
								pingCount.put(beacon, (pingAmount - 1));
							}
							
						}
					} else {
						pingCount.put(beacon, 0);
					}
				}
		});
	}
	
	public void connect() {
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			@Override
			public void onServiceReady() {
				try {
					beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
					Log.d(TAG, "Service ready");
				} catch (RemoteException e) {
					Log.e(TAG, "Cannot start ranging", e);
				}
			}
		});
	}
	
	public void stop() {
		try {
			beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
			Log.d(TAG, "Stop ranging");
		} catch (RemoteException e) {
			Log.e(TAG, "Cannot stop but it does not matter now", e);
		}
	}
	
}
