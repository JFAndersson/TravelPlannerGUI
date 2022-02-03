package com.example.bussknapppc.storageframework;

import android.text.TextUtils;

import com.example.bussknapppc.MainActivity;

public class GetStoredInformation {

    public static boolean checkUserInformation(String userName, String userPassword){

        for (String name : UserInformationStorage.userName){
            if (name.equals(userName)){
                for (String pass : UserInformationStorage.userPassword){
                    if (pass.equals(userPassword)){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean createAccount(String userName, String userPassword, String userBankInfo){

        //MainActivity.runSqlConnection("createAccount", userName, userPassword, userBankInfo, 0);

        boolean cond1 = TextUtils.isEmpty(userName);
        boolean cond2 = TextUtils.isEmpty(userPassword);
        boolean cond3 = TextUtils.isEmpty(userBankInfo);

        if (!cond1 && !cond2 && !cond3){
            for (String string : UserInformationStorage.userName){
                if (string.equals(userName)){
                    return false;
                }
            }

            for (String string : UserInformationStorage.userPassword){
                if (string.equals(userPassword)){
                    return false;
                }
            }

            UserInformationStorage.userName.add(userName);
            UserInformationStorage.userPassword.add(userPassword);
            UserInformationStorage.userBankInfo.add(userBankInfo);
        }
        else{
            TicketsFragmentStorage.detailsNull = true;
            return false;
        }

        return true;
    }

    public static void callStop(String userName, int ticketType){
        MainActivity.runSqlConnection("callStop", userName, null, null, ticketType);
    }
}
