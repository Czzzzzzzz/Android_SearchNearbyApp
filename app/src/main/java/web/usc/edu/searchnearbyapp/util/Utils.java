package web.usc.edu.searchnearbyapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class Utils {
    public static void loadImage(ImageView iv, String url){
        Picasso.get().load(url).into(iv);
    }

    public static float readFromSharedPreference(Activity activity, String key){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        float value = sharedPref.getFloat(key, -1);

        return value;
    }

    public static String convertMilesToMeters(String miles) {
        float f_miles = Float.parseFloat(miles);
        String meters = Float.toString(f_miles * 1609.34f );
        return meters;
    }
}
