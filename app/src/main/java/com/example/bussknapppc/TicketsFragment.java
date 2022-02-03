package com.example.bussknapppc;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.bussknapppc.storageframework.UserInformationStorage;
import com.example.bussknapppc.storageframework.GetStoredInformation;
import com.example.bussknapppc.storageframework.TicketsFragmentStorage;

public class TicketsFragment extends Fragment implements View.OnClickListener {

    private View view;

    private CardView logInMenu;
    private CardView logInBtn;
    private TextView zeroPurchasesHeader;
    private TextView createAccountHeader;
    private TextView logInBtnText;
    private TextView bankInfoHeader;
    private EditText nameInput;
    private EditText passInput;
    private EditText bankInput;

    private CardView enTicket;
    private CardView daTicket;
    private CardView peTicket;
    private CardView arTicket;

    //private final int regularLogInBotMargin = Math.round(MainActivity.convertDpToPx(90, getActivity()));
    //private final int newLogInBotMargin = Math.round(MainActivity.convertDpToPx(35, getActivity()));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tickets, container, false);

        //TODO: Lagra användarens val av köp i ett objekt i TicketsRegister klassen

        //region initializations

        CardView backgroundOverlay = view.findViewById(R.id.backgroundOverlay);
        logInMenu = view.findViewById(R.id.logInMenu);
        logInBtn = view.findViewById(R.id.logInBtn);
        TicketsFragmentStorage.recentTicket1 = view.findViewById(R.id.recentTicket1);
        TicketsFragmentStorage.recentTicket2 = view.findViewById(R.id.recentTicket2);
        createAccountHeader = view.findViewById(R.id.createAccountHeader);
        zeroPurchasesHeader = view.findViewById(R.id.zeroPurchasesHeader);
        logInBtnText = view.findViewById(R.id.logInBtnText);
        bankInfoHeader = view.findViewById(R.id.bankInfoHeader);
        nameInput = view.findViewById(R.id.enterUserName);
        passInput = view.findViewById(R.id.enterPassword);
        bankInput = view.findViewById(R.id.enterBankInfo);

        if (!TicketsFragmentStorage.firstInitialization){
            TicketsFragmentStorage.removeTicketBtn1 = view.findViewById(R.id.removeTicketBtn1);
            TicketsFragmentStorage.removeTicketBtn2 = view.findViewById(R.id.removeTicketBtn2);
            TicketsFragmentStorage.recentPurchase1Y = TicketsFragmentStorage.recentTicket1.getY();
            TicketsFragmentStorage.recentPurchase2Y = TicketsFragmentStorage.recentTicket2.getY();
            TicketsFragmentStorage.recentTicket1.setVisibility(View.GONE);
            TicketsFragmentStorage.recentTicket2.setVisibility(View.GONE);

            TicketsFragmentStorage.firstInitialization = true;
        }

        ObjectAnimator animateUp = ObjectAnimator.ofFloat(logInMenu, "translationY", TicketsFragmentStorage.logInMenuStartY, MainActivity.convertDpToPx(TicketsFragmentStorage.logInMenuStartY - 510, getActivity()));
        ObjectAnimator animateDown = ObjectAnimator.ofFloat(logInMenu, "translationY", MainActivity.convertDpToPx(TicketsFragmentStorage.logInMenuStartY - 510, getActivity()), TicketsFragmentStorage.logInMenuStartY);
        ObjectAnimator animateDown2 = ObjectAnimator.ofFloat(logInMenu, "translationY", MainActivity.convertDpToPx(TicketsFragmentStorage.logInMenuStartY - 690, getActivity()), TicketsFragmentStorage.logInMenuStartY);

        enTicket = view.findViewById(R.id.enTicket);
        daTicket = view.findViewById(R.id.daTicket);
        peTicket = view.findViewById(R.id.peTicket);
        arTicket = view.findViewById(R.id.arTicket);

        TicketsFragmentStorage.logInMenuStartY = Math.round(logInBtn.getY());

        //endregion

        //region internal_framework

