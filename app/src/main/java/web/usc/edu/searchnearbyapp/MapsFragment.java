package web.usc.edu.searchnearbyapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import web.usc.edu.searchnearbyapp.constants.JSONDetailProperty;
import web.usc.edu.searchnearbyapp.constants.MyConstants;
import web.usc.edu.searchnearbyapp.util.Utils;
import web.usc.edu.searchnearbyapp.widgets.Autocomplete;


public class MapsFragment extends Fragment implements OnMapReadyCallback, AdapterView.OnItemClickListener {

    private static final String TAG = "MapsFragment";

    private GoogleMap mGoogleMap;

    private JSONObject mData;

    private AutoCompleteTextView mAutoCompleteTextView;
    private Autocomplete mAutocomplete;
    private Spinner mSpinnerTravelMode;

    private boolean isInitiatedSpinner = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);;

        initializeData();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        mSpinnerTravelMode = view.findViewById(R.id.travel_mode);
        mSpinnerTravelMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!isInitiatedSpinner) {
                    Log.d(TAG, "onItemClick: " + mAutoCompleteTextView.getText());

                    String origin = mAutoCompleteTextView.getText().toString();
                    com.google.maps.model.LatLng des = getDestinationModelType(mData);

                    TravelMode travelMode = getTravelMode(mSpinnerTravelMode.getSelectedItem().toString());


                    mGoogleMap.clear();
                    updateDirections(travelMode, origin, des);

                    Log.d(TAG, "onItemClick: task finished");
                }
                else
                {
                    //this listener will be called at the time when the spinner is initiatied.
                    isInitiatedSpinner = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        //auto complete view
        mAutoCompleteTextView = view.findViewById(R.id.map_autocompleteTextView);
        mAutocomplete = new Autocomplete();
        mAutocomplete.createAutoCompleteView(getActivity(), mAutoCompleteTextView, this);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mAutocomplete.stopAutoManage(getActivity());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        LatLng destination = getDestinationGmsType(mData);
        mGoogleMap.addMarker(new MarkerOptions().position(destination).title(getLocationName(mData)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 12));
    }

    private void initializeData() {
        try {
            mData = new JSONObject(getArguments().getString(MyConstants.DATA));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private float[] getDestination(JSONObject data) {
        float[] destination = new float[2];
        try {
            JSONObject jsonObject = data.getJSONObject("result");
            destination[0] = Float.parseFloat(jsonObject.getString("latitude"));
            destination[1] = Float.parseFloat(jsonObject.getString("longitude"));

        } catch (JSONException e) {
            e.printStackTrace();
            destination[0] = 0f;
            destination[1] = 0f;
        }

        return destination;
    }

    private LatLng getDestinationGmsType(JSONObject data) {

        float[] destination = getDestination(data);

        return new LatLng(destination[0], destination[1]);
    }

    private com.google.maps.model.LatLng getDestinationModelType(JSONObject data) {
        float[] destination = getDestination(data);

        return new com.google.maps.model.LatLng(destination[0], destination[1]);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "onItemClick: " + mAutoCompleteTextView.getText());

        String origin = mAutoCompleteTextView.getText().toString();
        com.google.maps.model.LatLng des = getDestinationModelType(mData);

        TravelMode travelMode = getTravelMode(mSpinnerTravelMode.getSelectedItem().toString());

        mGoogleMap.clear();
        updateDirections(travelMode, origin, des);

        Log.d(TAG, "onItemClick: task finished" );
    }

    private void updateDirections(TravelMode travelMode, String origin, com.google.maps.model.LatLng des)
    {
        DateTime now = new DateTime();
        DirectionsResult result = null;
        try {
            //DirexctionApi.newReqeust might be a HTTP request. So we should examine whether our API key is unlimited
            // or limited to only android application in Google API Console
            result = DirectionsApi.newRequest(getGeoContext())
                    .mode(travelMode).origin(origin)
                    .destination(des).departureTime(now)
                    .await();

            Log.d(TAG, "onItemClick: result is retrieved" );

            addMarkersToMap(result, mGoogleMap);
            addPolyline(result, mGoogleMap);
            moveCamera(result);

            Log.d(TAG, "onItemClick: task finished" );
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.google_maps_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng)).title(results.routes[0].legs[0].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0].startAddress).snippet(getEndLocationTitle(results)));
    }

    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable;
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private void moveCamera(DirectionsResult result)
    {
        LatLng northeast = new LatLng(result.routes[0].bounds.northeast.lat, result.routes[0].bounds.northeast.lng);
        LatLng southwest = new LatLng(result.routes[0].bounds.southwest.lat, result.routes[0].bounds.southwest.lng);

        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds( bounds, 50));
    }

    private TravelMode getTravelMode(String travelModeStr) {
        TravelMode travelMode1 = TravelMode.DRIVING;
        if (travelModeStr.equals("Driving")) {
            travelMode1 = TravelMode.DRIVING;
        } else if (travelModeStr.equals("Bicycling")){
            travelMode1 = TravelMode.BICYCLING;
        } else if (travelModeStr.equals("Transit")){
            travelMode1 = TravelMode.TRANSIT;
        } else if (travelModeStr.equals("Walking")){
            travelMode1 = TravelMode.WALKING;
        }
        return travelMode1;
    }

    private String getLocationName(JSONObject data) {
        try {
            String name = data.getJSONObject(JSONDetailProperty.RESULT).getString(JSONDetailProperty.RESULT_NAME);
            return name;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
