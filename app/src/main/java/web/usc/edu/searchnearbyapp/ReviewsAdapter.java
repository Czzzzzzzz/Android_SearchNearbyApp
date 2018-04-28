package web.usc.edu.searchnearbyapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.internal.Util;
import web.usc.edu.searchnearbyapp.constants.JSONDetailProperty;
import web.usc.edu.searchnearbyapp.util.Utils;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(JSONObject item);
    }

    private JSONArray mData;

    private OnItemClickListener mItemListener;

    private Context mCtx;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIvProfilePhoto;
        public TextView mTextViewName;
        public TextView mTextViewText;
        public RatingBar mRbRating;
        public TextView mTextViewTime;

        public ViewHolder(View itemView)
        {
            super(itemView);
            mTextViewName = itemView.findViewById(R.id.review_name);
            mTextViewText = itemView.findViewById(R.id.review_text);
            mIvProfilePhoto = itemView.findViewById(R.id.review_profile_photo);
            mRbRating = itemView.findViewById(R.id.review_rating);
            mTextViewTime = itemView.findViewById(R.id.review_date);
        }

         public void bindItemListener(final JSONObject data, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(data);
                }
            });
         }

    }

    public ReviewsAdapter(JSONArray data, Context ctx, OnItemClickListener listener) {
        mData = data;
        mCtx = ctx;
        mItemListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject review = mData.getJSONObject(position);
            holder.mTextViewName.setText(review.getString(JSONDetailProperty.RESULT_REVIEWS_NAME));
            holder.mTextViewText.setText(review.getString(JSONDetailProperty.RESULT_REVIEWS_TEXT));
            holder.mRbRating.setRating(Float.parseFloat(review.getString(JSONDetailProperty.RESULT_REVIEWS_RATING)));
            holder.mTextViewTime.setText(review.getString(JSONDetailProperty.RESULT_REVIEWS_TIME));

            holder.bindItemListener(review, mItemListener);

            Utils.loadImage(holder.mIvProfilePhoto, review.getString(JSONDetailProperty.RESULT_REVIEWS_PHOTO));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }

    //specify the view type which could be used in onCreateViewHolder
    @Override
    public int getItemViewType(int position) {
        return R.layout.review_row;
    }
}
