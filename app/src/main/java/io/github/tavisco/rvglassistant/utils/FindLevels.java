package io.github.tavisco.rvglassistant.utils;

import android.os.Environment;
import android.util.Log;

import com.mikepenz.fastadapter.adapters.ItemAdapter;

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

import io.github.tavisco.rvglassistant.objects.Constants;
import io.github.tavisco.rvglassistant.objects.ItemType;
import io.github.tavisco.rvglassistant.objects.LevelItem;
import io.github.tavisco.rvglassistant.objects.RecyclerViewItems.LevelViewItem;

/**
 * Created by otavio.mpinheiro on 14/03/2018.
 */

public class FindLevels {


    public static List<LevelViewItem> getAllLevels(ItemAdapter<LevelViewItem> itemAdapter){

        List<String> dontShowTracks = new ArrayList<>();
        dontShowTracks.add("intro");
        dontShowTracks.add("frontend");
        dontShowTracks.add("stunts");

        List<LevelViewItem> list = new ArrayList<>();

        String path = Constants.RVGL_PATH + File.separator + "levels";
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (!directory.isDirectory() || !directory.canRead() || files.length == 0){

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

            //TODO: Make this runs on another thread
            LevelItem levelBase = (LevelItem) ItemParser.parse(file.getName(), Constants.RVGL_PATH, ItemType.LEVEL.getTypePath());
            if (levelBase != null){
                LevelViewItem levelView = new LevelViewItem(levelBase);
                itemAdapter.add(levelView);
            }
        }
        return list;
    }

}
