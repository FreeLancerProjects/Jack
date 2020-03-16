package com.endpoint.Jack.activities_fragments.activity_home.client_home.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_chat.ChatActivity;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Add_Coupon;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Bank_Account;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Delegate_Offer;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Notifications;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Order_Details;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Profile;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Store;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Delegate_Add_Offer;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Delegate_Comments;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Delegate_Current_Order_Details;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Delegate_Register;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Delegates;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Delegates_Result;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Documentation_Data;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Edit_Profile;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Explain_Courier;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Home;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Map;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Map_Follow_Order;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Map_Location_Details;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Reserve_Order;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Search;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Settings;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Shipment;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_orders.Fragment_Client_Orders;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_store_details.Fragment_Store_Details;
import com.endpoint.Jack.activities_fragments.activity_sign_in.activity.SignInActivity;
import com.endpoint.Jack.activities_fragments.activity_sign_in.fragments.Fragment_Phone;
import com.endpoint.Jack.activities_fragments.terms_conditions.TermsConditionsActivity;
import com.endpoint.Jack.language.Language_Helper;
import com.endpoint.Jack.models.BeDriverModel;
import com.endpoint.Jack.models.ChatUserModel;
import com.endpoint.Jack.models.Favourite_location;
import com.endpoint.Jack.models.FollowModel;
import com.endpoint.Jack.models.LocationError;
import com.endpoint.Jack.models.NotStateModel;
import com.endpoint.Jack.models.NotificationCountModel;
import com.endpoint.Jack.models.NotificationModel;
import com.endpoint.Jack.models.NotificationTypeModel;
import com.endpoint.Jack.models.OrderDataModel;
import com.endpoint.Jack.models.PlaceModel;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.preferences.Preferences;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientHomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,LocationListener {
    private final String gps_perm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int gps_req = 22;
    private FragmentManager fragmentManager;
    private Fragment_Home fragment_home;
    private Fragment_Client_Store fragment_client_store;
    private Fragment_Client_Orders fragment_client_orders;
    private Fragment_Client_Notifications fragment_client_notifications;
    private Fragment_Client_Profile fragment_client_profile;
    private Fragment_Store_Details fragment_store_details;
    private Fragment_Shipment fragment_shipment;
    private Fragment_Reserve_Order fragment_reserve_order;
    private Fragment_Search fragment_search;
    private Fragment_Settings fragment_settings;
    private Fragment_Edit_Profile fragment_edit_profile;
    private Fragment_Delegate_Register fragment_delegate_register;
    private Fragment_Map fragment_map;
    private Fragment_Delegates fragment_delegates;
    private Fragment_Phone fragment_phone;
    private Fragment_Delegate_Add_Offer fragment_delegate_add_offer;
    private Fragment_Client_Order_Details fragment_client_order_details;
    private Fragment_Client_Delegate_Offer fragment_client_delegate_offer;
    private Fragment_Delegate_Comments fragment_delegate_comments;
    private Fragment_Delegate_Current_Order_Details fragment_delegate_current_order_details;
    private Fragment_Map_Location_Details fragment_map_location_details;
    private Fragment_Add_Coupon fragment_add_coupon;
    private Fragment_Explain_Courier fragment_explain_courier;
    private Fragment_Documentation_Data fragment_documentation_data;
    private Fragment_Delegates_Result fragment_delegates_result;
    private Fragment_Map_Follow_Order fragment_map_follow_order;
    private Fragment_Bank_Account fragment_bank_account;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private Preferences preferences;
    //private Intent intentService;
    public  Location location = null;
    private String current_lang;
    private int fragment_count = 0;
    private boolean canRead = false;
    private Call<ResponseBody> call;
    private String state = "";
    private boolean canUpdateLocation = true;
    private double rate=0.0;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private  LocationCallback  locationCallback;



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Language_Helper.updateResources(base,Language_Helper.getLanguage(base)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);


        initView();

        if (savedInstanceState == null) {
            DisplayFragmentHome();

            CheckPermission();
        }
        getDataFromIntent();

    }

    private void getDataFromIntent()
    {
        Intent intent = getIntent();
        if (intent!=null)
        {
            try {
                if (intent.hasExtra("status"))
                {
                    String status = intent.getStringExtra("status");

                    Log.e("status",status+"");
                    if (status.equals(String.valueOf(Tags.STATE_ORDER_NEW)))
                    {

                        DisplayFragmentMyOrders();

                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        fragment_client_orders.NavigateToFragmentRefresh(0);
                                    }
                                },1000);

                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        manager.cancelAll();
                                    }
                                },1);
                    }else if (status.equals(String.valueOf(Tags.STATE_DELEGATE_SEND_OFFER)))
                    {
                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        DisplayFragmentNotification();

                                    }
                                },1000);
                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        manager.cancelAll();
                                    }
                                },1);
                    }else if (status.equals(String.valueOf(Tags.STATE_DELEGATE_REFUSE_ORDER)))
                    {
                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        DisplayFragmentNotification();

                                    }
                                },1000);

                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        manager.cancelAll();
                                    }
                                },1);

                    }else if (status.equals(String.valueOf(Tags.STATE_CLIENT_ACCEPT_OFFER)))
                    {

                        DisplayFragmentMyOrders();

                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        fragment_client_orders.NavigateToFragmentRefresh(1);

                                    }
                                },1000);

                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        manager.cancelAll();
                                    }
                                },1);


                    }else if (status.equals(String.valueOf(Tags.STATE_CLIENT_REFUSE_OFFER)))
                    {
                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        DisplayFragmentNotification();

                                    }
                                },1000);

                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        manager.cancelAll();
                                    }
                                },1);
                    }
                    else if (status.equals(String.valueOf(Tags.STATE_DELEGATE_COLLECTING_ORDER)) || status.equals(String.valueOf(Tags.STATE_DELEGATE_COLLECTED_ORDER)) || status.equals(String.valueOf(Tags.STATE_DELEGATE_DELIVERING_ORDER)))
                    {
                        DisplayFragmentMyOrders();

                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        fragment_client_orders.NavigateToFragmentRefresh(1);

                                    }
                                },1000);

                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        manager.cancelAll();
                                    }
                                },1);
                    }else if (status.equals(String.valueOf(Tags.STATE_DELEGATE_DELIVERED_ORDER)))
                    {
                        DisplayFragmentMyOrders();

                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        fragment_client_orders.NavigateToFragmentRefresh(2);

                                    }
                                },1000);

                        new Handler()
                                .postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        manager.cancelAll();
                                    }
                                },1);
                    }
                }
            }catch (Exception e)
            {
                Log.e("Exception",e.getMessage()+"_");
            }

        }
    }

    private void initView()
    {
        Paper.init(this);
        current_lang = Paper.book().read("lang", Locale.getDefault().getLanguage());

        preferences = Preferences.getInstance();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();

        fragmentManager = getSupportFragmentManager();

        if (userModel!=null)
        {
            updateToken();
            getNotificationCount();
            EventBus.getDefault().register(this);
        }

        String visitTime = preferences.getVisitTime(this);
        Calendar calendar = Calendar.getInstance();
        long timeNow = calendar.getTimeInMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
        String date = dateFormat.format(new Date(timeNow));

        if (!date.equals(visitTime))
        {
            addVisit(date);
        }



    }
    private void updateToken()
    {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful())
                        {
                            String token = task.getResult().getToken();
                            Api.getService(Tags.base_url)
                                    .updateToken(userModel.getData().getUser_id(),token)
                                    .enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                            if (response.isSuccessful())
                                            {
                                                Log.e("Success","token updated");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            try {
                                                Log.e("Error",t.getMessage());
                                            }catch (Exception e){}
                                        }
                                    });
                        }
                    }
                });
    }

    private void initGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }
    ///////////////////////////////////
   /* private void LocationListener(final Location location)
    {

        if (location!=null)
        {
            if (userModel!=null)
            {
                UpdateUserLocation(location);
            }
            ClientHomeActivity.this.location = location;
        }
        if (fragment_client_store!=null&&fragment_client_store.isAdded())
        {
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (canUpdateLocation)
                            {
                                canUpdateLocation = false;
                                fragment_client_store.getNearbyPlaces(location,"restaurant");

                            }
                            *//*if (intentService!=null)
                            {
                                stopService(intentService);
                            }*//*
                        }
                    },1);
        }
    }*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LocationErrorListener(final LocationError locationError)
    {
        /*stopService(intentService);
        if (locationError.getStatus()==0)
        {
            StartService(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }else if (locationError.getStatus()==1)
        {
            StartService(LocationRequest.PRIORITY_LOW_POWER);

        }*/
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenNotificationChange(final NotStateModel notStateModel)
    {

        if (fragment_client_order_details!=null&&fragment_client_order_details.isAdded())
        {
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment_client_order_details.updateStepView(Integer.parseInt(notStateModel.getNotification_state()));
                        }
                    },1);
        }

        canRead =true;
        RefreshFragment_Notification();
        getNotificationCount();
        RefreshFragment_Order();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenNotificationRate(NotificationTypeModel notificationTypeModel)
    {
        if (userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE))
        {
            getUserDataById(userModel.getData().getUser_id());

        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenNotificationBeDriver(BeDriverModel beDriverModel)
    {

        if (beDriverModel.getAction_status().equals("2"))
        {
            getUserDataById(userModel.getData().getUser_id());
            
        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenNotificationDriverUpdate(final FollowModel followModel)
    {

        Log.e("ssss",followModel.getDriver_lat()+"__");
       if (fragment_map_follow_order!=null&&fragment_map_follow_order.isAdded())
       {
           new Handler()
                   .postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           fragment_map_follow_order.getFollowData();
                       }
                   },10);
       }


    }

    private void getUserDataById(String user_id)
    {
        Api.getService(Tags.base_url)
                .getUserDataById(user_id)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        if (response.isSuccessful()&&response.body()!=null)
                        {
                            updateUserData(response.body());

                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        try {
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    //////////////////////////////////////////////////
    private void UpdateUserLocation(Location location)
    {
        Api.getService(Tags.base_url)
                .updateLocation(userModel.getData().getUser_id(),location.getLatitude(),location.getLongitude())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful())
                        {
                            Log.e("Success","Location_updated");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    private void addVisit(final String timeNow)
    {

        Api.getService(Tags.base_url)
                .updateVisit("android",timeNow)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful())
                        {
                            preferences.saveVisitTime(ClientHomeActivity.this,timeNow);
                        }else
                            {
                                try {
                                    Log.e("error_code",response.code()+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });

    }
    private void getNotificationCount()
    {
        Api.getService(Tags.base_url)
                .getNotificationCount(userModel.getData().getUser_id(),"count_unread")
                .enqueue(new Callback<NotificationCountModel>() {
                    @Override
                    public void onResponse(Call<NotificationCountModel> call, Response<NotificationCountModel> response) {
                        if (response.isSuccessful())
                        {
                            if (response.body()!=null)
                            {
                                updateNotificationCount(response.body().getCount_unread());
                            }

                        }else
                            {
                                try {
                                    Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                    }

                    @Override
                    public void onFailure(Call<NotificationCountModel> call, Throwable t) {
                        try
                        {
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    private void readNotification()
    {
        if (canRead){
            Api.getService(Tags.base_url)
                    .readNotification(userModel.getData().getUser_id(),"read_alert")
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful())
                            {
                                updateNotificationCount(0);
                            }else
                            {
                                try {
                                    Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            try
                            {
                                Log.e("Error",t.getMessage());
                            }catch (Exception e){}
                        }
                    });
        }

    }
    private void updateNotificationCount(final int count)
    {

        if (count>0){
            canRead = true;
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (fragment_home!=null&&fragment_home.isAdded())
                            {
                                fragment_home.updateNotificationCount(count);
                            }
                        }
                    },1);
        }else
            {
                new Handler()
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (fragment_home!=null&&fragment_home.isAdded())
                                {
                                    fragment_home.updateNotificationCount(0);
                                }
                            }
                        },1);
            }
    }
    ///////////////////////////////////
    public void updateUserData(final UserModel userModel)
    {
        preferences.create_update_userData(this,userModel);
        userSingleTone.setUserModel(userModel);
        this.userModel = userModel;
        if (fragment_client_profile!=null && fragment_client_profile.isAdded())
        {
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment_client_profile.updateUI(userModel);
                        }
                    },1);
        }
    }
    ///////////////////////////////////
    public void DisplayFragmentHome()
    {
        fragment_count+=1;
        if (fragment_home == null) {
            fragment_home = Fragment_Home.newInstance();
        }

        if (fragment_home.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_home).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_home, "fragment_home").addToBackStack("fragment_home").commit();
            DisplayFragmentStore();

        }

    }
    public void DisplayFragmentStore()
    {


        if (fragment_home != null && fragment_home.isAdded()) {
            fragment_home.updateBottomNavigationPosition(0);
        }
        if (fragment_shipment != null && fragment_shipment.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_shipment).commit();
        }
        if (fragment_client_orders != null && fragment_client_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }
        if (fragment_client_notifications != null && fragment_client_notifications.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_notifications).commit();
        }
        if (fragment_client_profile != null && fragment_client_profile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_profile).commit();
        }

        if (fragment_client_store == null) {
            fragment_client_store = Fragment_Client_Store.newInstance();
        }

        if (fragment_client_store.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_client_store).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_client_store, "fragment_client_store").addToBackStack("fragment_client_store").commit();
        }

    }
    public void DisplayFragmentShipment()
    {
        if (fragment_home != null && fragment_home.isAdded()) {
            fragment_home.updateBottomNavigationPosition(1);
        }

        if (fragment_client_store != null && fragment_client_store.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_store).commit();
        }
        if (fragment_client_notifications != null && fragment_client_notifications.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_notifications).commit();
        }
        if (fragment_client_profile != null && fragment_client_profile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_profile).commit();
        }

        if (fragment_client_orders != null && fragment_client_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }

        if (fragment_shipment == null) {
            fragment_shipment = Fragment_Shipment.newInstance();
        }

        if (fragment_shipment.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_shipment).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_shipment, "fragment_shipment").addToBackStack("fragment_shipment").commit();
        }

    }
    public void DisplayFragmentMyOrders()
    {
        if (fragment_home != null && fragment_home.isAdded()) {
            fragment_home.updateBottomNavigationPosition(2);
        }

        if (fragment_client_store != null && fragment_client_store.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_store).commit();
        }
        if (fragment_shipment != null && fragment_shipment.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_shipment).commit();
        }
        if (fragment_client_notifications != null && fragment_client_notifications.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_notifications).commit();
        }
        if (fragment_client_profile != null && fragment_client_profile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_profile).commit();
        }

        if (fragment_client_orders == null) {
            fragment_client_orders = Fragment_Client_Orders.newInstance();
        }

        if (fragment_client_orders.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_client_orders).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_client_orders, "fragment_client_orders").addToBackStack("fragment_client_orders").commit();
        }

    }
    public void DisplayFragmentNotification()
    {
        readNotification();

        if (fragment_home != null && fragment_home.isAdded()) {
            fragment_home.updateBottomNavigationPosition(3);
        }
        if (fragment_client_store != null && fragment_client_store.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_store).commit();
        }
        if (fragment_shipment != null && fragment_shipment.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_shipment).commit();
        }
        if (fragment_client_orders != null && fragment_client_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }
        if (fragment_client_profile != null && fragment_client_profile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_profile).commit();
        }

        if (fragment_client_notifications == null) {
            fragment_client_notifications = Fragment_Client_Notifications.newInstance();
        }

        if (fragment_client_notifications.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_client_notifications).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_client_notifications, "fragment_client_notifications").addToBackStack("fragment_client_notifications").commit();
        }

    }
    public void DisplayFragmentProfile()
    {
        if (fragment_home != null && fragment_home.isAdded()) {
            fragment_home.updateBottomNavigationPosition(4);
        }
        if (fragment_client_store != null && fragment_client_store.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_store).commit();
        }
        if (fragment_shipment != null && fragment_shipment.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_shipment).commit();
        }
        if (fragment_client_orders != null && fragment_client_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }
        if (fragment_client_notifications != null && fragment_client_notifications.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_notifications).commit();
        }

        if (fragment_client_profile == null) {
            fragment_client_profile = Fragment_Client_Profile.newInstance();
        }

        if (fragment_client_profile.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_client_profile).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_client_profile, "fragment_client_profile").addToBackStack("fragment_client_profile").commit();
        }

    }
    public void DisplayFragmentAddCoupon()
    {
        fragment_count+=1;

        fragment_add_coupon = Fragment_Add_Coupon.newInstance();


        if (fragment_add_coupon.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_add_coupon).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_add_coupon, "fragment_add_coupon").addToBackStack("fragment_add_coupon").commit();
        }

    }
    public void DisplayFragmentPhone()
    {

        fragment_count+=1;
        fragment_phone = Fragment_Phone.newInstance("edit_profile");

        if (fragment_phone.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_phone).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_phone, "fragment_phone").addToBackStack("fragment_phone").commit();
        }



    }
    public void DisplayFragmentSearch()
    {

        if (location!=null)
        {
            fragment_count+=1;

            fragment_search = Fragment_Search.newInstance(location.getLatitude(),location.getLongitude());



            if (fragment_search.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_search).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_search, "fragment_search").addToBackStack("fragment_search").commit();
            }
        }


    }
    public void DisplayFragmentSettings()
    {

        fragment_count+=1;
        fragment_settings = Fragment_Settings.newInstance();



            if (fragment_settings.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_settings).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_settings, "fragment_settings").addToBackStack("fragment_settings").commit();
            }



    }
    public void DisplayFragmentReserveOrder(PlaceModel placeModel)
    {


        if (userModel==null)
        {
            Common.CreateUserNotSignInAlertDialog(this);
        }else
            {
                if (userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE))
                {
                    Common.CreateSignAlertDialog(this,getString(R.string.serv_aval_client));
                }else
                    {
                        fragment_count+=1;

                        fragment_reserve_order = Fragment_Reserve_Order.newInstance(placeModel);

                        if (fragment_reserve_order.isAdded()) {
                            fragmentManager.beginTransaction().show(fragment_reserve_order).commit();

                        } else {
                            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_reserve_order, "fragment_reserve_order").addToBackStack("fragment_reserve_order").commit();
                        }

                    }
                    }




    }
    public void DisplayFragmentMap(String from)
    {
        fragment_count+=1;

        if (location!=null)
        {
            fragment_map = Fragment_Map.newInstance(location.getLatitude(),location.getLongitude(),from);

        }else
            {
                fragment_map = Fragment_Map.newInstance(0.0,0.0,from);

            }

        if (fragment_map.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_map).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_map, "fragment_map").addToBackStack("fragment_map").commit();
        }



    }
    public void DisplayFragmentEditProfile()
    {

        fragment_count+=1;
        fragment_edit_profile = Fragment_Edit_Profile.newInstance(this.userModel);


        if (fragment_edit_profile.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_edit_profile).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_edit_profile, "fragment_edit_profile").addToBackStack("fragment_edit_profile").commit();
        }



    }
    public void DisplayFragmentStoreDetails(PlaceModel placeModel)
    {

        fragment_count+=1;



        fragment_store_details = Fragment_Store_Details.newInstance(placeModel,location.getLatitude(),location.getLongitude());

        if (fragment_store_details.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_store_details).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_store_details, "fragment_store_details").addToBackStack("fragment_store_details").commit();
        }

    }
    public void getAddressFromMapListener(final Favourite_location favourite_location,String from)
    {

        if (from.equals("fragment_reserve_order"))
        {
            if (fragment_reserve_order!=null&&fragment_reserve_order.isAdded())
            {
                new Handler()
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragment_reserve_order.updateSelectedLocation(favourite_location);
                                fragmentManager.popBackStack("fragment_map",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                fragment_count-=1;
                            }
                        },1);
            }
        }else if (from.equals("fragment_shipment_pickup_location"))
            {
                if (fragment_shipment!=null&&fragment_shipment.isAdded())
                {
                    new Handler()
                            .postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    fragment_shipment.setLocationData(favourite_location.getPlace_id(),favourite_location.getStreet()+" "+favourite_location.getAddress(),favourite_location.getLat(),favourite_location.getLng(),"pickup_location");
                                    fragmentManager.popBackStack("fragment_map",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    fragment_count-=1;
                                }
                            },1);
                }
            }
        else if (from.equals("fragment_shipment_dropoff_location"))
        {
            if (fragment_shipment!=null&&fragment_shipment.isAdded())
            {
                new Handler()
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragment_shipment.setLocationData(favourite_location.getPlace_id(),favourite_location.getStreet()+" "+favourite_location.getAddress(),favourite_location.getLat(),favourite_location.getLng(),"dropoff_location");
                                fragmentManager.popBackStack("fragment_map",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                fragment_count-=1;
                            }
                        },1);
            }
        }

    }
    public void DisplayFragmentDelegates(double place_lat,double place_lng,String type,String client_id,String order_id)
    {
        fragment_count+=1;
        if (fragment_delegates == null) {
            fragment_delegates = Fragment_Delegates.newInstance(place_lat,place_lng,type,order_id,client_id);
        }

        if (fragment_delegates.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_delegates).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_delegates, "fragment_delegates").addToBackStack("fragment_delegates").commit();

        }

    }

    public void DisplayFragmentDelegatesResult(NotificationModel notificationModel)
    {
        fragment_count+=1;

        fragment_delegates_result = Fragment_Delegates_Result.newInstance(notificationModel);


        if (fragment_delegates_result.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_delegates_result).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_delegates_result, "fragment_delegates_result").addToBackStack("fragment_delegates_result").commit();

        }

    }
    public void DisplayFragmentRegisterDelegate()
    {

        if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT))
        {
            fragment_count+=1;

            fragment_delegate_register = Fragment_Delegate_Register.newInstance();



            if (fragment_delegate_register.isAdded())
            {
                fragmentManager.beginTransaction().show(fragment_delegate_register).commit();

            }else
                {
                    fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_delegate_register, "fragment_delegate_register").addToBackStack("fragment_delegate_register").commit();

                }
        }else
            {
                Common.CreateSignAlertDialog(this,getString(R.string.already_courier));
            }



    }
    public void DisplayFragmentDelegateAddOffer(OrderDataModel.OrderModel orderModel)
    {

        fragment_count+=1;
        fragment_delegate_add_offer = Fragment_Delegate_Add_Offer.newInstance(orderModel);

        if (fragment_delegate_add_offer.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_delegate_add_offer).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_delegate_add_offer, "fragment_delegate_add_offer").addToBackStack("fragment_delegate_add_offer").commit();
        }



    }
    public void DisplayFragmentClientOrderDetails(OrderDataModel.OrderModel orderModel)
    {

        fragment_count+=1;
        fragment_client_order_details = Fragment_Client_Order_Details.newInstance(orderModel);

        if (fragment_client_order_details.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_client_order_details).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_client_order_details, "fragment_client_order_details").addToBackStack("fragment_client_order_details").commit();
        }



    }
    public void DisplayFragmentClientDelegateOffer(NotificationModel notificationModel)
    {

        fragment_count+=1;
        fragment_client_delegate_offer = Fragment_Client_Delegate_Offer.newInstance(notificationModel);

        if (fragment_client_delegate_offer.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_client_order_details).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_client_delegate_offer, "fragment_client_delegate_offer").addToBackStack("fragment_client_delegate_offer").commit();
        }



    }
    public void DisplayFragmentDelegateComment()
    {

        fragment_count+=1;
        fragment_delegate_comments = Fragment_Delegate_Comments.newInstance();

        if (fragment_delegate_comments.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_delegate_comments).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_delegate_comments, "fragment_delegate_comments").addToBackStack("fragment_delegate_comments").commit();
        }



    }
    public void DisplayFragmentDelegateCurrentOrderDetails(OrderDataModel.OrderModel orderModel)
    {

        fragment_count+=1;
        fragment_delegate_current_order_details = Fragment_Delegate_Current_Order_Details.newInstance(orderModel);

        if (fragment_delegate_current_order_details.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_delegate_current_order_details).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_delegate_current_order_details, "fragment_delegate_current_order_details").addToBackStack("fragment_delegate_current_order_details").commit();
        }



    }

    public void DisplayFragmentMapLocationDetails(double lat,double lng,String address)
    {

        fragment_count+=1;
        fragment_map_location_details = Fragment_Map_Location_Details.newInstance(lat,lng,address);

        if (fragment_map_location_details.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_map_location_details).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_map_location_details, "fragment_map_location_details").addToBackStack("fragment_map_location_details").commit();
        }



    }
    public void DisplayFragmentMapFollowOrder(OrderDataModel.OrderModel orderModel)
    {

        fragment_count+=1;
        fragment_map_follow_order = Fragment_Map_Follow_Order.newInstance(orderModel);

        if (fragment_map_follow_order.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_map_follow_order).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_map_follow_order, "fragment_map_follow_order").addToBackStack("fragment_map_follow_order").commit();
        }



    }

    public void DisplayFragmentExplainCourier()
    {
        fragment_count+=1;

        fragment_explain_courier = Fragment_Explain_Courier.newInstance();


        if (fragment_explain_courier.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_explain_courier).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_explain_courier, "fragment_explain_courier").addToBackStack("fragment_explain_courier").commit();
        }

    }

    public void DisplayFragmentDocumentation()
    {

        if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT))
        {
            fragment_count+=1;

            fragment_documentation_data = Fragment_Documentation_Data.newInstance();


            if (fragment_documentation_data.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_documentation_data).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_documentation_data, "fragment_documentation_data").addToBackStack("fragment_documentation_data").commit();
            }

        }else
        {
            Common.CreateSignAlertDialog(this,getString(R.string.already_courier));
        }


    }

    public void DisplayFragmentBankAccount()
    {

        fragment_count+=1;

        fragment_bank_account = Fragment_Bank_Account.newInstance();


        if (fragment_bank_account.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_bank_account).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_bank_account, "fragment_bank_account").addToBackStack("fragment_bank_account").commit();
        }

    }

    // from fragment coupon
    public void updateUserDataProfile(UserModel userModel)
    {
        if (fragment_client_profile!=null&&fragment_client_profile.isAdded())
        {
            fragment_client_profile.updateUserData(userModel);
        }
    }

    public void NavigateToChatActivity(ChatUserModel chatUserModel,String from)
    {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("data",chatUserModel);
        intent.putExtra("from",from);
        startActivity(intent);
    }
    public void delegateAcceptOrder(String driver_id,String client_id,String order_id,String driver_offer)
    {

        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .delegateAccept(driver_id,client_id,order_id,"accept",driver_offer)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful())
                        {
                            fragment_count-=1;
                            ClientHomeActivity.super.onBackPressed();
                            Toast.makeText(ClientHomeActivity.this, R.string.accepted, Toast.LENGTH_SHORT).show();
                            RefreshFragment_Order();
                        }else
                            {
                                dialog.dismiss();
                                Toast.makeText(ClientHomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                try {
                                    Log.e("error_code",response.code()+""+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    public void delegateRefuse_FinishOrder(String driver_id, String client_id, String order_id, final String type)
    {

        final ProgressDialog progressDialog = Common.createProgressDialog(this,getString(R.string.wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        Api.getService(Tags.base_url)
                .delegateRefuse_Finish(driver_id,client_id,order_id,type)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful())
                        {
                            fragment_count-=1;
                            ClientHomeActivity.super.onBackPressed();
                            if (type.equals("refuse"))
                            {
                                Toast.makeText(ClientHomeActivity.this,getString(R.string.refused), Toast.LENGTH_SHORT).show();
                                RefreshFragment_Order();
                                RefreshFragment_Notification();
                            }else if (type.equals("end"))
                            {
                                ClientHomeActivity.super.onBackPressed();
                                fragment_count-=1;
                                Toast.makeText(ClientHomeActivity.this,getString(R.string.done), Toast.LENGTH_SHORT).show();
                                DisplayFragmentMyOrders();
                                RefreshFragment_Order();
                                new Handler()
                                        .postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (fragment_client_orders!=null&&fragment_client_orders.isAdded())
                                                {
                                                    fragment_client_orders.NavigateToFragmentRefresh(2);

                                                }
                                            }
                                        },1000);
                            }

                        }else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(ClientHomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("error_code",response.code()+""+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            progressDialog.dismiss();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    // from dialog or fragment delegate result
    public void clientAcceptOffer(final String driver_id, final String client_id, final String order_id, final String type, String driver_offer, final String from)
    {

        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .clientAccept_Refuse(client_id,driver_id,order_id,driver_offer,type)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful())
                        {
                            if (from.equals("fragment_delegate_result"))
                            {
                                fragment_count-=1;
                                ClientHomeActivity.super.onBackPressed();
                            }

                            Toast.makeText(ClientHomeActivity.this,getString(R.string.accepted), Toast.LENGTH_SHORT).show();

                            RefreshFragment_Notification();
                            RefreshFragment_Order();

                        }else
                        {
                            dialog.dismiss();
                            Toast.makeText(ClientHomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("error_code",response.code()+""+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    public void clientRefuseOffer(final String id_notification)
    {

        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .clientRefuseDelegateOffer(id_notification)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful())
                        {
                            RefreshFragment_Notification();
                            RefreshFragment_Order();

                        }else
                        {
                            dialog.dismiss();
                            Toast.makeText(ClientHomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("error_code",response.code()+""+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    private void clientCancelOrder(String order_id)
    {
        Log.e("order_id",order_id+"__");
        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .clientCancelOrder(order_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful())
                        {

                            fragment_count-=1;
                            ClientHomeActivity.super.onBackPressed();
                            RefreshFragment_Notification();
                            RefreshFragment_Order();
                            Toast.makeText(ClientHomeActivity.this, getString(R.string.refused), Toast.LENGTH_SHORT).show();

                        }else
                        {
                            dialog.dismiss();
                            Toast.makeText(ClientHomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("error_code",response.code()+""+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    ////////////////////////
   /* public  void CreateAcceptRefuseDialog(final String order_id, final double place_lat, final double place_long, final String client_id)
    {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_accept_refuse,null);
        Button btn_send = view.findViewById(R.id.btn_send);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayFragmentDelegates(place_lat,place_long,"resend_order",client_id,order_id);
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                clientCancelOrder(order_id);
            }
        });

        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }*/

    ////////////////////////

    //from fragment delegate,fragment client delegate offer order_id and client_id may be empty
   /* public void setDelegate_id(final String delegate_id,String client_id,String order_id, String type)
    {
        *//*if (type.equals("reserve_order"))
        {
            if (fragment_reserve_order!=null&&fragment_reserve_order.isAdded())
            {
                new Handler()
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ClientHomeActivity.super.onBackPressed();
                                fragment_count-=1;
                                fragment_reserve_order.sendOrder(delegate_id);
                                RefreshFragment_Notification();
                            }
                        },1);
            }
        }else*//*
        if (type.equals("resend_order"))
        {
            ClientHomeActivity.super.onBackPressed();
            fragment_count-=1;
            ResendOrder(client_id,delegate_id,order_id);
        }else if (type.equals("reserve_shipment"))
            {
                new Handler()
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ClientHomeActivity.super.onBackPressed();
                                fragment_count-=1;
                                fragment_shipment.sendOrder(delegate_id);
                            }
                        },1);
            }


    }*/

/*
    private void ResendOrder(String client_id, String delegate_id, String order_id) {

        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .resendOrderToDifferentDelegate(client_id,delegate_id,order_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful())
                        {

                            RefreshFragment_Notification();
                            RefreshFragment_Order();
                            Toast.makeText(ClientHomeActivity.this, getString(R.string.succ), Toast.LENGTH_SHORT).show();

                        }else
                        {
                            dialog.dismiss();
                            Toast.makeText(ClientHomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("error_code",response.code()+""+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
*/

    //from fragment reserve order
    public void FollowOrder()
    {
        super.onBackPressed();
        super.onBackPressed();

        fragment_count-=2;

        DisplayFragmentMyOrders();

        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (fragment_client_orders!=null&&fragment_client_orders.isAdded())
                        {
                            fragment_client_orders.NavigateToFragmentRefresh(0);

                        }
                    }
                },1000);
    }

    public void FollowOrderFromShipment()
    {

        DisplayFragmentMyOrders();
        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (fragment_client_orders!=null&&fragment_client_orders.isAdded())
                        {
                            fragment_client_orders.NavigateToFragmentRefresh(0);

                        }
                    }
                },1000);
    }

    public void registerDelegate(String national_id, String address, Uri image_national_id,Uri image_license,Uri image_front_uri,Uri image_behind_uri)
    {
        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();
        RequestBody user_id_part =Common.getRequestBodyText(userModel.getData().getUser_id());
        RequestBody national_id_part =Common.getRequestBodyText(national_id);
        RequestBody address_part =Common.getRequestBodyText(address);
        MultipartBody.Part image_national_id_part = Common.getMultiPart(this,image_national_id,"user_card_id_image");
        MultipartBody.Part image_license_part = Common.getMultiPart(this,image_license,"user_driving_license");

        MultipartBody.Part image_front_part = Common.getMultiPart(this,image_national_id,"image_car_front");
        MultipartBody.Part image_back_part = Common.getMultiPart(this,image_license,"image_car_back");


        Api.getService(Tags.base_url)
                .registerDelegate(user_id_part,national_id_part,address_part,image_national_id_part,image_license_part,image_front_part,image_back_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, final Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()&&response.body()!=null&&response.body().getData()!=null)
                        {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (fragment_client_profile!=null&&fragment_client_profile.isAdded()) {
                                        fragment_client_profile.updateUserData(response.body());
                                        ClientHomeActivity.this.userModel = response.body();
                                        userSingleTone.setUserModel(response.body());
                                        ClientHomeActivity.super.onBackPressed();
                                        fragment_count -= 1;
                                        new Handler()
                                                .postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Common.CreateSuccessDialog(ClientHomeActivity.this,getString(R.string.succ_be_courier));

                                                    }
                                                },1000);
                                    }
                                }
                            },1);
                        }else if (response.code() == 406)
                        {
                            Toast.makeText(ClientHomeActivity.this, getString(R.string.req_sent), Toast.LENGTH_LONG).show();
                        }else
                            {
                                try {
                                    Log.e("Error_code",response.code()+""+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(ClientHomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(ClientHomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });


    }

    // from fragment phone
    public void setPhoneData(String code, String country_code, String phone)
    {
        if (fragment_edit_profile!=null&&fragment_edit_profile.isAdded())
        {
            fragment_edit_profile.updatePhoneData(country_code,code,phone);
            fragment_count-=1;
            super.onBackPressed();
        }
    }
    //from pending fragment to fragment store details
    public void AddWaitOrderCount(int order_counter)
    {
        fragment_store_details.AddCounter(order_counter);
    }

    public void UpdateOrderMovement(final String client_id, final String driver_id, final String order_id, int order_movement) {


        if (order_movement == Tags.STATE_CLIENT_ACCEPT_OFFER)
        {
            final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.show();
            state = String.valueOf(Tags.STATE_DELEGATE_COLLECTING_ORDER);
            call = Api.getService(Tags.base_url).movementDelegate(order_id,state);
            // تجميع الطلب
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    dialog.dismiss();
                    if (response.isSuccessful())
                    {

                        fragment_delegate_current_order_details.updateOrderState(Integer.parseInt(state));

                        RefreshFragment_Order();
                        RefreshFragment_Notification();


                    }else
                    {
                        Toast.makeText(ClientHomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        try {
                            Log.e("error_code",response.code()+""+response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    try {
                        dialog.dismiss();
                        Toast.makeText(ClientHomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();

                        Log.e("Error",t.getMessage());
                    }catch (Exception e){}
                }
            });

        }else if (order_movement == Tags.STATE_DELEGATE_COLLECTING_ORDER)
        {
            final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.show();
            state = String.valueOf(Tags.STATE_DELEGATE_COLLECTED_ORDER);

            call = Api.getService(Tags.base_url).movementDelegate(order_id,state);

            // تم التجميع
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    dialog.dismiss();
                    if (response.isSuccessful())
                    {

                        fragment_delegate_current_order_details.updateOrderState(Integer.parseInt(state));

                        RefreshFragment_Order();
                        RefreshFragment_Notification();

                    }else
                    {
                        Toast.makeText(ClientHomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        try {
                            Log.e("error_code",response.code()+""+response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    try {
                        dialog.dismiss();
                        Toast.makeText(ClientHomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();

                        Log.e("Error",t.getMessage());
                    }catch (Exception e){}
                }
            });

        }
        else if (order_movement == Tags.STATE_DELEGATE_COLLECTED_ORDER)
        {
            final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.show();
            state = String.valueOf(Tags.STATE_DELEGATE_DELIVERING_ORDER);

            call = Api.getService(Tags.base_url).movementDelegate(order_id,state);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    dialog.dismiss();
                    if (response.isSuccessful())
                    {

                        fragment_delegate_current_order_details.updateOrderState(Integer.parseInt(state));
                        RefreshFragment_Order();
                        RefreshFragment_Notification();
                    }else
                    {
                        Toast.makeText(ClientHomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        try {
                            Log.e("error_code",response.code()+""+response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    try {
                        dialog.dismiss();
                        Toast.makeText(ClientHomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();

                        Log.e("Error",t.getMessage());
                    }catch (Exception e){}
                }
            });

        }
        else if (order_movement == Tags.STATE_DELEGATE_DELIVERING_ORDER)
        {
            delegateRefuse_FinishOrder(driver_id,client_id,order_id,"end");


        }


    }

    public void CreateAddRateAlertDialog(final NotificationModel notificationModel)
    {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_rate,null);
        ImageView img_close = view.findViewById(R.id.img_close);
        CircleImageView image = view.findViewById(R.id.image);
        TextView tv_name = view.findViewById(R.id.tv_name);
        final ImageView image_very_bad= view.findViewById(R.id.image_very_bad);
        final ImageView image_bad = view.findViewById(R.id.image_bad);
        final ImageView image_good = view.findViewById(R.id.image_good);
        final ImageView image_very_good = view.findViewById(R.id.image_very_good);
        final ImageView image_excellent = view.findViewById(R.id.image_excellent);

        final EditText edt_comment = view.findViewById(R.id.edt_comment);
        final TextView tv_rate = view.findViewById(R.id.tv_rate);
        final Button btn_rate = view.findViewById(R.id.btn_rate);
        Picasso.with(this).load(Uri.parse(Tags.IMAGE_URL+notificationModel.getFrom_user_image())).fit().into(image);
        tv_name.setText(notificationModel.getFrom_user_full_name());




        image_excellent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = 5.0;
                image_excellent.setImageResource(R.drawable.excellent_yellow);
                image_very_good.setImageResource(R.drawable.very_good_gray);
                image_good.setImageResource(R.drawable.good_gray);
                image_bad.setImageResource(R.drawable.bad_gray);
                image_very_bad.setImageResource(R.drawable.angry_gray);

                tv_rate.setText(R.string.excellent);
                btn_rate.setVisibility(View.VISIBLE);
            }
        });

        image_very_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = 4.0;
                image_excellent.setImageResource(R.drawable.excellent_gray);
                image_very_good.setImageResource(R.drawable.very_good_yeallow);
                image_good.setImageResource(R.drawable.good_gray);
                image_bad.setImageResource(R.drawable.bad_gray);
                image_very_bad.setImageResource(R.drawable.angry_gray);

                tv_rate.setText(R.string.very_good);
                btn_rate.setVisibility(View.VISIBLE);
            }
        });

        image_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = 3.0;

                image_excellent.setImageResource(R.drawable.excellent_gray);
                image_very_good.setImageResource(R.drawable.very_good_gray);
                image_good.setImageResource(R.drawable.good_yellow);
                image_bad.setImageResource(R.drawable.bad_gray);
                image_very_bad.setImageResource(R.drawable.angry_gray);

                tv_rate.setText(R.string.good);
                btn_rate.setVisibility(View.VISIBLE);
            }
        });

        image_bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = 2.0;

                image_excellent.setImageResource(R.drawable.excellent_gray);
                image_very_good.setImageResource(R.drawable.very_good_gray);
                image_good.setImageResource(R.drawable.good_gray);
                image_bad.setImageResource(R.drawable.bad_yellow);
                image_very_bad.setImageResource(R.drawable.angry_gray);

                tv_rate.setText(R.string.bad);
                btn_rate.setVisibility(View.VISIBLE);
            }
        });

        image_very_bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = 1.0;

                image_excellent.setImageResource(R.drawable.excellent_gray);
                image_very_good.setImageResource(R.drawable.very_good_gray);
                image_good.setImageResource(R.drawable.good_gray);
                image_bad.setImageResource(R.drawable.bad_gray);
                image_very_bad.setImageResource(R.drawable.angry_yellow);

                tv_rate.setText(R.string.very_bad);
                btn_rate.setVisibility(View.VISIBLE);
            }
        });

        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = edt_comment.getText().toString().trim();
                AddRate(dialog,notificationModel,rate,comment);
            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }

    private void AddRate(final AlertDialog alertDialog, NotificationModel notificationModel, double rate, String comment) {

        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .addRate(notificationModel.getClient_id(),notificationModel.getDriver_id(),notificationModel.getOrder_id(),rate,"end",comment)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful())
                        {

                            alertDialog.dismiss();
                            dialog.dismiss();
                            RefreshFragment_Notification();

                        }else
                        {
                            try {
                                Log.e("error_code",response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                            Toast.makeText(ClientHomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error",t.getMessage());
                            Toast.makeText(ClientHomeActivity.this,getString(R.string.something), Toast.LENGTH_SHORT).show();
                        }catch (Exception re){}
                    }
                });



    }

    private void RefreshFragment_Order()
    {
        if (fragment_client_orders!=null&&fragment_client_orders.isAdded())
        {
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                         fragment_client_orders.RefreshOrderFragments();
                        }
                    },1);
        }
    }
    private void RefreshFragment_Notification()
    {
        if (fragment_client_notifications!=null&&fragment_client_notifications.isAdded())
        {
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment_client_notifications.getNotification();
                        }
                    },1);
        }
    }
    public void RefreshActivity(String lang)
    {
        Paper.book().write("lang",lang);
        Language_Helper.setNewLocale(this,lang);
        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent =  getIntent();
                        finish();
                        startActivity(intent);
                    }
                },1050);



    }
    public void NavigateToTermsActivity(int type)
    {
        Intent intent = new Intent(this, TermsConditionsActivity.class);
        intent.putExtra("type",type);
        startActivity(intent);
        if (current_lang.equals("ar"))
        {
            overridePendingTransition(R.anim.from_right,R.anim.to_left);

        }else
        {
            overridePendingTransition(R.anim.from_left,R.anim.to_right);

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == 1255)
        {
            if (resultCode == RESULT_OK)
            {
                startLocationUpdate();
            }else
                {
                    //create dialog to open_gps
                }
        }

        /*if (requestCode == 33) {
            if (isGpsOpen()) {
                StartService(LocationRequest.PRIORITY_LOW_POWER);
            } else {
                CreateGpsDialog();
            }
        }*/
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (requestCode == gps_req && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            initGoogleApiClient();
        }
    }
    private void CheckPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, gps_perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{gps_perm}, gps_req);
        } else {

            initGoogleApiClient();
           /* if (isGpsOpen())
            {
                StartService(LocationRequest.PRIORITY_HIGH_ACCURACY);
            }else
                {
                    CreateGpsDialog();

                }*/
        }
    }
    private void StartService(int accuracy)
    {

       /* intentService = new Intent(this, UpdateLocationService.class);
        intentService.putExtra("accuracy",accuracy);
        startService(intentService);*/
    }
    private boolean isGpsOpen()
    {
        boolean isOpened = false;
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager != null) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)||manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                isOpened = true;
            }
        }

        return isOpened;
    }
    private void OpenGps()
    {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 33);
    }
    private void CreateGpsDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.gps_dialog,null);
        Button btn_allow = view.findViewById(R.id.btn_allow);
        Button btn_deny = view.findViewById(R.id.btn_deny);

        btn_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                OpenGps();
            }
        });
        btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }

    /////////////////////////////////////////////////////////////////
    private void intLocationRequest()
    {
        locationRequest = new LocationRequest();
        locationRequest.setFastestInterval(1000*60*2);
        locationRequest.setInterval(1000*60*2);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {

                Status status = result.getStatus();
                switch (status.getStatusCode())
                {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startLocationUpdate();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(ClientHomeActivity.this,1255);
                        }catch (Exception e)
                        {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e("not available","not available");
                        break;
                }
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate()
    {
        locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        intLocationRequest();

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient!=null)
        {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        DisplayFragmentHomeView();
    }

    @Override
    public void onLocationChanged(Location location) {
        //LocationListener(location);
    }
    /////////////////////////////////////////////////////////////////
    public void DisplayFragmentHomeView()
    {
        if (fragment_home!=null&&fragment_home.isAdded())
        {
            fragment_home.DisplayFragmentView();
        }
    }
    /////////////////////////////////////////////////////////////////
    public void Logout()
    {
        final ProgressDialog dialog =Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .logOut(userModel.getData().getUser_id())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful())
                        {
                            new Handler()
                                    .postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                            manager.cancelAll();
                                        }
                                    },1);
                            userSingleTone.clear(ClientHomeActivity.this);
                            Intent intent = new Intent(ClientHomeActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                            if (current_lang.equals("ar"))
                            {
                                overridePendingTransition(R.anim.from_left,R.anim.to_right);



                            }else
                            {
                                overridePendingTransition(R.anim.from_right,R.anim.to_left);


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }
    public void Back()
    {

        if (fragment_count>1)
        {
            super.onBackPressed();
            fragment_count-=1;

        }else
            {
                if (fragment_client_store!=null&&fragment_client_store.isVisible())
                {
                    NavigateToSignInActivity();
                }else
                {
                    DisplayFragmentStore();
                }
            }


    }

    public void NavigateToSignInActivity()
    {

        if (userModel!=null)
        {
            finish();
            if (current_lang.equals("ar"))
            {
                overridePendingTransition(R.anim.from_left,R.anim.to_right);



            }else
            {
                overridePendingTransition(R.anim.from_right,R.anim.to_left);


            }
        }else
        {
            Intent intent = new Intent(ClientHomeActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
            if (current_lang.equals("ar"))
            {
                overridePendingTransition(R.anim.from_left,R.anim.to_right);



            }else
            {
                overridePendingTransition(R.anim.from_right,R.anim.to_left);


            }
        }

    }

    @Override
    public void onBackPressed() {
        Back();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (locationCallback!=null)
        {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);

        }
        if (googleApiClient!=null)
        {
            googleApiClient.disconnect();
        }

        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }
    }



}
