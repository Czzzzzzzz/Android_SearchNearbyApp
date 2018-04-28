package web.usc.edu.searchnearbyapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import web.usc.edu.searchnearbyapp.constants.MyConstants;
import web.usc.edu.searchnearbyapp.util.Utils;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder>
{

    private JSONArray mPhotos;

    public PhotosAdapter(JSONArray photos)
    {
        mPhotos = photos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageViewPhoto;

        public ViewHolder(View itemView)
        {
            super(itemView);
            mImageViewPhoto = itemView.findViewById(R.id.iv_photo);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.mImageViewPhoto.set
        try {
            Utils.loadImage(holder.mImageViewPhoto, MyConstants.HOST_NAME + mPhotos.get(position).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        ViewHolder vh = new PhotosAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public int getItemCount() {
        return mPhotos.length();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.photos_row;
    }
}
