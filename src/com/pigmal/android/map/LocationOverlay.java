
package com.pigmal.android.map;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.pigmal.android.ex.accessory.R;

public class LocationOverlay extends ItemizedOverlay<LocationOverlayItem> {

    private Context mContext;

    private List<GeoPoint> pointList = new ArrayList<GeoPoint>();

    public LocationOverlay(Drawable defaultMarker, Context context) {
        super(boundCenterBottom(defaultMarker));
        mContext = context;
    }

    @Override
    protected LocationOverlayItem createItem(int i) {
        GeoPoint point = pointList.get(i);
        return new LocationOverlayItem(point, "title", "description");
    }

    @Override
    public int size() {
        return pointList.size();
    }

    public void addPoint(GeoPoint point) {
        pointList.add(point);
        populate();
    }

    public void clearPoint() {
        pointList.clear();
        populate();
    }

    @Override
    protected boolean onTap(int index) {
        final GeoPoint geoPoint = pointList.get(index);

        MapView mapView = (MapView) ((Activity) mContext).findViewById(R.id.map);
        MapController controller = mapView.getController();
        controller.animateTo(geoPoint);

        return true;
    }

}
