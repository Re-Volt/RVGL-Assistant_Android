package io.github.tavisco.rvglassistant.objects.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.objects.IOPackageItem;
import io.github.tavisco.rvglassistant.objects.enums.UpdateStatus;
import io.github.tavisco.rvglassistant.others.Constants;

/**
 * Created by Tavisco on 25/05/18.
 */
public class IOPackageViewItem extends AbstractItem<IOPackageViewItem, IOPackageViewItem.ViewHolder> {

    IOPackageItem packageItem;

    public IOPackageViewItem(String name) {
        packageItem = new IOPackageItem(name);
    }

    public IOPackageItem getPackageItem() {
        return packageItem;
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

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    public static class ViewHolder extends FastAdapter.ViewHolder<IOPackageViewItem> {
        @BindView(R.id.img_package_updateStatus)
            ImageView imgUpdateStatus;
        @BindView(R.id.tv_package_title)
            TextView tvPackageTitle;
        @BindView(R.id.tv_package_local_version)
            TextView tvPackageLocalVersion;
        @BindView(R.id.tv_package_last_version)
            TextView tvPackageLastVersion;
        @BindView(R.id.card_package)
            CardView cardView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }

        @Override
        public void bindView(final IOPackageViewItem viewItem, final List<Object> payloads) {
            final IOPackageItem item = viewItem.packageItem;
            final Context ctx = itemView.getContext();

            tvPackageTitle.setText(item.getName());
            tvPackageLocalVersion.setText(String.format(ctx.getString(R.string.package_local_version), item.getLocalVersion()));
            tvPackageLastVersion.setText(String.format(ctx.getString(R.string.package_IO_version), item.getRemoteVersion()));
            imgUpdateStatus.setImageDrawable(item.getImgDrawable(ctx));

            //if (item.getLocalVersion().equals(item.getRemoteVersion())){
            if (item.getUpdateStatus() == UpdateStatus.UPDATED){
                //imgUpdateStatus.setImageDrawable(ctx.getDrawable(R.drawable.ic_cloud_check));
                cardView.setCardBackgroundColor(ctx.getResources().getColor(R.color.updatedGreen));
            } else if (item.getUpdateStatus() == UpdateStatus.UPDATE_AVAIABLE){
                cardView.setCardBackgroundColor(ctx.getResources().getColor(R.color.newVersionRed));
            }

            if (!item.isRemoteVersionChecked()){
                // Ex. https://distribute.re-volt.io/assets/io_cars.txt
                String rvioRequest = Constants.RVIO_ASSETS_LINK + item.getName() + ".txt";

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(ctx);

                // Request a string response from the rvioRequest URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, rvioRequest,
                        response -> {
                            if (!response.isEmpty()){
                                item.setRemoteVersion(response.substring(0, 7));
                                item.setRemoteVersionChecked(true);
                                bindView(viewItem,payloads);
                            }
                        },
                        error -> {
                            Log.d(Constants.TAG, String.format("Error while making request to %s", rvioRequest));
                            item.setRemoteVersion("???");
                            item.setRemoteVersionChecked(true);
                            bindView(viewItem,payloads);
                        }
                );

                // Add the request to the RequestQueue.
                stringRequest.setShouldCache(false);
                queue.add(stringRequest);
            }
        }

        @Override
        public void unbindView(IOPackageViewItem item) {
            final Context ctx = itemView.getContext();
            tvPackageTitle.setText("");
            tvPackageLocalVersion.setText("");
            tvPackageLastVersion.setText("");
            cardView.setCardBackgroundColor(ctx.getResources().getColor(R.color.cardview_dark_background));
            imgUpdateStatus.setImageDrawable(null);
        }
    }
}
