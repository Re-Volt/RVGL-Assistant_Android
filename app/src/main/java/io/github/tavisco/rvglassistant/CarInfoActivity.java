package io.github.tavisco.rvglassistant;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;

import io.github.tavisco.rvglassistant.objects.BaseItem;
import io.github.tavisco.rvglassistant.objects.CarItem;
import io.github.tavisco.rvglassistant.objects.LevelItem;
import io.github.tavisco.rvglassistant.objects.enums.ItemType;
import io.github.tavisco.rvglassistant.others.Constants;
import io.github.tavisco.rvglassistant.others.CustomAnimatorListener;
import io.github.tavisco.rvglassistant.others.CustomTransitionListener;
import io.github.tavisco.rvglassistant.others.Others;
import io.github.tavisco.rvglassistant.utils.Animations;
import io.github.tavisco.rvglassistant.utils.ImageLoader;

//https://github.com/AnyChart/AnyChart-Android
public class CarInfoActivity extends AppCompatActivity {
    BaseItem baseItem;
    CarItem carItem;
    LevelItem levelItem;
    ItemType itemType;

    private String imgPath = "";

    private static final int ANIMATION_DURATION_SHORT = 150;
    private static final int ANIMATION_DURATION_MEDIUM = 300;
    private View mTitleContainer;
    private View mTitlesContainer;

    private ImageView mFabButton;
    private ImageView mFabShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        // Title container
        mTitleContainer = findViewById(R.id.activity_detail_title_container);
        Animations.configuredHideYView(mTitleContainer);

        mTitlesContainer = findViewById(R.id.activity_detail_titles);

        // Define toolbar as the shared element
        final Toolbar toolbar = findViewById(R.id.activity_detail_toolbar);
        setSupportActionBar(toolbar);
        //override text
        setTitle("");

