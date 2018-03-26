package io.github.tavisco.rvglassistant;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InstallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install);

        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.compareTo(Intent.ACTION_VIEW) == 0) {
            String scheme = intent.getScheme();
            ContentResolver resolver = getContentResolver();

            if (scheme.compareTo(ContentResolver.SCHEME_CONTENT) == 0) {
                Uri uri = intent.getData();
                String name = getContentName(resolver, uri);
                String importfilepath = Environment.getExternalStorageDirectory().toString() + "/RVGLAssist/" + name;

                Log.v(">>>" , "Content intent detected: " + action + " : " + intent.getDataString() + " : " + intent.getType() + " : " + uri);
                InputStream input = null;
                try {
                    File pasta = new File(importfilepath);
                    pasta.mkdir();
                    input = resolver.openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                InputStreamToFile(input, importfilepath);

                File file = new File(importfilepath);

                SevenZFile sevenZFile = null;
                try {
                    sevenZFile = new SevenZFile(file);
                    SevenZArchiveEntry entry = sevenZFile.getNextEntry();
                    while(entry!=null){
                        System.out.println(entry.getName());
                        FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/RVGLAssist/" + name + "/" + entry.getName());
                        byte[] content = new byte[(int) entry.getSize()];
                        sevenZFile.read(content, 0, content.length);
                        out.write(content);
                        out.close();
                        entry = sevenZFile.getNextEntry();
                    }
                    sevenZFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

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
