package io.github.tavisco.rvglassistant.objects.RecyclerViewItems;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.objects.BaseItem;
import io.github.tavisco.rvglassistant.objects.CarItem;
import io.github.tavisco.rvglassistant.objects.Constants;

/**
 * Created by otavio.mpinheiro on 15/03/2018.
 */

public class CarViewItem extends AbstractItem<CarViewItem, CarViewItem.ViewHolder> {

    public CarViewItem(CarItem car) {
        this.car = car;
    }

    private CarItem car;

    public CarItem getCar() {
        return car;
    }

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
    public void bindView(CarViewItem.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        viewHolder.trackName.setText(car.getName());
        //viewHolder.trackDescription.setText(carDiscription);
        viewHolder.imageView.setImageBitmap(null);

        //Load image
        if (car.getImagePath() != null) {
            File image = new File(car.getImagePath());
            if (image.isFile() && image.canRead()) {
                Glide.with(viewHolder.view.getContext()).load(car.getImagePath()).into(viewHolder.imageView);
            } else {
                Glide.with(viewHolder.view.getContext()).load(R.drawable.unknown_carbox).into(viewHolder.imageView);
            }
        } else {
            Glide.with(viewHolder.view.getContext()).load(R.drawable.unknown_carbox).into(viewHolder.imageView);
        }
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.imageView.setImageDrawable(null);
        holder.trackName.setText(null);
        holder.trackDescription.setText(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
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
