package com.example.bussknapppc.internalframework;

import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.bussknapppc.PlanningFragment;
import com.example.bussknapppc.R;

import java.util.ArrayList;

public class RecentRoutes {

    //region declarations

    public static ArrayList<ArrayList<String>> latestRoutes = new ArrayList<>(4);
    public static int latestRoutesCount;

    public static float recentRoute1Y;
    public static float recentRoute2Y;
    public static float recentRoute3Y;
    public static float recentRoute4Y;

    public static CardView recentRoute1;
    public static CardView recentRoute2;
    public static CardView recentRoute3;
    public static CardView recentRoute4;

    private static TextView recent1Origin;
    private static TextView recent2Origin;
    private static TextView recent3Origin;
    private static TextView recent4Origin;
    private static TextView recent1Destination;
    private static TextView recent2Destination;
    private static TextView recent3Destination;
    private static TextView recent4Destination;

    //endregion

    public static void addParameters(String from, String to){

        if (!checkIdenticals(from, to)){
            ArrayList<String> originDestination = new ArrayList<>(2);
            originDestination.add(from);
            originDestination.add(to);

            latestRoutes.add(originDestination);
            latestRoutesCount++;

            displayCardData();
        }
    }

    private static boolean checkIdenticals(String from, String to){

        for (ArrayList<String> list : latestRoutes) {
            if (list.get(0).equals(from) && list.get(1).equals(to)){
                return true;
            }
        }
        return false;
    }

    public static void initializeCardData(){
        recentRoute1 = PlanningFragment.view.findViewById(R.id.recentRoute1);
        recentRoute2 = PlanningFragment.view.findViewById(R.id.recentRoute2);
        recentRoute3 = PlanningFragment.view.findViewById(R.id.recentRoute3);
        recentRoute4 = PlanningFragment.view.findViewById(R.id.recentRoute4);

        recent1Origin = PlanningFragment.view.findViewById(R.id.recent1Origin);
        recent2Origin = PlanningFragment.view.findViewById(R.id.recent2Origin);
        recent3Origin = PlanningFragment.view.findViewById(R.id.recent3Origin);
        recent4Origin = PlanningFragment.view.findViewById(R.id.recent4Origin);
        recent1Destination = PlanningFragment.view.findViewById(R.id.recent1Destination);
        recent2Destination = PlanningFragment.view.findViewById(R.id.recent2Destination);
        recent3Destination = PlanningFragment.view.findViewById(R.id.recent3Destination);
        recent4Destination = PlanningFragment.view.findViewById(R.id.recent4Destination);
    }

    private static void displayCardData(){

        switch (latestRoutesCount){
            case 1:
                if (PlanningFragment.backgroundCover.getAlpha() > 0){
                    recentRoute1.setVisibility(View.VISIBLE);
                    recentRoute1.animate().alpha(1).setDuration(200);
                }

                recent1Origin.setText(latestRoutes.get(0).get(0));
                recent1Destination.setText(latestRoutes.get(0).get(1));

                recentRoute1Y = RecentRoutes.recentRoute1.getY();
                break;
            case 2:
                if (PlanningFragment.backgroundCover.getAlpha() > 0){
                    recentRoute2.setVisibility(View.VISIBLE);
                    recentRoute2.animate().alpha(1).setDuration(200);
                }

                recent2Origin.setText(latestRoutes.get(1).get(0));
                recent2Destination.setText(latestRoutes.get(1).get(1));

                recentRoute2Y = RecentRoutes.recentRoute2.getY();
                break;
            case 3:
                if (PlanningFragment.backgroundCover.getAlpha() > 0){
                    recentRoute3.setVisibility(View.VISIBLE);
                    recentRoute3.animate().alpha(1).setDuration(200);
                }

                recent3Origin.setText(latestRoutes.get(2).get(0));
                recent3Destination.setText(latestRoutes.get(2).get(1));

                recentRoute3Y = RecentRoutes.recentRoute3.getY();
                break;
            case 4:
                if (PlanningFragment.backgroundCover.getAlpha() > 0){
                    recentRoute4.setVisibility(View.VISIBLE);
                    recentRoute4.animate().alpha(1).setDuration(200);
                }

                recent4Origin.setText(latestRoutes.get(3).get(0));
                recent4Destination.setText(latestRoutes.get(3).get(1));

                recentRoute4Y = RecentRoutes.recentRoute4.getY();
                break;
            default:
                //nothing...
                break;
        }
    }

    public static void Run(String from, String to){
        try{
            addParameters(from, to);
        }
        catch (Exception ignored){
        }
    }
}
