package io.github.tavisco.rvglassistant.objects;

import java.io.File;

/**
 * Created by Tavisco on 29/04/18.
 */
public class CarItem extends BaseItem {
    private int acc;

    public int getAcc() {
        return acc;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    @Override
    public String getImagePath()
    {
        File image = new File(super.getFullPath() + File.separator + "carbox.bmp");
        if (image.exists()){
            return image.getPath();
        } else {
            return null;
        }
    }
}
