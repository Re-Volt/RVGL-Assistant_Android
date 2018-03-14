package io.github.tavisco.rvglassistant.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.items.TrackItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TracksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TracksFragment extends Fragment {

    //our rv
    RecyclerView mRecyclerView;
    //save our FastAdapter
    private FastAdapter<TrackItem> mFastAdapter;
    //save our FastAdapter
    private ItemAdapter<TrackItem> mItemAdapter;

    public TracksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TracksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TracksFragment newInstance() {
        TracksFragment fragment = new TracksFragment();
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
        mFastAdapter.withSelectable(true);
        mFastAdapter.withMultiSelect(true);
        mFastAdapter.withSelectOnLongClick(false);

        //configure our fastAdapter
        //get our recyclerView and do basic setup
        mRecyclerView = getView().findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        mRecyclerView.setAdapter(mFastAdapter);
//        mRecyclerView.setItemAnimator(new SlideDownAlphaAnimator());
//        mRecyclerView.getItemAnimator().setAddDuration(500);
//        mRecyclerView.getItemAnimator().setRemoveDuration(500);

        List<TrackItem> itens = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            TrackItem item = new TrackItem();
            item.withName("Super Market " + i);
            item.withDiscription("607M");
            itens.add(item);
        }

        mItemAdapter.add(itens);
    }

}
