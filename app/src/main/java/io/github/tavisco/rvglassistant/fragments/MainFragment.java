package io.github.tavisco.rvglassistant.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.tonyodev.fetch2.Fetch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.objects.adapters.IOPackageViewItem;
import io.github.tavisco.rvglassistant.others.Constants;

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
    @SuppressWarnings("FieldCanBeLocal")
    private FastAdapter<IOPackageViewItem> mFastAdapter;
    private ItemAdapter<IOPackageViewItem> mItemAdapter;


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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
    public void onDestroy() {
        super.onDestroy();
        mainFetch.close();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        checkForUpdates();
        createPackagesList();

        mainFetch = new Fetch.Builder(this.getContext(), "Main")
                .setDownloadConcurrentLimit(1) // Allows Fetch to download 1 file at moment.
                .enableLogging(true)
                .build();

    }

    private void createPackagesList() {
        Log.d(Constants.TAG, "createPackagesList: CRIADO");
        //create our ItemAdapter which will host our items
        mItemAdapter = new ItemAdapter<>();

        //create our FastAdapter which will manage everything
        mFastAdapter = FastAdapter.with(Collections.singletonList(mItemAdapter));
        mFastAdapter.withSelectable(true);
        mFastAdapter.withMultiSelect(true);
        mFastAdapter.withSelectOnLongClick(false);

        //configure our fastAdapter
        //get our recyclerView and do basic setup
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 1));
        mRecyclerView.setAdapter(mFastAdapter);

        Context ctx = this.getContext();

        if (ctx != null){
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
                                    mItemAdapter.add(new IOPackageViewItem(rvioPack));
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
        }

        //configure our fastAdapter
        mFastAdapter.withOnClickListener(new OnClickListener<IOPackageViewItem>() {
            @Override
            public boolean onClick(View v, @NonNull IAdapter<IOPackageViewItem> adapter, @NonNull IOPackageViewItem item, int position) {
                handlePackageClick(v, item);
                return false;
            }
        });
    }

    private void handlePackageClick(final View v, final IOPackageViewItem item) {
//        if (!item.isDownloadOngoing()){
//
//            new MaterialDialog.Builder(v.getContext())
//                    .title("Download ".concat(item.getName()).concat("?"))
//                    .content("Do you wish to download ".concat(item.getName()).concat(" pack?"))
//                    .positiveText(R.string.agree)
//                    .negativeText(R.string.disagree)
//                    .onPositive(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            downloadPack(v, item);
//                        }
//                    })
//                    .show();
//        } else {
//            new MaterialDialog.Builder(v.getContext())
//                    .title("Stop downloading ".concat(item.getName()).concat("?"))
//                    .content("Do you wish to stop downloading ".concat(item.getName()).concat("?"))
//                    .positiveText(R.string.agree)
//                    .negativeText(R.string.disagree)
//                    .onPositive(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            mainFetch.cancel(item.getDownloadID());
//                        }
//                    })
//                    .show();
//        }
    }

    public void downloadPack(final View v, final IOPackageViewItem item){
//        final com.tonyodev.fetch2.Request request = new com.tonyodev.fetch2.Request(item.getDownloadLink(), item.getDownloadSavePath());
//        request.setPriority(Priority.HIGH);
//        request.setNetworkType(NetworkType.WIFI_ONLY);
//
//        mainFetch.removeAll();
//
//        mainFetch.enqueue(request, new Func<Download>() {
//            @Override
//            public void call(Download download) {
//                Log.d(Constants.TAG, "call: Started downloading");
//                item.setDownloadID(download.getId());
//                item.setDownloadOngoing(true, item.getViewHolder(v));
//                //Request successfully Queued for download
//            }
//        }, new Func<Error>() {
//            @Override
//            public void call(Error error) {
//                //An error occurred when enqueuing a request.
//                Log.d(Constants.TAG, "ERRO STARTING DOWNLOAD.");
//            }
//        });
//
//        final FetchListener fetchListener = new FetchListener() {
//
//            @Override
//            public void onQueued(Download download) {
//
//            }
//
//            @Override
//            public void onCompleted(@NotNull Download download) {
//                mainFetch.removeListener(this);
//                item.downloadCompleted(item.getViewHolder(v));
//                //item.setDownloadOngoing(false, item.getViewHolder(v));
//            }
//
//            @Override
//            public void onError(@NotNull Download download) {
//                final Error error = download.getError();
//                final Throwable throwable = error.getThrowable(); //can be null
//                if (error == Error.UNKNOWN && throwable != null) {
//                    Log.d(Constants.TAG, "Throwable error", throwable);
//                }
//                mainFetch.removeListener(this);
//                item.setDownloadOngoing(false, item.getViewHolder(v));
//            }
//
//            @Override
//            public void onProgress(@NotNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
//                if (request.getId() == download.getId()) {
//                    item.updateDownloadView(v.getContext(), item.getViewHolder(v), etaInMilliSeconds, downloadedBytesPerSecond, download.getProgress(), download.getStatus().toString());
//                }
//            }
//
//            @Override
//            public void onPaused(@NotNull Download download) {
//
//            }
//
//            @Override
//            public void onResumed(@NotNull Download download) {
//
//            }
//
//            @Override
//            public void onCancelled(@NotNull Download download) {
//                Log.d(Constants.TAG, "onCancelled: DOWNLOAD CANCELADO");
//                item.setDownloadOngoing(false, item.getViewHolder(v));
//            }
//
//            @Override
//            public void onRemoved(@NotNull Download download) {
//
//            }
//
//            @Override
//            public void onDeleted(@NotNull Download download) {
//
//            }
//        };
//
//        mainFetch.addListener(fetchListener);
    }

    public void checkForUpdates(){
        final String localVersion = getLocalGameVersion();

        Activity activity = getActivity();
        if(activity != null){
            cardUpdate.setCardBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
            tvUpdateStatus.setText(R.string.main_checking_updates);
            tvLastVersion.setText(String.format(getString(R.string.main_last_version), getString(R.string.main_checking)));
            imgUpdateStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud_sync));

            Context ctx = this.getContext();

            if (ctx != null){
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(ctx);

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
                    localVersion = lines.findFirst().orElse(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                checkFailed = true;
            }
        } else {
            Scanner scanner;
            ArrayList<String> infos = new ArrayList<>();
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
            tvInstalledVersion.setText(String.format(getString(R.string.main_installed_version), localVersion));

        return localVersion;
    }

    public void compareWithLocalVersion(String localVersion, String lastVersion){
        Activity activity = getActivity();
        if (activity != null){
            if (localVersion.equals("-1") || lastVersion.equals("-1")){
                tvUpdateStatus.setText(R.string.main_error_getting_last_version);
                tvInstalledVersion.setText(String.format(getString(R.string.main_installed_version), getString(R.string.main_error_getting_last_version)));
                tvLastVersion.setText(String.format(getString(R.string.main_last_version), getString(R.string.main_error_getting_last_version)));
            } else {
                tvLastVersion.setText(String.format(getString(R.string.main_last_version), lastVersion));
                if (localVersion.equals(lastVersion)){
                    cardUpdate.setCardBackgroundColor(getResources().getColor(R.color.updatedGreen));
                    tvUpdateStatus.setText(R.string.main_you_are_up_to_date);
                    imgUpdateStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud_check));
                } else {
                    cardUpdate.setCardBackgroundColor(getResources().getColor(R.color.newVersionRed));
                    tvUpdateStatus.setText(String.format(getString(R.string.main_update_avaiable), lastVersion));
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

    /*public int createID(){
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
    }*/
}
