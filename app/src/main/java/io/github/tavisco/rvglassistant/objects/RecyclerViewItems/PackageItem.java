package io.github.tavisco.rvglassistant.objects.RecyclerViewItems;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.objects.Constants;
import io.github.tavisco.rvglassistant.utils.DownloadUtils;

/**
 * Created by Tavisco on 25/05/18.
 */
public class PackageItem extends AbstractItem<PackageItem, PackageItem.ViewHolder> {

    private String name;
    private String localVersion;
    private String lastVersion;
    private int downloadID;
    private boolean downloadOngoing;
    private boolean versionChecked = false;

    public PackageItem(String name) {
        this.name = name;
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

    public void setLocalVersion(String localVersion) {
        this.localVersion = localVersion;
    }

    public void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }

    public String getDownloadLink(){
        return Constants.RVIO_DOWNLOAD_PACKS_LINK.concat(getName()).concat(".zip");
    }

    public String getDownloadSavePath(){
        return Constants.PATH_RVGL_BUTLER + File.separator + getName().concat(".zip");
    }

    public int getDownloadID() {
        return downloadID;
    }

    public void setDownloadID(int downloadID) {
        this.downloadID = downloadID;
    }

    public boolean isDownloadOngoing() {
        return downloadOngoing;
    }

    public void setDownloadOngoing(boolean downloadOngoing, @NonNull final PackageItem.ViewHolder viewHolder) {
        this.downloadOngoing = downloadOngoing;
        if (downloadOngoing){
            viewHolder.lytPackageDownload.setVisibility(View.VISIBLE);
        } else {
            viewHolder.lytPackageDownload.setVisibility(View.GONE);
        }
    }

    public void updateDownloadView(Context ctx, @NonNull final PackageItem.ViewHolder vh, long etaInMilliSeconds, long downloadedBytesPerSecond, int progress, String status){
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
        installPackage(vh);
    }

    private void installPackage(ViewHolder vh){
        File zipFile = new File(this.getDownloadSavePath());
        AsyncUnzipFile asyncUnzipFile = new AsyncUnzipFile(zipFile,vh, getName(), getLastVersion());

        boolean installedWithSuccess = false;

        if (zipFile.isFile() && zipFile.canRead()) {
            installedWithSuccess = asyncUnzipFile.doInBackground();
            zipFile.delete();
        }

        if (installedWithSuccess){
            String successMessage = "%s was installed with success! Enjoy!";
            new MaterialDialog.Builder(vh.view.getContext())
                    .title("Success!")
                    .content(String.format(successMessage, this.getName()))
                    .positiveText(R.string.dialog_positive_text)
                    .show();
        } else {
            String errorMessage = "An error ocurred while installing %s";
            new MaterialDialog.Builder(vh.view.getContext())
                    .title("Er... An error ocurred!")
                    .content(String.format(errorMessage, this.getName()))
                    .positiveText(R.string.dialog_positive_text)
                    .show();
        }

        this.setDownloadOngoing(false,vh);
    }



    private static class AsyncUnzipFile extends AsyncTask<Void, String, Boolean> {
        private final File ZIP_FILE;
        private final ViewHolder VH;
        private boolean success = false;
        private final String PACKAGE_NAME;
        private final String PACKGE_VERSION;

        public AsyncUnzipFile(File ZIP_FILE, ViewHolder VH, String PACKAGE_NAME, String PACKGE_VERSION) {
            this.ZIP_FILE = ZIP_FILE;
            this.VH = VH;
            this.PACKAGE_NAME = PACKAGE_NAME;
            this.PACKGE_VERSION = PACKGE_VERSION;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                ZipInputStream zipStream = new ZipInputStream(new FileInputStream(ZIP_FILE));
                ZipEntry zEntry;
                while ((zEntry = zipStream.getNextEntry()) != null) {
                    //publishProgress(zEntry.getName());
                    VH.tvTimeRemaining.setText(zEntry.getName());
                    if (zEntry.isDirectory()) {
                        hanldeDirectory(zEntry.getName());
                    } else {
                        File directory = new File(
                                Constants.PATH_RVGL + File.separator + zEntry.getName());
                        directory = new File(directory.getParent());
                        if (!directory.exists()){
                            directory.mkdirs();
                        }
                        FileOutputStream fout = new FileOutputStream(
                                Constants.PATH_RVGL + File.separator + zEntry.getName());
                        BufferedOutputStream bufout = new BufferedOutputStream(fout);
                        byte[] buffer = new byte[1024];
                        int read = 0;
                        while ((read = zipStream.read(buffer)) != -1) {
                            bufout.write(buffer, 0, read);
                        }

                        zipStream.closeEntry();
                        bufout.flush();
                        bufout.close();
                        fout.close();
                    }
                }
                zipStream.close();

                hanldeDirectory(Constants.VERSIONS_FOLDER_NAME);

                PrintWriter writer = new PrintWriter(
                        Constants.PATH_RVGL.concat(File.separator).concat(
                                Constants.VERSIONS_FOLDER_NAME).concat(File.separator).concat(
                                        PACKAGE_NAME).concat(".txt"), "UTF-8");
                writer.print(PACKGE_VERSION);
                writer.close();

                success = true;
            } catch (Exception e) {
                Log.e(Constants.TAG.concat(" Unzip: "), e.getMessage());
                VH.tvDownloadStatus.setText("Error");
            }
            return success;
        }

        private void hanldeDirectory(String dir) {
            File f = new File(Constants.PATH_RVGL + File.separator + dir);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
        }
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
    public void bindView(@NonNull final PackageItem.ViewHolder viewHolder, @NonNull List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        if (!versionChecked){
            viewHolder.tvPackageTitle.setText(getName());
            viewHolder.tvPackageLocalVersion.setText("Local: Not installed");
            viewHolder.tvPackageLastVersion.setText("Last: Checking");
            final Context ctx = viewHolder.itemView.getContext();

            // Ex. https://distribute.re-volt.io/assets/io_cars.txt
            String rvioRequest = Constants.RVIO_ASSETS_LINK + getName() + ".txt";

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(ctx);

            // Request a string response from the rvioRequest URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, rvioRequest,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (!response.isEmpty()){
                                setLastVersion(response.substring(0, 7));
                                viewHolder.tvPackageLastVersion.setText(String.format("Last: %s", getLastVersion()));
                                versionChecked = true;
                                compareVersions(viewHolder);
                            }
                        }

                        private void compareVersions(ViewHolder viewHolder) {
                            File localVersionFile = new File(Constants.PATH_RVGL.concat(File.separator).concat(Constants.VERSIONS_FOLDER_NAME).concat(File.separator).concat(getName()).concat(".txt"));
                            if (localVersionFile.isFile() && localVersionFile.canRead()) {
                                setLocalVersion(readLocalVersion(localVersionFile));
                                viewHolder.tvPackageLocalVersion.setText(ctx.getString(R.string.package_local, getLocalVersion()));

                                if (getLocalVersion().equals(getLastVersion())){
                                    viewHolder.imgUpdateStatus.setImageDrawable(ctx.getDrawable(R.drawable.ic_cloud_check));
                                    viewHolder.cardView.setCardBackgroundColor(ctx.getResources().getColor(R.color.updatedGreen));
                                }
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

    private String readLocalVersion(File localVersionFile) {
        try (Scanner sc = new Scanner(localVersionFile)) {
            return sc.next();
        } catch (FileNotFoundException e) {
            return "Error";
        }
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
            this.view = (FrameLayout) view;

        }
    }
}
