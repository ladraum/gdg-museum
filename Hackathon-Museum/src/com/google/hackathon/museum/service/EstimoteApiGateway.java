package com.google.hackathon.museum.service;

import org.apache.http.Header;

import android.util.Log;

import com.estimote.sdk.Beacon;
import com.google.gson.Gson;
import com.google.hackathon.museum.delegate.EstimoteApiGatewayDelegate;
import com.google.hackathon.museum.model.Art;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class EstimoteApiGateway {
	
	private AsyncHttpClient client = new AsyncHttpClient();
	private static final String TAG = "Hackathon";
	private static EstimoteApiGateway instance;
	private EstimoteApiGatewayDelegate delegate;
	
	private String baseUrl = "http://gdg-museum-api.herokuapp.com/api";
	
	private EstimoteApiGateway(){}
	
	public static EstimoteApiGateway getinstance() {
		if (instance == null) {
			instance = new EstimoteApiGateway();
		}
		
		return instance;
	}
	
	public void findProductByNearbyBeacon(Beacon nearbyBeacon) {
		RequestParams params = new RequestParams();
		params.put("uuid", nearbyBeacon.getProximityUUID());
		params.put("major", String.valueOf(nearbyBeacon.getMajor()));
		params.put("minor", String.valueOf(nearbyBeacon.getMinor()));
		
		client.get(baseUrl + "/beacon/search", params, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		    	if (delegate != null) {
		    		Art product = new Gson().fromJson(response, Art.class);
		    		
		    		delegate.didMuseumProductFound(product);
		    	}
		    }
		    
		    @Override
		    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
		    		Throwable t) {
		    	Log.e(TAG, t.getMessage(), t);
		    }
		});
	}
	
	public void registerDelegate(EstimoteApiGatewayDelegate delegate) {
		this.delegate = delegate;
	}
	
}
