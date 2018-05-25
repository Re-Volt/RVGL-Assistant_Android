package io.github.tavisco.rvglassistant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;

import io.github.tavisco.rvglassistant.objects.Constants;
import io.github.tavisco.rvglassistant.objects.LevelItem;
import io.github.tavisco.rvglassistant.objects.RecyclerViewItems.LevelViewItem;

public class TrackInfoActivity extends AppCompatActivity {

    LevelViewItem levelView = null;
    LevelItem level = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String jsonTrack = intent.getStringExtra("levelViewItem");
        levelView = new Gson().fromJson(jsonTrack, LevelViewItem.class);
        level = levelView.getLevel();

        getSupportActionBar().setTitle(level.getName());

        final ImageView imgBackdrop = findViewById(R.id.backdrop);

        //Load image
        if (level.getImagePath() != null) {
            File image = new File(level.getImagePath());
            if (image.isFile() && image.canRead()) {
                Glide.with(this).load(level.getImagePath()).into(imgBackdrop);
            } else {
                Glide.with(this).load(Constants.LEVEL_PLACEHOLDER_IMAGE).into(imgBackdrop);
            }
        }
    }
}