        if (TicketsFragmentStorage.ticketCount.size() > 0){
            setRecentPurchaseDetails("recentTicket1", "null", "fragmentSwitch");
            TicketsFragmentStorage.recentTicket1.setVisibility(View.VISIBLE);
            TicketsFragmentStorage.recentTicket1.setAlpha(1);

            if (TicketsFragmentStorage.ticketCount.size() > 1){
                setRecentPurchaseDetails("recentTicket2", "null", "fragmentSwitch");
                TicketsFragmentStorage.recentTicket2.setVisibility(View.VISIBLE);
                TicketsFragmentStorage.recentTicket2.setAlpha(1);
            }
        }

        if (!TicketsFragmentStorage.loggedIn){
            if (!TicketsFragmentStorage.initialFragmentLoad){
                AnimatorSet animatorSet = new AnimatorSet();
                animateUp.setDuration(400);
                animateUp.setStartDelay(100);
                animatorSet.play(animateUp);
                animatorSet.start();

                backgroundOverlay.animate().alpha(0.5f).setDuration(400).setStartDelay(100);

                TicketsFragmentStorage.initialFragmentLoad = true;
            }
            else{
                AnimatorSet animatorSet3 = new AnimatorSet();
                animateUp.setDuration(0);
                animateUp.setStartDelay(0);
                animatorSet3.play(animateUp);
                animatorSet3.start();

                backgroundOverlay.animate().alpha(0.5f).setDuration(0);
            }
        }
        else{
            backgroundOverlay.setVisibility(View.GONE);

            if (TicketsFragmentStorage.ticketCount.size() > 0){
                zeroPurchasesHeader.animate().alpha(0).setDuration(200).withEndAction(() -> {
                    zeroPurchasesHeader.setVisibility(View.GONE);
                });
            }
            else{
                zeroPurchasesHeader.setVisibility(View.VISIBLE);
                zeroPurchasesHeader.animate().alpha(1).setDuration(0);
            }
        }

        //endregion

        //region onClickListeners

        createAccountHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TicketsFragmentStorage.createAccount){
                    logInAnimation();
                    TicketsFragmentStorage.createAccount = true;
                }
                else{
                    logInAnimation();
                    TicketsFragmentStorage.createAccount = false;
                }
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAccountDetails(animateDown, animateDown2, backgroundOverlay, view);
            }
        });

        backgroundOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (nameInput.isFocused()){
                        if (getActivity() != null){
                            MainActivity.dismissKeyboard(nameInput, getActivity(), view);
                        }
                    }
                    else if (passInput.isFocused()){
                        if (getActivity() != null){
                            MainActivity.dismissKeyboard(passInput, getActivity(), view);
                        }
                    }
                    else if (bankInput.isFocused()){
                        if (getActivity() != null){
                            MainActivity.dismissKeyboard(bankInput, getActivity(), view);
                        }
                    }
                }
                catch (Exception ignored){
                }
            }
        });

        enTicket.setOnClickListener(this);
        daTicket.setOnClickListener(this);
        peTicket.setOnClickListener(this);
        arTicket.setOnClickListener(this);
        TicketsFragmentStorage.removeTicketBtn1.setOnClickListener(this);
        TicketsFragmentStorage.removeTicketBtn2.setOnClickListener(this);

        //endregion

        //region focusChangeListeners

        nameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !TicketsFragmentStorage.movedMenuUp){
                    logInMenu.animate().translationY(TicketsFragmentStorage.logInMenuStartY - MainActivity.convertDpToPx(690, getActivity())).setDuration(300);
                    TicketsFragmentStorage.movedMenuUp = true;
                }
                else{
                    logInMenu.animate().translationY(TicketsFragmentStorage.logInMenuStartY - MainActivity.convertDpToPx(510, getActivity())).setDuration(300);
                    TicketsFragmentStorage.movedMenuUp = false;
                }
            }
        });

        passInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !TicketsFragmentStorage.movedMenuUp){
                    logInMenu.animate().translationY(TicketsFragmentStorage.logInMenuStartY - MainActivity.convertDpToPx(690, getActivity())).setDuration(300);
                    TicketsFragmentStorage.movedMenuUp = true;
                }
                else{
                    if (!TicketsFragmentStorage.createAccount){
                        if (getActivity() != null){
                            MainActivity.dismissKeyboard(bankInput, getActivity(), view);
                        }
                    }
                    logInMenu.animate().translationY(TicketsFragmentStorage.logInMenuStartY - MainActivity.convertDpToPx(510, getActivity())).setDuration(300);
                    TicketsFragmentStorage.movedMenuUp = false;
                }
            }
        });

        bankInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !TicketsFragmentStorage.movedMenuUp){
                    logInMenu.animate().translationY(TicketsFragmentStorage.logInMenuStartY - MainActivity.convertDpToPx(690, getActivity())).setDuration(300);
                    TicketsFragmentStorage.movedMenuUp = true;
                }
                else{
                    if (TicketsFragmentStorage.createAccount && !TextUtils.isEmpty(bankInput.getText()) && !TextUtils.isEmpty(passInput.getText()) && !TextUtils.isEmpty(nameInput.getText())){
                        if (getActivity() != null){
                            MainActivity.dismissKeyboard(bankInput, getActivity(), view);
                        }
                        submitAccountDetails(animateDown, animateDown2, backgroundOverlay, view);
                        logInMenu.animate().translationY(TicketsFragmentStorage.logInMenuStartY - MainActivity.convertDpToPx(510, getActivity())).setDuration(300);
                        TicketsFragmentStorage.movedMenuUp = false;
                    }
                    else{
                        if (getActivity() != null){
                            MainActivity.dismissKeyboard(bankInput, getActivity(), view);
                        }
                        logInMenu.animate().translationY(TicketsFragmentStorage.logInMenuStartY - MainActivity.convertDpToPx(510, getActivity())).setDuration(300);
                        TicketsFragmentStorage.movedMenuUp = false;
                    }
                }
            }
        });

        //endregion

        return view;
    }

    //region animationframework

    private void submitAccountDetails(ObjectAnimator animateDown, ObjectAnimator animateDown2, CardView backgroundOverlay, View view){
        if (!TicketsFragmentStorage.createAccount){
            if (GetStoredInformation.checkUserInformation(nameInput.getText().toString(), passInput.getText().toString())){
                createToast("Inloggning lyckades");

                AnimatorSet animatorSet = new AnimatorSet();

                if (nameInput.isFocused()){
                    if (getActivity() != null){
                        MainActivity.dismissKeyboard(nameInput, getActivity(), view);
                    }
                }
                else if (passInput.isFocused()){
                    if (getActivity() != null){
                        MainActivity.dismissKeyboard(passInput, getActivity(), view);
                    }
                }
                else if (bankInput.isFocused()){
                    if (getActivity() != null){
                        MainActivity.dismissKeyboard(bankInput, getActivity(), view);
                    }
                }

                if (TicketsFragmentStorage.movedMenuUp){
                    animateDown2.setDuration(400);
                    animatorSet.play(animateDown2);
                    animatorSet.start();
                }
                else{
                    animateDown.setDuration(400);
                    animatorSet.play(animateDown);
                    animatorSet.start();
                }

                TicketsFragmentStorage.loggedIn = true;

                if (TicketsFragmentStorage.ticketCount.size() < 1){
                    zeroPurchasesHeader.setVisibility(View.VISIBLE);
                    zeroPurchasesHeader.animate().alpha(1).setDuration(400);
                }

                backgroundOverlay.animate().alpha(0).setDuration(400).withEndAction(() -> backgroundOverlay.setVisibility(View.GONE));
            }
            else{
                createToast("Inloggning misslyckades");
            }
        }
        else{
            if (GetStoredInformation.createAccount(nameInput.getText().toString(), passInput.getText().toString(), bankInput.getText().toString())){
                createToast("Kontoskapning lyckades");
                logInAnimation();
                TicketsFragmentStorage.createAccount = false;
            }
            else{
                if (TicketsFragmentStorage.detailsNull){
                    createToast("Indata null");
                    TicketsFragmentStorage.detailsNull = false;
                }
                else{
                    createToast("Kontoskapning misslyckades");
                }
            }
        }
    }

    private void logInAnimation(){
        if (!TicketsFragmentStorage.createAccount){
            TransitionManager.beginDelayedTransition(logInMenu, new TransitionSet().addTransition(new ChangeBounds()));
            ViewGroup.LayoutParams params = logInMenu.getLayoutParams();

            bankInfoHeader.setVisibility(View.VISIBLE);
            bankInput.setVisibility(View.VISIBLE);
            bankInfoHeader.animate().alpha(1).setDuration(300);
            bankInput.animate().alpha(1).setDuration(300);

            createAccountHeader.setText("Användare? Klicka här");
            logInBtnText.setText("Registrera användare");

            params.height = Math.round(MainActivity.convertDpToPx(350, getActivity()));
            logInMenu.setLayoutParams(params);

            TicketsFragmentStorage.createAccount = true;
        }
        else{
            TransitionManager.beginDelayedTransition(logInMenu, new TransitionSet().addTransition(new ChangeBounds()));
            ViewGroup.LayoutParams params2 = logInMenu.getLayoutParams();

            bankInfoHeader.animate().alpha(0).setDuration(200);
            bankInput.animate().alpha(0).setDuration(200).withEndAction(() -> {
                bankInfoHeader.setVisibility(View.GONE);
                bankInput.setVisibility(View.GONE);
            });

            createAccountHeader.setText("Ny användare? Klicka här");
            logInBtnText.setText("Logga in");

            params2.height = Math.round(MainActivity.convertDpToPx(270, getActivity()));
            logInMenu.setLayoutParams(params2);

            TicketsFragmentStorage.createAccount = false;
        }
    }

    //endregion

    //region internal framework

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case (R.id.enTicket):
                if (TicketsFragmentStorage.loggedIn && TicketsFragmentStorage.ticketCount.size() < 2){
                    if (TicketsFragmentStorage.ticket2MovedUp){
                        moveRecentPurchase2("down");
                    }

                    String ticketChoice = "Enkelbiljett";
                    UserInformationStorage.userTickets.add(ticketChoice);
                    addTicketState(ticketChoice);
                }
                break;
            case (R.id.daTicket):
                if (TicketsFragmentStorage.ticket2MovedUp){
                    moveRecentPurchase2("down");
                }

                if (TicketsFragmentStorage.loggedIn && TicketsFragmentStorage.ticketCount.size() < 2){
                    String ticketChoice = "Dagsbiljett";
                    UserInformationStorage.userTickets.add(ticketChoice);
                    addTicketState(ticketChoice);
                }
                break;
            case (R.id.peTicket):
                if (TicketsFragmentStorage.ticket2MovedUp){
                    moveRecentPurchase2("down");
                }

                if (TicketsFragmentStorage.loggedIn && TicketsFragmentStorage.ticketCount.size() < 2){
                    String ticketChoice = "Periodsbiljett";
                    UserInformationStorage.userTickets.add(ticketChoice);
                    addTicketState(ticketChoice);
                }
                break;
            case (R.id.arTicket):
                if (TicketsFragmentStorage.ticket2MovedUp){
                    moveRecentPurchase2("down");
                }

                if (TicketsFragmentStorage.loggedIn && TicketsFragmentStorage.ticketCount.size() < 2){
                    String ticketChoice = "Årsbiljett";
                    UserInformationStorage.userTickets.add(ticketChoice);
                    addTicketState(ticketChoice);
                }
                break;
            case (R.id.removeTicketBtn1):
                TicketsFragmentStorage.recentTicket1.animate().alpha(0).setDuration(200).withEndAction(() -> {
                    TicketsFragmentStorage.recentTicket1.setVisibility(View.GONE);
                });

                if (TicketsFragmentStorage.recentTicket2.getVisibility() != View.VISIBLE){
                    TicketsFragmentStorage.ticketCount.clear();

                    zeroPurchasesHeader.setVisibility(View.VISIBLE);
                    zeroPurchasesHeader.animate().alpha(1).setDuration(400);
                }
                else{
                    TicketsFragmentStorage.ticketCount.remove(1);

                    moveRecentPurchase2("up");

                    TicketsFragmentStorage.ticket2MovedUp = true;
                }
                break;
            case (R.id.removeTicketBtn2):
                if (TicketsFragmentStorage.ticketCount.size() == 2){
                    TicketsFragmentStorage.ticketCount.remove(1);
                }
                else{
                    TicketsFragmentStorage.ticketCount.remove(0);
                }

                TicketsFragmentStorage.recentTicket2.animate().alpha(0).setDuration(200).withEndAction(() -> {
                    TicketsFragmentStorage.recentTicket2.setVisibility(View.GONE);
                });
                break;
            default:
                break;
        }
    }

    private void addTicketState(String ticketChoice){
        if (TicketsFragmentStorage.ticketCount.size() <= 2){
            TicketsFragmentStorage.ticketCount.add(1);

            if (zeroPurchasesHeader.getVisibility() == View.VISIBLE){
                zeroPurchasesHeader.animate().alpha(0).setDuration(200).withEndAction(() -> {
                    zeroPurchasesHeader.setVisibility(View.GONE);
                });
            }

            switch (TicketsFragmentStorage.ticketCount.size()){
                case 1:
                    if (TicketsFragmentStorage.initialTicket1Load){
                        TicketsFragmentStorage.recentTicket1 = view.findViewById(R.id.recentTicket1);
                        TicketsFragmentStorage.initialTicket1Load = false;
                    }

                    setRecentPurchaseDetails("recentTicket1", ticketChoice, "null");
                    break;
                case 2:
                    if (TicketsFragmentStorage.initialTicket2Load){
                        TicketsFragmentStorage.recentTicket1 = view.findViewById(R.id.recentTicket2);
                        TicketsFragmentStorage.initialTicket2Load = false;
                    }

                    setRecentPurchaseDetails("recentTicket2", ticketChoice, "null");
                    break;
                default:
                    break;
            }

            if (TicketsFragmentStorage.ticket2MovedUp){
                TicketsFragmentStorage.ticketCount.add(1);
                TicketsFragmentStorage.ticket2MovedUp = false;
            }
        }
    }

    private void setRecentPurchaseDetails(String setView, String ticketChoice, String action){
        switch (setView){
            case "recentTicket1":
                int cost1 = 0;

                if (action.equals("fragmentSwitch")){

                    //Toast. makeText(getActivity(),"Import data A",Toast. LENGTH_SHORT).show();

                    switch (TicketsFragmentStorage.ticketChoice1) {
                        case "Enkelbiljett":
                            cost1 = 27;
                            TicketsFragmentStorage.recentTicketTypeDrawing1.setBackgroundResource(R.drawable.clock);
                            TicketsFragmentStorage.recentTicketDuration1.setText(R.string.nittio_min);
                            break;
                        case "Dagsbiljett":
                            cost1 = 115;
                            TicketsFragmentStorage.recentTicketTypeDrawing1.setBackgroundResource(R.drawable.time);
                            TicketsFragmentStorage.recentTicketDuration1.setText(R.string.tolv_timmar);
                            break;
                        case "Periodsbiljett":
                            cost1 = 849;
                            TicketsFragmentStorage.recentTicketTypeDrawing1.setBackgroundResource(R.drawable.calendar);
                            TicketsFragmentStorage.recentTicketDuration1.setText(R.string.trettio_dagar);
                            break;
                        case "Årsbiljett":
                            cost1 = 4990;
                            TicketsFragmentStorage.recentTicketTypeDrawing1.setBackgroundResource(R.drawable.newyear);
                            TicketsFragmentStorage.recentTicketDuration1.setText(R.string.trehundrasextio_dagar);
                            break;
                        default:
                            break;
                    }
                }
                else{
                    //Toast.makeText(getActivity(),"Create data A",Toast. LENGTH_SHORT).show();

                    TicketsFragmentStorage.recentTicket1 = view.findViewById(R.id.recentTicket1);
                    TicketsFragmentStorage.recentTicketHeader1 = view.findViewById(R.id.recentTicketHeader1);
                    TicketsFragmentStorage.recentTicketCost1 = view.findViewById(R.id.recentTicketCost1);
                    TicketsFragmentStorage.recentTicketTypeDrawing1 = view.findViewById(R.id.recentTicketTypeDrawing1);
                    TicketsFragmentStorage.recentTicketZone1 = view.findViewById(R.id.recentTicketZone1);
                    TicketsFragmentStorage.recentTicketDuration1 = view.findViewById(R.id.recentTicketDuration1);
                    TicketsFragmentStorage.recentTicketHeader1.setText(ticketChoice);

                    TicketsFragmentStorage.ticketChoice1 = ticketChoice;

                    switch (ticketChoice){
                        case "Enkelbiljett":
                            cost1 = 27;
                            TicketsFragmentStorage.recentTicketTypeDrawing1.setBackgroundResource(R.drawable.clock);
                            TicketsFragmentStorage.recentTicketDuration1.setText(R.string.nittio_min);
                            break;
                        case "Dagsbiljett":
                            cost1 = 115;
                            TicketsFragmentStorage.recentTicketTypeDrawing1.setBackgroundResource(R.drawable.time);
                            TicketsFragmentStorage.recentTicketDuration1.setText(R.string.tolv_timmar);
                            break;
                        case "Periodsbiljett":
                            cost1 = 849;
                            TicketsFragmentStorage.recentTicketTypeDrawing1.setBackgroundResource(R.drawable.calendar);
                            TicketsFragmentStorage.recentTicketDuration1.setText(R.string.trettio_dagar);
                            break;
                        case "Årsbiljett":
                            cost1 = 4990;
                            TicketsFragmentStorage.recentTicketTypeDrawing1.setBackgroundResource(R.drawable.newyear);
                            TicketsFragmentStorage.recentTicketDuration1.setText(R.string.trehundrasextio_dagar);
                            break;
                        default:
                            break;
                    }

                    TicketsFragmentStorage.recentTicket1.setVisibility(View.VISIBLE);
                    TicketsFragmentStorage.recentTicket1.animate().alpha(1).setDuration(200);
                }

                TicketsFragmentStorage.recentTicketCost1.setText(String.format("%s kr", cost1));
                TicketsFragmentStorage.recentTicketZone1.setText(R.string.zonb);
                break;
            case "recentTicket2":
                int cost2 = 0;

                if (action.equals("fragmentSwitch")){

                    //Toast. makeText(getActivity(),"B",Toast. LENGTH_SHORT).show();

                    switch (TicketsFragmentStorage.ticketChoice2) {
                        case "Enkelbiljett":
                            cost2 = 27;
                            TicketsFragmentStorage.recentTicketTypeDrawing2.setBackgroundResource(R.drawable.clock);
                            TicketsFragmentStorage.recentTicketDuration2.setText(R.string.nittio_min);
                            break;
                        case "Dagsbiljett":
                            cost2 = 115;
                            TicketsFragmentStorage.recentTicketTypeDrawing2.setBackgroundResource(R.drawable.time);
                            TicketsFragmentStorage.recentTicketDuration2.setText(R.string.tolv_timmar);
                            break;
                        case "Periodsbiljett":
                            cost2 = 849;
                            TicketsFragmentStorage.recentTicketTypeDrawing2.setBackgroundResource(R.drawable.calendar);
                            TicketsFragmentStorage.recentTicketDuration2.setText(R.string.trettio_dagar);
                            break;
                        case "Årsbiljett":
                            cost2 = 4990;
                            TicketsFragmentStorage.recentTicketTypeDrawing2.setBackgroundResource(R.drawable.newyear);
                            TicketsFragmentStorage.recentTicketDuration2.setText(R.string.trehundrasextio_dagar);
                            break;
                        default:
                            break;
                    }
                }
                else{
                    TicketsFragmentStorage.recentTicket2 = view.findViewById(R.id.recentTicket2);
                    TicketsFragmentStorage.recentTicketHeader2 = view.findViewById(R.id.recentTicketHeader2);
                    TicketsFragmentStorage.recentTicketCost2 = view.findViewById(R.id.recentTicketCost2);
                    TicketsFragmentStorage.recentTicketTypeDrawing2 = view.findViewById(R.id.recentTicketTypeDrawing2);
                    TicketsFragmentStorage.recentTicketZone2 = view.findViewById(R.id.recentTicketZone2);
                    TicketsFragmentStorage.recentTicketDuration2 = view.findViewById(R.id.recentTicketDuration2);
                    TicketsFragmentStorage.recentTicketHeader2.setText(ticketChoice);

                    TicketsFragmentStorage.ticketChoice2 = ticketChoice;

                    switch (ticketChoice){
                        case "Enkelbiljett":
                            cost2 = 27;
                            TicketsFragmentStorage.recentTicketTypeDrawing2.setBackgroundResource(R.drawable.clock);
                            TicketsFragmentStorage.recentTicketDuration2.setText(R.string.nittio_min);
                            break;
                        case "Dagsbiljett":
                            cost2 = 115;
                            TicketsFragmentStorage.recentTicketTypeDrawing2.setBackgroundResource(R.drawable.time);
                            TicketsFragmentStorage.recentTicketDuration2.setText(R.string.tolv_timmar);
                            break;
                        case "Periodsbiljett":
                            cost2 = 849;
                            TicketsFragmentStorage.recentTicketTypeDrawing2.setBackgroundResource(R.drawable.calendar);
                            TicketsFragmentStorage.recentTicketDuration2.setText(R.string.trettio_dagar);
                            break;
                        case "Årsbiljett":
                            cost2 = 4990;
                            TicketsFragmentStorage.recentTicketTypeDrawing2.setBackgroundResource(R.drawable.newyear);
                            TicketsFragmentStorage.recentTicketDuration2.setText(R.string.trehundrasextio_dagar);
                            break;
                        default:
                            break;
                    }

                    TicketsFragmentStorage.recentTicket2.setVisibility(View.VISIBLE);
                    TicketsFragmentStorage.recentTicket2.animate().alpha(1).setDuration(200);
                }

                TicketsFragmentStorage.recentTicketCost2.setText(String.format("%s kr", Integer.toString(cost2)));
                TicketsFragmentStorage.recentTicketZone2.setText(R.string.zonb);
                break;
            default:
                break;
        }
    }

    private void createToast(String text){
        Toast toast = new Toast(getContext());
        View view;

        switch (text){
            case "Inloggning lyckades":
                view = LayoutInflater.from(getContext()).inflate(R.layout.toast_loginsuccess, null);
                break;
            case "Kontoskapning lyckades":
                view = LayoutInflater.from(getContext()).inflate(R.layout.toast_accountsuccess, null);
                break;
            case "Inloggning misslyckades":
                view = LayoutInflater.from(getContext()).inflate(R.layout.toast_loginfailed, null);
                break;
            case "Kontoskapning misslyckades":
                view = LayoutInflater.from(getContext()).inflate(R.layout.toast_accountfail, null);
                break;
            case "Indata null":
                view = LayoutInflater.from(getContext()).inflate(R.layout.toast_indatanull, null);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + text);
        }

        if (view != null){
            toast.setView(view);
        }
        else{
            toast.setText("error");
        }

        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 290);
        toast.show();
    }

    private void moveRecentPurchase2(String action){

        switch (action){
            case "down":
                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator movePurchase2Up = ObjectAnimator.ofFloat(TicketsFragmentStorage.recentTicket2, "translationY", TicketsFragmentStorage.recentPurchase2Y - 361, TicketsFragmentStorage.recentPurchase2Y);
                animatorSet.play(movePurchase2Up);
                animatorSet.setDuration(200);
                animatorSet.start();
                break;
            case "up":
                AnimatorSet animatorSet2 = new AnimatorSet();
                ObjectAnimator movePurchase2Down = ObjectAnimator.ofFloat(TicketsFragmentStorage.recentTicket2, "translationY", TicketsFragmentStorage.recentPurchase2Y, TicketsFragmentStorage.recentPurchase2Y - 361);
                animatorSet2.play(movePurchase2Down);
                animatorSet2.setDuration(200);
                animatorSet2.start();
                break;
            default:
                break;
        }
    }

    //endregion
}
