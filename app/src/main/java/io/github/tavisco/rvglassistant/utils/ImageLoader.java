package io.github.tavisco.rvglassistant.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.objects.BaseItem;
import io.github.tavisco.rvglassistant.objects.enums.ItemType;
import io.github.tavisco.rvglassistant.others.StockCarImages;

public class ImageLoader {

    public static Bitmap loadItemImage(Context ctx, BaseItem baseItem, ImageView imageView) {
        if (baseItem.getImagePath() != null) {
            File image = new File(baseItem.getImagePath());
            if (image.isFile() && image.canRead()) {
                Glide.with(ctx).load(baseItem.getImagePath()).into(imageView);
                return BitmapFactory.decodeFile(baseItem.getImagePath());
            }
        } else {
            if (baseItem.getType() == ItemType.CAR){
                StockCarImages stockImgs = StockCarImages.getInstance();
                if (stockImgs.getCarsImgs().containsKey(baseItem.getName())){
                    Glide.with(ctx).load(stockImgs.getCarsImgs().get(baseItem.getName())).into(imageView);
                    return BitmapFactory.decodeResource(ctx.getResources(), stockImgs.getCarsImgs().get(baseItem.getName()));
                }
            }
        }

        Glide.with(ctx).load(R.drawable.unknown_carbox).into(imageView);
        return BitmapFactory.decodeResource(ctx.getResources(), R.drawable.unknown_carbox);
    }

}
