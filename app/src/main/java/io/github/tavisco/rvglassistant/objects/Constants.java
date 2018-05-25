package io.github.tavisco.rvglassistant.objects;

import android.os.Environment;

import java.io.File;

/**
 * Created by Tavisco on 29/04/18.
 */

public class Constants {
    //Paths
    public static final String RVGL_PATH = Environment.getExternalStorageDirectory().toString()
            + File.separator + "RVGL";
    public static final String RVGL_ASSIST_UNZIP_PATH = Environment.getExternalStorageDirectory().toString()
            + File.separator + "RVGLAssist" + File.separator + "unzipped";

    //Links
    public static final String RVGL_LAST_VERSION_LINK = "https://distribute.re-volt.io/releases/rvgl_version.txt";
    public static final String RVGL_ANDROID_APK_LINK = "https://forum.re-volt.io/viewtopic.php?f=8&t=76";
    public static final String RVIO_AVAIABLE_PACKAGES_LINK = "http://distribute.re-volt.io/packages.txt";
    public static final String RVIO_ASSETS_LINK = "https://distribute.re-volt.io/assets/";

    //Files
    public static final String RVGL_CURRENT_VERSION_TXT = "rvgl_version.txt";
    public static final String CAR_PARAMETER_FILE_NAME = "parameters.txt";
    public static final String LEVEL_PLACEHOLDER_IMAGE = RVGL_PATH + File.separator + "gfx" + File.separator + "acclaim.bmp";

    //Misc
    public static final String TAG = ">>>";

}
