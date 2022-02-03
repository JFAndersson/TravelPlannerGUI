package com.example.bussknapppc.internalframework;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bussknapppc.PlanningFragment;
import com.example.bussknapppc.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class GeoCoder extends AppCompatActivity {

    //region declarations

    public static ArrayList<LatLng> latLngList = new ArrayList<>(2);
    public static ArrayList<Marker> busStops = new ArrayList<>();

    public static Marker fromMarker;
    public static Marker toMarker;

    public static String fromBusStopMarker = "";
    public static String toBusStopMarker = "";

    public static boolean fromMarkerCheck = false;
    public static boolean toMarkerCheck = false;
    public static boolean latestMarkerIsTo;

    //endregion


    public static void attemptMarkerRemoval(String textview){
        if (textview.equals("from")){
            if (fromMarkerCheck){
                fromMarker.remove();
                fromMarkerCheck = false;
            }
        }
        else if (textview.equals("to")){
            if (toMarkerCheck){
                toMarker.remove();
                toMarkerCheck = false;
            }
        }
    }

    public static void setCameraExistingMarker(SupportMapFragment mapFragment, String boxView){

        if (boxView.equals("boxFrom")){
            LatLng location = getLocationFromAddress(mapFragment.getActivity(), fromBusStopMarker);

            if (location != null){
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(location)
                        .zoom(17)                   // Sets the zoom
                        .tilt(30)                   // Sets the tilt of the camera
                        .build();                   // Creates a CameraPosition from the builder
                PlanningFragment.map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            latestMarkerIsTo = false;
        }
        else if (boxView.equals("boxTo")){
            LatLng location = getLocationFromAddress(mapFragment.getActivity(), toBusStopMarker);

            if (location != null){
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(location)
                        .zoom(17)                   // Sets the zoom
                        .tilt(30)                   // Sets the tilt of the camera
                        .build();                   // Creates a CameraPosition from the builder
                PlanningFragment.map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            latestMarkerIsTo = true;
        }
    }

    public static void addBusStopMarker(SupportMapFragment mapFragment, String stopName){

        LatLng location = getLocationFromAddress(mapFragment.getActivity(), stopName);

        if (location != null){
            busStops.add(PlanningFragment.map.
                    addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstopmarker))
                            .position(location)
                            .title(stopName)));
        }
    }

    public static boolean addFromMarker(SupportMapFragment mapFragment, int id, AutoCompleteTextView textView, String boxText){
        LatLng location;

        try{
            location = getLocationFromAddress(mapFragment.getActivity(), textView.getAdapter().getItem(id).toString());
            String markerItemName = textView.getAdapter().getItem(id).toString();

            if (location != null){
                attemptMarkerRemoval("from");

                fromMarker = PlanningFragment.map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.custommarker_a))
                        .position(location)
                        .title(markerItemName));

                fromMarkerCheck = true;
            }
        }
        catch (Exception ignored){
            String markerName;

            if (!boxText.isEmpty()){
                Toast.makeText(mapFragment.getActivity(), "boxText Nonnull",Toast. LENGTH_SHORT).show();
                location = getLocationFromAddress(mapFragment.getActivity(), boxText);
                markerName = boxText;
            }
            else{
                Toast.makeText(mapFragment.getActivity(), "boxText Null",Toast. LENGTH_SHORT).show();
                location = getLocationFromAddress(mapFragment.getActivity(), textView.getText().toString());
                markerName = textView.getText().toString();
            }

            if (location != null){
                attemptMarkerRemoval("from");

                fromMarker = PlanningFragment.map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.custommarker_a))
                        .position(location)
                        .title(markerName));

                fromMarkerCheck = true;
            }
        }

        if (location != null){
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(location)
                    .zoom(17)                   // Sets the zoom
                    .tilt(30)                   // Sets the tilt of the camera
                    .build();                   // Creates a CameraPosition from the builder
            PlanningFragment.map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 200, null);
        }

        if (fromMarkerCheck){
            latestMarkerIsTo = false;
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean addToMarker(SupportMapFragment mapFragment, int id, AutoCompleteTextView textView, String boxText){
        LatLng location;

        try{
            location = getLocationFromAddress(mapFragment.getActivity(), textView.getAdapter().getItem(id).toString());
            String markerItemName = textView.getAdapter().getItem(id).toString();

            if (location != null){
                attemptMarkerRemoval("to");

                toMarker = PlanningFragment.map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.custommarker_b))
                        .position(location)
                        .title(markerItemName));

                toMarkerCheck = true;
            }
        }
        catch (Exception ignored){
            String markerName;

            if (!boxText.isEmpty()){
                Toast.makeText(mapFragment.getActivity(), "boxText Nonnull",Toast. LENGTH_SHORT).show();
                location = getLocationFromAddress(mapFragment.getActivity(), boxText);
                markerName = boxText;
            }
            else{
                Toast.makeText(mapFragment.getActivity(), "boxText Null",Toast. LENGTH_SHORT).show();
                location = getLocationFromAddress(mapFragment.getActivity(), textView.getText().toString());
                markerName = textView.getText().toString();
            }

            if (location != null){
                attemptMarkerRemoval("to");

                toMarker = PlanningFragment.map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.custommarker_b))
                        .position(location)
                        .title(markerName));

                toMarkerCheck = true;
            }
        }

        if (location != null){
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(location)
                    .zoom(17)                   // Sets the zoom
                    .tilt(30)                   // Sets the tilt of the camera
                    .build();                   // Creates a CameraPosition from the builder
            PlanningFragment.map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 200, null);
        }

        if (toMarkerCheck){
            latestMarkerIsTo = true;
            return true;
        }
        else{
            return false;
        }
    }


    public static LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception e) {
            //nothing...
        }

        return p1;
    }
}

