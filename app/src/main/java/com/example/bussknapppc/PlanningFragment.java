package com.example.bussknapppc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.bussknapppc.routinghelpers.FetchURL;
import com.example.bussknapppc.internalframework.BuildPolyZoom;
import com.example.bussknapppc.internalframework.CustomInfoMarker;
import com.example.bussknapppc.internalframework.GeoCoder;
import com.example.bussknapppc.internalframework.RecentRoutes;
import com.example.bussknapppc.internalframework.ResultCardData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.Objects;

public class PlanningFragment extends Fragment implements OnMapReadyCallback {


    //TODO: OBS!!! OM APPEN CRASHAR PGA CPU LOADING TIME SÅ SKA MAN OMINSTALLERA AVD:N


    //TODO: Uppgifter:

    //TODO: Implementera cache som lagrar information mellan fragment
    //TODO: Implementera ett shuffle system för köpta biljetter
    //TODO: Implementera ett shuffle system mellan fragments


    //region declarations

    public static GoogleMap map;
    public static Polyline currentPolyline;

    public static View view;
    private SupportMapFragment mapFragment;

    private static final String TAG = PlanningFragment.class.getSimpleName();

    private ObjectAnimator mapbtnTransUp;
    private ObjectAnimator clrbtnUp;
    private ObjectAnimator mapbtnAnimDown;
    private ObjectAnimator clrbtnDown;

    private ObjectAnimator mapbtnFadeIn;
    private ObjectAnimator mapbtnFadeOut;

    private boolean isBoxFromCollapsed = false;
    private boolean isBoxToCollapsed = false;
    private boolean polylineDisplayed = false;
    private boolean routeResult1Clicked = false;
    private boolean routeResult2Clicked = false;
    private boolean routeResult3Clicked = false;
    private boolean initialLoad = true;
    private boolean recentRoutePlanning = false;

    public static boolean setInputText = false;
    public static boolean customMarkerFrom = false;
    public static boolean customMarkerTo = false;

    private final int ogEditWidth = 580;
    private final int newEditWidth = 860;
    private final int zeroEditWidth = 0;

    private float cardViewY;
    private float boxFromY;
    private float boxToY;
    private float mapBtnY;
    private float sr1Y;
    private float sr2Y;
    private float sr3Y;

    private ImageView myOriginCross;
    private ImageView myCollapsedOriginCross;
    private ImageView myDestinationCross;
    private ImageView myCollapsedDestinationCross;

    private TextView recentRouteHeader;
    public static TextView zeroRecentRoutes;
    private TextView mapBtnTextView;
    public static AutoCompleteTextView boxFrom;
    private View inputline;
    public static AutoCompleteTextView boxTo;
    public static CardView planRouteBtn;
    private CardView clearRouteBtn;
    private CardView timetblBtn;
    private CardView mapBtn;
    public static CardView backgroundCover;
    private CardView cardView;
    private CardView searchResult1;
    private CardView searchResult2;
    private CardView searchResult3;

    //endregion


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_planning, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        try {
            mapFragment.getMapAsync(this);
        }
        catch (Exception ignored){
        }

        //region initializations

        recentRouteHeader = view.findViewById(R.id.recentRouteHeader);
        zeroRecentRoutes = view.findViewById(R.id.zeroRecentRoutes);
        mapBtnTextView = view.findViewById(R.id.mapBtnTextView);
        boxFrom = view.findViewById(R.id.myOrigin);
        inputline = view.findViewById(R.id.inputLine);
        boxTo = view.findViewById(R.id.myDestination);
        planRouteBtn = view.findViewById(R.id.planRoute);
        clearRouteBtn = view.findViewById(R.id.clearRoute);
        mapBtn  = view.findViewById(R.id.mapButton);
        backgroundCover = view.findViewById(R.id.backgroundCover);
        cardView = view.findViewById(R.id.inputView);
        searchResult1 = view.findViewById(R.id.searchResult1);
        searchResult2 = view.findViewById(R.id.searchResult2);
        searchResult3 = view.findViewById(R.id.searchResult3);

        myOriginCross = view.findViewById(R.id.myOriginCross);
        myCollapsedOriginCross = view.findViewById(R.id.myCollapsedOriginCross);
        myDestinationCross = view.findViewById(R.id.myDestinationCross);
        myCollapsedDestinationCross = view.findViewById(R.id.myCollapsedDestinationCross);

        cardViewY = cardView.getY();
        boxFromY = boxFrom.getY();
        boxToY = boxTo.getY();
        mapBtnY = mapBtn.getY();
        sr1Y = searchResult1.getY();
        sr2Y = searchResult2.getY();
        sr3Y = searchResult3.getY();

        ConstraintLayout planningLayout = view.findViewById(R.id.planningLayout);
        ViewGroup.MarginLayoutParams inputViewLayoutParams = (ViewGroup.MarginLayoutParams) planningLayout.getLayoutParams();

        final int ogViewTopMargin = inputViewLayoutParams.topMargin;
        final int ogViewBtmMargin = inputViewLayoutParams.bottomMargin;
        final int ogCardDimension = cardView.getLayoutParams().height;
        final int newCardDimension = Math.round(ogCardDimension / 2.0f);

        mapbtnTransUp = ObjectAnimator.ofFloat(mapBtn, "translationY", mapBtnY, mapBtnY - 50);
        clrbtnUp = ObjectAnimator.ofFloat(clearRouteBtn, "translationY", mapBtnY, mapBtnY - 50);
        mapbtnAnimDown = ObjectAnimator.ofFloat(mapBtn, "translationY", mapBtnY - 50, mapBtnY);
        clrbtnDown = ObjectAnimator.ofFloat(clearRouteBtn, "translationY", mapBtnY - 50, mapBtnY);

        mapbtnFadeIn = ObjectAnimator.ofFloat(mapBtn, "alpha", 0, 1);
        mapbtnFadeOut = ObjectAnimator.ofFloat(mapBtn, "alpha", 1, 0);

        mapbtnTransUp.setDuration(225);
        clrbtnUp.setDuration(225);
        mapbtnAnimDown.setDuration(300);
        clrbtnDown.setDuration(300);

        mapbtnFadeIn.setDuration(200);
        mapbtnFadeOut.setDuration(200);

        //endregion

        //region autoComplete

        //Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.hallplatser_array));
        boxFrom.setAdapter(adapter);
        boxTo.setAdapter(adapter);

        //endregion

