package io.github.tavisco.rvglassistant.utils;

import java.io.File;
import io.github.tavisco.rvglassistant.objects.enums.ItemType;

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

    public static ItemType determineWhileInstalling(String folderPath){
        File directory = new File(folderPath);

        ItemType type = ItemType.UNKNOWN;

        for (File fileInsideZip : directory.listFiles()) {
            if (fileInsideZip.isDirectory()) {
                if (fileInsideZip.getName().contains(ItemType.CAR.getTypePath())){
                    type = ItemType.CAR;
                } else if (fileInsideZip.getName().contains(ItemType.LEVEL.getTypePath())){
                    type = ItemType.LEVEL;
                }
            }
        }

        return type;
    }
}
