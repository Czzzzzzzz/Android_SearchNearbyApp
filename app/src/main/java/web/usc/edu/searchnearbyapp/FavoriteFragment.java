package web.usc.edu.searchnearbyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import web.usc.edu.searchnearbyapp.constants.SharePreferenceConstants;
import web.usc.edu.searchnearbyapp.event.FavoriteButtonEvent;
import web.usc.edu.searchnearbyapp.event.SearchedItemEvent;
import web.usc.edu.searchnearbyapp.util.LocalStorageHandler;


public class FavoriteFragment extends Fragment {
    private static final String TAG = "FavoriteFragment";

    //nullable: denotes a parameter that can be null.
    //inflater: initiate a layout XML file into corresponding View objects.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        TextView tvNoFavorites = view.findViewById(R.id.tv_no_favorites);

        RecyclerView rvFavorites = view.findViewById(R.id.rv_favorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        JSONArray jsonArray = fetechData();
        SearchedItemEvent searchedItemEvent = new SearchedItemEvent(getContext());
        SearchedResultsAdapter searchedResultsAdapter = new SearchedResultsAdapter(jsonArray, getContext(), SearchedResultsAdapter.Favorites_PAGE);
        searchedResultsAdapter.setItemClickListener(searchedItemEvent);
        searchedResultsAdapter.setmFavoriteButtonEvent(new FavoriteButtonEvent(getContext()));
        searchedResultsAdapter.setmTvNoFavoriteReminder(tvNoFavorites);

        rvFavorites.setAdapter(searchedResultsAdapter);


        if (isFavoritesExisted(jsonArray))
        {
            tvNoFavorites.setVisibility(View.GONE);
        }
        else
        {
            tvNoFavorites.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private JSONArray fetechData() {

        JSONArray out = new JSONArray();
        try {
            JSONArray favoriteList = new JSONArray(LocalStorageHandler.readStringFromSharedPreference(getContext(), SharePreferenceConstants.FAVORITE_LIST));
            for (int i = 0; i < favoriteList.length(); i++) {
                JSONObject singleFavorite = new JSONObject(LocalStorageHandler.readStringFromSharedPreference(getContext(), favoriteList.getString(i)));
                out.put(singleFavorite);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "fetechData: " + out.toString());
        return out;
    }

    private boolean isFavoritesExisted(JSONArray jsonArray) {
        if (jsonArray.length() != 0)
            return true;
        else
            return false;
    }
}