        // Fab button
        mFabButton = findViewById(R.id.activity_detail_fab);
        mFabButton.setScaleX(0);
        mFabButton.setScaleY(0);
        mFabButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_chart_bar));
        mFabButton.setOnClickListener(onFabButtonListener);

        // Fab share button
        mFabShareButton = findViewById(R.id.activity_detail_fab_share);
        mFabShareButton.setScaleX(0);
        mFabShareButton.setScaleY(0);
        mFabShareButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_share_variant));
        mFabShareButton.setOnClickListener(onFabShareButtonListener);

        Intent intent = getIntent();
        String jsonItem = intent.getStringExtra("itemJson");
        itemType = (ItemType) intent.getSerializableExtra("itemType");
        if (itemType == ItemType.LEVEL){
            levelItem = new Gson().fromJson(jsonItem, LevelItem.class);
            imgPath = levelItem.getImagePath();
            baseItem = levelItem;
        } else if (itemType == ItemType.CAR){
            carItem = new Gson().fromJson(jsonItem, CarItem.class);
            imgPath = carItem.getImagePath();
            baseItem = carItem;
        }

        //get the imageHeader and set the coverImage
        final ImageView image = findViewById(R.id.activity_detail_image);

        image.setMinimumWidth(600);

        //Load image
        Bitmap carImg = ImageLoader.loadCarImage(this.getBaseContext(), carItem, image);

        if (Build.VERSION.SDK_INT >= 21) {
            image.setTransitionName("cover");
            // Add a listener to get noticed when the transition ends to animate the fab button
            getWindow().getSharedElementEnterTransition().addListener(new CustomTransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    animateActivityStart();
                }
            });
        } else {
            Animations.showViewByScale(image).setDuration(ANIMATION_DURATION_MEDIUM).start();
            animateActivityStart();
        }


        // Generate palette colors
        Palette palette;
        if (carImg != null) {
            palette = Palette.from(carImg).generate();

            Palette.Swatch s = palette.getDominantSwatch();
            if (s == null) {
                s = palette.getDarkVibrantSwatch();
            }
            if (s == null) {
                s = palette.getLightVibrantSwatch();
            }
            if (s == null) {
                s = palette.getMutedSwatch();
            }

            if (s != null) {
                setColors(s.getTitleTextColor(), s.getRgb());
            }
        }

        findViewById(R.id.container).setOnClickListener(v -> onBackPressed());

    }

    /**
     * animate the start of the activity
     */
    private void animateActivityStart() {
        ViewPropertyAnimator showTitleAnimator = Animations.showViewByScale(mTitleContainer);
        showTitleAnimator.setListener(new CustomAnimatorListener() {

            @Override
            public void onAnimationEnd(Animator animation) {

                super.onAnimationEnd(animation);
                mTitlesContainer.startAnimation(AnimationUtils.loadAnimation(CarInfoActivity.this, R.anim.alpha_on));
                mTitlesContainer.setVisibility(View.VISIBLE);

                //animate the fab
                Animations.showViewByScale(mFabButton).setDuration(ANIMATION_DURATION_SHORT).start();

                //animate the share fab
                Animations.showViewByScale(mFabShareButton)
                        .setDuration(ANIMATION_DURATION_SHORT * 2)
                        .start();
                mFabShareButton.animate()
                        .translationX((-1) * Others.pxFromDp(CarInfoActivity.this, 58))
                        .setStartDelay(ANIMATION_DURATION_SHORT)
                        .setDuration(ANIMATION_DURATION_SHORT)
                        .start();

            }
        });

        showTitleAnimator.start();
    }

    private View.OnClickListener onFabShareButtonListener = v -> Log.d(Constants.TAG, "Share!");

    private View.OnClickListener onFabButtonListener = v -> Log.d(Constants.TAG, "Outro!");
    /**
     * @param titleTextColor
     * @param rgb
     */
    private void setColors(int titleTextColor, int rgb) {
        mTitleContainer.setBackgroundColor(rgb);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(titleTextColor);
        }

        TextView titleTV = mTitleContainer.findViewById(R.id.activity_detail_title);
        titleTV.setTextColor(titleTextColor);
        titleTV.setText(baseItem.getName());

        TextView subtitleTV = mTitleContainer.findViewById(R.id.activity_detail_subtitle);
        subtitleTV.setTextColor(titleTextColor);

        subtitleTV.setText(String.format("It's a %s", itemType.getTypeText()));

        ((TextView) mTitleContainer.findViewById(R.id.activity_detail_subtitle))
                .setTextColor(titleTextColor);
    }


    @Override
    public void onBackPressed() {

        //move the share fab below the normal fab (58 because this is the margin top + the half
        mFabShareButton.animate()
                .translationX(0)
                .setDuration(ANIMATION_DURATION_SHORT)
                .setListener(animationFinishListener1)
                .start();
    }

    private CustomAnimatorListener animationFinishListener1 = new CustomAnimatorListener() {
        private int animateFinish1 = 0;

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            process();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            super.onAnimationCancel(animation);
            process();
        }

        private void process() {
            animateFinish1 = animateFinish1 + 1;
            if (animateFinish1 >= 1) {
                //create the fab animation and hide fabProgress animation, set an delay so those will hide after the shareFab is below the main fab
                Animations.hideViewByScaleXY(mFabShareButton)
                        .setDuration(ANIMATION_DURATION_SHORT)
                        .setListener(animationFinishListener2)
                        .start();
                Animations.hideViewByScaleXY(mFabButton)
                        .setDuration(ANIMATION_DURATION_SHORT)
                        .setListener(animationFinishListener2)
                        .start();
            }
        }
    };

    private CustomAnimatorListener animationFinishListener2 = new CustomAnimatorListener() {
        private int animateFinish2 = 0;

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            process();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            super.onAnimationCancel(animation);
            process();
        }

        private void process() {
            animateFinish2 = animateFinish2 + 1;
            if (animateFinish2 >= 2) {
                ViewPropertyAnimator hideFabAnimator = Animations.hideViewByScaleY(mTitleContainer);
                hideFabAnimator.setListener(new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        coolBack();
                    }
                });
            }
        }
    };

    /**
     *
     */
    private void coolBack() {
        try {
            super.onBackPressed();
        } catch (Exception e) {
            // ew;
        }
    }
}
