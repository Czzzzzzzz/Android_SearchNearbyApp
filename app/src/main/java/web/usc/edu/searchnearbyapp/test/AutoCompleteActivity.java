package web.usc.edu.searchnearbyapp.test;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import web.usc.edu.searchnearbyapp.widgets.AutoCompleteAdapter;
import web.usc.edu.searchnearbyapp.constants.MyConstants;
import web.usc.edu.searchnearbyapp.R;

public class AutoCompleteActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private static final String TAG = "AutoCompleteActivity";

    private AutoCompleteTextView mAutoCompleteTextView;
    private GoogleApiClient mGoogleApiClient;;
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private static final int GOOGLE_API_CLIENT_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_complete);

        mAutoCompleteTextView = findViewById(R.id.autocomplete);
        mAutoCompleteTextView.setThreshold(3);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addApi(Places.GEO_DATA_API)
                            .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                            .addConnectionCallbacks(this)
                            .build();

        mAutoCompleteAdapter = new AutoCompleteAdapter(this, mGoogleApiClient, MyConstants.BOUNDS_MOUNTAIN_VIEW, null);
        mAutoCompleteTextView.setAdapter(mAutoCompleteAdapter);

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
}
