package com.genix.foodgenix;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

/**
 * Created by PC on 5/12/2017.
 */

public class SaveSharedPreferences {
        static final String apiKey = "417ebb5ba57f1379ddc9d66311b91278";
        static final String PREF_USER_ID= "USERID";
        static SharedPreferences getSharedPreferences(Context ctx) {
            return PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        public static String getApiKey(){
            return apiKey;
        }
        public static void setAlreadyRun(Context ctx){
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putBoolean("PREF_ALREADY_RUN",true);
            editor.commit();
        }
        public static boolean getAlreadyRun(Context ctx){
            return getSharedPreferences(ctx).getBoolean("PREF_ALREADY_RUN",false);
        }
        public static void setUserID(Context ctx, String userName)
        {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_USER_ID, userName);
            editor.commit();
        }

        public static String getUserID(Context ctx)
        {
            return getSharedPreferences(ctx).getString(PREF_USER_ID, "");
        }

}
