package io.github.tavisco.rvglassistant.fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.Arrays;

import io.github.tavisco.rvglassistant.CarInfoActivity;
import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.objects.adapters.CarViewItem;
import io.github.tavisco.rvglassistant.utils.FindCars;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarsFragment extends Fragment {

    //our rv
    RecyclerView mRecyclerView;
    //save our FastAdapter
    private FastAdapter<CarViewItem> mFastAdapter;
    //save our FastAdapter
    private ItemAdapter<CarViewItem> mItemAdapter;

    public CarsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CarsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarsFragment newInstance() {
        CarsFragment fragment = new CarsFragment();
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
        return inflater.inflate(R.layout.fragment_cars, container, false);
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
        mRecyclerView = getView().findViewById(R.id.rvCars);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        mRecyclerView.setAdapter(mFastAdapter);

        FindCars.getAllCars(mItemAdapter);

        //configure our fastAdapter
        mFastAdapter.withOnClickListener(new OnClickListener<CarViewItem>() {
            @Override
            public boolean onClick(View v, IAdapter<CarViewItem> adapter, @NonNull CarViewItem item, int position) {
                Intent intent = new Intent(getActivity(), CarInfoActivity.class);
                intent.putExtra("carViewItem", (new Gson()).toJson(item));


                ImageView coverImage = v.findViewById(R.id.track_img);
                if (coverImage == null) {
                    coverImage = ((View) v.getParent()).findViewById(R.id.track_img);
                }

                if (Build.VERSION.SDK_INT >= 21) {
                    if (coverImage.getParent() != null) {
                        ((ViewGroup) coverImage.getParent()).setTransitionGroup(false);
                    }
                }

                // Setup the transition to the detail activity
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), coverImage, "cover");
                startActivity(intent, options.toBundle());

                return false;
            }
        });
    }

}
