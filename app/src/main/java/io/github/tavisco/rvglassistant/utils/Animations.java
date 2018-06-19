package io.github.tavisco.rvglassistant.utils;

import android.view.View;
import android.view.ViewPropertyAnimator;

public class Animations {
    public final static int DEFAULT_DELAY = 0;

    public static ViewPropertyAnimator showViewByScale(View v) {

        ViewPropertyAnimator propertyAnimator = v.animate().setStartDelay(DEFAULT_DELAY)
                .scaleX(1).scaleY(1);

        return propertyAnimator;
    }

    public static void configuredHideYView(View v) {
        v.setScaleY(0);
        v.setPivotY(0);
    }

    public static ViewPropertyAnimator hideViewByScaleXY(View v) {
        return hideViewByScale(v, DEFAULT_DELAY, 0, 0);
    }

    public static ViewPropertyAnimator hideViewByScaleY(View v) {

        return hideViewByScale(v, DEFAULT_DELAY, 1, 0);
    }

    private static ViewPropertyAnimator hideViewByScale(View v, int delay, int x, int y) {

        ViewPropertyAnimator propertyAnimator = v.animate().setStartDelay(delay)
                .scaleX(x).scaleY(y);

        return propertyAnimator;
    }
}
