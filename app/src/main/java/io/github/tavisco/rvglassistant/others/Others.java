package io.github.tavisco.rvglassistant.others;

import android.content.Context;

public class Others {
    /**
     * @param context
     * @param dp
     * @return
     */
    public static float pxFromDp(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
