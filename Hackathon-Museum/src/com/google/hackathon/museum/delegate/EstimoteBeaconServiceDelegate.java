package com.google.hackathon.museum.delegate;

import java.util.List;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;

public interface EstimoteBeaconServiceDelegate {
	
	public void didGetCloserToBeacon(Beacon beacon);

	public void onBeaconsDiscovered(final Region region, final List<Beacon> beacons);

}
