package io.github.tavisco.rvglassistant.objects.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.others.Constants;
import io.github.tavisco.rvglassistant.objects.IOPackageItem;
import io.github.tavisco.rvglassistant.utils.DownloadUtils;

/**
 * Created by Tavisco on 25/05/18.
 */
public class IOPackageViewItem extends AbstractItem<IOPackageViewItem, IOPackageViewItem.ViewHolder> {

    IOPackageItem packageItem;
    private boolean downloadOngoing;
    private boolean versionChecked = false;

    public IOPackageViewItem(String name) {
        packageItem = new IOPackageItem(name);

    }



    public boolean isDownloadOngoing() {
        return downloadOngoing;
    }

    public void setDownloadOngoing(boolean downloadOngoing, @NonNull final IOPackageViewItem.ViewHolder viewHolder) {
        this.downloadOngoing = downloadOngoing;
        if (downloadOngoing){
            viewHolder.lytPackageDownload.setVisibility(View.VISIBLE);
        } else {
            viewHolder.lytPackageDownload.setVisibility(View.GONE);
        }
    }

    public void updateDownloadView(Context ctx, @NonNull final IOPackageViewItem.ViewHolder vh, long etaInMilliSeconds, long downloadedBytesPerSecond, int progress, String status){
        vh.tvDownloadSpeed.setText(DownloadUtils.getDownloadSpeedString(ctx, downloadedBytesPerSecond));
        vh.tvTimeRemaining.setText(DownloadUtils.getETAString(ctx, etaInMilliSeconds));
        vh.barDownloadProgress.setProgress(progress);
        vh.tvDownloadProgress.setText(String.valueOf(progress).concat("%"));
        vh.tvDownloadStatus.setText(status);
    }

    public void downloadCompleted(ViewHolder vh) {
        vh.tvDownloadSpeed.setVisibility(View.INVISIBLE);
        vh.tvTimeRemaining.setText("Preparing...");
        vh.barDownloadProgress.setVisibility(View.INVISIBLE);
        vh.tvDownloadProgress.setVisibility(View.INVISIBLE);
        vh.tvDownloadStatus.setText("Unzipping...");
        packageItem.installPackage(vh);
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
        @BindView(R.id.lnLyt_package_download)
            LinearLayout lytPackageDownload;
        @BindView(R.id.tv_package_download_speed)
            TextView tvDownloadSpeed;
        @BindView(R.id.tv_package_download_status)
            TextView tvDownloadStatus;
        @BindView(R.id.tv_package_download_progress)
            TextView tvDownloadProgress;
        @BindView(R.id.tv_package_time_remaining)
            TextView tvTimeRemaining;
        @BindView(R.id.bar_package_progress)
            ProgressBar barDownloadProgress;
        @BindView(R.id.card_package)
            CardView cardView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            //this.view = (FrameLayout) view;

        }

        @Override
        public void bindView(final IOPackageViewItem viewItem, final List<Object> payloads) {
            final IOPackageItem item = viewItem.packageItem;
            final Context ctx = itemView.getContext();

            tvPackageTitle.setText(item.getName());
            tvPackageLocalVersion.setText(item.getLocalVersion());
            tvPackageLastVersion.setText(item.getRemoteVersion());
            imgUpdateStatus.setImageDrawable(item.getImgDrawable(ctx));

            if (item.getLocalVersion().equals(item.getRemoteVersion())){
                imgUpdateStatus.setImageDrawable(ctx.getDrawable(R.drawable.ic_cloud_check));
                cardView.setCardBackgroundColor(ctx.getResources().getColor(R.color.updatedGreen));
            }

            if (!item.isRemoteVersionChecked()){
                // Ex. https://distribute.re-volt.io/assets/io_cars.txt
                String rvioRequest = Constants.RVIO_ASSETS_LINK + item.getName() + ".txt";

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(ctx);

                // Request a string response from the rvioRequest URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, rvioRequest,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (!response.isEmpty()){
                                    item.setRemoteVersion(response.substring(0, 7));
                                    item.setRemoteVersionChecked(true);
                                    //tvPackageLastVersion.setText(String.format("Last: %s", item.getRemoteVersion()));
                                    bindView(viewItem,payloads);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(Constants.TAG, error.getLocalizedMessage());
                    }
                });

                // Add the request to the RequestQueue.
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
