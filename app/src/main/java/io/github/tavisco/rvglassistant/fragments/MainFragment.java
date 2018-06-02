package io.github.tavisco.rvglassistant.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.Func;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.objects.Constants;
import io.github.tavisco.rvglassistant.objects.RecyclerViewItems.PackageItem;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    // =-=-=-= Bindings =-=-=-=
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
    @BindView(R.id.recycler_main_packages)
    RecyclerView mRecyclerView;


    // =-=-=-= Recycler =-=-=-=
    private FastAdapter<PackageItem> mFastAdapter;
    private ItemAdapter<PackageItem> mItemAdapter;


    // =-=-=-= Items/Variables =-=-=-=
    boolean updateAvaiable = false;
    private Fetch mainFetch;



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
        createPackagesList();

        mainFetch = new Fetch.Builder(this.getContext(), "Main")
                .setDownloadConcurrentLimit(1) // Allows Fetch to download 1 file at moment.
                .enableLogging(true)
                .build();

    }

    private void createPackagesList() {
        //create our ItemAdapter which will host our items
        mItemAdapter = new ItemAdapter<>();

        //create our FastAdapter which will manage everything
        mFastAdapter = FastAdapter.with(Arrays.asList(mItemAdapter));
        mFastAdapter.withSelectable(true);
        mFastAdapter.withMultiSelect(true);
        mFastAdapter.withSelectOnLongClick(false);

        //configure our fastAdapter
        //get our recyclerView and do basic setup
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 1));
        mRecyclerView.setAdapter(mFastAdapter);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this.getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.RVIO_AVAIABLE_PACKAGES_LINK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.isEmpty()){
                            // Split on new lines
                            String rvioPackages[] = response.split("\\r?\\n");
                            for (String rvioPack : rvioPackages) {
                                mItemAdapter.add(new PackageItem(rvioPack));
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(Constants.TAG, error.getLocalizedMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        //configure our fastAdapter
        mFastAdapter.withOnClickListener(new OnClickListener<PackageItem>() {
            @Override
            public boolean onClick(View v, IAdapter<PackageItem> adapter, @NonNull PackageItem item, int position) {
                handlePackageClick(v, item);
                return false;
            }
        });
    }

    private void handlePackageClick(View v, final PackageItem item) {

        if (!item.isDownloadOngoing()){
            final com.tonyodev.fetch2.Request request = new com.tonyodev.fetch2.Request(item.getDownloadLink(), item.getDownloadSavePath());
            request.setPriority(Priority.HIGH);
            request.setNetworkType(NetworkType.WIFI_ONLY);

            mainFetch.enqueue(request, new Func<Download>() {
                @Override
                public void call(Download download) {
                    Log.d(Constants.TAG, "call: Started downloading");
                    item.setDownloadID(download.getId());
                    item.setDownloadOngoing(true);
                    //Request successfully Queued for download
                }
            }, new Func<Error>() {
                @Override
                public void call(Error error) {
                    //An error occurred when enqueuing a request.
                }
            });

            final FetchListener fetchListener = new FetchListener() {

                @Override
                public void onQueued(Download download) {

                }

                @Override
                public void onCompleted(@NotNull Download download) {
                    mainFetch.removeListener(this);
                    item.setDownloadOngoing(false);
                }

                @Override
                public void onError(@NotNull Download download) {
                    final Error error = download.getError();
                    final Throwable throwable = error.getThrowable(); //can be null
                    if (error == Error.UNKNOWN && throwable != null) {
                        Log.d("Fetch", "Throwable error", throwable);
                    }
                    mainFetch.removeListener(this);
                    item.setDownloadOngoing(false);
                }

                @Override
                public void onProgress(@NotNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
                    if (request.getId() == download.getId()) {
                        //updateDownload(download, etaInMilliSeconds);
                    }
                    final int progress = download.getProgress();
                    Log.d("Fetch", "Progress Completed :" + progress);
                }

                @Override
                public void onPaused(@NotNull Download download) {

                }

                @Override
                public void onResumed(@NotNull Download download) {

                }

                @Override
                public void onCancelled(@NotNull Download download) {
                    Log.d(Constants.TAG, "onCancelled: DOWNLOAD CANCELADO");
                }

                @Override
                public void onRemoved(@NotNull Download download) {

                }

                @Override
                public void onDeleted(@NotNull Download download) {

                }
            };

            mainFetch.addListener(fetchListener);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(v.getContext(), Constants.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_cloud_download)
                    .setContentTitle("Downloading " + item.getName() + " from RV I/O")
                    .setContentText("Essa foi a primeira notificalçao evar que já fiz no android!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(v.getContext());

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(Constants.NOTIFICATION_CHANNEL_ID, createID(), mBuilder.build());
        } else {
            mainFetch.cancel(item.getDownloadID());
        }
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

        File versionFile = new File(Constants.PATH_RVGL + File.separator + Constants.RVGL_CURRENT_VERSION_TXT);

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

        Activity activity = getActivity();
        if(!checkFailed && activity != null)
            tvInstalledVersion.setText("Installed version:\n" + localVersion);

        return localVersion;
    }

    public void compareWithLocalVersion(String localVersion, String lastVersion){
        Activity activity = getActivity();
        if (activity != null){
            if (localVersion.equals("-1") || lastVersion.equals("-1")){
                tvUpdateStatus.setText("Oops! Couldn't get the last version");
                tvInstalledVersion.setText("Installed version:\nCouldn't get the local version");
                tvLastVersion.setText("Last version:\nCouldn't get the last version");
            } else {
                tvLastVersion.setText("Last version:\n" + lastVersion);

                if (localVersion.equals(lastVersion)){
                    cardUpdate.setCardBackgroundColor(getResources().getColor(R.color.updatedGreen));
                    tvUpdateStatus.setText("You are up to date!");
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

    public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }
}
