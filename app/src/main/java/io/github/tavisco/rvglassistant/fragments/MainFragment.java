package io.github.tavisco.rvglassistant.fragments;

import android.Manifest;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

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
import io.github.tavisco.rvglassistant.objects.enums.UpdateStatus;
import io.github.tavisco.rvglassistant.others.Constants;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import io.github.tavisco.rvglassistant.BuildConfig;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@RuntimePermissions
public class MainFragment extends Fragment {

    // =-=-=-= Bindings =-=-=-=
    @BindView(R.id.card_main_updateStatus)
    CardView cardUpdate;
    @BindView(R.id.tv_main_game_installedVersion)
    TextView tvInstalledVersion;
    @BindView(R.id.tv_main_game_lastVersion)
    TextView tvLastVersion;
    @BindView(R.id.tv_main_updateStatus)
    TextView tvUpdateStatus;
    @BindView(R.id.img_main_updateStatus)
    ImageView imgUpdateStatus;
    @BindView(R.id.recycler_main_packages)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_main_app_installedVersion)
    TextView tvAppInstalledVersion;
    @BindView(R.id.tv_main_app_lastVersion)
    TextView tvAppLastVersion;
    @BindView(R.id.card_main_appUpdateVersions)
    CardView cardAppUpdateVersions;


    // =-=-=-= Recycler =-=-=-=
    @SuppressWarnings("FieldCanBeLocal")
    private FastAdapter<IOPackageViewItem> mFastAdapter;
    private ItemAdapter<IOPackageViewItem> mItemAdapter;


    // =-=-=-= Items/Variables =-=-=-=
    UpdateStatus gameUpdateStatus = UpdateStatus.UNKNOWN;
    UpdateStatus appUpdateStatus = UpdateStatus.UNKNOWN;


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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getOnlineData();
    }

    private void getOnlineData(){
        // checkForGameUpdates();
        MainFragmentPermissionsDispatcher.checkForGameUpdatesWithPermissionCheck(this);
        // createPackagesList();
        MainFragmentPermissionsDispatcher.createPackagesListWithPermissionCheck(this);

        checkForAppUpdates();
    }

    private void checkForAppUpdates() {
        tvAppInstalledVersion.setText(String.format(getString(R.string.main_installed_version),
                BuildConfig.VERSION_NAME));

        Context ctx = this.getContext();

        if (ctx != null) {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this.getContext());

            // Request a string response from the rvioRequest URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.APP_LAST_VERSION_LINK,
                    response -> {
                        if (!response.isEmpty()){
                            String appLastVersion = response.substring(0, 8);
                            tvAppLastVersion.setText(String.format(getString(R.string.main_app_last_version), appLastVersion));

                            if (BuildConfig.DEBUG) {
                                appUpdateStatus = UpdateStatus.UNKNOWN;
                                cardAppUpdateVersions.setCardBackgroundColor(ctx.getResources().getColor(R.color.primary_dark));
                                return;
                            }

                            if (appLastVersion.equals(BuildConfig.VERSION_NAME)){
                                appUpdateStatus = UpdateStatus.UPDATED;
                            } else {
                                appUpdateStatus = UpdateStatus.UPDATE_AVAIABLE;
                                cardAppUpdateVersions.setCardBackgroundColor(ctx.getResources().getColor(R.color.newVersionRed));
                                new MaterialDialog.Builder(ctx)
                                        .title("Update avaiable!")
                                        .content("There's a new version of the APP avaiable!\nDownload now?")
                                        .positiveText(R.string.yes)
                                        .negativeText(R.string.no)
                                        .onPositive((dialog, which) -> clickCardAppUpdate())
                                        .show();
                            }
                        }
                    },
                    error -> {
                        tvAppLastVersion.setText(String.format(getString(R.string.main_app_last_version), getString(R.string.main_error_getting_last_version)));
                        appUpdateStatus = UpdateStatus.ERROR;
                        Log.d(Constants.TAG, error.getLocalizedMessage());
                    }
                );

            // Add the request to the RequestQueue.
            stringRequest.setShouldCache(false);
            queue.add(stringRequest);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void createPackagesList() {
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

        populateRecycler();

        //configure our fastAdapter
        mFastAdapter.withOnClickListener((v, adapter, item, position) -> {
            if (v != null) {
                item.getPackageItem().install(this);
            }
            return false;
        });
    }

    public void populateRecycler() {
        mItemAdapter.clear();

        Context ctx = this.getContext();

        if (ctx != null){
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this.getContext());

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.RVIO_AVAIABLE_PACKAGES_LINK,
                    response -> {
                        if (!response.isEmpty()){
                            // Split on new lines
                            String rvioPackages[] = response.split("\\r?\\n");
                            for (String rvioPack : rvioPackages) {
                                mItemAdapter.add(new IOPackageViewItem(rvioPack));
                            }
                        }
                    }, error -> Log.d(Constants.TAG, error.getLocalizedMessage()));

            // Add the request to the RequestQueue.
            stringRequest.setShouldCache(false);
            queue.add(stringRequest);
        }
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void checkForGameUpdates(){
        final String localVersion = getLocalGameVersion();

        if (localVersion.equals("-1")) {
            tvUpdateStatus.setText(R.string.main_is_game_installed);
            tvInstalledVersion.setText(String.format(getString(R.string.main_installed_version), "---"));
        } else {
            tvInstalledVersion.setText(String.format(getString(R.string.main_installed_version), localVersion));
        }

        Activity activity = getActivity();
        if(activity != null){
            cardUpdate.setCardBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
            tvUpdateStatus.setText(R.string.main_checking_updates);
            tvLastVersion.setText(String.format(getString(R.string.main_game_last_version), getString(R.string.main_checking)));
            imgUpdateStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud_sync));

            Context ctx = this.getContext();

            if (ctx != null){
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(ctx);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.RVGL_LAST_VERSION_LINK,
                        response -> {
                            // Need to substring the version to not get garbage
                            compareWithLocalVersion(localVersion, response.substring(0, 7));
                        }, error -> {
                            tvLastVersion.setText(String.format(getString(R.string.main_game_last_version), getString(R.string.main_error_getting_last_version)));
                            tvUpdateStatus.setText(getString(R.string.main_connection_error));
                            imgUpdateStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud_alert));
                            gameUpdateStatus = UpdateStatus.ERROR;
                        }
                );

                // Add the request to the RequestQueue.
                stringRequest.setShouldCache(false);
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

        return localVersion;
    }

    public void compareWithLocalVersion(String localVersion, String lastVersion){
        Activity activity = getActivity();
        if (activity != null){
            if (lastVersion.equals("-1")){
                tvLastVersion.setText(String.format(getString(R.string.main_game_last_version), getString(R.string.main_error_getting_last_version)));
                gameUpdateStatus = UpdateStatus.ERROR;
            } else {
                tvLastVersion.setText(String.format(getString(R.string.main_game_last_version), lastVersion));
            }

            if (!localVersion.equals("-1") && !lastVersion.equals("-1")){
                if (localVersion.equals(lastVersion)){
                    cardUpdate.setCardBackgroundColor(getResources().getColor(R.color.updatedGreen));
                    tvUpdateStatus.setText(R.string.main_you_are_up_to_date);
                    imgUpdateStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud_check));
                    gameUpdateStatus = UpdateStatus.UPDATED;
                } else {
                    cardUpdate.setCardBackgroundColor(getResources().getColor(R.color.newVersionRed));
                    tvUpdateStatus.setText(String.format(getString(R.string.main_update_avaiable), lastVersion));
                    imgUpdateStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud_download));
                    gameUpdateStatus = UpdateStatus.UPDATE_AVAIABLE;
                }
            }
        }
    }

    @OnClick(R.id.card_main_updateStatus)
    public void clickCardGameUpdate(){
        if (gameUpdateStatus == UpdateStatus.UPDATE_AVAIABLE){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.RVGL_ANDROID_APK_LINK));
            startActivity(browserIntent);
        } else {
            getOnlineData();
        }
    }

    @OnClick(R.id.card_main_appUpdateVersions)
    public void clickCardAppUpdate(){
        if (appUpdateStatus == UpdateStatus.UPDATE_AVAIABLE){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_RELEASES_LINK));
            startActivity(browserIntent);
        } else {
            getOnlineData();
        }
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onStorageDenied() {
        Context ctx = getContext();

        if (ctx != null){
            new MaterialDialog.Builder(ctx)
                    .title(R.string.storage_permission_denied_dialog_title)
                    .content(R.string.storage_permission_denied_dialog_feedback)
                    .positiveText(android.R.string.ok)
                    .show();
        }
    }
}
