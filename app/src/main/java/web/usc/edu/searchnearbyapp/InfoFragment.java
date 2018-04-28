package web.usc.edu.searchnearbyapp;

import android.media.Rating;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import web.usc.edu.searchnearbyapp.constants.MyConstants;
import web.usc.edu.searchnearbyapp.util.DrawnableUtil;

public class InfoFragment extends Fragment {

    private static final String TAG = "InfoFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        String jsonStr = getArguments().getString(MyConstants.DATA);
        Log.d(TAG, "onCreateView: " + jsonStr);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonStr).getJSONObject("result");

        } catch (JSONException e) {
            jsonObject = new JSONObject();
            e.printStackTrace();
        }

        try {
            TextView tv_address = view.findViewById(R.id.info_address);
            tv_address.setText(jsonObject.getString("formatted_address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            TextView tv_phone = view.findViewById(R.id.info_phone);
            tv_phone.setText(jsonObject.getString("formatted_phone_number"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            TextView tv_price_level = view.findViewById(R.id.info_price_level);
            int number = 0;
            if(!jsonObject.getString("price_level").equals("null"))
            {
                number = Integer.parseInt(jsonObject.getString("price_level"));
            }
            String priceLevelSymbols = DrawnableUtil.repeatSymbol(number, "$");
            tv_price_level.setText(priceLevelSymbols);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            RatingBar tv_rating = view.findViewById(R.id.info_rating);
            tv_rating.setRating(Float.parseFloat(jsonObject.getString("rating")));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            TextView tv_google_page = view.findViewById(R.id.info_google_page);
            tv_google_page.setText(jsonObject.getString("url"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            TextView tv_website = view.findViewById(R.id.info_website);
            tv_website.setText(jsonObject.getString("website"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

}
