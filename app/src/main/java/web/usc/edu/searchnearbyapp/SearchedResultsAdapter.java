package web.usc.edu.searchnearbyapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import web.usc.edu.searchnearbyapp.event.FavoriteButtonEvent;
import web.usc.edu.searchnearbyapp.util.JSONHandler;
import web.usc.edu.searchnearbyapp.util.LocalStorageHandler;
import web.usc.edu.searchnearbyapp.util.Utils;

public class SearchedResultsAdapter extends RecyclerView.Adapter<SearchedResultsAdapter.ViewHolder> {

    public static final int RESULTS_PAGE = 1;
    public static final int Favorites_PAGE = 2;

    public interface OnItemClickListener {
        void onItemClick(JSONObject item);
    }

    private static final String TAG = "SearchedResultsAdapter";

    private JSONArray mData;
    private Context mCtx;
    private OnItemClickListener mListener;
    private FavoriteButtonEvent mFavoriteButtonEvent;

    private TextView mTvNoFavoriteReminder;

    private int mType = RESULTS_PAGE;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageViewIcon;
        public TextView mTextViewVicinity;
        public TextView mTextViewName;
        public ImageButton mImageButtonFavorite;

        public ViewHolder(View itemView)
        {
            super(itemView);
            mImageViewIcon = itemView.findViewById(R.id.category_url);
            mTextViewName = itemView.findViewById(R.id.name);
            mTextViewVicinity = itemView.findViewById(R.id.vicinity);
            mImageButtonFavorite = itemView.findViewById(R.id.iv_favorite);
        }

        //the reason why i write bind function inside Viewholder is that I need get itemView
        public void bind(final JSONObject data, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(data);
                }
            });
        }
    }

    public SearchedResultsAdapter(JSONArray data, Context ctx, int type) {

        mData = data;
        mCtx = ctx;
        mType = type;
    }

    public void setItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }

    public void setmFavoriteButtonEvent(FavoriteButtonEvent mFavoriteButtonEvent) {
        this.mFavoriteButtonEvent = mFavoriteButtonEvent;
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

            final JSONObject result = mData.getJSONObject(position);

            holder.bind(result, mListener);

            Utils.loadImage(holder.mImageViewIcon, result.getString("icon"));

            holder.mTextViewName.setText(result.getString("name"));

            holder.mTextViewVicinity.setText(result.getString("vicinity"));

            if (isMarkedAsFavorite(result.getString("place_id"))) {
                holder.mImageButtonFavorite.setBackgroundResource(R.drawable.heart_fill_red);
            } else {
                holder.mImageButtonFavorite.setBackgroundResource(R.drawable.heart_outline_black);
            }

            final int pos = position;
            holder.mImageButtonFavorite.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    switch (mType)
                    {
                        case RESULTS_PAGE:
                            mFavoriteButtonEvent.onClick(result, v);
                            break;
                        case Favorites_PAGE:
                            mFavoriteButtonEvent.onClick(result, v);

                            mData = JSONHandler.removeItemFromJsonArray(mData, pos);

                            TextView noFavorites = getmTvNoFavoriteReminder();
                            Log.d(TAG, "onClick: " + noFavorites.getText());
                            if (isFavoritesExisted())
                                noFavorites.setVisibility(View.GONE);
                            else
                                noFavorites.setVisibility(View.VISIBLE);

                            notifyDataSetChanged();
                            break;
                    }
                }
            });

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
        return R.layout.reuslts_row;
    }

    private boolean isMarkedAsFavorite(String place_id) {
        if(LocalStorageHandler.readStringFromSharedPreference(mCtx, place_id) == null)
            return false;
        else
            return true;
    }

    private boolean isFavoritesExisted() {
        if (getItemCount() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public TextView getmTvNoFavoriteReminder() {
        return mTvNoFavoriteReminder;
    }

    public void setmTvNoFavoriteReminder(TextView mTvNoFavoriteReminder) {
        this.mTvNoFavoriteReminder = mTvNoFavoriteReminder;
    }
}

