package com.google.hackathon.museum.delegate;

import java.util.List;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;

public interface EstimoteBeaconServiceDelegate {
	
	public void onBeaconsDiscovered(Region region, List<Beacon> beacons);

}
