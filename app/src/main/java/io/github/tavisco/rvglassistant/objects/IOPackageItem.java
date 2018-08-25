package io.github.tavisco.rvglassistant.objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tonyodev.fetch2.AbstractFetchListener;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.fragments.MainFragment;
import io.github.tavisco.rvglassistant.objects.enums.UpdateStatus;
import io.github.tavisco.rvglassistant.others.Constants;
import io.github.tavisco.rvglassistant.utils.DownloadUtils;

public class IOPackageItem {
    private final String name;
    private String localVersion;
    private String remoteVersion;
    private int downloadID;
    private boolean downloadOngoing;
    private boolean remoteVersionChecked = false;
    private UpdateStatus updateStatus;
    private Fetch mainFetch;

    private final String ERROR_STRING = "Error";



    public IOPackageItem(String name) {
        this.name = name;
        determineLocalVersion();
    }

    private void determineLocalVersion() {
        File localVersionFile = new File(Constants.PATH_RVGL.concat(File.separator).concat(Constants.VERSIONS_FOLDER_NAME).concat(File.separator).concat(getName()).concat(".txt"));
        if (localVersionFile.isFile() && localVersionFile.canRead()) {
            setLocalVersion(readLocalVersion(localVersionFile));
        } else {
            setLocalVersion(ERROR_STRING);
        }
    }

    private String readLocalVersion(File localVersionFile) {
        try (Scanner sc = new Scanner(localVersionFile)) {
            return sc.next();
        } catch (FileNotFoundException e) {
            return ERROR_STRING;
        }
    }

    public boolean isRemoteVersionChecked() {
        return remoteVersionChecked;
    }

    public String getName() {
        return name;
    }

    public String getLocalVersion() {
        if (localVersion.equals(ERROR_STRING)){
            return "---";
        }
        return localVersion;
    }

    public String getRemoteVersion() {
        if (isRemoteVersionChecked()){
            return remoteVersion;
        } else {
            return "Checking...";
        }
    }

    public void setLocalVersion(String localVersion) {
        this.localVersion = localVersion;
    }

    public void setRemoteVersion(String remoteVersion) {
        this.remoteVersion = remoteVersion;
    }

    public String getDownloadLink(){
        return Constants.RVIO_DOWNLOAD_PACKS_LINK.concat(getName()).concat(".zip");
    }

    private String getDownloadSavePath(){
        return Constants.PATH_RVGL_BUTLER + File.separator + getName().concat(".zip");
    }

    public int getDownloadID() {
        return downloadID;
    }

    public void setDownloadID(int downloadID) {
        this.downloadID = downloadID;
    }

    public void setRemoteVersionChecked(boolean remoteVersionChecked) {
        this.remoteVersionChecked = remoteVersionChecked;

        if (remoteVersionChecked){
            determinePackageUpdateStatus();
        }

    }

    private void determinePackageUpdateStatus() {
        if (localVersion.equals(ERROR_STRING)){
            updateStatus = UpdateStatus.NOT_INSTALLED;
            return;
        }

        if (getLocalVersion().equals(getRemoteVersion())){
            updateStatus = UpdateStatus.UPDATED;
        } else {
            updateStatus = UpdateStatus.UPDATE_AVAIABLE;
        }
    }

    public UpdateStatus getUpdateStatus() {
        return updateStatus;
    }

    public Drawable getImgDrawable(Context ctx) {
        if (updateStatus == UpdateStatus.UPDATED){
            return ctx.getDrawable(R.drawable.ic_cloud_check);
        } else if (updateStatus == UpdateStatus.UPDATE_AVAIABLE){
            return ctx.getDrawable(R.drawable.ic_cloud_alert);
        } else if (updateStatus == UpdateStatus.NOT_INSTALLED){
            return ctx.getDrawable(R.drawable.ic_cloud_download);
        }

        return ctx.getDrawable(R.drawable.ic_cloud);
    }

    public void install(MainFragment frag) {

        Context ctx = frag.getContext();

        if (ctx != null){
            new MaterialDialog.Builder(ctx)
                    .title("Download ".concat(getName()).concat("?"))
                    .content("Do you wish to download ".concat(getName()).concat(" pack?"))
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .onPositive((dialog, which) -> downloadPackage(frag))
                    .show();
        }


    }

