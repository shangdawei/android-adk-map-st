package com.pigmal.android.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class LocationOverlayItem extends OverlayItem {

	public LocationOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

}
