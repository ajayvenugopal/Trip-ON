package com.ajay.floow.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesUtils {

    public static void saveData(String name, String value, Context context) {

        try {

            SharedPreferences settings = context
                    .getSharedPreferences(getPrefsName(), 0);

            SharedPreferences.Editor editor = settings.edit();

            editor.putString(name, value);
            editor.commit();
        } catch (NullPointerException ignored) {

        }

    }


    public static final String PREFS_NAME = "AJAY";


    /**
     * @return the prefsName
     */
    public static String getPrefsName() {
        return PREFS_NAME;
    }


    public static String getData(String name, Context context, String defaultvalue) {

        try {


            SharedPreferences settings = context
                    .getSharedPreferences(getPrefsName(), 0);

            return settings.getString(name, defaultvalue);
        } catch (NullPointerException ignored) {
            return "";
        }

    }

    public static void cleardata(Context context) {
        SharedPreferences settings = context
                .getSharedPreferences(getPrefsName(), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

}
