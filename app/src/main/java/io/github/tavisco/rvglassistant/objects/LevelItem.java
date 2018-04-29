package io.github.tavisco.rvglassistant.objects;

import java.io.File;

/**
 * Created by Tavisco on 29/04/18.
 */
public class LevelItem extends BaseItem {
    @Override
    public String getImagePath() {
        return super.basePath + File.separator + "gfx" + File.separator + super.itemPath + ".bmp";
    }
}
