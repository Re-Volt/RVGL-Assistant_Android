package io.github.tavisco.rvglassistant;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import io.github.tavisco.rvglassistant.fragments.CarsFragment;
import io.github.tavisco.rvglassistant.fragments.MainFragment;
import io.github.tavisco.rvglassistant.fragments.TracksFragment;

public class MainActivity extends AppCompatActivity {

    //https://github.com/mikepenz/FastAdapter

    private BottomNavigationView mBottomNav = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectFragment(item);
                    return true;
                case R.id.navigation_tracks:
                    selectFragment(item);
                    return true;
                case R.id.navigation_cars:
                    selectFragment(item);
                    return true;
            }
            return false;
        }
    };

    private void selectFragment(MenuItem item) {
        Fragment frag = null;
        // init corresponding fragment
        switch (item.getItemId()){
            case R.id.navigation_home:
                frag = MainFragment.newInstance();
                break;
            case R.id.navigation_tracks:
                frag = TracksFragment.newInstance();
                break;
            case R.id.navigation_cars:
                frag = CarsFragment.newInstance();
                break;
        }

        int mSelectedItem = item.getItemId();

        for (int i = 0; i < mBottomNav.getMenu().size(); i++){
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == mSelectedItem);
        }

        if (frag != null) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, frag, frag.getTag());
            ft.commit();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNav = findViewById(R.id.navigation);
        mBottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment frag = MainFragment.newInstance();

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, frag, frag.getTag());
        ft.commit();
    }

}
