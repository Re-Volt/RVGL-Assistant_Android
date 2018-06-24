package io.github.tavisco.rvglassistant.utils;

import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.tavisco.rvglassistant.others.Constants;
import io.github.tavisco.rvglassistant.objects.enums.ItemType;
import io.github.tavisco.rvglassistant.objects.LevelItem;
import io.github.tavisco.rvglassistant.objects.adapters.LevelViewItem;

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

        String path = Constants.PATH_RVGL + File.separator + ItemType.LEVEL.getTypePath();
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (!directory.isDirectory() || !directory.canRead() || files.length == 0){

        }

        boolean skip;
        for (File levelFile : files) {
            skip = false;

            for (String dontShow : dontShowTracks) {
                if (dontShow.equals(levelFile.getName()))
                    skip = true;
            }

            if (skip)
                continue;

            //TODO: Make this runs on another thread
            //levelFile = "/storage/emulated/0/RVGL/levels/markar"
            //basePath = "/storage/emulated/0/RVGL"

            LevelItem levelBase = (LevelItem) ItemParser.parse(levelFile, Constants.PATH_RVGL, ItemType.LEVEL);
            if (levelBase != null){
                LevelViewItem levelView = new LevelViewItem(levelBase);
                itemAdapter.add(levelView);
            }
        }
        return list;
    }

}
