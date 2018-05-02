package io.github.tavisco.rvglassistant.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.objects.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    @BindView(R.id.card_main_updateStatus)
    CardView cardUpdate;
    @BindView(R.id.tv_main_installedVersion)
    TextView tvInstalledVersion;
    @BindView(R.id.tv_main_lastVersion)
    TextView tvLastVersion;
    @BindView(R.id.tv_main_updateStatus)
    TextView tvUpdateStatus;
    @BindView(R.id.img_main_updateStatus)
    ImageView imgUpdateStatus;

    boolean updateAvaiable = false;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        checkForUpdates();
    }

    public void checkForUpdates(){
        final String localVersion = getLocalGameVersion();

        Activity activity = getActivity();
        if(activity != null){
            cardUpdate.setCardBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
            tvUpdateStatus.setText("Checking for updates...");
            tvLastVersion.setText("Last version:\nFetching...");
            imgUpdateStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud_sync));

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this.getContext());

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.RVGL_LAST_VERSION_LINK,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Need to substring the version to not get garbage
                            compareWithLocalVersion(localVersion ,response.substring(0, 7));
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(Constants.TAG, error.getLocalizedMessage());
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

    }

    public String getLocalGameVersion(){
        boolean checkFailed = false;
        String localVersion = "-1";
        String basePath = Environment.getExternalStorageDirectory().toString() + File.separator +
                "RVGL" + File.separator;

        File versionFile = new File(basePath + File.separator + Constants.RVGL_CURRENT_VERSION_TXT);

        if (!versionFile.isFile() || !versionFile.canRead()) {
            checkFailed = true;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                try (Stream<String> lines = Files.lines(versionFile.toPath())) {
                    localVersion = lines.findFirst().get();
                }
            } catch (IOException e) {
                e.printStackTrace();
                checkFailed = true;
            }
        } else {
            Scanner scanner = null;
            ArrayList<String> infos = new ArrayList<String>();
            try {
                scanner = new Scanner(versionFile).useDelimiter("\n");
                while (scanner.hasNext()) {
                    infos.add(scanner.next());
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                checkFailed = true;
            }

            if (!checkFailed)
                localVersion = infos.get(0);

        }

        if(!checkFailed)
            tvInstalledVersion.setText("Installed version:\n" + localVersion);

        return localVersion;
    }

    public void compareWithLocalVersion(String localVersion, String lastVersion){

        Activity activity = getActivity();
        if(activity != null){
            if (localVersion.equals("-1") || lastVersion.equals("-1")){
                tvUpdateStatus.setText("Oops! Couldn't get the last version");
                tvInstalledVersion.setText("Installed version:\nCouldn't get the local version");
                tvLastVersion.setText("Last version:\nCouldn't get the last version");
            } else {
                tvLastVersion.setText("Last version:\n" + lastVersion);

                if (localVersion.equals(lastVersion)){
                    cardUpdate.setCardBackgroundColor(getResources().getColor(R.color.updatedGreen));
                    tvUpdateStatus.setText("Running the last version!");
                    imgUpdateStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud_check));
                } else {
                    cardUpdate.setCardBackgroundColor(getResources().getColor(R.color.newVersionRed));
                    tvUpdateStatus.setText("Version " + lastVersion + " is avaiable to download!\nClick here for more info.");
                    imgUpdateStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud_download));
                    updateAvaiable = true;
                }
            }
        }
    }

    @OnClick(R.id.card_main_updateStatus)
    public void clickCardUpdate(){
        if (updateAvaiable){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.RVGL_ANDROID_APK_LINK));
            startActivity(browserIntent);
        } else {
            checkForUpdates();
        }
    }
}
