package web.usc.edu.searchnearbyapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LocalStorageHandler {

    private static SharedPreferences buildSharedPreference(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void saveInSharedPreference(Context ctx, String key, float value){
//        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putFloat(key, value);
//        editor.commit();


        SharedPreferences sharedPref = buildSharedPreference(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void saveInSharedPreference(Context ctx, String key, int value){
        SharedPreferences sharedPref = buildSharedPreference(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void saveInSharedPreference(Context ctx, String key, String value){
        SharedPreferences sharedPref = buildSharedPreference(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static float readFloatFromSharedPreference(Context ctx, String key){
        SharedPreferences sharedPref = buildSharedPreference(ctx);
        float value = sharedPref.getFloat(key, -1);

        return value;
    }

    public static int readIntFromSharedPreference(Context ctx, String key){
        SharedPreferences sharedPref = buildSharedPreference(ctx);
        int value = sharedPref.getInt(key, -1);

        return value;
    }

    public static String readStringFromSharedPreference(Context ctx, String key){
        SharedPreferences sharedPref = buildSharedPreference(ctx);
        String value = sharedPref.getString(key, null);

        return value;
    }

    public static void deleteKeyFromSharedPreference(Context ctx, String key){
        SharedPreferences sharedPref = buildSharedPreference(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }

    public static boolean isKeyExisted(Context ctx, String key) {
        if (readStringFromSharedPreference(ctx, key) == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
