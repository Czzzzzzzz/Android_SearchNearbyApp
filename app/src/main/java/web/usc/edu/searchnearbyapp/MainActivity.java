package web.usc.edu.searchnearbyapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;

import org.json.JSONArray;

import web.usc.edu.searchnearbyapp.constants.JSONProperty;
import web.usc.edu.searchnearbyapp.constants.MyConstants;
import web.usc.edu.searchnearbyapp.constants.SharePreferenceConstants;
import web.usc.edu.searchnearbyapp.util.LocalStorageHandler;
import web.usc.edu.searchnearbyapp.widgets.IconSectionPageAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;

    private ViewPager mViewPager;

    private FusedLocationProviderClient mFusedLocationClient;

    private LocationManager mLocationManager;
    private LocationListener mlocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: starting");
        initializeData();

        setupTabs();

        requestLocationPermission(this);

    }

    @Override
    protected void onPause() {
        super.onPause();

//        mLocationManager.removeUpdates(mlocationListener);
    }

    private void initializeData() {

        //initialize the sharedPreference to store data marked as favorite items.
        if (LocalStorageHandler.readStringFromSharedPreference(getApplicationContext(), SharePreferenceConstants.FAVORITE_LIST) == null) {
            LocalStorageHandler.saveInSharedPreference(getApplicationContext(), SharePreferenceConstants.FAVORITE_LIST, new JSONArray().toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Log.d(TAG, "onRequestPermissionsResult: permission success");
                    queryCurrentLocation();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "onRequestPermissionsResult: permission fails");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void requestLocationPermission(AppCompatActivity thisActivity) {
        if (ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        } else {
            // Permission has already been granted

            Log.d(TAG, "requestLocationPermission: permission success");

            queryCurrentLocation();
        }
    }

    //@SuppressWarnings is added since the getLastLocation() can be called only when location permission is permitted by user.
    @SuppressWarnings("MissingPermission")
    private void queryCurrentLocation(){

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        mlocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                Log.d(TAG, "onLocationChanged: " + "latitude:" + location.getLatitude() + ",longitude:" + location.getLongitude());

                LocalStorageHandler.saveInSharedPreference(getApplicationContext(), MyConstants.latitude, (float)location.getLatitude());
                LocalStorageHandler.saveInSharedPreference(getApplicationContext(), MyConstants.longitude, (float)location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        // when locationManager.NETWORK_PROVIDE is used, it cannot work.
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mlocationListener);

    }

    private void setupTabs() {

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        IconSectionPageAdapter adapter = new IconSectionPageAdapter(getSupportFragmentManager(), this);
        adapter.addFragment(new ResultsFragment(), "Search", R.drawable.search);
        adapter.addFragment(new FavoriteFragment(), "favorite", R.drawable.heart_fill_white);

        viewPager.setAdapter(adapter);
    }



}
