package io.github.tavisco.rvglassistant;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import io.github.tavisco.rvglassistant.fragments.CarsFragment;
import io.github.tavisco.rvglassistant.fragments.MainFragment;
import io.github.tavisco.rvglassistant.fragments.LevelsFragment;
import io.github.tavisco.rvglassistant.objects.Constants;

public class MainActivity extends AppCompatActivity {

    //This is the main activity
    //the only thing that is handled here
    //is the navbar.
    //
    //The remaining stuff about the main screen
    //is located at fragments/MainFragment.java

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
                frag = LevelsFragment.newInstance();
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

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
