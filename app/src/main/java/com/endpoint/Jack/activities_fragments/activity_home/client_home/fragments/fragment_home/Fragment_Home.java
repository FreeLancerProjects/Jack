package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.preferences.Preferences;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Fragment_Home extends Fragment {

    private ClientHomeActivity activity;
    private AHBottomNavigation ah_bottom_nav;
    private BottomNavigationView bottomNavigationView;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private Preferences preferences;
    private LinearLayout ll_progress, ll_container;
    private ProgressBar progBar;

    public static Fragment_Home newInstance() {
        return new Fragment_Home();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        setimage();
        return view;
    }

    public void setimage() {
        userModel = preferences.getUserData(activity);
        if (userModel != null) {
            bottomNavigationView.setItemIconTintList(null); // this is important
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bottomNavigationView.getMenu().getItem(4).setIconTintList(null);
                bottomNavigationView.getMenu().getItem(4).setIconTintMode(null);
            }
            Log.e("lflgllg", userModel.getData().getUser_image());
            Glide.with(getApplicationContext()).asBitmap().load(Tags.IMAGE_URL + userModel.getData().getUser_image())
                    .apply(RequestOptions.circleCropTransform()).into(new SimpleTarget<Bitmap>() {

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    // Log.e("lflgllg,";fllflf");

                    Drawable profileImage = new BitmapDrawable(getResources(), resource);
                    bottomNavigationView.getMenu().findItem(R.id.profile).setIcon(profileImage);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {


                    bottomNavigationView.getMenu().findItem(R.id.profile).setIcon(R.drawable.ic_nav_user);


                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    bottomNavigationView.getMenu().findItem(R.id.profile).setIcon(R.drawable.ic_nav_user);

                }
            });
        }
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);

        final View iconView =
                menuView.getChildAt(4).findViewById(com.google.android.material.R.id.icon);
        final ViewGroup.LayoutParams layoutParams =
                iconView.getLayoutParams();
        final DisplayMetrics displayMetrics =
                getResources().getDisplayMetrics();
        layoutParams.height = (int)
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45,
                        displayMetrics);
        layoutParams.width = (int)
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45,
                        displayMetrics);
        iconView.setLayoutParams(layoutParams);
    }

    @Override
    public void onResume() {
        super.onResume();
        setimage();
    }

    @Override
    public void onStart() {
        super.onStart();
        setimage();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setimage();
    }

    private void initView(View view) {
        activity = (ClientHomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userSingleTone = UserSingleTone.getInstance();
        String session = preferences.getSession(activity);
        if (session.equals(Tags.session_login)) {
            userSingleTone.setUserModel(preferences.getUserData(activity));
        }
        userModel = userSingleTone.getUserModel();
        ah_bottom_nav = view.findViewById(R.id.ah_bottom_nav);
        bottomNavigationView = view.findViewById(R.id.bottom_nav);
        ll_container = view.findViewById(R.id.ll_container);
        ll_progress = view.findViewById(R.id.ll_progress);
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.store:
                        activity.DisplayFragmentStore();

                        break;

                    case R.id.order:
                        if (userModel == null) {
                            Common.CreateUserNotSignInAlertDialog(activity);

                        } else {

                            activity.DisplayFragmentMyOrders();
                            return true;

                        }


                        break;
                    case R.id.naoti:

                        if (userModel == null) {
                            Common.CreateUserNotSignInAlertDialog(activity);

                        } else {
                            activity.DisplayFragmentNotification();
                            return true;

                        }


                        break;
//                    case R.id.blog:
//
//                            activity.DisplayFragmentBlog();
//
//                        break;
                    case R.id.profile:
                        if (userModel == null) {
                            Common.CreateUserNotSignInAlertDialog(activity);
                        } else {

                            activity.DisplayFragmentProfile();
                            return true;

                        }
                        break;
                }

                return true;
            }
        });
        setUpBottomNavigation();
        ah_bottom_nav.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        activity.DisplayFragmentStore();
                        break;
                    case 1:
                        if (userModel == null) {
                            Common.CreateUserNotSignInAlertDialog(activity);
                        } else {
                            activity.DisplayFragmentShipment();

                        }
                        break;
                    case 2:
                        if (userModel == null) {
                            Common.CreateUserNotSignInAlertDialog(activity);
                        } else {
                            activity.DisplayFragmentMyOrders();

                        }


                        break;
                    case 3:

                        if (userModel == null) {
                            Common.CreateUserNotSignInAlertDialog(activity);
                        } else {
                            activity.DisplayFragmentNotification();

                        }


                        break;
//                    case 4:
//
//                            activity.DisplayFragmentBlog();
//
//                        break;
                    case 4:
                        if (userModel == null) {
                            Common.CreateUserNotSignInAlertDialog(activity);
                        } else {
                            activity.DisplayFragmentProfile();

                        }
                        break;
                }
                return false;
            }
        });

    }

    private void setUpBottomNavigation() {

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getString(R.string.stores), R.drawable.ic_nav_store);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getString(R.string.shipment), R.drawable.ic_box);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getString(R.string.my_orders), R.drawable.ic_nav_order);
       // AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.notifications), R.drawable.ic_nav_notification);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.profile), R.drawable.ic_nav_user);

        ah_bottom_nav.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        ah_bottom_nav.setDefaultBackgroundColor(ContextCompat.getColor(activity, R.color.white));
        ah_bottom_nav.setTitleTextSizeInSp(14, 12);
        ah_bottom_nav.setForceTint(true);
        ah_bottom_nav.setAccentColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        ah_bottom_nav.setInactiveColor(ContextCompat.getColor(activity, R.color.gray4));
        ah_bottom_nav.addItem(item1);
        ah_bottom_nav.addItem(item2);
        ah_bottom_nav.addItem(item3);
        ah_bottom_nav.addItem(item4);
      //  ah_bottom_nav.addItem(item5);

    }

    public void updateBottomNavigationPosition(int pos) {
        ah_bottom_nav.setCurrentItem(pos, false);
    }

    public void updateNotificationCount(int count) {
        AHNotification.Builder builder = new AHNotification.Builder();
        builder.setTextColor(ContextCompat.getColor(activity, R.color.white));
        builder.setBackgroundColor(ContextCompat.getColor(activity, R.color.golden_stars));
        if (count > 0) {
            builder.setText(count + "");
            ah_bottom_nav.setNotification(builder.build(), 3);
        } else {
            builder.setText("");
            ah_bottom_nav.setNotification(builder.build(), 3);
        }
    }

    public void DisplayFragmentView() {
        ll_progress.setVisibility(View.GONE);
        ll_container.setVisibility(View.VISIBLE);
        ah_bottom_nav.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }
}
