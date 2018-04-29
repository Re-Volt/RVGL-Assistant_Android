package io.github.tavisco.rvglassistant.objects;

import java.io.File;

/**
 * Created by Tavisco on 29/04/18.
 */
public abstract class BaseItem {
    protected String name;
    //protected String imagePath;
    protected ItemType type;
    protected  String basePath;
    protected String itemPath;

    public String getName() {
        return name;
    }

    public void setName(String itemName) {
        this.name = itemName;
    }

    public abstract String getImagePath();

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType itemType) {
        this.type = itemType;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getItemPath() {
        return itemPath;
    }

    public void setItemPath(String itemPath) {
        this.itemPath = itemPath;
    }

    public String getFullPath() {
        return basePath + File.separator + type.getTypePath() + File.separator + itemPath;
    }

}