        //region uiInteractListeners

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backgroundCover.getAlpha() > 0){
                    backgroundCover.animate().alpha(0).setDuration(200);
                    mapBtnTextView.setText(getString(R.string.utan_karta));

                    if (RecentRoutes.latestRoutes.size() > 0){
                        setRecentRouteVisibility("hide", "null");
                        recentRouteHeader.animate().alpha(0).setDuration(200);
                    }
                }
                else{
                    backgroundCover.animate().alpha(1).setDuration(200);
                    mapBtnTextView.setText(getString(R.string.med_karta));

                    if (RecentRoutes.latestRoutes.size() > 0 && !boxFrom.isFocused() && !boxTo.isFocused() && !planRouteBtn.isPressed()){
                        setRecentRouteVisibility("show", "null");
                        recentRouteHeader.animate().alpha(1).setDuration(200);
                    }
                }
            }
        });

        boxFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if (!boxFrom.getText().toString().equals("") && !isBoxFromCollapsed){
                        myOriginCross.setVisibility(View.VISIBLE);
                        myOriginCross.animate().alpha(0.9f).setDuration(100);
                    }

                    if (RecentRoutes.latestRoutes.size() > 0){
                        setRecentRouteVisibility("hide", "null");
                        recentRouteHeader.animate().alpha(0).setDuration(200);
                    }
                    else{
                        if (zeroRecentRoutes.getVisibility() == View.VISIBLE){
                            zeroRecentRoutes.animate().alpha(0).setDuration(200);
                        }
                    }
                }
                else{
                    myOriginCross.animate().alpha(0).setDuration(0).withEndAction(() -> myOriginCross.setVisibility(View.GONE));

                    if (RecentRoutes.latestRoutes.size() > 0 && backgroundCover.getAlpha() > 0){
                        setRecentRouteVisibility("show", "null");
                        recentRouteHeader.animate().alpha(1).setDuration(200);
                    }
                    else{
                        if (zeroRecentRoutes.getVisibility() == View.VISIBLE){
                            zeroRecentRoutes.animate().alpha(1).setDuration(200);
                        }
                    }
                }
            }
        });

        boxTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if (!boxTo.getText().toString().equals("")  && !isBoxToCollapsed){
                        myDestinationCross.setVisibility(View.VISIBLE);
                        myDestinationCross.animate().alpha(0.9f).setDuration(100);
                    }

                    if (RecentRoutes.latestRoutes.size() > 0){
                        setRecentRouteVisibility("hide", "null");
                        recentRouteHeader.animate().alpha(0).setDuration(200);
                    }
                    else{
                        if (zeroRecentRoutes.getVisibility() == View.VISIBLE){
                            zeroRecentRoutes.animate().alpha(0).setDuration(200);
                        }
                    }
                }
                else{
                    myDestinationCross.animate().alpha(0).setDuration(0).withEndAction(() -> myDestinationCross.setVisibility(View.GONE));

                    if (RecentRoutes.latestRoutes.size() > 0 && backgroundCover.getAlpha() > 0){
                        setRecentRouteVisibility("show", "null");
                        recentRouteHeader.animate().alpha(1).setDuration(200);
                    }
                    else{
                        if (zeroRecentRoutes.getVisibility() == View.VISIBLE){
                            zeroRecentRoutes.animate().alpha(1).setDuration(200);
                        }
                    }
                }
            }
        });

        myOriginCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInputText = true;
                boxFrom.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxTo.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxFrom.setText("");
                myOriginCross.animate().alpha(0).setDuration(50).withEndAction(() -> myOriginCross.setVisibility(View.GONE));
                setInputText = false;
            }
        });

        myDestinationCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInputText = true;
                boxFrom.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxTo.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxTo.setText("");
                myDestinationCross.animate().alpha(0).setDuration(50).withEndAction(() -> myDestinationCross.setVisibility(View.GONE));
                setInputText = false;
            }
        });

        myCollapsedOriginCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInputText = true;
                boxFrom.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxTo.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxFrom.setText("");
                setInputText = false;
            }
        });


        myCollapsedDestinationCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInputText = true;
                boxFrom.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxTo.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxTo.setText("");
                setInputText = false;
            }
        });

        //region recentroutelisteners

        //OBS!!! Annars nullpointerexception error
        RecentRoutes.initializeCardData();

        RecentRoutes.recentRoute1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInputText = true;
                boxFrom.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxTo.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxFrom.setText(RecentRoutes.latestRoutes.get(0).get(0));
                boxTo.setText(RecentRoutes.latestRoutes.get(0).get(1));
                setInputText = false;

                recentRouteClickActions(RecentRoutes.latestRoutes.get(0).get(0), RecentRoutes.latestRoutes.get(0).get(1));

                planRouteBtn.performClick();
            }
        });

        RecentRoutes.recentRoute2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInputText = true;
                boxFrom.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxTo.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxFrom.setText(RecentRoutes.latestRoutes.get(1).get(0));
                boxTo.setText(RecentRoutes.latestRoutes.get(1).get(1));
                setInputText = false;

                recentRouteClickActions(RecentRoutes.latestRoutes.get(1).get(0), RecentRoutes.latestRoutes.get(1).get(1));

                planRouteBtn.performClick();
            }
        });

        RecentRoutes.recentRoute3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInputText = true;
                boxFrom.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxTo.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxFrom.setText(RecentRoutes.latestRoutes.get(2).get(0));
                boxTo.setText(RecentRoutes.latestRoutes.get(2).get(1));
                setInputText = false;

                recentRouteClickActions(RecentRoutes.latestRoutes.get(2).get(0), RecentRoutes.latestRoutes.get(2).get(1));

                planRouteBtn.performClick();
            }
        });

        RecentRoutes.recentRoute4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInputText = true;
                boxFrom.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxTo.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                boxFrom.setText(RecentRoutes.latestRoutes.get(3).get(0));
                boxTo.setText(RecentRoutes.latestRoutes.get(3).get(1));
                setInputText = false;

                recentRouteClickActions(RecentRoutes.latestRoutes.get(3).get(0), RecentRoutes.latestRoutes.get(3).get(1));

                planRouteBtn.performClick();
            }
        });

        //endregion

        //endregion

        //region myOriginTextView listener

        boxFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //setInputText gör så att inmatningsrutan förblir statisk när senaste rutt trycks
                if (!setInputText){
                    if(!isBoxFromCollapsed){
                        collapseFromCardView(newCardDimension, inputViewLayoutParams, planningLayout);
                        isBoxFromCollapsed = true;

                        startFade("fadeOut", "null", "modButtons");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                boxFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                            long id) {
                        if (!setInputText){
                            if (!customMarkerFrom){
                                GeoCoder.fromBusStopMarker = "";
                            }
                            else{
                                GeoCoder.fromMarker.remove();
                            }

                            GeoCoder.fromBusStopMarker = boxFrom.getText().toString();
                            GeoCoder.setCameraExistingMarker(mapFragment, "boxFrom");
                            customMarkerFrom = false;

                            myCollapsedOriginCross.animate().alpha(0).setDuration(200).withEndAction(() -> myCollapsedOriginCross.setVisibility(View.GONE));
                            extendFromCardView(ogCardDimension,  ogViewTopMargin, ogViewBtmMargin, inputViewLayoutParams,
                                    planningLayout);
                            isBoxFromCollapsed = false;

                            startFade("fadeIn", "null", "modButtons");

                            setEditFocus(boxTo, boxFrom);
                        }
                    }
                });

                boxFrom.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                        if (!setInputText){
                            //If the keyevent is a key-down event on the "enter" button
                            if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                                if (adapter.getCount() > 0){
                                    boxFrom.setText(adapter.getItem(0));
                                }

                                if (!customMarkerFrom){
                                    GeoCoder.fromBusStopMarker = "";
                                }
                                else{
                                    GeoCoder.fromMarker.remove();
                                }

                                if (!checkDuplicates(boxFrom)){
                                    GeoCoder.addFromMarker(mapFragment, 0, boxFrom, "");
                                    customMarkerFrom = true;
                                }
                                else{
                                    GeoCoder.fromBusStopMarker = boxFrom.getText().toString();
                                    GeoCoder.setCameraExistingMarker(mapFragment, "boxFrom");
                                    customMarkerFrom = false;
                                }

                                myCollapsedOriginCross.animate().alpha(0).setDuration(200).withEndAction(() -> myCollapsedOriginCross.setVisibility(View.GONE));
                                extendFromCardView(ogCardDimension,  ogViewTopMargin, ogViewBtmMargin, inputViewLayoutParams,
                                        planningLayout);
                                isBoxFromCollapsed = false;

                                startFade("fadeIn", "null", "modButtons");

                                setEditFocus(boxTo, boxFrom);

                                return true;
                            }
                            else if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {

                                if (boxFrom.getText().length() == 0 && isBoxFromCollapsed){
                                    myCollapsedOriginCross.animate().alpha(0).setDuration(200).withEndAction(() -> myCollapsedOriginCross.setVisibility(View.GONE));
                                    extendFromCardView(ogCardDimension,  ogViewTopMargin, ogViewBtmMargin, inputViewLayoutParams,
                                            planningLayout);
                                    isBoxFromCollapsed = false;

                                    startFade("fadeIn", "null", "modButtons");
                                }
                            }
                        }
                        return false;
                    }
                });

                if (!boxFrom.getText().toString().equals("")){
                    myOriginCross.animate().alpha(0).setDuration(100).withEndAction(() -> myOriginCross.setVisibility(View.GONE));
                    myCollapsedOriginCross.setVisibility(View.VISIBLE);
                    myCollapsedOriginCross.animate().alpha(1).setDuration(200);
                }
                else{
                    myCollapsedOriginCross.animate().alpha(0).setDuration(200).withEndAction(() -> myCollapsedOriginCross.setVisibility(View.GONE));
                }

                //gömmer resetidsinformationen om användaren börjar redigera texten då informationen är synlig
                if (searchResult1.getAlpha() > 0){
                    clearRouteBtn.performClick();
                }
            }
        });

        //endregion

        //region myDestinationTextView listener

        boxTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!setInputText){
                    if(!isBoxToCollapsed){
                        collapseToCardView(newCardDimension, inputViewLayoutParams, planningLayout);
                        isBoxToCollapsed = true;

                        startFade("fadeOut", "null", "modButtons");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                boxTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                            long id) {
                        if (!setInputText){
                            if (!customMarkerTo){
                                GeoCoder.toBusStopMarker = "";
                            }
                            else{
                                GeoCoder.toMarker.remove();
                            }

                            GeoCoder.toBusStopMarker = boxTo.getText().toString();
                            GeoCoder.setCameraExistingMarker(mapFragment, "boxTo");
                            customMarkerTo = false;

                            myCollapsedDestinationCross.animate().alpha(0).setDuration(200).withEndAction(() -> myCollapsedDestinationCross.setVisibility(View.GONE));
                            extendToCardView(ogViewTopMargin, ogViewBtmMargin, ogCardDimension, inputViewLayoutParams,
                                    planningLayout);
                            isBoxToCollapsed = false;

                            startFade("fadeIn", "null", "modButtons");

                            setEditFocus(boxFrom, boxTo);
                        }
                    }
                });

                boxTo.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                        if (!setInputText){
                            //If the keyevent is a key-down event on the "enter" button
                            if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                                if (adapter.getCount() > 0){
                                    boxTo.setText(adapter.getItem(0));
                                }

                                if (!customMarkerTo){
                                    GeoCoder.toBusStopMarker = "";
                                }
                                else{
                                    GeoCoder.toMarker.remove();
                                }

                                if (!checkDuplicates(boxTo)){
                                    GeoCoder.addToMarker(mapFragment, 0, boxTo, "");
                                    customMarkerTo = true;
                                }
                                else{
                                    GeoCoder.toBusStopMarker = boxTo.getText().toString();
                                    GeoCoder.setCameraExistingMarker(mapFragment, "boxTo");
                                    customMarkerTo = false;
                                }

                                myCollapsedDestinationCross.animate().alpha(0).setDuration(200).withEndAction(() -> myCollapsedDestinationCross.setVisibility(View.GONE));
                                extendToCardView(ogViewTopMargin, ogViewBtmMargin, ogCardDimension, inputViewLayoutParams,
                                        planningLayout);
                                isBoxToCollapsed = false;

                                startFade("fadeIn", "null", "modButtons");

                                setEditFocus(boxFrom, boxTo);

                                return true;
                            }
                            else if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                                if (boxTo.getText().length() == 0 && isBoxToCollapsed){
                                    myCollapsedDestinationCross.animate().alpha(0).setDuration(200).withEndAction(() -> myCollapsedDestinationCross.setVisibility(View.GONE));
                                    extendToCardView(ogViewTopMargin, ogViewBtmMargin, ogCardDimension, inputViewLayoutParams,
                                            planningLayout);
                                    isBoxToCollapsed = false;

                                    startFade("fadeIn", "null", "modButtons");
                                }
                            }
                        }
                        return false;
                    }
                });

                if (!boxTo.getText().toString().equals("")){
                    myDestinationCross.animate().alpha(0).setDuration(100).withEndAction(() -> myDestinationCross.setVisibility(View.GONE));
                    myCollapsedDestinationCross.setVisibility(View.VISIBLE);
                    myCollapsedDestinationCross.animate().alpha(1).setDuration(200);
                }
                else {
                    myCollapsedDestinationCross.animate().alpha(0).setDuration(200).withEndAction(() -> myCollapsedDestinationCross.setVisibility(View.GONE));
                }

                if (searchResult1.getAlpha() > 0){
                    clearRouteBtn.performClick();
                }
            }
        });

        //endregion

        //region routeActions

        planRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxFrom.length() > 0 && boxTo.length() > 0){
                    try{
                        if (boxFrom.isFocused()){
                            MainActivity.dismissKeyboard(boxFrom, getActivity(), view);
                        }
                        else if (boxTo.isFocused()){
                            MainActivity.dismissKeyboard(boxTo, getActivity(), view);
                        }

                        showSearchResults();
                        clearBusStationMarkers();

                        zeroRecentRoutes.animate().alpha(0).setDuration(200).withEndAction(() -> zeroRecentRoutes.setVisibility(View.GONE));

                        if (RecentRoutes.latestRoutes.size() > 0){
                            setRecentRouteVisibility("hide", "null");
                            recentRouteHeader.animate().alpha(0).setDuration(200);
                        }

                        if (GeoCoder.fromBusStopMarker.equals("") && GeoCoder.toBusStopMarker.equals("")){
                            GeoCoder.latLngList.add(GeoCoder.fromMarker.getPosition());
                            GeoCoder.latLngList.add(GeoCoder.toMarker.getPosition());
                        }
                        else{
                            if (!GeoCoder.fromBusStopMarker.equals("") && !GeoCoder.toBusStopMarker.equals("")){
                                GeoCoder.latLngList.add(GeoCoder.getLocationFromAddress(getActivity(), GeoCoder.fromBusStopMarker));
                                GeoCoder.latLngList.add(GeoCoder.getLocationFromAddress(getActivity(), GeoCoder.toBusStopMarker));
                            }
                            else if (!GeoCoder.fromBusStopMarker.equals("")){
                                GeoCoder.latLngList.add(GeoCoder.getLocationFromAddress(getActivity(), GeoCoder.fromBusStopMarker));
                                GeoCoder.latLngList.add(GeoCoder.toMarker.getPosition());
                            }
                            else{
                                GeoCoder.latLngList.add(GeoCoder.fromMarker.getPosition());
                                GeoCoder.latLngList.add(GeoCoder.getLocationFromAddress(getActivity(), GeoCoder.toBusStopMarker));
                            }
                        }
                        /*
                        if (!recentRoutePlanning){

                        }
                        else{
                            recentRoutePlanning = false;
                        }

                         */

                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                if (!polylineDisplayed){

                                    //TODO: efter att kameran ändrats av användaren vänta 3s sedan visa knapp som tar användaren tillbaka till polyline sträcket
                                    if (GeoCoder.fromBusStopMarker.equals("") && GeoCoder.toBusStopMarker.equals("")){
                                        new FetchURL(getContext()).execute(getUrl(
                                                        GeoCoder.fromMarker.getPosition(),
                                                        GeoCoder.toMarker.getPosition(),
                                                        "transit"), "transit"
                                        );
                                    }
                                    else{
                                        if (!GeoCoder.fromBusStopMarker.equals("") && !GeoCoder.toBusStopMarker.equals("")){
                                            new FetchURL(getContext()).execute(getUrl(
                                                    Objects.requireNonNull(GeoCoder.getLocationFromAddress(getActivity(), GeoCoder.fromBusStopMarker)),
                                                    Objects.requireNonNull(GeoCoder.getLocationFromAddress(getActivity(), GeoCoder.toBusStopMarker)),
                                                    "transit"), "transit"
                                            );
                                        }
                                        else if (!GeoCoder.fromBusStopMarker.equals("")){
                                            new FetchURL(getContext()).execute(getUrl(
                                                    Objects.requireNonNull(GeoCoder.getLocationFromAddress(getActivity(), GeoCoder.fromBusStopMarker)),
                                                    GeoCoder.toMarker.getPosition(),
                                                    "transit"), "transit"
                                            );
                                        }
                                        else{
                                            new FetchURL(getContext()).execute(getUrl(
                                                    GeoCoder.fromMarker.getPosition(),
                                                    Objects.requireNonNull(GeoCoder.getLocationFromAddress(getActivity(), GeoCoder.toBusStopMarker)),
                                                    "transit"), "transit"
                                            );
                                        }
                                    }

                                    BuildPolyZoom.createZoomOffset(getActivity());
                                    startFade("fadeOut", "null", "modButtons");
                                    startFade("fadeIn", "null", "clrButton");

                                    clearRouteBtn.setVisibility(View.VISIBLE);
                                    clearRouteBtn.animate().alpha(1).setDuration(200);

                                    polylineDisplayed = true;
                                }
                            }
                        });
                    }
                    catch (Exception ignored){
                    }
                }
            }

            //region showSearchResults

            private void showSearchResults() {

                ResultCardData.Run();

                searchResult1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!routeResult1Clicked){
                            mapBtnTextView.setText(getString(R.string.utan_karta));
                            backgroundCover.animate().alpha(0).setDuration(200);
                            searchResult1.animate().translationY(sr1Y - 340).setDuration(500);
                            searchResult2.animate().alpha(0).setDuration(500).withStartAction(() -> searchResult2.animate().translationY(sr2Y));
                            searchResult3.animate().alpha(0).setDuration(500).withStartAction(() -> searchResult3.animate().translationY(sr3Y));
                            routeResult1Clicked = true;
                        }
                        else{
                            mapBtnTextView.setText(getString(R.string.med_karta));
                            backgroundCover.animate().alpha(1).setDuration(200);
                            searchResult1.animate().translationY(sr1Y - 1030).setDuration(500);
                            searchResult2.animate().alpha(1).setDuration(500).withStartAction(() -> searchResult2.animate().translationY(sr2Y - 1030));
                            searchResult3.animate().alpha(1).setDuration(500).withStartAction(() -> searchResult3.animate().translationY(sr3Y - 1030));
                            routeResult1Clicked = false;
                        }
                    }
                });

                searchResult2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!routeResult2Clicked){
                            mapBtnTextView.setText(getString(R.string.utan_karta));
                            backgroundCover.animate().alpha(0).setDuration(200);
                            searchResult1.animate().alpha(0).setDuration(500).withStartAction(() -> searchResult1.animate().translationY(sr1Y));
                            searchResult2.animate().translationY(sr1Y - 643).setDuration(500);
                            searchResult3.animate().alpha(0).setDuration(500).withStartAction(() -> searchResult3.animate().translationY(sr3Y));
                            routeResult2Clicked = true;
                        }
                        else{
                            mapBtnTextView.setText(getString(R.string.med_karta));
                            backgroundCover.animate().alpha(1).setDuration(200);
                            searchResult1.animate().alpha(1).setDuration(500).withStartAction(() -> searchResult1.animate().translationY(sr1Y - 1030));
                            searchResult2.animate().translationY(sr2Y - 1030).setDuration(500);
                            searchResult3.animate().alpha(1).setDuration(500).withStartAction(() -> searchResult3.animate().translationY(sr3Y - 1030));
                            routeResult2Clicked = false;
                        }
                    }
                });

                searchResult3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!routeResult3Clicked){
                            mapBtnTextView.setText(getString(R.string.utan_karta));
                            backgroundCover.animate().alpha(0).setDuration(200);
                            searchResult1.animate().alpha(0).setDuration(500).withStartAction(() -> searchResult1.animate().translationY(sr1Y));
                            searchResult2.animate().alpha(0).setDuration(500).withStartAction(() -> searchResult2.animate().translationY(sr2Y));
                            searchResult3.animate().translationY(sr1Y - 946).setDuration(500);
                            routeResult3Clicked = true;
                        }
                        else{
                            mapBtnTextView.setText(getString(R.string.med_karta));
                            backgroundCover.animate().alpha(1).setDuration(200);
                            searchResult1.animate().alpha(1).setDuration(500).withStartAction(() -> searchResult1.animate().translationY(sr1Y - 1030));
                            searchResult2.animate().alpha(1).setDuration(500).withStartAction(() -> searchResult2.animate().translationY(sr2Y - 1030));
                            searchResult3.animate().translationY(sr3Y - 1030).setDuration(500);
                            routeResult3Clicked = false;
                        }

                    }
                });


                searchResult1.setVisibility(View.VISIBLE);
                searchResult2.setVisibility(View.VISIBLE);
                searchResult3.setVisibility(View.VISIBLE);

                if (backgroundCover.getAlpha() > 0){
                    searchResult1.animate().translationY(sr1Y - 1030).alpha(1).setDuration(500);
                    searchResult2.animate().translationY(sr2Y - 1030).alpha(1).setDuration(500);
                    searchResult3.animate().translationY(sr3Y - 1030).alpha(1).setDuration(500);
                }
                else{
                    mapBtnTextView.setText(getString(R.string.utan_karta));
                    searchResult1.animate().translationY(sr1Y - 340).alpha(1).setDuration(500);
                    searchResult2.animate().translationY(sr2Y - 10).alpha(1).setDuration(500);
                    searchResult3.animate().translationY(sr3Y - 10).alpha(1).setDuration(500);
                    routeResult1Clicked = true;
                }
            }

            //endregion
        });

        clearRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (polylineDisplayed){
                    try{
                        hideSearchResults();

                        displayBusStationMarkers();

                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {

                                if (GeoCoder.fromBusStopMarker.equals("") && GeoCoder.toBusStopMarker.equals("")){
                                    if (GeoCoder.latestMarkerIsTo){
                                        setCameraPosition(googleMap, "markerTo");
                                    }
                                    else{
                                        setCameraPosition(googleMap, "markerFrom");
                                    }
                                }
                                else{
                                    if (!GeoCoder.fromBusStopMarker.equals("") && !GeoCoder.toBusStopMarker.equals("")){
                                        if (GeoCoder.latestMarkerIsTo){
                                            setCameraPosition(googleMap, "existingMarkerTo");
                                        }
                                        else{
                                            setCameraPosition(googleMap, "existingMarkerFrom");
                                        }
                                    }
                                    else if (!GeoCoder.fromBusStopMarker.equals("")){
                                        if (GeoCoder.latestMarkerIsTo){
                                            setCameraPosition(googleMap, "markerTo");
                                        }
                                        else{
                                            setCameraPosition(googleMap, "existingMarkerFrom");
                                        }
                                    }
                                    else{
                                        if (GeoCoder.latestMarkerIsTo){
                                            setCameraPosition(googleMap, "existingMarkerTo");
                                        }
                                        else{
                                            setCameraPosition(googleMap, "markerFrom");
                                        }
                                    }
                                }

                                currentPolyline.remove();
                                GeoCoder.latLngList.clear();
                                polylineDisplayed = false;

                                startFade("fadeOut", "noTranslationY", "clrButton");
                                if (!boxFrom.isFocused() && !boxTo.isFocused()){
                                    startFade("fadeIn", "null", "modButtons");
                                }

                                clearRouteBtn.animate().alpha(0).setDuration(200).withEndAction(() -> clearRouteBtn.setVisibility(View.GONE));
                            }
                        });
                    }
                    catch (Exception ignored){
                    }
                }
            }

            //region hideSearchResults

            private void hideSearchResults() {

                searchResult3.animate().translationY(sr3Y).alpha(0).setDuration(500);
                searchResult2.animate().translationY(sr2Y).alpha(0).setDuration(500);
                searchResult1.animate().translationY(sr1Y).alpha(0).setDuration(500).withEndAction(() -> {
                    searchResult3.setVisibility(View.GONE);
                    searchResult2.setVisibility(View.GONE);
                    searchResult1.setVisibility(View.GONE);

                    if (!boxFrom.isFocused() && !boxTo.isFocused()){
                        RecentRoutes.Run(boxFrom.getText().toString(), boxTo.getText().toString());

                        if (backgroundCover.getAlpha() > 0){
                            setRecentRouteVisibility("show", "null");
                            recentRouteHeader.animate().alpha(1).setDuration(200);
                        }
                    }
                });
            }

            //endregion
        });

        //endregion

        return view;
    }

    //space...

    //region fadeButtons

    private void startFade(String animationType, String modification, String object){

        AnimatorSet animatorSet = new AnimatorSet();

        switch (animationType){
            case "fadeOut":

                switch (object){
                    case "modButtons":
                        if (!modification.equals("noTranslationY")){
                            //animatorSet.playTogether(timetblTransUp, mapbtnTransUp);
                            animatorSet.play(mapbtnTransUp);
                            animatorSet.start();
                        }

                        //animatorSet.playTogether(timetblFadeOut, mapbtnFadeOut);
                        animatorSet.play(mapbtnFadeOut);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                //timetblBtn.setVisibility(View.GONE);
                                mapBtn.setVisibility(View.GONE);
                            }
                        });
                        animatorSet.start();

                        break;
                    case "clrButton":
                        if (!modification.equals("noTranslationY")){
                            clrbtnUp.start();
                        }

                        clearRouteBtn.animate()
                                .alpha(0)
                                .setDuration(50)
                                .withEndAction(() -> clearRouteBtn.setVisibility(View.GONE));
                        break;
                    default:
                        break;
                }


                break;
            case "fadeIn":

                switch (object){
                    case "modButtons":
                        //timetblBtn.setVisibility(View.VISIBLE);
                        mapBtn.setVisibility(View.VISIBLE);

                        if (!modification.equals("noTranslationY")){
                            //animatorSet.playTogether(timetblAnimDown, mapbtnAnimDown);
                            animatorSet.play(mapbtnAnimDown);
                            animatorSet.start();
                        }

                        //animatorSet.playTogether(timetblFadeIn, mapbtnFadeIn);
                        animatorSet.play(mapbtnFadeIn);
                        animatorSet.start();

                        break;
                    case "clrButton":
                        clearRouteBtn.setVisibility(View.VISIBLE);

                        if (!modification.equals("noTranslationY")){
                            clrbtnDown.start();
                        }

                        //timetblBtn.animate().alpha(1).setDuration(50);
                        break;
                    default:
                        break;
                }


                break;
            default:
                //nothing...
                break;
        }
    }

    //endregion

    //region fromCardAnimation

    private void extendFromCardView(int ogCardDimension, int ogViewTopMargin, int ogViewBtmMargin,
                                    ViewGroup.MarginLayoutParams inputViewLayoutParams, ConstraintLayout planningLayout){

        /* LayoutCardView */

        inputViewLayoutParams.topMargin = ogViewTopMargin;
        inputViewLayoutParams.bottomMargin = ogViewBtmMargin;
        planningLayout.setLayoutParams(inputViewLayoutParams);

        TransitionManager.beginDelayedTransition(cardView, new TransitionSet().addTransition(new ChangeBounds()));

        ViewGroup.LayoutParams params = cardView.getLayoutParams();
        params.height = ogCardDimension;
        cardView.setLayoutParams(params);

        isBoxFromCollapsed = false;

        /* inputLine */

        inputline.animate().alpha(1).setDuration(200);

        /* TextViewFrom */

        boxFrom.setWidth(ogEditWidth);
        boxFrom.animate().translationY(boxFromY);

        /* TextViewTo */

        //OBS!!! Att skapa en "withstartaction" för setVisibility fördröjer animationen och fokus kan inte läggas på inmatningsrutan
        boxTo.setVisibility(View.VISIBLE);
        boxTo.setWidth(ogEditWidth);
        boxTo.animate().alpha(1).setDuration(400);

        /* Button */

        planRouteBtn.animate().alpha(1).setDuration(150).setStartDelay(50).withStartAction(() -> {
            planRouteBtn.animate().scaleX(1).setDuration(200);
            planRouteBtn.animate().scaleY(1).setDuration(200);
        });
    }

    private void collapseFromCardView(int newCardDimension, ViewGroup.MarginLayoutParams newLayoutParams,
                                      ConstraintLayout planningLayout){

        /* Button */

        planRouteBtn.animate().alpha(0).setDuration(150);
        planRouteBtn.animate().scaleX(0.6f).setDuration(125);
        planRouteBtn.animate().scaleY(0.6f).setDuration(125);

        /* inputLine */

        inputline.animate().alpha(0).setDuration(200);

        /* deleteTextCross */

        myCollapsedDestinationCross.setVisibility(View.GONE);

        /* TextViewFrom */

        boxFrom.setWidth(newEditWidth);
        boxFrom.animate().translationY(boxFromY + 20);

        /* TextViewTo */

        boxTo.setWidth(zeroEditWidth);
        boxTo.animate().alpha(0).setDuration(200).withEndAction(() -> boxTo.setVisibility(View.GONE));

        /* LayoutCardView */

        newLayoutParams.topMargin = 0;
        newLayoutParams.bottomMargin = 0;
        planningLayout.setLayoutParams(newLayoutParams);

        TransitionManager.beginDelayedTransition(cardView, new TransitionSet().addTransition(new ChangeBounds()));

        ViewGroup.LayoutParams params_c = cardView.getLayoutParams();
        params_c.height = newCardDimension;
        cardView.setLayoutParams(params_c);

        isBoxFromCollapsed = true;
    }

    //endregion

    //region toCardAnimation

    private void extendToCardView(int ogViewTopMargin, int ogViewBtmMargin, int ogCardDimension,
                                  ViewGroup.MarginLayoutParams inputViewLayoutParams, ConstraintLayout planningLayout){

        /* LayoutCardView */

        inputViewLayoutParams.topMargin = ogViewTopMargin;
        inputViewLayoutParams.bottomMargin = ogViewBtmMargin;
        planningLayout.setLayoutParams(inputViewLayoutParams);

        TransitionManager.beginDelayedTransition(cardView, new TransitionSet().addTransition(new ChangeBounds()));

        ViewGroup.LayoutParams params = cardView.getLayoutParams();
        params.height = ogCardDimension;
        cardView.setLayoutParams(params);

        isBoxFromCollapsed = false;

        /* inputLine */

        inputline.animate().alpha(1).setDuration(500);

        /* TextViewFrom */

        //OBS!!! Att skapa en "withstartaction" för setVisibility fördröjer animationen och fokus kan inte läggas på inmatningsrutan
        boxFrom.setVisibility(View.VISIBLE);
        boxFrom.setWidth(ogEditWidth);
        boxFrom.animate().alpha(1).setDuration(400);

        /* TextViewTo */

        boxTo.setWidth(ogEditWidth);
        boxTo.animate().translationY(boxToY);

        /* Button */

        planRouteBtn.animate().alpha(1).setDuration(150).setStartDelay(50).withStartAction(() -> {
            planRouteBtn.animate().scaleX(1).setDuration(200);
            planRouteBtn.animate().scaleY(1).setDuration(200);
        });
    }

    private void collapseToCardView(int newCardDimension, ViewGroup.MarginLayoutParams newLayoutParams,
                                    ConstraintLayout planningLayout){

        /* Button */

        planRouteBtn.animate().alpha(0).setDuration(150);
        planRouteBtn.animate().scaleX(0.6f).setDuration(125);
        planRouteBtn.animate().scaleY(0.6f).setDuration(125);

        /* inputLine */

        inputline.animate().alpha(0).setDuration(200);

        /* deleteTextCross */

        myCollapsedOriginCross.setVisibility(View.GONE);

        /* TextViewFrom */

        boxFrom.setWidth(zeroEditWidth);
        boxFrom.animate().alpha(0).setDuration(200).withEndAction(() -> boxFrom.setVisibility(View.GONE));

        /* TextViewTo */

        boxTo.setWidth(newEditWidth);
        boxTo.animate().translationY(boxFromY - 30);

        /* LayoutCardView */

        newLayoutParams.topMargin = 0;
        newLayoutParams.bottomMargin = 0;
        planningLayout.setLayoutParams(newLayoutParams);

        TransitionManager.beginDelayedTransition(cardView, new TransitionSet().addTransition(new ChangeBounds()));

        ViewGroup.LayoutParams params_c = cardView.getLayoutParams();
        params_c.height = newCardDimension;
        cardView.setLayoutParams(params_c);

        isBoxFromCollapsed = true;
    }

    //endregion

    //region mapCallback

    private void setCameraPosition(GoogleMap googleMap, String option){

        LatLng platsinfo = null;

        try {
            switch (option) {
                case "default":
                    for (Marker marker : GeoCoder.busStops){
                        if (Objects.equals(marker.getTitle(), "Borås Resecentrum")){
                            platsinfo = marker.getPosition();
                            break;
                        }
                    }
                    break;
                case "markerFrom":
                    platsinfo = GeoCoder.latLngList.get(0);
                    break;
                case "markerTo":
                    platsinfo = GeoCoder.latLngList.get(1);
                    break;
                case "existingMarkerFrom":
                    platsinfo = Objects.requireNonNull(GeoCoder.getLocationFromAddress(getActivity(), GeoCoder.fromBusStopMarker));
                    break;
                case "existingMarkerTo":
                    platsinfo = Objects.requireNonNull(GeoCoder.getLocationFromAddress(getActivity(), GeoCoder.toBusStopMarker));
                    break;
                default:
                    //null...
                    break;
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(platsinfo)
                    .zoom(17)                   // Sets the zoom
                    .tilt(30)                   // Sets the tilt of the camera
                    .build();                   // Creates a CameraPosition from the builder

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        catch (Exception ignored){
        }
    }

    private boolean checkDuplicates(AutoCompleteTextView textView){
        for (String stopName : getResources().getStringArray(R.array.hallplatser_array)){
            if (stopName.equals(textView.getText().toString())){
                return true;
            }
        }
        return false;
    }

    private void displayBusStationMarkers(){

        if (GeoCoder.fromBusStopMarker.equals("") && GeoCoder.toBusStopMarker.equals("")){
            for (String stopName : getResources().getStringArray(R.array.hallplatser_array)){
                GeoCoder.addBusStopMarker(mapFragment, stopName);
            }
        }
        else{
            if (!GeoCoder.fromBusStopMarker.equals("") && !GeoCoder.toBusStopMarker.equals("")){
                for (String stopName : getResources().getStringArray(R.array.hallplatser_array)){
                    if (!stopName.equals(GeoCoder.fromBusStopMarker) && !stopName.equals(GeoCoder.toBusStopMarker)){
                        GeoCoder.addBusStopMarker(mapFragment, stopName);
                    }
                }
            }
            else if (!GeoCoder.fromBusStopMarker.equals("")){
                for (String stopName : getResources().getStringArray(R.array.hallplatser_array)){
                    if (!stopName.equals(GeoCoder.fromBusStopMarker)){
                        GeoCoder.addBusStopMarker(mapFragment, stopName);
                    }
                }
            }
            else{
                for (String stopName : getResources().getStringArray(R.array.hallplatser_array)){
                    if (!stopName.equals(GeoCoder.toBusStopMarker)){
                        GeoCoder.addBusStopMarker(mapFragment, stopName);
                    }
                }
            }
        }
    }

    private void clearBusStationMarkers(){
        if (GeoCoder.fromBusStopMarker.equals("") && GeoCoder.toBusStopMarker.equals("")){
            for (Marker marker : GeoCoder.busStops){
                marker.remove();
            }
        }
        else{
            if (!GeoCoder.fromBusStopMarker.equals("") && !GeoCoder.toBusStopMarker.equals("")){
                for (Marker marker : GeoCoder.busStops){
                    if (!Objects.equals(marker.getTitle(), GeoCoder.fromBusStopMarker) && !Objects.equals(marker.getTitle(), GeoCoder.toBusStopMarker)){
                        marker.remove();
                    }
                }
            }
            else if (!GeoCoder.fromBusStopMarker.equals("")){
                for (Marker marker : GeoCoder.busStops){
                    if (!Objects.equals(marker.getTitle(), GeoCoder.fromBusStopMarker)){
                        marker.remove();
                    }
                }
            }
            else{
                for (Marker marker : GeoCoder.busStops){
                    if (!Objects.equals(marker.getTitle(), GeoCoder.toBusStopMarker)){
                        marker.remove();
                    }
                }
            }
        }
    }

    public String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.map_key);
        return url;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        googleMap.setPadding(0, 200, 0, 0);

        if (initialLoad){
            displayBusStationMarkers();

            CustomInfoMarker customInfoWindow = new CustomInfoMarker(getActivity(), mapFragment);
            googleMap.setInfoWindowAdapter(customInfoWindow);

            initialLoad = false;
        }

        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        //region mapInteractListeners

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        if (boxFrom.isFocused()){
                            MainActivity.dismissKeyboard(boxFrom, getActivity(), view);
                        }
                        else if (boxTo.isFocused()){
                            MainActivity.dismissKeyboard(boxTo, getActivity(), view);
                        }
                    }
                });
            }
        });

        //endregion

        setCameraPosition(googleMap, "default");

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            requireContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    //endregion

    //region focusMethods

    private void setRecentRouteVisibility(@NonNull String action, String exception){

        switch (action){
            case "show":
                switch (RecentRoutes.latestRoutes.size()){
                    case 1:
                        RecentRoutes.recentRoute1.setVisibility(View.VISIBLE);
                        RecentRoutes.recentRoute1.animate().alpha(1).setDuration(200);
                        break;
                    case 2:
                        RecentRoutes.recentRoute1.setVisibility(View.VISIBLE);
                        RecentRoutes.recentRoute2.setVisibility(View.VISIBLE);
                        RecentRoutes.recentRoute1.animate().alpha(1).setDuration(200);
                        RecentRoutes.recentRoute2.animate().alpha(1).setDuration(200);
                        break;
                    case 3:
                        RecentRoutes.recentRoute1.setVisibility(View.VISIBLE);
                        RecentRoutes.recentRoute2.setVisibility(View.VISIBLE);
                        RecentRoutes.recentRoute3.setVisibility(View.VISIBLE);
                        RecentRoutes.recentRoute1.animate().alpha(1).setDuration(200);
                        RecentRoutes.recentRoute2.animate().alpha(1).setDuration(200);
                        RecentRoutes.recentRoute3.animate().alpha(1).setDuration(200);
                        break;
                    case 4:
                        RecentRoutes.recentRoute1.setVisibility(View.VISIBLE);
                        RecentRoutes.recentRoute2.setVisibility(View.VISIBLE);
                        RecentRoutes.recentRoute3.setVisibility(View.VISIBLE);
                        RecentRoutes.recentRoute4.setVisibility(View.VISIBLE);
                        RecentRoutes.recentRoute1.animate().alpha(1).setDuration(200);
                        RecentRoutes.recentRoute2.animate().alpha(1).setDuration(200);
                        RecentRoutes.recentRoute3.animate().alpha(1).setDuration(200);
                        RecentRoutes.recentRoute4.animate().alpha(1).setDuration(200);
                        break;
                    default:
                        break;
                }
                break;
            case "hide":
                switch (RecentRoutes.latestRoutes.size()){
                    case 1:
                        RecentRoutes.recentRoute1.animate().alpha(0).setDuration(200).withEndAction(() -> {
                            RecentRoutes.recentRoute1.setVisibility(View.GONE);
                        });
                        break;
                    case 2:
                        RecentRoutes.recentRoute1.animate().alpha(0).setDuration(200);
                        RecentRoutes.recentRoute2.animate().alpha(0).setDuration(200).withEndAction(() -> {
                            RecentRoutes.recentRoute1.setVisibility(View.GONE);
                            RecentRoutes.recentRoute2.setVisibility(View.GONE);
                        });
                        break;
                    case 3:
                        RecentRoutes.recentRoute1.animate().alpha(0).setDuration(200);
                        RecentRoutes.recentRoute2.animate().alpha(0).setDuration(200);
                        RecentRoutes.recentRoute3.animate().alpha(0).setDuration(200).withEndAction(() -> {
                            RecentRoutes.recentRoute1.setVisibility(View.GONE);
                            RecentRoutes.recentRoute2.setVisibility(View.GONE);
                            RecentRoutes.recentRoute3.setVisibility(View.GONE);
                        });
                        break;
                    case 4:
                        RecentRoutes.recentRoute1.animate().alpha(0).setDuration(200);
                        RecentRoutes.recentRoute2.animate().alpha(0).setDuration(200);
                        RecentRoutes.recentRoute3.animate().alpha(0).setDuration(200);
                        RecentRoutes.recentRoute4.animate().alpha(0).setDuration(200).withEndAction(() -> {
                            RecentRoutes.recentRoute1.setVisibility(View.GONE);
                            RecentRoutes.recentRoute2.setVisibility(View.GONE);
                            RecentRoutes.recentRoute3.setVisibility(View.GONE);
                            RecentRoutes.recentRoute4.setVisibility(View.GONE);
                        });
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

    }

    private void setEditFocus(@NonNull AutoCompleteTextView textView, AutoCompleteTextView parent){
        if (textView.getText().length() == 0){
            textView.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        else{
            MainActivity.dismissKeyboard(parent, getActivity(), view);
        }
    }

    private void recentRouteClickActions(String boxFromText, String boxToText){

        //GeoCoder.latLngList.clear();
        if (GeoCoder.fromMarker != null){
            GeoCoder.fromMarker.remove();
        }

        if (GeoCoder.toMarker != null){
            GeoCoder.toMarker.remove();
        }

        //GeoCoder.latLngList.clear();
        GeoCoder.fromBusStopMarker = "";
        GeoCoder.toBusStopMarker = "";
        customMarkerFrom = false;
        customMarkerTo = false;

        if (!checkDuplicates(boxFrom) && !checkDuplicates(boxTo)){
            //Toast.makeText(getActivity(),"A",Toast. LENGTH_SHORT).show();

            GeoCoder.addFromMarker(mapFragment, 0, boxFrom, boxFromText);
            GeoCoder.addToMarker(mapFragment, 0, boxTo, boxToText);
            customMarkerFrom = true;
            customMarkerTo = true;
        }
        else{
            if (checkDuplicates(boxFrom) && checkDuplicates(boxTo)){
                //Toast. makeText(getActivity(),"B",Toast. LENGTH_SHORT).show();

                GeoCoder.fromBusStopMarker = boxFromText;
                GeoCoder.toBusStopMarker = boxToText;
                customMarkerFrom = false;
                customMarkerTo = false;
            }
            else if (checkDuplicates(boxFrom) && !checkDuplicates(boxTo)){
                //Toast. makeText(getActivity(),"C",Toast. LENGTH_SHORT).show();

                GeoCoder.fromBusStopMarker = boxFromText;
                GeoCoder.addToMarker(mapFragment, 0, boxTo, boxToText);
                customMarkerFrom = false;
                customMarkerTo = true;
            }
            else{
                //Toast. makeText(getActivity(),"D",Toast. LENGTH_SHORT).show();

                GeoCoder.addFromMarker(mapFragment, 0, boxFrom, boxFromText);
                GeoCoder.toBusStopMarker = boxToText;
                customMarkerFrom = true;
                customMarkerTo = false;
            }
        }

        //recentRoutePlanning = true;
    }

    //endregion

    //space...
}


