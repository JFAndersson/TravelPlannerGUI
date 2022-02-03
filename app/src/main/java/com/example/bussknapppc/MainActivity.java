package com.example.bussknapppc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.bussknapppc.routinghelpers.TaskLoadedCallback;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements TaskLoadedCallback {

    private Fragment selectedFragment = null;

    private static final String user = "root";
    private static final String pass = "03JulfhaFhA03J";
    private static String sqlAction;

    private static String userName;
    private static String userPassword;
    private static String userBankInfo;
    private static String userBoughtTicketType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            Objects.requireNonNull(getSupportActionBar()).setTitle("Mina biljetter");
            //Gör så att användaren landar på home-fragmentet vid första körning
        }
        else{
            //Restore the fragment's state here
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);
    }

    //region instanceState

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "myFragmentName", selectedFragment);
    }

    //endregion

    //region navigation

    private final BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            Objects.requireNonNull(getSupportActionBar()).setTitle("Mina biljetter");
                            break;
                        case R.id.nav_tickets:
                            selectedFragment = new TicketsFragment();
                            Objects.requireNonNull(getSupportActionBar()).setTitle("Köpa biljetter");
                            break;
                        case R.id.nav_planning:
                            selectedFragment = new PlanningFragment();
                            Objects.requireNonNull(getSupportActionBar()).setTitle("Planering");
                            break;
                    }

                    assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };


    @Override
    public void onTaskDone(Object... values) {
        if (PlanningFragment.currentPolyline != null)
            PlanningFragment.currentPolyline.remove();
        PlanningFragment.currentPolyline = PlanningFragment.map.addPolyline((PolylineOptions) values[0]);
        PlanningFragment.currentPolyline.setColor(Color.rgb(77, 136, 255));
        PlanningFragment.currentPolyline.setWidth(10);
    }

    //endregion

    //region internal_framework

    private void dismissKeyboard(AutoCompleteTextView textView, Activity activity, View view) {
        if (activity.getSystemService(Context.INPUT_METHOD_SERVICE) != null){
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            textView.clearFocus();
        }
    }

    public static float convertDpToPx(float dp, Activity activity) {
        if (activity != null){
            return dp * activity.getResources().getDisplayMetrics().density;
        }
        else{
            return 0;
        }
    }

    public static String initializeURL(String ip, String port, String schemaName){
        return "jdbc:mysql://" + ip + ":" + port + "/" + schemaName;
    }

    public static void runSqlConnection(String action, String userName, String userPassword, String bankInfo, int ticketType){

        MainActivity.userName = userName;

        switch (action){
            case "callStop":
                sqlAction = "callStop";
                ConnectMySql connectMySqlStop = new ConnectMySql();
                connectMySqlStop.execute("");
                break;
                /*
            case "createAccount":
                MainActivity.userPassword = userPassword;
                MainActivity.userBankInfo = bankInfo;

                sqlAction = "createAccount";
                ConnectMySql connectMySqlAccount = new ConnectMySql();
                connectMySqlAccount.execute("");
                break;

                 */
            default:
                break;
        }
    }

    public static void dismissKeyboard(EditText textView, Activity activity, View view) {
        if (activity.getSystemService(Context.INPUT_METHOD_SERVICE) != null){
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            textView.clearFocus();
        }
    }

    //endregion

    public static class ConnectMySql extends AsyncTask<String, Void, String> {

        String res;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                String url = initializeURL("10.0.2.2", "3306", "bussapplikation");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Database connection success");

                String result = "Database Connection Successful\n";
                Statement st = con.createStatement();

                int rs = 0;

                switch (sqlAction){
                    case "callStop":
                        try{
                            st.executeUpdate("UPDATE bussapplikation.userinformation SET hasCalledStop = '1' WHERE (idUser = '1')");
                        }
                        catch (Exception ignored){
                        }
                        break;
                    default:
                        break;
                }

                res = result;
            }
            catch (Exception e){
                e.printStackTrace();
                res = e.toString();
            }

            return res;
        }
    }
}