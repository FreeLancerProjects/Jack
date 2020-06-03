package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.models.FollowModel;
import com.endpoint.Jack.models.OrderDataModel;
import com.endpoint.Jack.models.PlaceDirectionModel;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Map_Follow_Order extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "DATA";
    private ClientHomeActivity activity;
    private ImageView arrow;
    private LinearLayout ll_back;
    private String current_language;
    private TextView tv_distance, tv_time;
    private OrderDataModel.OrderModel orderModel;
    private GoogleMap mMap;
    private Marker marker_client, marker_place, marker_driver;
    private float zoom = 15.6f;
    private Circle circle_driver;
    private long distance = 0, time = 0;
    private ProgressDialog dialog = null;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private FollowModel followModel;


    public static Fragment_Map_Follow_Order newInstance(OrderDataModel.OrderModel orderModel) {
        Fragment_Map_Follow_Order fragment_map = new Fragment_Map_Follow_Order();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, orderModel);
        fragment_map.setArguments(bundle);
        return fragment_map;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_follow_order, container, false);
        initView(view);
        return view;
    }


    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        arrow = view.findViewById(R.id.arrow);

        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);
            arrow.setColorFilter(ContextCompat.getColor(activity, R.color.white), PorterDuff.Mode.SRC_IN);
        } else {
            arrow.setImageResource(R.drawable.ic_left_arrow);
            arrow.setColorFilter(ContextCompat.getColor(activity, R.color.white), PorterDuff.Mode.SRC_IN);


        }
        ll_back = view.findViewById(R.id.ll_back);

        tv_distance = view.findViewById(R.id.tv_distance);
        tv_time = view.findViewById(R.id.tv_time);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });


        Bundle bundle = getArguments();
        if (bundle != null) {

            this.orderModel = (OrderDataModel.OrderModel) bundle.getSerializable(TAG);
            updateUI();
        }



    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null) {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            getFollowData();


        }
    }


    public void updateDriverLocation(FollowModel followModel) {
        dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        mMap.clear();

        AddMarker_Client(followModel.getClientLat(), followModel.getClientLng(), orderModel.getClient_address());
        AddMarker_Place(followModel.getPlaceLat(), followModel.getPlaceLng(), orderModel.getPlace_address());
        AddMarker_Driver(followModel.getDriverLat(), followModel.getDriverLng());
        getDirection(followModel.getClient_lat(),followModel.getClient_long(),followModel.getPlace_lat(),followModel.getPlace_long(),followModel.getDriver_lat(),followModel.getDriver_long(),1);

    }


    public void getFollowData() {
        dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .getFollowData(orderModel.getOrder_id(),orderModel.getDriver_id(),userModel.getData().getUser_id())
                .enqueue(new Callback<FollowModel>() {
                    @Override
                    public void onResponse(Call<FollowModel> call, Response<FollowModel> response) {
                        if (response.isSuccessful()&&response.body()!=null)
                        {

                            followModel =response.body();
                            mMap.clear();

                            AddMarker_Client(response.body().getClientLat(), response.body().getClientLng(), orderModel.getClient_address());
                            AddMarker_Place(response.body().getPlaceLat(), response.body().getPlaceLng(), orderModel.getPlace_address());
                            AddMarker_Driver(response.body().getDriverLat(), response.body().getDriverLng());
                            getDirection(response.body().getClient_lat(),response.body().getClient_long(),response.body().getPlace_lat(),response.body().getPlace_long(),response.body().getDriver_lat(),response.body().getDriver_long(),1);

                        }else
                        {
                            dialog.dismiss();
                            Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FollowModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }



    private void updateUI() {

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

    }
    private void getDirection(String client_lat,String client_lng,String place_lat,String place_lng,String driver_lat,String driver_lng, final int type) {

        String origin = "", dest = "";

        if (type == 1) {
            origin = client_lat + "," + client_lng;
            dest = place_lat + "," + place_lng;

        } else if (type == 2) {
            origin = place_lat + "," + place_lng;
            dest = driver_lat + "," + driver_lng;
        }


        Api.getService(Tags.googleDirectionBase_url)
                .getDirection(origin, dest, "rail", getString(R.string.map_api_key2))
                .enqueue(new Callback<PlaceDirectionModel>() {
                    @Override
                    public void onResponse(Call<PlaceDirectionModel> call, Response<PlaceDirectionModel> response) {
                        if (response.body() != null && response.body().getRoutes().size() > 0) {

                            drawRoute(PolyUtil.decode(response.body().getRoutes().get(0).getOverview_polyline().getPoints()));
                            distance += response.body().getRoutes().get(0).getLegs().get(0).getDistance().getValue();
                            time += response.body().getRoutes().get(0).getLegs().get(0).getDuration().getValue();

                            if (type == 1) {
                                getDirection(followModel.getClient_lat(),followModel.getClient_long(),followModel.getPlace_lat(),followModel.getPlace_long(),followModel.getDriver_lat(),followModel.getDriver_long(), 2);
                            } else if (type == 2) {
                                tv_distance.setText(String.format("%s %s", String.valueOf(Math.round(distance / 1000.0)), getString(R.string.km)));
                                tv_time.setText(String.format("%s %s", String.valueOf(Math.round(time / 60.0)), getString(R.string.min2)));
                                dialog.dismiss();
                            }
                        } else {
                            dialog.dismiss();
                            Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PlaceDirectionModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                        }
                    }
                });

    }

    private void drawRoute(List<LatLng> latLngList) {
        PolylineOptions options = new PolylineOptions();
        options.geodesic(true);
        options.color(ContextCompat.getColor(activity, R.color.colorPrimary));
        options.width(8.0f);
        options.addAll(latLngList);
        mMap.addPolyline(options);
    }

    private void AddMarker_Client(double lat, double lng, String address) {


        IconGenerator iconGenerator = new IconGenerator(activity);
        iconGenerator.setBackground(null);
        View view = LayoutInflater.from(activity).inflate(R.layout.map_you_icon, null);
        iconGenerator.setContentView(view);
        marker_client = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(address).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV()));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(lat, lng));
        builder.include(new LatLng(followModel.getPlaceLat(),followModel.getPlaceLng()));
        builder.include(new LatLng(followModel.getDriverLat(),followModel.getDriverLng()));


        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));

    }

    private void AddMarker_Place(double lat, double lng, String address) {


        IconGenerator iconGenerator = new IconGenerator(activity);
        iconGenerator.setBackground(null);
        View view = LayoutInflater.from(activity).inflate(R.layout.map_shop_icon, null);
        iconGenerator.setContentView(view);
        marker_place = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(address).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV()));

    }

    public void AddMarker_Driver(double lat, double lng) {


        IconGenerator iconGenerator = new IconGenerator(activity);
        iconGenerator.setBackground(null);
        View view = LayoutInflater.from(activity).inflate(R.layout.map_delegate_icon, null);
        iconGenerator.setContentView(view);
        marker_driver = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV()));

    }


}