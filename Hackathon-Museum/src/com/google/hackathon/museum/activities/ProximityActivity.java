package com.google.hackathon.museum.activities;

import android.view.View;
import android.widget.TextView;

public class ProximityActivity {

	private TextView questionMark = null;
	private TextView exclamationMark = null;
	private float questionAlpha = 1;
	private float exclamationAlpha = 0;
	private boolean isGettingCloser = true;

	public ProximityActivity( TextView questionMark, TextView exclamationMark ) {
		this.questionMark = questionMark;
		this.exclamationMark = exclamationMark;
	}

	public void changeFakeProximity( View view ) {
		changeProximity();
		toggleDirection();
		applyAlpha( view );
	}

	private void changeProximity() {
		if ( isGettingCloser )
			getCloser();
		else
			getAway();
	}

	private void getCloser() {
		exclamationAlpha = fadeIn( exclamationAlpha );
		questionAlpha = fadeOut( questionAlpha );
	}

	private void getAway() {
		questionAlpha = fadeIn( questionAlpha );
		exclamationAlpha = fadeOut( exclamationAlpha );
	}

	private float fadeOut( float oldAlfa ) {
		float alfa = oldAlfa - 0.1f;
		if ( alfa < 0 )
			alfa = 0;
		return alfa;
	}

	private float fadeIn( float oldAlfa ) {
		float alfa = oldAlfa + 0.1f;
		if ( alfa > 1 )
			alfa = 1;
		return alfa;
	}

	private void toggleDirection() {
		if ( questionAlpha == 0 )
			isGettingCloser = false;
		if ( exclamationAlpha == 0 )
			isGettingCloser = true;
	}

	private void applyAlpha( View view ) {
		this.questionMark.setAlpha( questionAlpha );
		this.exclamationMark.setAlpha( exclamationAlpha );
		view.invalidate();
	}
}
