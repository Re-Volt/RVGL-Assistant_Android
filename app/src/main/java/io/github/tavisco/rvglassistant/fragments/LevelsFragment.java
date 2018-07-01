package io.github.tavisco.rvglassistant.fragments;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.Arrays;

import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.TrackInfoActivity;
import io.github.tavisco.rvglassistant.objects.adapters.LevelViewItem;
import io.github.tavisco.rvglassistant.utils.FindLevels;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LevelsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LevelsFragment extends Fragment {

    //our rv
    RecyclerView mRecyclerView;
    //save our FastAdapter
    private FastAdapter<LevelViewItem> mFastAdapter;
    //save our FastAdapter
    private ItemAdapter<LevelViewItem> mItemAdapter;

    public LevelsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LevelsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LevelsFragment newInstance() {
        LevelsFragment fragment = new LevelsFragment();
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
        return inflater.inflate(R.layout.fragment_tracks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //create our ItemAdapter which will host our items
        mItemAdapter = new ItemAdapter<>();

        //create our FastAdapter which will manage everything
        mFastAdapter = FastAdapter.with(Arrays.asList(mItemAdapter));
        mFastAdapter.withSelectOnLongClick(false);

        //configure our fastAdapter
        //get our recyclerView and do basic setup
        mRecyclerView = getView().findViewById(R.id.rvTracks);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        mRecyclerView.setAdapter(mFastAdapter);

        FindLevels.getAllLevels(mItemAdapter);

        //configure our fastAdapter
        mFastAdapter.withOnClickListener(new OnClickListener<LevelViewItem>() {
            @Override
            public boolean onClick(View v, IAdapter<LevelViewItem> adapter, @NonNull LevelViewItem item, int position) {
                Intent intent = new Intent(getActivity(), TrackInfoActivity.class);
                intent.putExtra("levelViewItem", (new Gson()).toJson(item));

                String transitionName = "cover";

                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), v, transitionName);
                startActivity(intent, transitionActivityOptions.toBundle());

                return false;
            }
        });
    }

}
