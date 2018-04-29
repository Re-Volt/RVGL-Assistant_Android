package io.github.tavisco.rvglassistant.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import io.github.tavisco.rvglassistant.objects.RecyclerViewItems.TrackViewItem;

/**
 * Created by otavio.mpinheiro on 14/03/2018.
 */

public class FindTracks {


    public static List<TrackViewItem> getAllTracks(){

        List<String> dontShowTracks = new ArrayList<>();
        dontShowTracks.add("intro");
        dontShowTracks.add("frontend");
        dontShowTracks.add("stunts");

        List<TrackViewItem> list = new ArrayList<>();

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

            list.add(populateItem(file.getName(), false));
        }
        return list;
    }

    public static TrackViewItem populateItem(String levelName, boolean isGettingInstalled){
        TrackViewItem item = new TrackViewItem();

        String basePath;

        if (isGettingInstalled){
            basePath = Environment.getExternalStorageDirectory().toString() + File.separator + "RVGLAssist" + File.separator + "unzipped" + File.separator;
        } else {
            basePath = Environment.getExternalStorageDirectory().toString() + File.separator + "RVGL" + File.separator;
        }


        File levelDirectory = new File(basePath + "levels" + File.separator + levelName);

        if (!levelDirectory.isDirectory() || !levelDirectory.canRead()){
            return null;
        }

        File infoFiles[] = levelDirectory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename)
            { return filename.endsWith(".inf"); }
        } );

        if (infoFiles.length == 0){
            return null;
        }

        File infoFile = infoFiles[0];

        if (!infoFile.isFile() || !infoFile.canRead()){
            return null;
        }

        // Detecting the ingame name of the level
        String lineTrackName = "";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                try (Stream<String> lines = Files.lines(infoFile.toPath())) {
                    lineTrackName = lines.skip(4).findFirst().get();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
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

            lineTrackName = infos.get(4);
        }

        Pattern p = Pattern.compile("\\'(.*?)\\'");
        Matcher m = p.matcher(lineTrackName);
        if (m.find()) {
            item.setName(m.group(0).replace("\'", ""));
        }
        // End of detecting


        //String levelPathName = infoFile.getName().replace(".inf", "");
        String levelImagemPath = basePath + "gfx" + File.separator + levelName + ".bmp";

        File imgFile = new File(levelImagemPath);
        if (!imgFile.isFile() || !imgFile.canRead()){
            Log.d(">>>", "Image error: " + item.getTrackName());
            item.setImage(null);
        } else {
            item.setImage(imgFile.getAbsolutePath());
        }

        return item;
    }
}
