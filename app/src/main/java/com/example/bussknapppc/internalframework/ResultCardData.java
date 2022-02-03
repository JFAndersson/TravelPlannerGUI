package com.example.bussknapppc.internalframework;

import android.widget.TextView;

import com.example.bussknapppc.PlanningFragment;
import com.example.bussknapppc.R;

import java.util.Calendar;

public class ResultCardData {

    //region initialization

    public static TextView sr1Lage;
    public static TextView sr1Fran;
    public static TextView sr1Till;
    public static TextView sr1Restid;
    public static TextView sr1TidA;
    public static TextView sr1TidB;

    public static TextView sr2Lage;
    public static TextView sr2Fran;
    public static TextView sr2Till;
    public static TextView sr2Restid;
    public static TextView sr2TidA;
    public static TextView sr2TidB;

    public static TextView sr3Lage;
    public static TextView sr3Fran;
    public static TextView sr3Till;
    public static TextView sr3Restid;
    public static TextView sr3TidA;
    public static TextView sr3TidB;

    private static String departureTime1;
    private static String departureTime2;
    private static String departureTime3;
    private static String arrivalTime1;
    private static String arrivalTime2;
    private static String arrivalTime3;

    private static String restid1;
    private static String restid2;
    private static String restid3;

    //endregion

    public static void initializeRouteData(){
        sr1Lage = PlanningFragment.view.findViewById(R.id.sr1Lage);
        sr1Fran = PlanningFragment.view.findViewById(R.id.sr1Fran);
        sr1Till = PlanningFragment.view.findViewById(R.id.sr1Till);
        sr1Restid = PlanningFragment.view.findViewById(R.id.sr1Restid);
        sr1TidA = PlanningFragment.view.findViewById(R.id.sr1TidA);
        sr1TidB = PlanningFragment.view.findViewById(R.id.sr1TidB);

        sr2Lage = PlanningFragment.view.findViewById(R.id.sr2Lage);
        sr2Fran = PlanningFragment.view.findViewById(R.id.sr2Fran);
        sr2Till = PlanningFragment.view.findViewById(R.id.sr2Till);
        sr2Restid = PlanningFragment.view.findViewById(R.id.sr2Restid);
        sr2TidA = PlanningFragment.view.findViewById(R.id.sr2TidA);
        sr2TidB = PlanningFragment.view.findViewById(R.id.sr2TidB);

        sr3Lage = PlanningFragment.view.findViewById(R.id.sr3Lage);
        sr3Fran = PlanningFragment.view.findViewById(R.id.sr3Fran);
        sr3Till = PlanningFragment.view.findViewById(R.id.sr3Till);
        sr3Restid = PlanningFragment.view.findViewById(R.id.sr3Restid);
        sr3TidA = PlanningFragment.view.findViewById(R.id.sr3TidA);
        sr3TidB = PlanningFragment.view.findViewById(R.id.sr3TidB);
    }

    public static void setTime(){

        boolean post10pre40 = false;
        boolean post40 = false;

        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // returns the hour in 24 hrs format (ranging from 0-23)
        int currentMinute = rightNow.get(Calendar.MINUTE); // returns the minute (ranging from 1-59)

        if (currentMinute > 10 && currentMinute < 40){
            post10pre40 = true;
        }
        else if (currentMinute > 40){
            post40 = true;
        }

        if (currentHourIn24Format <= 21){
            if (post10pre40){
                departureTime1 = currentHourIn24Format + ":40";
                arrivalTime1 = (currentHourIn24Format + 1) + ":05";

                departureTime2 = (currentHourIn24Format + 1) + ":10";
                arrivalTime2 = (currentHourIn24Format + 1) + ":40";

                departureTime3 = (currentHourIn24Format + 1) + ":40";
                arrivalTime3 = (currentHourIn24Format + 2) + ":06";
            }
            else if (post40){
                departureTime1 = (currentHourIn24Format + 1) + ":10";
                arrivalTime1 = (currentHourIn24Format + 1) + ":37";

                departureTime2 = (currentHourIn24Format + 1) + ":40";
                arrivalTime2 = (currentHourIn24Format + 2) + ":04";

                departureTime3 = (currentHourIn24Format + 2) + ":10";
                arrivalTime3 = (currentHourIn24Format + 2) + ":34";
            }
            else{
                departureTime1 = currentHourIn24Format + ":10";
                arrivalTime1 = currentHourIn24Format + ":39";

                departureTime2 = currentHourIn24Format + ":40";
                arrivalTime2 = (currentHourIn24Format + 1) + ":08";

                departureTime3 = (currentHourIn24Format + 1) + ":10";
                arrivalTime3 = (currentHourIn24Format + 1) + ":35";
            }
        }
        else if (currentHourIn24Format == 22){
            if (post10pre40){
                departureTime1 = "22:40";
                arrivalTime1 = "23:06";

                departureTime2 = "23:10";
                arrivalTime2 = "23:35";

                departureTime3 = "23:40";
                arrivalTime3 = "00:08";
            }
            else if (post40){
                departureTime1 = "23:10";
                arrivalTime1 = "23:36";

                departureTime2 = "23:40";
                arrivalTime2 = "00:10";

                departureTime3 = "00:10";
                arrivalTime3 = "00:34";
            }
            else{
                departureTime1 = "22:10";
                arrivalTime1 = "22:38";

                departureTime2 = "22:40";
                arrivalTime2 = "23:10";

                departureTime3 = "23:10";
                arrivalTime3 = "23:34";
            }
        }
        else {
            if (post10pre40){
                departureTime1 = "23:40";
                arrivalTime1 = "00:07";

                departureTime2 = "00:10";
                arrivalTime2 = "00:33";

                departureTime3 = "00:40";
                arrivalTime3 = "01:10";
            }
            else if (post40){
                departureTime1 = "00:10";
                arrivalTime1 = "00:34";

                departureTime2 = "00:40";
                arrivalTime2 = "01:05";

                departureTime3 = "01:10";
                arrivalTime3 = "01:36";
            }
            else{
                departureTime1 = "23:10";
                arrivalTime1 = "23:34";

                departureTime2 = "23:40";
                arrivalTime2 = "00:05";

                departureTime3 = "00:10";
                arrivalTime3 = "00:36";
            }
        }
    }

