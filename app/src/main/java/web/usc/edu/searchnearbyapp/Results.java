package web.usc.edu.searchnearbyapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Results
{
    private String mCategoryUrl;
    private String mName;
    private String mAddress;
    private String mPlaceId;

    public Results(String categoryUrl, String name, String address, String placeId){
        mCategoryUrl = categoryUrl;
        mName = name;
        mAddress = address;
        mPlaceId = placeId;
    }

    public String getmCategoryUrl() {
        return mCategoryUrl;
    }

    public void setmCategoryUrl(String mCategoryUrl) {
        this.mCategoryUrl = mCategoryUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmPlaceId() {
        return mPlaceId;
    }

    public void setmPlaceId(String mPlaceId) {
        this.mPlaceId = mPlaceId;
    }

    public static ArrayList<Results> fromJson(JSONArray jsonArray) throws JSONException {
        ArrayList<Results> results = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            Results result = new Results(jsonObj.getString("icon"), jsonObj.getString("name"), jsonObj.getString("vicinity"),
                    jsonObj.getString("place_id"));
            results.add(result);
        }

        return results;
    }

    public static ArrayList<Results> fromJsonString(String jsonString) throws JSONException {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fromJson(jsonArray);
    }

    public static JSONObject toJsonObj(Results result) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", result.getmName());
            jsonObject.put("icon", result.getmCategoryUrl());
            jsonObject.put("vicinity", result.getmAddress());
            jsonObject.put("place_id", result.getmPlaceId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
