package io.github.tavisco.rvglassistant.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.objects.CarItem;
import io.github.tavisco.rvglassistant.others.StockCarImages;

public class ImageLoader {

    public static Bitmap loadCarImage(Context ctx, CarItem car, ImageView imageView) {
        if (car.getImagePath() != null) {
            File image = new File(car.getImagePath());
            if (image.isFile() && image.canRead()) {
                Glide.with(ctx).load(car.getImagePath()).into(imageView);
                return BitmapFactory.decodeFile(car.getImagePath());
            }
        } else {
            StockCarImages stockImgs = StockCarImages.getInstance();
            if (stockImgs.getCarsImgs().containsKey(car.getName())){
                Glide.with(ctx).load(stockImgs.getCarsImgs().get(car.getName())).into(imageView);
                return BitmapFactory.decodeResource(ctx.getResources(), stockImgs.getCarsImgs().get(car.getName()));
            }
        }

        Glide.with(ctx).load(R.drawable.unknown_carbox).into(imageView);
        return BitmapFactory.decodeResource(ctx.getResources(), R.drawable.unknown_carbox);
    }

}
