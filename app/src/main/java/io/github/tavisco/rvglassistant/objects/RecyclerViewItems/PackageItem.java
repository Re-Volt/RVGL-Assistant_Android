package io.github.tavisco.rvglassistant.objects.RecyclerViewItems;

import android.support.annotation.NonNull;
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
import io.github.tavisco.rvglassistant.objects.Constants;

/**
 * Created by Tavisco on 25/05/18.
 */
public class PackageItem extends AbstractItem<PackageItem, PackageItem.ViewHolder> {

    String name;
    String localVersion;
    String lastVersion;

    public PackageItem(String name, String localVersion, String lastVersion) {
        this.name = name;
        this.localVersion = localVersion;
        this.lastVersion = lastVersion;
    }

    public String getName() {
        return name;
    }

    public String getLocalVersion() {
        return localVersion;
    }

    public String getLastVersion() {
        return lastVersion;
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.rvio_package_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.package_item;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(@NonNull PackageItem.ViewHolder viewHolder, @NonNull List<Object> payloads) {
        super.bindView(viewHolder, payloads);
        viewHolder.tvPackageTitle.setText(getName());
        viewHolder.tvPackageLastVersion.setText("Last: " + getLastVersion());
        viewHolder.tvPackageLocalVersion.setText("Local: " + getLocalVersion());
    }

    @Override
    public void unbindView(@NonNull ViewHolder holder) {
        super.unbindView(holder);
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
        @BindView(R.id.img_package_updateStatus)
        ImageView imgUpdateStatus;
        @BindView(R.id.tv_package_title)
        TextView tvPackageTitle;
        @BindView(R.id.tv_package_local_version)
        TextView tvPackageLocalVersion;
        @BindView(R.id.tv_package_last_version)
        TextView tvPackageLastVersion;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = (FrameLayout) view;


        }
    }
}