    private static void calcRestid() {
        int restTill60;

        String[] splitDeparture1 = departureTime1.split(":", 2);
        String[] splitArrival1 = arrivalTime1.split(":", 2);

        String[] splitDeparture2 = departureTime2.split(":", 2);
        String[] splitArrival2 = arrivalTime2.split(":", 2);

        String[] splitDeparture3 = departureTime3.split(":", 2);
        String[] splitArrival3 = arrivalTime3.split(":", 2);

        if (Integer.parseInt(splitDeparture1[1]) < Integer.parseInt(splitArrival1[1])){
            restid1 = String.valueOf(Integer.parseInt(splitArrival1[1]) - Integer.parseInt(splitDeparture1[1]));
        }
        else{
            restTill60 = 60 - Integer.parseInt(splitDeparture1[1]);
            restid1 = String.valueOf(restTill60 + Integer.parseInt(splitArrival1[1]));
        }

        if (Integer.parseInt(splitDeparture2[1]) < Integer.parseInt(splitArrival2[1])){
            restid2 = String.valueOf(Integer.parseInt(splitArrival2[1]) - Integer.parseInt(splitDeparture2[1]));
        }
        else{
            restTill60 = 60 - Integer.parseInt(splitDeparture2[1]);
            restid2 = String.valueOf(restTill60 + Integer.parseInt(splitArrival2[1]));
        }

        if (Integer.parseInt(splitDeparture3[1]) < Integer.parseInt(splitArrival3[1])){
            restid3 = String.valueOf(Integer.parseInt(splitArrival3[1]) - Integer.parseInt(splitDeparture3[1]));
        }
        else{
            restTill60 = 60 - Integer.parseInt(splitDeparture3[1]);
            restid3 = String.valueOf(restTill60 + Integer.parseInt(splitArrival3[1]));
        }
    }

    public static void applyData(){

        String lage;

        calcRestid();

        if (PlanningFragment.boxFrom.getText().toString().equals("Borås Resecentrum")
                && PlanningFragment.boxTo.getText().toString().equals("Viskafors Station")){
            lage = "Läge G";
        }
        else if (PlanningFragment.boxFrom.getText().toString().equals("Borås Resecentrum")
                && PlanningFragment.boxTo.getText().toString().equals("Fristad Station")){
            lage = "Läge R";
        }
        else{
            lage = "Läge A";
        }

        sr1Lage.setText(lage);
        sr1Fran.setText(PlanningFragment.boxFrom.getText().toString());
        sr1Till.setText(PlanningFragment.boxTo.getText().toString());
        sr1Restid.setText(restid1 + " min");
        sr1TidA.setText(departureTime1);
        sr1TidB.setText(arrivalTime1);

        sr2Lage.setText(lage);
        sr2Fran.setText(PlanningFragment.boxFrom.getText().toString());
        sr2Till.setText(PlanningFragment.boxTo.getText().toString());
        sr2Restid.setText(restid2 + " min");
        sr2TidA.setText(departureTime2);
        sr2TidB.setText(arrivalTime2);

        sr3Lage.setText(lage);
        sr3Fran.setText(PlanningFragment.boxFrom.getText().toString());
        sr3Till.setText(PlanningFragment.boxTo.getText().toString());
        sr3Restid.setText(restid3 + " min");
        sr3TidA.setText(departureTime3);
        sr3TidB.setText(arrivalTime3);
    }

    public static void Run(){
        try{
            initializeRouteData();
            setTime();
            applyData();
        }
        catch (Exception ignored){
        }
    }
}
