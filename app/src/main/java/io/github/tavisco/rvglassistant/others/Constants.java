package io.github.tavisco.rvglassistant.others;

import android.os.Environment;

import java.io.File;

/**
 * Created by Tavisco on 29/04/18.
 */

public class Constants {
    //Paths
    public static final String PATH_RVGL = Environment.getExternalStorageDirectory().toString()
            + File.separator + "RVGL";
    public static final String PATH_RVGL_BUTLER = Environment.getExternalStorageDirectory().toString()
            + File.separator + "RVGLAssist";
    public static final String PATH_RVGL_BUTLER_UNZIP = PATH_RVGL_BUTLER + File.separator + "unzipped";
    public static final String VERSIONS_FOLDER_NAME = "versions";

    //Links
    public static final String RVGL_LAST_VERSION_LINK = "https://distribute.re-volt.io/releases/rvgl_version.txt";
    public static final String RVGL_ANDROID_APK_LINK = "https://forum.re-volt.io/viewtopic.php?f=8&t=76";
    public static final String RVIO_AVAIABLE_PACKAGES_LINK = "http://distribute.re-volt.io/packages.txt";
    public static final String RVIO_ASSETS_LINK = "https://distribute.re-volt.io/assets/";
    public static final String RVIO_DOWNLOAD_PACKS_LINK = "https://distribute.re-volt.io/packs/";

    //Files
    public static final String RVGL_CURRENT_VERSION_TXT = "rvgl_version.txt";
    public static final String CAR_PARAMETER_FILE_NAME = "parameters.txt";
    public static final String LEVEL_PLACEHOLDER_IMAGE = PATH_RVGL + File.separator + "gfx" + File.separator + "acclaim.bmp";

    //Misc
    public static final String TAG = ">>>";
    public static final String NOTIFICATION_CHANNEL_ID = "io.github.tavisco.rvglassistant";

}
