package io.github.tavisco.rvglassistant.utils;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import io.github.tavisco.rvglassistant.objects.BaseItem;
import io.github.tavisco.rvglassistant.objects.CarItem;
import io.github.tavisco.rvglassistant.objects.Constants;
import io.github.tavisco.rvglassistant.objects.ItemType;
import io.github.tavisco.rvglassistant.objects.LevelItem;

/**
 * Created by Tavisco on 29/04/18.
 */
public class ItemParser {

    public static BaseItem parse(String folderName, String basePath){
        BaseItem item = null;
        String itemPath = basePath + File.separator + folderName;
        ItemType itemType = ItemTypeDeterminer.determine(itemPath);
        //boolean isGettingInstalled = itemPath.equals(Constants.RVGL_ASSIST_UNZIP_PATH);

        if (itemType == ItemType.CAR){
            item = new CarItem();
        } else if (itemType == ItemType.LEVEL){
            item = new LevelItem();
        } else {
            return null;
        }

        item.setItemPath(itemPath);
        String parameterFile = "";

        if (itemType == ItemType.CAR){
            parameterFile = Constants.CAR_PARAMETER_FILE_NAME;
        } else if (itemType == ItemType.LEVEL) {
            parameterFile = folderName + ".txt";
        }

        File infoFile = new File(itemPath + File.separator + parameterFile);

        if (!infoFile.isFile() || !infoFile.canRead()) {
            Log.d(Constants.TAG, "Error reading parameter file for " + folderName);
            return null;
        }

        String wholeParamLine = "";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                try (Stream<String> lines = Files.lines(infoFile.toPath())) {
                    wholeParamLine = lines.skip(itemType.getTypeParameterNameLine()).findFirst()
                            .get();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Scanner scanner = null;
            try {
                scanner = new Scanner(infoFile).useDelimiter("\n");
                int counter = 0;
                while (scanner.hasNext() && counter <=itemType.getTypeParameterNameLine()) {
                    if (counter == itemType.getTypeParameterNameLine()){
                        wholeParamLine = scanner.next();
                    } else {
                        counter++;
                    }
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        //Now I use REGEX to extract the name of the item
        Pattern p = Pattern.compile("\"(.*?)\"");
        Matcher m = p.matcher(wholeParamLine);
        if (m.find()) {
            item.setName(m.group(0).replace("\"", ""));
        }

        return item;
    }
}
