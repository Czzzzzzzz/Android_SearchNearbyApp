package web.usc.edu.searchnearbyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import web.usc.edu.searchnearbyapp.constants.JSONProperty;
import web.usc.edu.searchnearbyapp.constants.MyConstants;
import web.usc.edu.searchnearbyapp.event.FavoriteButtonEvent;
import web.usc.edu.searchnearbyapp.event.SearchedItemEvent;

public class SearchedResultsActivity extends AppCompatActivity {

    private static final String TAG = "SearchedResultsActivity";
    public static final String SINGLE_RESULT = "single_result";

    private JSONObject mData;
    private List<JSONObject> mPreviousData = new ArrayList<>();

    private int mPageNum;

    private Button mPreviousButton;
    private Button mNextButton;
    private RecyclerView mLv;
    private TextView mTvNoResults;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_results);

        mPreviousButton = findViewById(R.id.btn_previous);
        mNextButton = findViewById(R.id.btn_next);
        mTvNoResults = findViewById(R.id.tv_no_results);

        try {

            initializeData();
            initViews();
            setupBackActionBar();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //when back button is pressed, this callback function will be called.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeData() throws JSONException {
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra(MyConstants.DATA);
        mData = new JSONObject(jsonString);

        setmPageNum(1);
    }

    private void initViews() throws JSONException {

        final SearchedResultsAdapter searchedResultsAdapter = getSearchedResultsAdapter(mData);

        mLv = (RecyclerView) findViewById(R.id.lv_results);
        mLv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mLv.setAdapter(searchedResultsAdapter);

        if (mData.getJSONArray("results").length() == 0)
        {
            mTvNoResults.setVisibility(View.VISIBLE);
            mPreviousButton.setVisibility(View.GONE);
            mNextButton.setVisibility(View.GONE);
        }
        else
        {
            mTvNoResults.setVisibility(View.GONE);
            mPreviousButton.setVisibility(View.VISIBLE);
            mNextButton.setVisibility(View.VISIBLE);
        }

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = createProgessBar("Fetching Data");
                mProgressDialog.show();

                setmPageNum(getmPageNum() - 1);
                try {
                    mData = mPreviousData.get(getmPageNum()-1);
                    SearchedResultsAdapter searchedResultsAdapter = getSearchedResultsAdapter(mData);
                    mLv.setAdapter(searchedResultsAdapter);

                    checkButtonStatus();

                    mProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = createProgessBar("Fetching Data");
                mProgressDialog.show();

                setmPageNum(getmPageNum() + 1);
                String nextTokenPage;
                try {
                    nextTokenPage = mData.getString(JSONProperty.NEXT_PAGE_TOKEN);

                    //if the data of previous pages has been stored, directly fetch data from mPrevisouData list
                    if (getmPageNum() <= mPreviousData.size()) {
                        SearchedResultsAdapter searchedResultsAdapter = getSearchedResultsAdapter(mPreviousData.get(getmPageNum()-1));
                        mLv.setAdapter(searchedResultsAdapter);
                    } else {
                        String url = MyConstants.HOST_NAME + MyConstants.relative_directory + MyConstants.NEXT_PAGE_PHP + "?next_page_token=" + nextTokenPage;
                        Log.d(TAG, "onClick: " + url);
                        searchNextTokenPage(url);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        checkButtonStatus();
    }

    private ProgressDialog createProgessBar(String msg)
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        return progressDialog;
    }

    private void checkButtonStatus() throws JSONException {
        if (getmPageNum() == 1) {
            mPreviousButton.setEnabled(false);
        } else {
            mPreviousButton.setEnabled(true);
        }

        Log.d(TAG, "checkButtonStatus: " + mData.getString(JSONProperty.NEXT_PAGE_TOKEN));
        if (mData.getString(JSONProperty.NEXT_PAGE_TOKEN).equals("null")) {
            mNextButton.setEnabled(false);
        } else {
            mNextButton.setEnabled(true);
        }
    }

    private void searchNextTokenPage(String url)
    {
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                try {
                    mPreviousData.add(mData);

                    mData = new JSONObject(response);
                    SearchedResultsAdapter searchedResultsAdapter = getSearchedResultsAdapter(mData);
                    mLv.setAdapter(searchedResultsAdapter);

                    checkButtonStatus();

                    mProgressDialog.dismiss();

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

    private void setupBackActionBar() {

        if (getSupportActionBar() == null) {
            // getActionBar() returns null probably because it has been deprecated
            //        getActionBar().setDisplayHomeAsUpEnabled(true);
            Log.d(TAG, "onCreate: action bar is null");
        }
        else {

            Log.d(TAG, "onCreate: action bar is set");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private SearchedResultsAdapter getSearchedResultsAdapter(JSONObject data) throws JSONException {
        JSONArray jsonArray = data.getJSONArray(MyConstants.RESULTS);
        Log.d(TAG, "getSearchedResultsAdapter: " + jsonArray.toString());

        //when the parameter of SearchedItemEvent is getApplicationContext(), the error comes out that bad token Exception: unable
        //to add window. This might happens because the pop up window is shown too quickly before activity finishes the construction.
        //But I don't know why SearchedResultsActivity.this can work.
        SearchedResultsAdapter searchedResultsAdapter = new SearchedResultsAdapter(jsonArray, getApplicationContext(), SearchedResultsAdapter.RESULTS_PAGE);
        searchedResultsAdapter.setItemClickListener(new SearchedItemEvent(SearchedResultsActivity.this));
        searchedResultsAdapter.setmFavoriteButtonEvent(new FavoriteButtonEvent(getApplicationContext()));

        return searchedResultsAdapter;
    }


    //get and set page number
    public int getmPageNum() {
        return mPageNum;
    }

    public void setmPageNum(int mPageNum) {
        this.mPageNum = mPageNum;
    }
}
