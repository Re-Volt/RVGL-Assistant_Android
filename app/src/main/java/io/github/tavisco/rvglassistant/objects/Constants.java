package io.github.tavisco.rvglassistant.objects;

import android.os.Environment;

import java.io.File;

/**
 * Created by Tavisco on 29/04/18.
 */

public class Constants {
    //Links
    public static final String RVGL_LAST_VERSION_LINK = "https://distribute.re-volt.io/releases/rvgl_version.txt";
    public static final String RVGL_ANDROID_APK_LINK = "https://forum.re-volt.io/viewtopic.php?f=8&t=76";

    //Files
    public static final String RVGL_CURRENT_VERSION_TXT = "rvgl_version.txt";
    public static final String CAR_PARAMETER_FILE_NAME = "parameters.txt";

    //Paths
    public static final String RVGL_PATH = Environment.getExternalStorageDirectory().toString()
            + File.separator + "RVGL";
    public static final String RVGL_ASSIST_UNZIP_PATH = Environment.getExternalStorageDirectory().toString()
            + File.separator + "RVGLAssist" + File.separator + "unzipped";

    //Misc
    public static final String TAG = ">>>";
}
