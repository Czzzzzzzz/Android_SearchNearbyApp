package web.usc.edu.searchnearbyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import web.usc.edu.searchnearbyapp.constants.JSONDetailProperty;
import web.usc.edu.searchnearbyapp.constants.MyConstants;
import web.usc.edu.searchnearbyapp.constants.WidgesValue;
import web.usc.edu.searchnearbyapp.event.ReviewItemEvent;
import web.usc.edu.searchnearbyapp.util.JSONHandler;


public class ReviewFragment extends Fragment {

    private final String TAG = "ReviewFragment";

    private final int ORDER_METHOD_DEFAULT = 0;

    private JSONObject mData;

    private JSONArray mGoogleReviews;
    private JSONArray mYelpReviews;
    private boolean isYelpRequested = true;

    private RecyclerView mRc;
    private Spinner mSpinnerReviewContent;
    private Spinner mSpinnerOrderMethod;
    private TextView mTVNoReviews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_review, container, false);

        mRc = view.findViewById(R.id.recyclerview_reviews);
        mSpinnerReviewContent = view.findViewById(R.id.spinner_review_mode);
        mSpinnerOrderMethod = view.findViewById(R.id.spinner_order_method);

        mTVNoReviews = view.findViewById(R.id.tv_no_reviews);

        mData = initializeData();

        try {
            mGoogleReviews = mData.getJSONObject(JSONDetailProperty.RESULT).getJSONArray(JSONDetailProperty.RESULT_REVIEWS);
            setReviewAdapter(mGoogleReviews);

            checkReviewsLength(mGoogleReviews);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSpinnerReviewContent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                try
                {
                    if (selectedItem.equals(WidgesValue.SPINNER_GOOGLE_REVIEW)) {
                        Log.d(TAG, "onItemSelected: update google review");
                        setReviewAdapter(mGoogleReviews);
                    }
                    else if (selectedItem.equals(WidgesValue.SPINNER_YELP_REVIEW)) {
                        Log.d(TAG, "onItemSelected: update yelp review");
                        if (isYelpRequested) {
                            searchYelpReviews(mData);
                            isYelpRequested = false;
                        } else {
                            setReviewAdapter(mYelpReviews);
                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                //mSpinnerOrderMethod.setSelection(ORDER_METHOD_DEFAULT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected: when invoked?");
            }
        });

        mSpinnerOrderMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                String reviewContentItem = mSpinnerReviewContent.getSelectedItem().toString();

                JSONArray currentReviews = new JSONArray();

                if (reviewContentItem.equals(WidgesValue.SPINNER_GOOGLE_REVIEW)) {
                    currentReviews = mGoogleReviews;
                }
                else if (reviewContentItem.equals(WidgesValue.SPINNER_YELP_REVIEW)) {
                    currentReviews = mYelpReviews;
                }

                Comparator c;
                if (selectedItem.equals(WidgesValue.SPINNER_ORDER_DEFAULT)) {
                    setReviewAdapter(currentReviews);
                }
                else if (selectedItem.equals(WidgesValue.SPINNER_ORDER_HIGHEST_RATING)) {
                    c = getComparator(JSONDetailProperty.RESULT_REVIEWS_RATING, WidgesValue.SPINNER_ORDER_HIGHEST_RATING);
                    Log.d(TAG, "onItemSelected: " + JSONHandler.sort(currentReviews, c).toString());

                    setReviewAdapter(JSONHandler.sort(currentReviews, c));
                }
                else if (selectedItem.equals(WidgesValue.SPINNER_ORDER_LOWEREST_RATING)) {
                    c = getComparator(JSONDetailProperty.RESULT_REVIEWS_RATING, WidgesValue.SPINNER_ORDER_LOWEREST_RATING);
                    Log.d(TAG, "onItemSelected: " + JSONHandler.sort(currentReviews, c).toString());

                    setReviewAdapter(JSONHandler.sort(currentReviews, c));
                }
                else if (selectedItem.equals(WidgesValue.SPINNER_ORDER_LEAST_RECENT)) {
                    c = getComparator(JSONDetailProperty.RESULT_REVIEWS_TIME, WidgesValue.SPINNER_ORDER_LEAST_RECENT);
                    setReviewAdapter(JSONHandler.sort(currentReviews, c));
                }
                else if (selectedItem.equals(WidgesValue.SPINNER_ORDER_MOST_RECENT)) {
                    c = getComparator(JSONDetailProperty.RESULT_REVIEWS_TIME, WidgesValue.SPINNER_ORDER_MOST_RECENT);
                    setReviewAdapter(JSONHandler.sort(currentReviews, c));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected: when invoked?");
            }
        });

        return view;
    }

    private JSONObject initializeData()
    {
        try {
            String jsonStr = getArguments().getString(MyConstants.DATA);
            JSONObject jsonObject = new JSONObject(jsonStr);
            return jsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private void searchYelpReviews(JSONObject data) throws JSONException {
        String url = MyConstants.HOST_NAME + MyConstants.relative_directory + MyConstants.SEARCH_YELP_PHP;

        JSONObject result = data.getJSONObject(JSONDetailProperty.RESULT);
        JSONObject address = result.getJSONObject(JSONDetailProperty.RESULT_ADDRESS_COMPONENTS);

        url = url + "?name=" + result.getString(JSONDetailProperty.RESULT_NAME)
                + "&city=" + address.getString(JSONDetailProperty.RESULT_ARS_COM_CITY)
                + "&state=" + address.getString(JSONDetailProperty.RESULT_ARS_COM_STATE)
                + "&street=" + address.getString(JSONDetailProperty.RESULT_ARS_COM_STREET)
                + "&zip_code=" + address.getString(JSONDetailProperty.RESULT_ARS_COM_ZIP)
                + "&country=" + address.getString(JSONDetailProperty.RESULT_ARS_COM_COUNTRY);

        Log.d(TAG, "searchYelpReviews: " + url);

        searchHttpRequest(url);
    }

    private void searchHttpRequest(String url)
    {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                try {

                    mYelpReviews = new JSONObject(response).getJSONObject(JSONDetailProperty.RESULT).getJSONArray(JSONDetailProperty.RESULT_REVIEWS);
                    setReviewAdapter(mYelpReviews);

                    Log.d(TAG, "onResponse: " + mYelpReviews.toString());

                    checkReviewsLength(mYelpReviews);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
            }
        });

        queue.add(stringRequest);
    }

    private void setReviewAdapter(JSONArray data) {
        ReviewItemEvent listener = new ReviewItemEvent(getContext());
        ReviewsAdapter adapter = new ReviewsAdapter(data, getContext(), listener);
        mRc.setLayoutManager(new LinearLayoutManager(getContext()));
        mRc.setAdapter(adapter);
    }

    private void checkReviewsLength(JSONArray array)
    {
        if (isReviewsExisted(array))
        {
            mTVNoReviews.setVisibility(View.GONE);
        }
        else
        {
            mTVNoReviews.setVisibility(View.VISIBLE);
        }
    }

    private boolean isReviewsExisted(JSONArray array)
    {
        if (array.length() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private Comparator getComparator(final String tagName, final String type)
    {
            Comparator c = new Comparator() {
                @Override
                public int compare(Object o1, Object o2)
                {

                    try
                    {
                        JSONObject jsonObject_1 = (JSONObject) o1;
                        JSONObject jsonObject_2 = (JSONObject) o2;

                        float rating_1;
                        float rating_2;

                        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date_1;
                        Date date_2;

                        switch (type) {
                            case WidgesValue.SPINNER_ORDER_DEFAULT:
                                return 1;
                            case WidgesValue.SPINNER_ORDER_HIGHEST_RATING:
                                    rating_1 = Float.parseFloat(jsonObject_1.getString(tagName));
                                    rating_2 = Float.parseFloat(jsonObject_2.getString(tagName));
                                    if (rating_1 < rating_2)
                                        return 1;
                                    else
                                        return -1;
                            case WidgesValue.SPINNER_ORDER_LOWEREST_RATING:
                                rating_1 = Float.parseFloat(jsonObject_1.getString(tagName));
                                rating_2 = Float.parseFloat(jsonObject_2.getString(tagName));
                                if (rating_1 < rating_2)
                                    return -1;
                                else
                                    return 1;
                            case WidgesValue.SPINNER_ORDER_LEAST_RECENT:
                                date_1 = formate.parse(jsonObject_1.getString(tagName));
                                date_2 = formate.parse(jsonObject_2.getString(tagName));

                                return date_1.compareTo(date_2);
                            case WidgesValue.SPINNER_ORDER_MOST_RECENT:
                                date_1 = formate.parse(jsonObject_1.getString(tagName));
                                date_2 = formate.parse(jsonObject_2.getString(tagName));

                                return date_2.compareTo(date_1);
                            default:
                                return 1;
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        return 0;
                    }

                }
            };

            return c;
    }

    private String[] parseFormattedAddress(String formattedAddress) {
        return new String[1];
    }
}
