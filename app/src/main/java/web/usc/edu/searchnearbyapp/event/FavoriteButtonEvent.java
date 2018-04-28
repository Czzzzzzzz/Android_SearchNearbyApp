package web.usc.edu.searchnearbyapp.event;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import web.usc.edu.searchnearbyapp.R;
import web.usc.edu.searchnearbyapp.constants.SharePreferenceConstants;
import web.usc.edu.searchnearbyapp.util.JSONHandler;
import web.usc.edu.searchnearbyapp.util.LocalStorageHandler;

public class FavoriteButtonEvent {

    private static final String TAG = "FavoriteButtonEvent";

    private Context mCtx;

    public FavoriteButtonEvent(Context ctx) {
        mCtx = ctx;
    }

    public void onClick(JSONObject jsonObject, View view)
    {

        onClickResultsPage(jsonObject, view);
    }

    public void onClick(JSONObject jsonObject, MenuItem view)
    {
        onClickDetailPage(jsonObject, view);
    }



    private void onClickResultsPage(JSONObject jsonObject, View v)
    {
        try {

            Log.d(TAG, "onClick: " + jsonObject.getString("name") + " was added to favorites");

            JSONArray favoriteList = new JSONArray();
            favoriteList = new JSONArray(LocalStorageHandler.readStringFromSharedPreference(mCtx, SharePreferenceConstants.FAVORITE_LIST));


            String idFromResult = jsonObject.getString("place_id");

            if (!isMarkedAsFavorite(idFromResult)) {

                v.setBackgroundResource(R.drawable.heart_fill_red);
                Toast.makeText(mCtx, jsonObject.getString("name") + " was added to favorites", Toast.LENGTH_SHORT).show();
            } else {

                v.setBackgroundResource(R.drawable.heart_outline_black);
                Toast.makeText(mCtx, jsonObject.getString("name") + " was deleted from favorites", Toast.LENGTH_SHORT).show();
            }

            alterFavoriteList(jsonObject, idFromResult);

            Log.d(TAG, "onClick: " + favoriteList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onClickDetailPage(JSONObject jsonObject, MenuItem v)
    {
        try {

            Log.d(TAG, "onClick: " + jsonObject.getString("name") + " was added to favorites");

            JSONArray favoriteList = new JSONArray();
            favoriteList = new JSONArray(LocalStorageHandler.readStringFromSharedPreference(mCtx, SharePreferenceConstants.FAVORITE_LIST));


            String idFromResult = jsonObject.getString("place_id");

            if (!isMarkedAsFavorite(idFromResult)) {

                v.setIcon(R.drawable.heart_fill_red);
                Toast.makeText(mCtx, jsonObject.getString("name") + " was added to favorites", Toast.LENGTH_SHORT).show();
            } else {

                v.setIcon(R.drawable.heart_fill_white);
                Toast.makeText(mCtx, jsonObject.getString("name") + " was deleted from favorites", Toast.LENGTH_SHORT).show();
            }

            alterFavoriteList(jsonObject, idFromResult);

            Log.d(TAG, "onClick: " + favoriteList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private  void onClickFavoritePage(JSONObject jsonObject, View v, JSONArray data, int position)
//    {
//        onClickResultsPage(jsonObject, v);
//        data = JSONHandler.removeItemFromJsonArray(data, position);
//        data = new JSONArray();
//    }

    private void alterFavoriteList(JSONObject jsonObject, String idFromResult) throws JSONException {
        JSONArray favoriteList = new JSONArray();
        favoriteList = new JSONArray(LocalStorageHandler.readStringFromSharedPreference(mCtx, SharePreferenceConstants.FAVORITE_LIST));

        if (!isMarkedAsFavorite(idFromResult)) {
            favoriteList.put(idFromResult);
            LocalStorageHandler.saveInSharedPreference(mCtx, SharePreferenceConstants.FAVORITE_LIST, favoriteList.toString());

            LocalStorageHandler.saveInSharedPreference(mCtx, idFromResult, jsonObject.toString());
        } else {
            favoriteList = JSONHandler.removeItemFromJsonArray(favoriteList, idFromResult);
            LocalStorageHandler.saveInSharedPreference(mCtx, SharePreferenceConstants.FAVORITE_LIST, favoriteList.toString());

            LocalStorageHandler.deleteKeyFromSharedPreference(mCtx, idFromResult);
        }
    }

    private boolean isMarkedAsFavorite(String place_id) {
        if(LocalStorageHandler.readStringFromSharedPreference(mCtx, place_id) == null)
            return false;
        else
            return true;
    }
}
