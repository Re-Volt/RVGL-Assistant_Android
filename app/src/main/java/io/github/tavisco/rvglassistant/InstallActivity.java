package io.github.tavisco.rvglassistant;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.tavisco.rvglassistant.items.CarItem;
import io.github.tavisco.rvglassistant.items.TrackItem;
import io.github.tavisco.rvglassistant.utils.FindCars;
import io.github.tavisco.rvglassistant.utils.FindTracks;

public class InstallActivity extends AppCompatActivity {

    private static final int ASSET_TYPE_UNKNOWN = -1;
    private static final int ASSET_TYPE_CAR = 0;
    private static final int ASSET_TYPE_LEVEL = 1;

    private Intent pIntent;

    @BindView(R.id.imgInstall)
    ImageView imgInstall;
    @BindView(R.id.tvInstallType)
    TextView tvType;
    @BindView(R.id.tvInstallName)
    TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install);

        pIntent = getIntent();

        ButterKnife.bind(this);
    }

    @Override
    public void onResume(){
        super.onResume();

        //optimization to preset the correct height for our device
        int screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        int finalHeight = (int) (screenWidth / 1.5) / 2;
        imgInstall.setMinimumHeight(finalHeight);
        imgInstall.setMaxHeight(finalHeight);
        imgInstall.setAdjustViewBounds(false);
        //set height as layoutParameter too
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imgInstall.getLayoutParams();
        lp.height = finalHeight;
        imgInstall.setLayoutParams(lp);

        AsyncUnzipFile unzip = new AsyncUnzipFile(InstallActivity.this);

        unzip.execute(false);
    }

    private class AsyncUnzipFile extends AsyncTask<Boolean, String, String> {

        private MaterialDialog dialog;
        private Context mContext;
        String destinationFolder;
        boolean install;

        public AsyncUnzipFile(Context context){
            mContext = context;
        }

        @Override
        protected String doInBackground(Boolean... booleans) {
            install = booleans[0];

            if (install){
                destinationFolder = Environment.getExternalStorageDirectory().toString() + File.separator + "RVGL";
            } else {
                destinationFolder = Environment.getExternalStorageDirectory().toString() + File.separator + "RVGLAssist" + File.separator + "unzipped";
            }

            String action = pIntent.getAction();

            if (action.compareTo(Intent.ACTION_VIEW) == 0) {
                String scheme = pIntent.getScheme();
                ContentResolver resolver = getContentResolver();

                if (scheme.compareTo(ContentResolver.SCHEME_CONTENT) == 0) {
                    Uri uri = pIntent.getData();
                    String name = getContentName(resolver, uri);
                    String unzipFile =  destinationFolder + File.separator + name;

                    InputStream input = null;
                    try {
                        File folder = new File(destinationFolder);

                        if (!install){
                            if (folder.exists())
                                deleteRecursive(folder);
                        }

                        if (!folder.exists() && !folder.isDirectory())
                            folder.mkdir();

                        if (uri != null) {
                            input = resolver.openInputStream(uri);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    InputStreamToFile(input, unzipFile);

                    File file = new File(unzipFile);

                    try {
                        SevenZFile sevenZFile = new SevenZFile(file);
                        SevenZArchiveEntry entry;
                        while ((entry = sevenZFile.getNextEntry()) != null){
                            if (entry.isDirectory()){
                                continue;
                            }

                            String fileName = entry.getName();
                            publishProgress(fileName);

                            File curfile = new File(destinationFolder + File.separator, fileName);
                            File parent = curfile.getParentFile();
                            if (!parent.exists()) {
                                parent.mkdirs();
                            }
                            FileOutputStream out = new FileOutputStream(curfile);
                            byte[] content = new byte[(int) entry.getSize()];
                            sevenZFile.read(content, 0, content.length);
                            out.write(content);
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            return "ok";
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.setTitle("Processing file");
            dialog.setContent("Detecting asset type");

            int assetType = detectAssetType();

            if (install){
                dialog.dismiss();

                String item = assetType == ASSET_TYPE_CAR?"car":"level";

                new MaterialDialog.Builder(mContext)
                        .title("Success!")
                        .content("The " + item + " was installed successfully! Enjoy!")
                        .positiveText("Ok")
                        .show();
            } else {
                dialog.setContent("Filling screen");

                if (assetType == ASSET_TYPE_LEVEL){
                    File directory = new File(destinationFolder + File.separator + "levels");
                    File[] files = directory.listFiles();

                    String levelFolderName = "";

                    for (File file : files) {
                        if (file.isDirectory()) {
                            levelFolderName = file.getName();
                        }
                    }

                    TrackItem track = FindTracks.populateItem(levelFolderName, true);

                    if (track.getTrackImgPath() != null)
                        Glide.with(InstallActivity.this).load(track.getTrackImgPath()).into(imgInstall);

                    tvType.setText("Type: Level");
                    tvName.setText("Name: " + track.getTrackName());
                } else if (assetType == ASSET_TYPE_CAR){
                    File directory = new File(destinationFolder + File.separator + "cars");
                    File[] files = directory.listFiles();

                    String carFolderName = "";

                    for (File file : files) {
                        if (file.isDirectory()) {
                            carFolderName = file.getName();
                        }
                    }

                    CarItem car = FindCars.populateItem(carFolderName, true);

                    if (car.getCarImgPath() != null)
                        Glide.with(InstallActivity.this).load(car.getCarImgPath()).into(imgInstall);

                    tvType.setText("Type: Car");
                    tvName.setText("Name: " + car.getCarName());
                }

                dialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            dialog = new MaterialDialog.Builder(mContext)
                    .title("Unzipping file")
                    .content("Start unzipping")
                    .progress(true, 0)
                    .show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            dialog.setContent(values[0]);
        }

        private int detectAssetType() {
            File directory = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "RVGLAssist" + File.separator + "unzipped");

            File[] files = directory.listFiles();

            for (File file : files){
                if (file.isDirectory()){
                    String dirName = file.getName().toLowerCase();

                    if (dirName.equals("levels")){
                        return ASSET_TYPE_LEVEL;
                    } else if (dirName.equals("cars")){
                        return ASSET_TYPE_CAR;
                    }
                }
            }

            return ASSET_TYPE_UNKNOWN;
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    private void InputStreamToFile(InputStream in, String file) {
        try {

            OutputStream out = new FileOutputStream(new File(file));

            int size = 0;
            byte[] buffer = new byte[1024];

            while ((size = in.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }

            out.close();
        }
        catch (Exception e) {
            Log.e("MainActivity", "InputStreamToFile exception: " + e.getMessage());
        }
    }

    private String getContentName(ContentResolver resolver, Uri uri){
        Cursor cursor = resolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
        if (nameIndex >= 0) {
            return cursor.getString(nameIndex);
        } else {
            return null;
        }
    }

    @OnClick(R.id.btnInstall)
    public void installContent(){
        AsyncUnzipFile unzip = new AsyncUnzipFile(InstallActivity.this);

        unzip.execute(true);
    }

}
