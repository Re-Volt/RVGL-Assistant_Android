package io.github.tavisco.rvglassistant.objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

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
import io.github.tavisco.rvglassistant.objects.adapters.IOPackageViewItem.ViewHolder;
import io.github.tavisco.rvglassistant.objects.enums.UpdateStatus;
import io.github.tavisco.rvglassistant.others.Constants;

public class IOPackageItem {
    private final String name;
    private String localVersion;
    private String remoteVersion;
    private int downloadID;
    private boolean downloadOngoing;
    private boolean remoteVersionChecked = false;
    private UpdateStatus updateStatus;
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
        if (getLocalVersion().equals(ERROR_STRING)){
            updateStatus = UpdateStatus.NOT_INSTALLED;
            return;
        }

        if (getLocalVersion().equals(getRemoteVersion())){
            updateStatus = UpdateStatus.UPDATED;
        } else {
            updateStatus = UpdateStatus.UPDATE_AVAIABLE;
        }
    }

    public void installPackage(ViewHolder vh){
        File zipFile = new File(this.getDownloadSavePath());
        AsyncUnzipFile asyncUnzipFile = new AsyncUnzipFile(zipFile,vh, getName(), getRemoteVersion());

        boolean installedWithSuccess = false;

        if (zipFile.isFile() && zipFile.canRead()) {
            installedWithSuccess = asyncUnzipFile.doInBackground();
            zipFile.delete();
        }

        if (installedWithSuccess){
//            String successMessage = "%s was installed with success! Enjoy!";
//            new MaterialDialog.Builder(vh.getContext())
//                    .title("Success!")
//                    .content(String.format(successMessage, this.getName()))
//                    .positiveText(R.string.dialog_positive_text)
//                    .show();
        } else {
//            String errorMessage = "An error ocurred while installing %s";
//            new MaterialDialog.Builder(vh.view.getContext())
//                    .title("Er... An error ocurred!")
//                    .content(String.format(errorMessage, this.getName()))
//                    .positiveText(R.string.dialog_positive_text)
//                    .show();
        }

        //this.setDownloadOngoing(false,vh);
    }

    public Drawable getImgDrawable(Context ctx) {
        if (updateStatus == UpdateStatus.UPDATED){
            return ctx.getDrawable(R.drawable.ic_cloud_check);
        } else if (updateStatus == UpdateStatus.UPDATE_AVAIABLE){
            return ctx.getDrawable(R.drawable.ic_cloud_sync);
        } else if (updateStatus == UpdateStatus.NOT_INSTALLED){
            return ctx.getDrawable(R.drawable.ic_cloud_download);
        }

        return ctx.getDrawable(R.drawable.ic_cloud_alert);
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

        private void hanldeDirectory(String dir) {
            File f = new File(Constants.PATH_RVGL + File.separator + dir);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
        }
    }

}
