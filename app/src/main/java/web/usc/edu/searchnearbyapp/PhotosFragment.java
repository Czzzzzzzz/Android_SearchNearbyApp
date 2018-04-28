package web.usc.edu.searchnearbyapp;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import web.usc.edu.searchnearbyapp.constants.MyConstants;
import web.usc.edu.searchnearbyapp.util.Utils;


public class PhotosFragment extends Fragment {
    private static final String TAG = "PhotosFragment";

    private TextView mTvNoPhotos;
    private RecyclerView mRecyclerViewPhotos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        mTvNoPhotos = view.findViewById(R.id.tv_no_photos);
        mRecyclerViewPhotos = view.findViewById(R.id.recyclerview_photos);

        String jsonStr = getArguments().getString(MyConstants.DATA);


        try {
            JSONObject jsonObject = new JSONObject(jsonStr).getJSONObject("result");
            JSONArray photos = jsonObject.getJSONArray("photos");

            Log.d(TAG, "onCreateView: photos" + photos.toString());
            PhotosAdapter photosAdapter = new PhotosAdapter(photos);
            mRecyclerViewPhotos.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerViewPhotos.setAdapter(photosAdapter);
//
//            LinearLayout ll_photos = view.findViewById(R.id.linear_photos);
//
//            for (int i = 0; i < photos.length(); i++) {
//                ImageView iv_photo = new ImageView(getActivity());
//
//                iv_photo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                iv_photo.setScaleType(ImageView.ScaleType.FIT_XY);
//                int padding = (int)getResources().getDimension(R.dimen.common_margin);
//                iv_photo.setPadding(0, padding, 0, padding);
//
//                Utils.loadImage(iv_photo, MyConstants.HOST_NAME + photos.get(i).toString());
//
//                ll_photos.addView(iv_photo);
//
//                Log.d(TAG, "onCreateView: photo url:" + MyConstants.HOST_NAME + photos.get(i).toString());
//            }



            if (isPhotosExisted(photos))
            {
                mTvNoPhotos.setVisibility(View.GONE);
            }
            else
            {
                mTvNoPhotos.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
    }

    private boolean isPhotosExisted(JSONArray photos)
    {
        if (photos.length() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
