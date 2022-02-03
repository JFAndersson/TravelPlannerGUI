package com.example.bussknapppc.internalframework;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bussknapppc.PlanningFragment;
import com.example.bussknapppc.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CustomInfoMarker implements GoogleMap.InfoWindowAdapter {

    private final View infoWindowLayout;
    private Activity activityContext;
    private SupportMapFragment supportMapFragment;
    private boolean clickable = false;
    public static boolean mapNavActive = false;

    public CustomInfoMarker(Activity activityContext, SupportMapFragment supportMapFragment) {
        infoWindowLayout = activityContext.getLayoutInflater().inflate(R.layout.marker_infowindow, null);
        this.activityContext = activityContext;
        this.supportMapFragment = supportMapFragment;
    }

    private void customInfoWindow(Marker marker, View layoutView){
        TextView travelInfoHeader = layoutView.findViewById(R.id.travelHeaderText);
        TextView travelActionText = layoutView.findViewById(R.id.travelActionText);

        travelInfoHeader.setText(marker.getTitle());

        if (PlanningFragment.boxFrom.getText().toString().equals("") && PlanningFragment.boxTo.getText().toString().equals("")){
            travelActionText.setText("Res från");
            clickable = true;
        }
        else{
            if (!PlanningFragment.boxFrom.getText().toString().equals("") && !PlanningFragment.boxTo.getText().toString().equals("")){

                if (marker.getTitle().equals(PlanningFragment.boxFrom.getText().toString()) || marker.getTitle().equals(PlanningFragment.boxTo.getText().toString())){
                    travelActionText.setText("Resa tillänglig");
                    clickable = true;
                }
                else{
                    travelActionText.setText("Resa ej tillänglig");
                    travelActionText.setTextColor(Color.rgb(131, 10, 23));
                }
            }
            else if (!PlanningFragment.boxFrom.getText().toString().equals("")){
                travelActionText.setText("Res till");
                clickable = true;
            }
            else{
                travelActionText.setText("Res från");
                clickable = true;
            }
        }

        if (clickable){
            PlanningFragment.map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(@NonNull Marker marker) {

                    switch (travelActionText.getText().toString()){
                        case "Res från":
                            PlanningFragment.setInputText = true;
                            PlanningFragment.boxFrom.setText(marker.getTitle());
                            PlanningFragment.setInputText = false;

                            GeoCoder.fromBusStopMarker = PlanningFragment.boxFrom.getText().toString();
                            PlanningFragment.customMarkerFrom = false;

                            if (!PlanningFragment.boxTo.getText().toString().equals("")){
                                if (PlanningFragment.customMarkerTo){
                                    GeoCoder.toBusStopMarker = PlanningFragment.boxTo.getText().toString();
                                    PlanningFragment.customMarkerTo = false;
                                }
                                else if (!PlanningFragment.customMarkerTo){

                                    LatLng location = GeoCoder.getLocationFromAddress(supportMapFragment.getActivity(), PlanningFragment.boxTo.getText().toString());
                                    String markerName = PlanningFragment.boxTo.getText().toString();

                                    if (location != null){
                                        GeoCoder.attemptMarkerRemoval("to");

                                        GeoCoder.toMarker = PlanningFragment.map.addMarker(new MarkerOptions()
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.custommarker_b))
                                                .position(location)
                                                .title(markerName));

                                        GeoCoder.toMarkerCheck = true;
                                        PlanningFragment.customMarkerTo = true;
                                    }
                                }

                                GeoCoder.latestMarkerIsTo = false;
                                PlanningFragment.planRouteBtn.performClick();
                            }
                            break;
                        case "Res till":
                            PlanningFragment.setInputText = true;
                            PlanningFragment.boxTo.setText(marker.getTitle());
                            PlanningFragment.setInputText = false;

                            GeoCoder.toBusStopMarker = PlanningFragment.boxTo.getText().toString();
                            PlanningFragment.customMarkerTo = false;

                            if (!PlanningFragment.boxFrom.getText().toString().equals("")){
                                if (PlanningFragment.customMarkerFrom){
                                    GeoCoder.fromBusStopMarker = PlanningFragment.boxFrom.getText().toString();
                                    PlanningFragment.customMarkerFrom = false;
                                }
                                else if (!PlanningFragment.customMarkerFrom){
                                    LatLng location = GeoCoder.getLocationFromAddress(supportMapFragment.getActivity(), PlanningFragment.boxFrom.getText().toString());
                                    String markerName = PlanningFragment.boxFrom.getText().toString();

                                    if (location != null){
                                        GeoCoder.attemptMarkerRemoval("from");

                                        GeoCoder.fromMarker = PlanningFragment.map.addMarker(new MarkerOptions()
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.custommarker_a))
                                                .position(location)
                                                .title(markerName));

                                        GeoCoder.fromMarkerCheck = true;
                                        PlanningFragment.customMarkerFrom = true;
                                    }
                                }

                                GeoCoder.latestMarkerIsTo = true;
                                PlanningFragment.planRouteBtn.performClick();
                            }
                            break;
                        case "Resa tillänglig":
                            if (marker.getTitle().equals(PlanningFragment.boxFrom.getText().toString())){
                                if (PlanningFragment.customMarkerFrom){
                                    GeoCoder.fromBusStopMarker = PlanningFragment.boxFrom.getText().toString();
                                    PlanningFragment.customMarkerFrom = false;
                                }
                                else if (!PlanningFragment.customMarkerFrom){
                                    LatLng location = GeoCoder.getLocationFromAddress(supportMapFragment.getActivity(), PlanningFragment.boxFrom.getText().toString());
                                    String markerName = PlanningFragment.boxFrom.getText().toString();

                                    if (location != null){
                                        GeoCoder.attemptMarkerRemoval("from");

                                        GeoCoder.fromMarker = PlanningFragment.map.addMarker(new MarkerOptions()
                                                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationpin))
                                                .position(location)
                                                .title(markerName));

                                        GeoCoder.fromMarkerCheck = true;
                                        PlanningFragment.customMarkerFrom = true;
                                    }
                                }

                                GeoCoder.latestMarkerIsTo = false;
                            }
                            else{
                                if (PlanningFragment.customMarkerTo){
                                    GeoCoder.toBusStopMarker = PlanningFragment.boxTo.getText().toString();
                                    PlanningFragment.customMarkerTo = false;
                                }
                                else if (!PlanningFragment.customMarkerTo){

                                    LatLng location = GeoCoder.getLocationFromAddress(supportMapFragment.getActivity(), PlanningFragment.boxTo.getText().toString());
                                    String markerName = PlanningFragment.boxTo.getText().toString();

                                    if (location != null){
                                        GeoCoder.attemptMarkerRemoval("to");

                                        GeoCoder.toMarker = PlanningFragment.map.addMarker(new MarkerOptions()
                                                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationpin))
                                                .position(location)
                                                .title(markerName));

                                        GeoCoder.toMarkerCheck = true;
                                        PlanningFragment.customMarkerTo = true;
                                    }
                                }

                                GeoCoder.latestMarkerIsTo = true;
                            }

                            PlanningFragment.planRouteBtn.performClick();
                            break;
                        default:
                            break;
                    }

                    marker.hideInfoWindow();
                }
            });
        }
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        customInfoWindow(marker, infoWindowLayout);
        return infoWindowLayout;
    }
}
