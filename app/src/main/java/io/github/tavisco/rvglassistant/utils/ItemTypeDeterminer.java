package io.github.tavisco.rvglassistant.utils;

import android.util.Log;

import java.io.File;

import io.github.tavisco.rvglassistant.objects.Constants;
import io.github.tavisco.rvglassistant.objects.ItemType;

/**
 * Created by Tavisco on 29/04/18.
 */
public class ItemTypeDeterminer {
    public static ItemType determine(String folderPath){
        File directory = new File(folderPath);

        ItemType type = ItemType.CAR;
        if (directory.getParent().contains(type.getTypePath()))
            return type;

        type = ItemType.LEVEL;
        if (directory.getParent().contains(type.getTypePath()))
            return type;

        return ItemType.UNKNOWN;
    }
}
