package com.example.bussknapppc.storageframework;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.bussknapppc.R;

import java.util.ArrayList;

public class TicketsFragmentStorage {

    public static ArrayList<Integer> ticketCount = new ArrayList<>(); //lagrar antalet biljetter köpta

    public static boolean initialFragmentLoad = false;
    public static boolean initialTicket1Load = false;
    public static boolean initialTicket2Load = false;
    public static boolean movedMenuUp = false;
    public static boolean detailsNull = false;
    public static boolean createAccount = false;
    public static boolean loggedIn = false;
    public static boolean ticket2MovedUp = false;
    public static boolean firstInitialization = false;

    public static String ticketChoice1;
    public static String ticketChoice2;

    public static CardView removeTicketBtn1;
    public static CardView removeTicketBtn2;

    public static float logInMenuStartY;
    public static float recentPurchase1Y;
    public static float recentPurchase2Y;

    public static CardView recentTicket1;
    public static TextView recentTicketHeader1;
    public static TextView recentTicketCost1;
    public static ImageView recentTicketTypeDrawing1;
    public static TextView recentTicketZone1;
    public static TextView recentTicketDuration1;

    public static CardView recentTicket2;
    public static TextView recentTicketHeader2;
    public static TextView recentTicketCost2;
    public static ImageView recentTicketTypeDrawing2;
    public static TextView recentTicketZone2;
    public static TextView recentTicketDuration2;
}
