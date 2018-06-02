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

        String path = Constants.PATH_RVGL + File.separator + "cars";
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (!directory.isDirectory() || !directory.canRead() || files.length == 0) {
            //TODO: Error while listing cars
            //The app have storage permissions?
        }

        for (File carFile : files) {
            //TODO: Make this runs on another thread
            //carFile = "/storage/emulated/0/RVGL/cars/trolley"
            //basePath = "/storage/emulated/0/RVGL"

            CarItem carBase = (CarItem) ItemParser.parse(carFile, Constants.PATH_RVGL, ItemType.CAR);
            if (carBase != null){
                CarViewItem carView = new CarViewItem(carBase);
                itemAdapter.add(carView);
            }
        }
    }
}