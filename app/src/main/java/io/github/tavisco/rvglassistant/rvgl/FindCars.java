package io.github.tavisco.rvglassistant.rvgl;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.tavisco.rvglassistant.items.CarItem;
import io.github.tavisco.rvglassistant.items.TrackItem;

/**
 * Created by otavio.mpinheiro on 15/03/2018.
 */

public class FindCars {

    public static List<CarItem> getAllCars() {

        List<String> dontShowTracks = new ArrayList<>();
        dontShowTracks.add("intro");
        dontShowTracks.add("frontend");
        dontShowTracks.add("stunts");

        List<CarItem> list = new ArrayList<>();

        String path = Environment.getExternalStorageDirectory().toString() + "/RVGL/cars";
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (!directory.isDirectory() || !directory.canRead() || files.length == 0) {
            return null;
        }

        boolean skip;
        for (File file : files) {
            skip = false;

            for (String dontShow : dontShowTracks) {
                if (dontShow.equals(file.getName()))
                    skip = true;
            }

            if (skip)
                continue;

            list.add(populateItem(file.getName()));
        }
        return list;
    }

    private static CarItem populateItem(String car) {
        CarItem item = new CarItem();

        String path = Environment.getExternalStorageDirectory().toString() + "/RVGL/cars/" + car;
        File directory = new File(path);

        if (!directory.isDirectory() || !directory.canRead()) {
            return null;
        }

        File infoFile = new File(path + "/parameters.txt");

        if (!infoFile.isFile() || !infoFile.canRead()) {
            Log.d(">>>", "Erro arquivo " + car);
            return null;
        }

        Scanner scanner = null;
        ArrayList<String> infos = new ArrayList<String>();
        try {
            scanner = new Scanner(infoFile).useDelimiter("\n");
            while (scanner.hasNext()) {
                infos.add(scanner.next());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Pattern p = Pattern.compile("\"(.*?)\"");
        Matcher m = p.matcher(infos.get(7));
        if (m.find()) {
            item.withCarName(m.group(0).replace("\"", ""));
        }

        File imgFile = new File(Environment.getExternalStorageDirectory().toString() + "/RVGL/cars/" + car + "/car.bmp");
        if (!imgFile.isFile() || !imgFile.canRead()) {
            Log.d(">>>", "Erro imagem " + car);
            item.withCarImgPath(null);
        } else {
            item.withCarImgPath(imgFile.getAbsolutePath());
        }


        return item;
    }
}