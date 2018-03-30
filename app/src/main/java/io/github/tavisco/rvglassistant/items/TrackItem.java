package io.github.tavisco.rvglassistant.items;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.tavisco.rvglassistant.InstallActivity;
import io.github.tavisco.rvglassistant.R;

/**
 * Created by Tavisco on 14/03/2018.
 */

public class TrackItem extends AbstractItem<TrackItem, TrackItem.ViewHolder> {

    private String trackName;
    // private String trackDiscription;
    private String trackImgPath;

    public String getTrackName() {
        return trackName;
    }

    /*public String getTrackDiscription() {
        return trackDiscription;
    }*/

    public String getTrackImgPath() {
        return trackImgPath;
    }

    public void setImage(String trackImage) {
        this.trackImgPath = trackImage;
    }

    public void setName(String trackName) {
        this.trackName = trackName;
    }

    /*public TrackItem withDiscription(String discription) {
        this.trackDiscription = discription;
        return this;
    }*/


    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.track_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.track_item;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(@NonNull TrackItem.ViewHolder viewHolder, @NonNull List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        viewHolder.trackName.setText(trackName);
        // viewHolder.trackDescription.setText(trackDiscription);
        viewHolder.imageView.setImageBitmap(null);

        //Load image
        if (trackImgPath != null) {
            Glide.with(viewHolder.view.getContext()).load(trackImgPath).into(viewHolder.imageView);
        }


    }

    @Override
    public void unbindView(@NonNull ViewHolder holder) {
        super.unbindView(holder);
        holder.imageView.setImageDrawable(null);
        holder.trackName.setText(null);
        holder.trackDescription.setText(null);
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected FrameLayout view;
        @BindView(R.id.track_img)
        ImageView imageView;
        @BindView(R.id.tv_track_name)
        TextView trackName;
        @BindView(R.id.tv_track_description)
        TextView trackDescription;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = (FrameLayout) view;

            //optimization to preset the correct height for our device
            int screenWidth = view.getContext().getResources().getDisplayMetrics().widthPixels;
            int finalHeight = (int) (screenWidth / 1.5) / 2;
            imageView.setMinimumHeight(finalHeight);
            imageView.setMaxHeight(finalHeight);
            imageView.setAdjustViewBounds(false);
            //set height as layoutParameter too
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            lp.height = finalHeight;
            imageView.setLayoutParams(lp);
        }
    }
}
