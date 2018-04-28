package web.usc.edu.searchnearbyapp.util;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JSONHandler {

    public static JSONArray removeItemFromJsonArray(JSONArray in, String value) {
        try {
            JSONArray out = new JSONArray();
            for (int i = 0; i < in.length(); i++) {
                    if (!in.get(i).equals(value)) {
                        out.put(in.get(i));
                    }
            }

            return out;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static JSONArray removeItemFromJsonArray(JSONArray in, int position) {
        try {
            JSONArray out = new JSONArray();
            for (int i = 0; i < in.length(); i++) {
                if (i != position) {
                    out.put(in.get(i));
                }
            }

            return out;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static JSONArray sort(JSONArray jsonArray, Comparator c) {
        List asList = new ArrayList(jsonArray.length());
        JSONArray out = new JSONArray();
        try
        {

            for (int i = 0; i < jsonArray.length(); i++) {
                asList.add(jsonArray.get(i));
            }

            Collections.sort(asList, c);

            for (Object o: asList) {
                out.put(o);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return out;

    }
}
