package io.github.tavisco.rvglassistant.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import io.github.tavisco.rvglassistant.items.CarItem;

/**
 * Created by otavio.mpinheiro on 15/03/2018.
 */

public class FindCars {

    public static void getAllCars(ItemAdapter<CarItem> itemAdapter) {

        List<CarItem> list = new ArrayList<>();

        String path = Environment.getExternalStorageDirectory().toString() + "/RVGL/cars";
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (!directory.isDirectory() || !directory.canRead() || files.length == 0) {
            //return null;
        }

        for (File file : files) {
            CarItem car = populateItem(file.getName(), false);
            itemAdapter.add(car);
            list.add(populateItem(file.getName(), false));
        }
        //return list;
    }

    public static CarItem populateItem(String carName, boolean isGettingInstalled) {
        CarItem item = new CarItem();

        String basePath;

        if (isGettingInstalled){
            basePath = Environment.getExternalStorageDirectory().toString() + File.separator + "RVGLAssist" + File.separator + "unzipped" + File.separator;
        } else {
            basePath = Environment.getExternalStorageDirectory().toString() + File.separator + "RVGL" + File.separator;
        }

        String carDirectoryPath = basePath + "cars" + File.separator + carName;
        File carDirectory = new File(carDirectoryPath);

        if (!carDirectory.isDirectory() || !carDirectory.canRead()) {
            return null;
        }

        File infoFile = new File(carDirectoryPath + File.separator + "parameters.txt");

        if (!infoFile.isFile() || !infoFile.canRead()) {
            Log.d(">>>", "Erro arquivo " + carName);
            return null;
        }

        String lineCarName = "";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                try (Stream<String> lines = Files.lines(infoFile.toPath())) {
                    lineCarName = lines.skip(7).findFirst().get();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Scanner scanner = null;
            ArrayList<String> infos = new ArrayList<String>();
            try {
                scanner = new Scanner(infoFile).useDelimiter("\n");
                int counter = 0;
                while (scanner.hasNext() && counter <=7) {
                    infos.add(scanner.next());
                    counter++;
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            lineCarName = infos.get(7);
        }

        Pattern p = Pattern.compile("\"(.*?)\"");
        Matcher m = p.matcher(lineCarName);
        if (m.find()) {
            item.withCarName(m.group(0).replace("\"", ""));
        }

        File imgFile;

        imgFile = new File(carDirectoryPath + File.separator + "carbox.bmp");

        if (!imgFile.exists())
            imgFile = new File(carDirectoryPath + File.separator + "car.bmp");


        if (!imgFile.isFile() || !imgFile.canRead()) {
            Log.d(">>>", "Erro imagem " + carName);
            item.withCarImgPath(null);
        } else {
            item.withCarImgPath(imgFile.getAbsolutePath());
        }

        return item;
    }
}