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

import io.github.tavisco.rvglassistant.items.TrackItem;

/**
 * Created by otavio.mpinheiro on 14/03/2018.
 */

public class FindTracks {


    public static List<TrackItem> getAllTracks(){

        List<String> dontShowTracks = new ArrayList<>();
        dontShowTracks.add("intro");
        dontShowTracks.add("frontend");
        dontShowTracks.add("stunts");

        List<TrackItem> list = new ArrayList<>();

        String path = Environment.getExternalStorageDirectory().toString()+"/RVGL/levels";
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (!directory.isDirectory() || !directory.canRead() || files.length == 0){
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

    private static TrackItem populateItem(String track){
        TrackItem item = new TrackItem();

        String path = Environment.getExternalStorageDirectory().toString() + "/RVGL/levels/" + track;
        File directory = new File(path);

        if (!directory.isDirectory() || !directory.canRead()){
            return null;
        }

        File infoFile = new File(path + "/"+track +".inf");

        if (!infoFile.isFile() || !infoFile.canRead()){
            Log.d(">>>", "Erro arquivo " + track);
            return null;
        }

        Scanner scanner = null;
        ArrayList<String> infos = new ArrayList<String>();
        try {
            scanner = new Scanner(infoFile).useDelimiter("\n");
            while (scanner.hasNext()){
                infos.add(scanner.next());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Pattern p = Pattern.compile("\\'(.*?)\\'");
        Matcher m = p.matcher(infos.get(4));
        if (m.find()) {
            item.withName(m.group(0).replace("\'", ""));
        }

        File imgFile = new File(Environment.getExternalStorageDirectory().toString() + "/RVGL/gfx/" + track + ".bmp");
        if (!imgFile.isFile() || !imgFile.canRead()){
            Log.d(">>>", "Erro imagem " + track);
            item.withImage(null);
        } else {
            item.withImage(imgFile.getAbsolutePath());
        }


        return item;
    }
}
