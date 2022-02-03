package com.example.bussknapppc.internalframework;

import android.app.Activity;

import com.example.bussknapppc.PlanningFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Objects;

public class BuildPolyZoom {

    public static void createZoomOffset(Activity activityContext){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        try{
            if (GeoCoder.fromBusStopMarker.equals("") && GeoCoder.toBusStopMarker.equals("")){
                builder.include(GeoCoder.fromMarker.getPosition());
                builder.include(GeoCoder.toMarker.getPosition());
            }
            else{
                if (!GeoCoder.fromBusStopMarker.equals("") && !GeoCoder.toBusStopMarker.equals("")){
                    builder.include(Objects.requireNonNull(GeoCoder.getLocationFromAddress(activityContext, GeoCoder.fromBusStopMarker)));
                    builder.include(Objects.requireNonNull(GeoCoder.getLocationFromAddress(activityContext, GeoCoder.toBusStopMarker)));
                }
                else if (!GeoCoder.fromBusStopMarker.equals("")){
                    builder.include(Objects.requireNonNull(GeoCoder.getLocationFromAddress(activityContext, GeoCoder.fromBusStopMarker)));
                    builder.include(GeoCoder.toMarker.getPosition());
                }
                else{
                    builder.include(GeoCoder.fromMarker.getPosition());
                    builder.include(Objects.requireNonNull(GeoCoder.getLocationFromAddress(activityContext, GeoCoder.toBusStopMarker)));
                }
            }

            //initialize the padding for map boundary
            int padding = 430;
            //create the bounds from latlngBuilder to set into map camera
            LatLngBounds bounds = builder.build();
            //create the camera with bounds and padding to set into map
            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            //call the map call back to know map is loaded or not

            PlanningFragment.map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    PlanningFragment.map.animateCamera(cu);
                }
            });
        }
        catch (Exception e){
            //nothing...
        }
    }


}
