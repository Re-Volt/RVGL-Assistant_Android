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
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

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

public class InstallActivity extends AppCompatActivity {

    private static final int ASSET_TYPE_UNKNOWN = -1;
    private static final int ASSET_TYPE_CAR = 0;
    private static final int ASSET_TYPE_LEVEL = 1;

    Intent pIntent;

    @BindView(R.id.imgInstall)
    ImageView imgInstall;
    @BindView(R.id.tvInstallType)
    TextView tvType;

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

        UnzipFile unzip = new UnzipFile(InstallActivity.this);

        unzip.execute(pIntent);
    }

    private class UnzipFile extends AsyncTask<Intent, String, String> {

        private MaterialDialog dialog;
        private Context mContext;
        String assistFolder = Environment.getExternalStorageDirectory().toString() + File.separator + "RVGLAssist";

        public UnzipFile(Context context){
            mContext = context;
        }


        @Override
        protected String doInBackground(Intent... mIntent) {
            Intent intent = mIntent[0];
            String action = intent.getAction();

            if (action.compareTo(Intent.ACTION_VIEW) == 0) {
                String scheme = intent.getScheme();
                ContentResolver resolver = getContentResolver();

                if (scheme.compareTo(ContentResolver.SCHEME_CONTENT) == 0) {
                    Uri uri = intent.getData();
                    String name = getContentName(resolver, uri);
                    String unzipFile =  assistFolder + File.separator + name;

                    InputStream input = null;
                    try {
                        File folder = new File(assistFolder);
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

                            File curfile = new File(assistFolder + File.separator + "unzipped" + File.separator, fileName);
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

            int type = detectAssetType();

            dialog.setContent("Filling screen");



            dialog.dismiss();

            //Manipular View aqui



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
            File directory = new File(assistFolder + File.separator + "unzipped");

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
}
