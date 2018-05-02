package io.github.tavisco.rvglassistant;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.Bar3d;
import com.anychart.anychart.Cartesian;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.ValueDataEntry;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.tavisco.rvglassistant.objects.CarItem;
import io.github.tavisco.rvglassistant.objects.Constants;
import io.github.tavisco.rvglassistant.objects.LevelItem;
import io.github.tavisco.rvglassistant.objects.RecyclerViewItems.CarViewItem;
import io.github.tavisco.rvglassistant.objects.RecyclerViewItems.LevelViewItem;

public class CarInfoActivity extends AppCompatActivity {
    CarViewItem carView = null;
    CarItem car = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);
        Toolbar toolbar = findViewById(R.id.car_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String jsonTrack = intent.getStringExtra("carViewItem");
        carView = new Gson().fromJson(jsonTrack, CarViewItem.class);
        car = carView.getCar();

        getSupportActionBar().setTitle(car.getName());

        final ImageView imgBackdrop = findViewById(R.id.car_backdrop);

        //Load image
        if (car.getImagePath() != null) {
            File image = new File(car.getImagePath());
            if (image.isFile() && image.canRead()) {
                Glide.with(this).load(car.getImagePath()).into(imgBackdrop);
            } else {
                Glide.with(this).load(R.drawable.unknown_carbox).into(imgBackdrop);
            }
        }

        Cartesian bar = AnyChart.bar();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Speed", 10000));
        data.add(new ValueDataEntry("Acc", 12000));
        data.add(new ValueDataEntry("Weight", 18000));

        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.car_stats_chart);
        anyChartView.setChart(bar);

        //https://github.com/AnyChart/AnyChart-Android

    }
}