    private void downloadPackage(MainFragment frag) {
        Context context = frag.getContext();

        if (context != null) {

            MaterialDialog dialog = new MaterialDialog.Builder(context)
                    .title("Downloading " + getName())
                    .content("Starting...")
                    .progress(false, 100, false)
                    .cancelable(false)
                    .positiveText("Cancel")
                    .onPositive((dialog1, which) -> {
                        mainFetch.cancel(getDownloadID());
                        Log.d(Constants.TAG, "downloadPackage: CANCELED");
                    })
                    .show();


            FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
                    .setDownloadConcurrentLimit(3)
                    .build();

            mainFetch = Fetch.Impl.getInstance(fetchConfiguration);

            final Request request = new Request(getDownloadLink(), getDownloadSavePath());
            request.setPriority(Priority.HIGH);
            request.setNetworkType(NetworkType.WIFI_ONLY);

            mainFetch.removeAll();

            mainFetch.enqueue(request, updatedRequest -> {
                //Request was successfully enqueued for download.
                Log.d(Constants.TAG, "call: Started downloading");
                setDownloadID(request.getId());
            }, error -> {
                //An error occurred enqueuing the request.
                Log.d(Constants.TAG, "ERRO STARTING DOWNLOAD.");
            });

            final FetchListener fetchListener = new AbstractFetchListener() {

                @Override
                public void onCompleted(@NotNull Download download) {
                    mainFetch.removeListener(this);
                    mainFetch.close();
                    dialog.dismiss();

                    File zipFile = new File(getDownloadSavePath());
                    AsyncUnzipFile asyncUnzipFile = new AsyncUnzipFile(zipFile, frag, getName(), getRemoteVersion());

                    asyncUnzipFile.execute();
                }

                @Override
                public void onError(@NotNull Download download) {
                    final Error error = download.getError();
                    final Throwable throwable = error.getThrowable(); //can be null
                    if (error == Error.UNKNOWN && throwable != null) {
                        Log.d(Constants.TAG, "Throwable error", throwable);
                    }
                    mainFetch.removeListener(this);
                    mainFetch.close();
                    File zipFile = new File(getDownloadSavePath());
                    zipFile.delete();
                    //item.setDownloadOngoing(false, item.getViewHolder(v));
                }

                @Override
                public void onProgress(@NotNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
                    if (request.getId() == download.getId()) {
                        dialog.setProgress(download.getProgress());
                        dialog.setContent(String.format("Downloading at %s\nRemaining: %s",
                                DownloadUtils.getDownloadSpeedString(context, downloadedBytesPerSecond),
                                DownloadUtils.getETAString(context, etaInMilliSeconds)));
                        //item.updateDownloadView(v.getContext(), item.getViewHolder(v), etaInMilliSeconds, downloadedBytesPerSecond, download.getProgress(), download.getStatus().toString());
                    }
                }


                @Override
                public void onCancelled(@NotNull Download download) {
                    Log.d(Constants.TAG, "onCancelled: DOWNLOAD CANCELADO");
                    File zipFile = new File(getDownloadSavePath());
                    zipFile.delete();
                    //item.setDownloadOngoing(false, item.getViewHolder(v));
                }
            };

            mainFetch.addListener(fetchListener);
        }
    }

    private static class AsyncUnzipFile extends AsyncTask<Void, String, Boolean> {
        private final File ZIP_FILE;
        private Context CTX;
        private boolean success = false;
        private final String PACKAGE_NAME;
        private final String PACKGE_VERSION;
        private MaterialDialog asyncDialog;
        private final MainFragment FRAG;

        public AsyncUnzipFile(File ZIP_FILE, MainFragment frag, String PACKAGE_NAME, String PACKGE_VERSION) {
            this.ZIP_FILE = ZIP_FILE;
            Context context = frag.getContext();
            if (context != null) {
                this.CTX = frag.getContext();
            }
            this.PACKAGE_NAME = PACKAGE_NAME;
            this.PACKGE_VERSION = PACKGE_VERSION;
            this.FRAG = frag;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            asyncDialog = new MaterialDialog.Builder(CTX)
                    .title("Unzipping files")
                    .content("Starting...")
                    .cancelable(false)
                    .progress(true, 0)
                    .show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                ZipInputStream zipStream = new ZipInputStream(new FileInputStream(ZIP_FILE));
                ZipEntry zEntry;
                while ((zEntry = zipStream.getNextEntry()) != null) {
                    publishProgress(zEntry.getName());
                    //VH.tvTimeRemaining.setText(zEntry.getName());
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
                //VH.tvDownloadStatus.setText("Error");
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (asyncDialog != null){
                asyncDialog.setContent(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            asyncDialog.dismiss();

            ZIP_FILE.delete();

            String message;
            String title;
            if (success){
                title = "Success!";
                message = String.format("%s was installed with success! Enjoy!", PACKAGE_NAME);
                FRAG.populateRecycler();

            } else {
                title = "Er... An error ocurred!";
                message = String.format("An error ocurred while installing %s", PACKAGE_NAME);
            }

            new MaterialDialog.Builder(CTX)
                    .title(title)
                    .content(message)
                    .cancelable(false)
                    .positiveText("Ok")
                    .show();
        }

        private void hanldeDirectory(String dir) {
            File f = new File(Constants.PATH_RVGL + File.separator + dir);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
        }
    }

}
