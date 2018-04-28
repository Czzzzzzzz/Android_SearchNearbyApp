package web.usc.edu.searchnearbyapp.event;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import web.usc.edu.searchnearbyapp.DetailsActivity;
import web.usc.edu.searchnearbyapp.SearchedResultsActivity;
import web.usc.edu.searchnearbyapp.SearchedResultsAdapter;
import web.usc.edu.searchnearbyapp.constants.MyConstants;

public class SearchedItemEvent implements SearchedResultsAdapter.OnItemClickListener{
    private static final String TAG = "SearchedItemEvent";

    private ProgressDialog mProgressDialog;

    private Context mCtx;

    public SearchedItemEvent(Context context) {
        mCtx = context;
    }

    @Override
    public void onItemClick(JSONObject item) {
        try {
            mProgressDialog = createProgessBar("Fetching detail");
            mProgressDialog.show();

            String placeId = item.getString("place_id");

            String url = MyConstants.HOST_NAME + MyConstants.relative_directory + MyConstants.QUERY_DETAIL_PHP;
            url += "?place_id=" + placeId;

            Log.d(TAG, "onItemClick: url:" + url );

            searchHttpRequest(url, item);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void searchHttpRequest(String url, final JSONObject passedItem)
    {
        RequestQueue queue = Volley.newRequestQueue(mCtx);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);

                Intent intent = new Intent(mCtx , DetailsActivity.class);
                intent.putExtra(MyConstants.DATA, response);
                intent.putExtra(SearchedResultsActivity.SINGLE_RESULT, passedItem.toString());

                mProgressDialog.dismiss();

                mCtx.startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
            }
        });

        queue.add(stringRequest);
    }

    private ProgressDialog createProgessBar(String msg)
    {
        ProgressDialog progressDialog = new ProgressDialog(mCtx);
        progressDialog.setMessage(msg);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        return progressDialog;
    }
}
