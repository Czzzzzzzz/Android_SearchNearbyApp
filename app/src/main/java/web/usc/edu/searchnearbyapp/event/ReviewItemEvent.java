package web.usc.edu.searchnearbyapp.event;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import web.usc.edu.searchnearbyapp.ReviewsAdapter;
import web.usc.edu.searchnearbyapp.constants.JSONDetailProperty;

public class ReviewItemEvent implements ReviewsAdapter.OnItemClickListener{

    private static final String TAG = "ReviewItemEvent";
    Context mCtx;

    public ReviewItemEvent(Context ctx)
    {
        mCtx = ctx;
    }

    @Override
    public void onItemClick(JSONObject item) {
        try {
            String url = item.getString(JSONDetailProperty.RESULT_REVIEWS_URL);

            Log.d(TAG, "onItemClick: " + url);

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mCtx.startActivity(browserIntent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
