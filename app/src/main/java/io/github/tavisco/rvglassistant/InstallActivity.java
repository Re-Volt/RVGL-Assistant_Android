package io.github.tavisco.rvglassistant;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.tavisco.rvglassistant.objects.ItemType;
import io.github.tavisco.rvglassistant.objects.BaseItem;
import io.github.tavisco.rvglassistant.objects.Constants;
import io.github.tavisco.rvglassistant.utils.ItemParser;
import io.github.tavisco.rvglassistant.utils.ItemTypeDeterminer;

public class InstallActivity extends AppCompatActivity {

    private Intent pIntent;

    @BindView(R.id.imgInstall)
    ImageView imgInstall;
    @BindView(R.id.tvInstallType)
    TextView tvType;
    @BindView(R.id.tvInstallName)
    TextView tvName;
    @BindView(R.id.card_install_image)
    CardView cardInstall;

    boolean alreadyCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install);

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pIntent = getIntent();

        ButterKnife.bind(this);
    }

    @Override
    public void onResume(){
        super.onResume();

        cardInstall.setMinimumHeight(cardInstall.getWidth());

        //optimization to the imageView
        int screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        int finalHeight = (int) (screenWidth / 1.5) / 2;
        imgInstall.setMinimumHeight(finalHeight);
        imgInstall.setMaxHeight(finalHeight);
        imgInstall.setAdjustViewBounds(false);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imgInstall.getLayoutParams();
        lp.height = finalHeight;
        imgInstall.setLayoutParams(lp);

        //Instantiate a new AsyncUnzipFile task
        AsyncUnzipFile unzip = new AsyncUnzipFile(InstallActivity.this);

        if (!alreadyCreated){
            alreadyCreated = true;
            //The "false" here is to tell the task to not install
            //the files to the game, just to unzip them
            unzip.execute(false);
        }
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
        protected void onPreExecute() {
            dialog = new MaterialDialog.Builder(mContext)
                    .title("Unzipping file")
                    .content("Start unzipping")
                    .progress(true, 0)
                    .show();
        }

        @Override
        protected String doInBackground(Boolean... booleans) {
            install = booleans[0];

            if (install){
                destinationFolder = Constants.RVGL_PATH;
            } else {
                destinationFolder = Constants.RVGL_ASSIST_UNZIP_PATH;
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
                            //We need to clean up files from
                            //previous installations
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

                    boolean isZip = name.matches(".*\\.zip");
                    boolean is7Z = name.matches(".*\\.7z") || name.matches(".*\\.7Z");

                    if (is7Z){
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

                                File curfile = new File(destinationFolder
                                        + File.separator, fileName);
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

                    if (isZip) {
                        try {
                            ZipInputStream zipStream = new ZipInputStream(input);
                            ZipEntry zEntry = null;
                            while ((zEntry = zipStream.getNextEntry()) != null) {
                                publishProgress(zEntry.getName());
                                if (zEntry.isDirectory()) {
                                    hanldeDirectory(zEntry.getName());
                                } else {
                                    FileOutputStream fout = new FileOutputStream(
                                            this.destinationFolder + "/" + zEntry.getName());
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
                        } catch (Exception e) {
                            Log.d("Unzip", "Unzipping failed");
                            e.printStackTrace();
                        }
                    }
                }

            }

            //TODO: Make return type a real thing
            return "ok";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            dialog.setContent(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.setTitle("Processing file");
            dialog.setContent("Detecting asset type");

            ItemType assetType = ItemTypeDeterminer.determine(destinationFolder);

            if (install){
                dialog.dismiss();

                new MaterialDialog.Builder(mContext)
                        .title("Success!")
                        .content("The " + assetType.getTypeText() + " was installed successfully! Enjoy!")
                        .positiveText("Ok")
                        .show();
            } else {
                dialog.setContent("Filling screen");

                //if (assetType == Constants.ITEM_TYPE_LEVEL){
                File directory = new File(destinationFolder + File.separator + assetType.getTypePath());
                File[] files = directory.listFiles();

                String levelFolderName = "";

                for (File file : files) {
                    if (file.isDirectory()) {
                        levelFolderName = file.getName();
                    }
                }

                /*BaseItem item = ItemParser.parse(levelFolderName, destinationFolder);
                    //LevelViewItem track = FindLevels.populateItem(levelFolderName, true);

                if (item.getImagePath() != null)
                    Glide.with(InstallActivity.this).load(item.getImagePath()).into(imgInstall);

                tvType.setText("Type: " + item.getType().getTypeText());
                tvName.setText("Name: " + item.getName());*/

                dialog.dismiss();
            }
        }

        public void hanldeDirectory(String dir) {
            File f = new File(this.destinationFolder + File.separator + dir);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
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
            Log.e(">>>", "InputStreamToFile exception: " + e.getMessage());
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
