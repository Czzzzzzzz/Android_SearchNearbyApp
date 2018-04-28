package web.usc.edu.searchnearbyapp.widgets;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import web.usc.edu.searchnearbyapp.constants.MyConstants;

public class Autocomplete implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    private static final String TAG = "AutoCompleteActivity";

    private AutoCompleteTextView mAutoCompleteTextView;
    private GoogleApiClient mGoogleApiClient;;
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private static final int GOOGLE_API_CLIENT_ID = 1;

    public AutoCompleteTextView createAutoCompleteView(FragmentActivity fragmentActivity, AutoCompleteTextView view, AdapterView.OnItemClickListener itermlistener) {

        mAutoCompleteTextView = view;
        mAutoCompleteTextView.setThreshold(3);

        mGoogleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(fragmentActivity, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        mAutoCompleteAdapter = new AutoCompleteAdapter(fragmentActivity, mGoogleApiClient, MyConstants.BOUNDS_MOUNTAIN_VIEW, null);
        mAutoCompleteTextView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteTextView.setOnItemClickListener(itermlistener);

        return mAutoCompleteTextView;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Google places API connected ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google places API suspended ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Google places API failed ");
    }

    public void stopAutoManage(FragmentActivity activity) {
        mGoogleApiClient.stopAutoManage(activity);
        mGoogleApiClient.disconnect();
    }
}
