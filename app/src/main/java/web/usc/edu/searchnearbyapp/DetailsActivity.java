package web.usc.edu.searchnearbyapp;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import web.usc.edu.searchnearbyapp.constants.JSONDetailProperty;
import web.usc.edu.searchnearbyapp.constants.JSONRestulsProperty;
import web.usc.edu.searchnearbyapp.constants.MyConstants;
import web.usc.edu.searchnearbyapp.event.FavoriteButtonEvent;
import web.usc.edu.searchnearbyapp.util.LocalStorageHandler;
import web.usc.edu.searchnearbyapp.widgets.IconSectionPageAdapter;

//this should extends from FragmentActicity since it can be successfully used in Autocomplete
public class DetailsActivity extends AppCompatActivity {


    private static final String TAG = "DetailsActivity";
    ViewPager mViewPager;

    JSONObject mData;
    JSONObject mSingleResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        try {
            String jsonStr = getDataFromParentActivity(MyConstants.DATA);
            mData = new JSONObject(jsonStr);

            String singleResultStr = getDataFromParentActivity(SearchedResultsActivity.SINGLE_RESULT);
            mSingleResult = new JSONObject(singleResultStr);

            setupBackActionBar(mData);
            setupTabs(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupTabs(String data) {

        mViewPager = (ViewPager) findViewById(R.id.details_container);
        setupViewPager(mViewPager, data);

        TabLayout tabLayout = findViewById(R.id.details_tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager, String data) {

        Bundle bundle = new Bundle();
        bundle.putString(MyConstants.DATA, data);

        IconSectionPageAdapter adapter = new IconSectionPageAdapter(getSupportFragmentManager(), this);

        Fragment[] fragments = {new InfoFragment(), new PhotosFragment(), new MapsFragment(), new ReviewFragment()};
        String[] titles = {"Info", "Photos", "Map", "Review"};
        int[] iconIds = {R.drawable.info_outline, R.drawable.photos, R.drawable.map, R.drawable.review};

        for (int i = 0; i < fragments.length; i++) {
            fragments[i].setArguments(bundle);
            adapter.addFragment(fragments[i], titles[i], iconIds[i]);
        }


        viewPager.setAdapter(adapter);
    }

    //this sets a back button alongside the app icon. When the button is clicked,
    //the callback function OnOptionsItemSelected is called.
    private void setupBackActionBar(JSONObject jsonObject) throws JSONException {

        if (getSupportActionBar() == null) {
            // getActionBar() returns null probably because it has been deprecated
            //        getActionBar().setDisplayHomeAsUpEnabled(true);
            Log.d(TAG, "onCreate: action bar is null");
        }
        else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            String title = jsonObject.getJSONObject(JSONDetailProperty.RESULT).getString(JSONDetailProperty.RESULT_NAME);
            getSupportActionBar().setTitle(title);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        try {
            if (LocalStorageHandler.isKeyExisted(this, mSingleResult.getString(JSONRestulsProperty.PLACE_ID)))
            {
                menu.findItem(R.id.action_favorite).setIcon(R.drawable.heart_fill_red);
            }
            else
            {
                menu.findItem(R.id.action_favorite).setIcon(R.drawable.heart_fill_white);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try
        {

            switch (item.getItemId())
            {
                // Respond to the action bar's Up/Home button
                case android.R.id.home:
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                case R.id.action_favorite:
                    favorite(item);
                    return true;
                case R.id.action_twitter:
                    share(mData.getJSONObject(JSONDetailProperty.RESULT));
                    return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    private void favorite(MenuItem v)
    {
        try {
            String jsonStr = getDataFromParentActivity(SearchedResultsActivity.SINGLE_RESULT);
            JSONObject jsonObj = new JSONObject(jsonStr);
            FavoriteButtonEvent favoriteButtonEvent = new FavoriteButtonEvent(this);
            favoriteButtonEvent.onClick(jsonObj, v);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void share(JSONObject data){
        try {
            String url = "https://twitter.com/intent/tweet?text=" + "Check out "
                    + data.getString(JSONDetailProperty.RESULT_NAME)
                    + " located at " + data.getString(JSONDetailProperty.RESULT_ADDRESS)
                    + ". Website: " + data.getString(JSONDetailProperty.RESULT_WEBSITE) + " %23TravelAndEntertainmentSearch";

//            url = url.replace("#", "%23");

            Log.d(TAG, "share: " + url);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getDataFromParentActivity(String key)
    {
        Intent intent = getIntent();
        return intent.getStringExtra(key);
    }
}
