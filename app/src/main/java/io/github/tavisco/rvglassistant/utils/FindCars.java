package io.github.tavisco.rvglassistant.utils;

import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.io.File;

import io.github.tavisco.rvglassistant.objects.CarItem;
import io.github.tavisco.rvglassistant.objects.Constants;
import io.github.tavisco.rvglassistant.objects.ItemType;
import io.github.tavisco.rvglassistant.objects.RecyclerViewItems.CarViewItem;

/**
 * Created by otavio.mpinheiro on 15/03/2018.
 */

public class FindCars {

    public static void getAllCars(ItemAdapter<CarViewItem> itemAdapter) {

        String path = Constants.RVGL_PATH + File.separator + "cars";
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (!directory.isDirectory() || !directory.canRead() || files.length == 0) {
            //TODO: Error while listing cars
            //The app have storage permissions?
        }

        for (File file : files) {
            //TODO: Make this runs on another thread
            CarItem carBase = (CarItem) ItemParser.parse(file.getName(), Constants.RVGL_PATH, ItemType.CAR.getTypePath());
            if (carBase != null){
                CarViewItem carView = new CarViewItem(carBase);
                itemAdapter.add(carView);
            }
        }
    }
}