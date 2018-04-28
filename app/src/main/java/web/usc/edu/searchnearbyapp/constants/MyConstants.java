package web.usc.edu.searchnearbyapp.constants;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public interface MyConstants {
//    public static final String HOST_NAME = "localhost:8888/";
    public static final String HOST_NAME = "http://10.0.2.2:8888/";
    public static final String relative_directory = "NearbySearchApp/phps/";

    public static final String SEARCH_PHP = "search.php";
    public static final String QUERY_DETAIL_PHP = "queryPlaceDetails.php";
    public static final String SEARCH_YELP_PHP = "search_yelp.php";
    public static final String NEXT_PAGE_PHP = "nextPage.php";

    //DATA is used as a flag when data is passed between activities by intent.
    public static final String DATA = "data";
//    public static final String SINGLE = "data";

    public static final String PLACE_ID = "place_id";

    //json data keys
    public static final String RESULTS = "results";

    public static final String longitude = "longitude";
    public static final String latitude = "latitude";

    //bounds
    public static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
//            new LatLng(24.7433195, -124.7844079), new LatLng(49.3457868, -66.9513812));
            new LatLng(33.7433195, -120.7844079), new LatLng(35.3457868, -114.9513812));
}
