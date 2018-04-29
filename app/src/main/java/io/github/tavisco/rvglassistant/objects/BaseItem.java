package io.github.tavisco.rvglassistant.objects;

/**
 * Created by Tavisco on 29/04/18.
 */
public abstract class BaseItem {
    protected String name;
    //protected String imagePath;
    protected ItemType type;
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

    public String getItemPath() {
        return itemPath;
    }

    public void setItemPath(String itemPath) {
        this.itemPath = itemPath;
    }
}
