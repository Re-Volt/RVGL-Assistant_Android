package io.github.tavisco.rvglassistant.items;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.tavisco.rvglassistant.R;

/**
 * Created by otavio.mpinheiro on 14/03/2018.
 */

public class TrackItem extends AbstractItem<TrackItem, TrackItem.ViewHolder> {

private String trackName;
private String trackDiscription;
private String trackImgPath;

public TrackItem withImage(String imagePath){
        this.trackImgPath=imagePath;
        return this;
        }

public TrackItem withName(String trackName){
        this.trackName=trackName;
        return this;
        }

public TrackItem withDiscription(String discription){
        this.trackDiscription=discription;
        return this;
        }

/**
 * defines the type defining this item. must be unique. preferably an id
 *
 * @return the type
 */
@Override
public int getType(){
        return R.id.track_item_id;
        }

/**
 * defines the layout which will be used for this item in the list
 *
 * @return the layout for this item
 */
@Override
public int getLayoutRes(){
        return R.layout.track_item;
        }

/**
 * binds the data of this item onto the viewHolder
 *
 * @param viewHolder the viewHolder of this item
 */
@Override
public void bindView(TrackItem.ViewHolder viewHolder,List<Object> payloads){
        super.bindView(viewHolder,payloads);

        //get the context
        Context ctx = viewHolder.itemView.getContext();

        viewHolder.trackName.setText(trackName);
        viewHolder.trackDescription.setText(trackDiscription);
        viewHolder.imageView.setImageBitmap(null);

        //Load image
        if (trackImgPath != null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(trackImgPath, options);
            viewHolder.imageView.setImageBitmap(bitmap);
        }


        }

@Override
public void unbindView(ViewHolder holder){
        super.unbindView(holder);
        holder.imageView.setImageDrawable(null);
        holder.trackName.setText(null);
        holder.trackDescription.setText(null);
        }

@Override
public ViewHolder getViewHolder(View v){
        return new ViewHolder(v);
        }

/**
 * our ViewHolder
 */
protected static class ViewHolder extends RecyclerView.ViewHolder {
    protected FrameLayout view;
    @BindView(R.id.track_img)
    protected ImageView imageView;
    @BindView(R.id.tv_track_name)
    protected TextView trackName;
    @BindView(R.id.tv_track_description)
    protected TextView trackDescription;

    public ViewHolder(View view) {
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
