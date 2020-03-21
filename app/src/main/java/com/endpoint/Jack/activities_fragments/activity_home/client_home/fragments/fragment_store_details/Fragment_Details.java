package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_store_details;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.models.NearDelegateDataModel;
import com.endpoint.Jack.models.PlaceDetailsModel;
import com.endpoint.Jack.models.PlaceModel;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.ui.IconGenerator;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Details extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "Data";
    private static final String TAG2 = "LAT";
    private static final String TAG3 = "LNG";

    private ClientHomeActivity activity;
    private LinearLayout ll_map,ll_delegate,ll_today,ll_open_hour;
    private TextView tv_delegate_count,tv_name,tv_address,tv_state,tv;
    private FloatingActionButton fab;
    private ProgressBar progBar;
    private PlaceModel placeModel;
    private double lat = 0.0,lng = 0.0;
    private GoogleMap mMap;
    private SupportMapFragment supportMapFragment;
    private ExpandableLayout expandedLayout;
    private ImageView arrow_down;
    private TextView tv_today;
    private TextView tv_d1,tv_d11,tv_d2,tv_d22,tv_d3,tv_d33,tv_d4,tv_d44,tv_d5,tv_d55,tv_d6,tv_d66,tv_d7,tv_d77;
    private ProgressDialog dialog;
    private String current_language;
    private int delegate_count = 0;
    private UserSingleTone userSingleTone;
    private UserModel userModel;

    public static Fragment_Details newInstance(PlaceModel placeModel, double lat, double lng)
    {
        Fragment_Details fragment_details = new Fragment_Details();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,placeModel);
        bundle.putDouble(TAG2,  lat);
        bundle.putDouble(TAG3,  lng);
        fragment_details.setArguments(bundle);
        return fragment_details;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        ll_map = view.findViewById(R.id.ll_map);
        ll_delegate = view.findViewById(R.id.ll_delegate);
        ll_delegate.setEnabled(false);
        tv_delegate_count = view.findViewById(R.id.tv_delegate_count);
        tv_name = view.findViewById(R.id.tv_name);
        tv_address = view.findViewById(R.id.tv_address);
        tv = view.findViewById(R.id.tv);
        tv_state = view.findViewById(R.id.tv_state);
        fab = view.findViewById(R.id.fab);
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        expandedLayout = view.findViewById(R.id.expandedLayout);
        arrow_down = view.findViewById(R.id.arrow_down);
        ll_today = view.findViewById(R.id.ll_today);
        ll_open_hour = view.findViewById(R.id.ll_open_hour);

        tv_today = view.findViewById(R.id.tv_today);
        tv_d1 = view.findViewById(R.id.tv_d1);
        tv_d11 = view.findViewById(R.id.tv_d11);
        tv_d2 = view.findViewById(R.id.tv_d2);
        tv_d22 = view.findViewById(R.id.tv_d22);
        tv_d3 = view.findViewById(R.id.tv_d3);
        tv_d33 = view.findViewById(R.id.tv_d33);
        tv_d4 = view.findViewById(R.id.tv_d4);
        tv_d44 = view.findViewById(R.id.tv_d44);
        tv_d5 = view.findViewById(R.id.tv_d5);
        tv_d55 = view.findViewById(R.id.tv_d55);
        tv_d6 = view.findViewById(R.id.tv_d6);
        tv_d66 = view.findViewById(R.id.tv_d66);
        tv_d7 = view.findViewById(R.id.tv_d7);
        tv_d77 = view.findViewById(R.id.tv_d77);


        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            placeModel = (PlaceModel) bundle.getSerializable(TAG);
            lat = bundle.getDouble(TAG2);
            lng = bundle.getDouble(TAG3);

            updateUI(placeModel);

        }



        ll_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sAdd = lat+","+lng;
                String dAdd = placeModel.getLat()+","+placeModel.getLng();
                String path = "http://maps.google.com/maps?saddr="+sAdd+"&daddr="+dAdd;

                try {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(path));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);

                }catch (Exception e)
                {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(path));
                    startActivity(intent);

                }

            }
        });

        ll_open_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedLayout.isExpanded())
                {
                    expandedLayout.collapse(true);

                    arrow_down.clearAnimation();
                    arrow_down.animate().setDuration(700).rotation(0).start();
                }else
                    {
                        expandedLayout.expand(true);
                        arrow_down.clearAnimation();
                        arrow_down.animate().setDuration(700).rotation(180).start();

                    }
            }
        });

        ll_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedLayout.isExpanded())
                {
                    expandedLayout.collapse(true);

                    arrow_down.clearAnimation();
                    arrow_down.animate().setDuration(700).rotation(0).start();
                }else
                {
                    expandedLayout.expand(true);
                    arrow_down.clearAnimation();
                    arrow_down.animate().setDuration(700).rotation(180).start();

                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentReserveOrder(placeModel);
            }
        });


        getDelegateCount();

    }

    private void getDelegateCount()
    {
        Api.getService(Tags.base_url)
                .getDelegate(placeModel.getLat(),placeModel.getLng(),1)
                .enqueue(new Callback<NearDelegateDataModel>() {
                    @Override
                    public void onResponse(Call<NearDelegateDataModel> call, Response<NearDelegateDataModel> response) {
                        progBar.setVisibility(View.GONE);

                        Log.e("dddd","ddd");
                        Log.e("respons_code",response.code()+"");
                        if (response.isSuccessful()&&response.body()!=null&&response.body().getMeta()!=null)
                        {
                            delegate_count = response.body().getMeta().getTotal_drivers();
                            tv_delegate_count.setText(String.valueOf(delegate_count));
                            ll_delegate.setEnabled(true);

                        }else
                            {
                                try {
                                    Log.e("Error_code",response.code()+""+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                tv_delegate_count.setText("0");

                            }
                    }

                    @Override
                    public void onFailure(Call<NearDelegateDataModel> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            tv_delegate_count.setText("0");
                            Log.e("Error_count",t.getMessage());
                            Log.e("Error_count",t.getLocalizedMessage()+"_");

                        }catch (Exception e)
                        {

                        }
                    }
                });
    }

    private void setUpMap() {

        if (supportMapFragment == null)
        {
            supportMapFragment = SupportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
        }

        getChildFragmentManager().beginTransaction().add(R.id.fragment_map_container,supportMapFragment).commit();
    }

    private void updateUI(PlaceModel placeModel) {
        dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        getPlaceDetails(placeModel);

        tv_name.setText(placeModel.getName());
        tv_address.setText(placeModel.getAddress());
        tv.setVisibility(View.VISIBLE);
        tv_state.setVisibility(View.VISIBLE);
        if (placeModel.isOpenNow())
        {
            tv_state.setText(getString(R.string.active));
            tv_state.setTextColor(ContextCompat.getColor(activity,R.color.colorPrimary));

        }else
            {
                tv_state.setText(getString(R.string.inactive));
                tv_state.setTextColor(ContextCompat.getColor(activity, R.color.gray4));

            }



        setUpMap();


    }

    private void getPlaceDetails(PlaceModel placeModel) {

        String fields ="opening_hours";

        Api.getService("https://maps.googleapis.com/maps/api/")
                .getPlaceDetails(placeModel.getPlace_id(),fields,current_language,getString(R.string.map_api_key))
                .enqueue(new Callback<PlaceDetailsModel>() {
                    @Override
                    public void onResponse(Call<PlaceDetailsModel> call, Response<PlaceDetailsModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            dialog.dismiss();

                            updateHoursUI(response.body());
                        } else {
                            dialog.dismiss();


                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceDetailsModel> call, Throwable t) {
                        try {
                            dialog.dismiss();

                            Log.e("Error",t.getMessage());
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void updateHoursUI(PlaceDetailsModel body) {

        if (body.getResult()!=null&&body.getResult().getOpening_hours()!=null)
        {
            ll_open_hour.setVisibility(View.VISIBLE);
            ll_today.setVisibility(View.VISIBLE);
            if (body.getResult().getOpening_hours().getPeriods().size()==7)
            {
                List<String> time = body.getResult().getOpening_hours().getWeekday_text();

                tv_today.setText(time.get(0).split(":",2)[1].trim());

                tv_d1.setText(time.get(0).split(":", 2)[0].trim());
                tv_d11.setText(time.get(0).split(":", 2)[1].trim());

                tv_d2.setText(time.get(1).split(":",2)[0].trim());
                tv_d22.setText(time.get(1).split(":",2)[1].trim());

                tv_d3.setText(time.get(2).split(":",2)[0].trim());
                tv_d33.setText(time.get(2).split(":",2)[1].trim());

                tv_d4.setText(time.get(3).split(":",2)[0].trim());
                tv_d44.setText(time.get(3).split(":",2)[1].trim());

                tv_d5.setText(time.get(4).split(":",2)[0].trim());
                tv_d55.setText(time.get(4).split(":",2)[1].trim());

                tv_d6.setText(time.get(5).split(":",2)[0].trim());
                tv_d66.setText(time.get(5).split(":",2)[1].trim());

                tv_d7.setText(time.get(6).split(":",2)[0].trim());
                tv_d77.setText(time.get(6).split(":",2)[1].trim());


            }else if (body.getResult().getOpening_hours().getPeriods().size()==1)
            {
                List<String> time = body.getResult().getOpening_hours().getWeekday_text();

                tv_today.setText(R.string.all_day);

                tv_d1.setText(time.get(0).split(":")[0].trim());
                tv_d11.setText(R.string.all_day);

                tv_d2.setText(time.get(1).split(":")[0].trim());
                tv_d22.setText(R.string.all_day);

                tv_d3.setText(time.get(2).split(":")[0].trim());
                tv_d33.setText(R.string.all_day);

                tv_d4.setText(time.get(3).split(":")[0].trim());
                tv_d44.setText(R.string.all_day);

                tv_d5.setText(time.get(4).split(":")[0].trim());
                tv_d55.setText(R.string.all_day);

                tv_d6.setText(time.get(5).split(":")[0].trim());
                tv_d66.setText(R.string.all_day);

                tv_d7.setText(time.get(6).split(":")[0].trim());
                tv_d77.setText(R.string.all_day);

            }
        }else
            {
                ll_open_hour.setVisibility(View.GONE);
                //tv_today.setVisibility(View.GONE);
            }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap!=null)
        {
            mMap = googleMap;
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            mMap.setTrafficEnabled(false);
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity,R.raw.maps));
            AddMarkers();
        }
    }

    private void AddMarkers() {

        View view = LayoutInflater.from(activity).inflate(R.layout.map_you_icon,null);
        View view2 = LayoutInflater.from(activity).inflate(R.layout.map_shop_icon,null);

        IconGenerator iconGenerator= new IconGenerator(activity);
        iconGenerator.setContentView(view);
        iconGenerator.setBackground(null);
        Marker marker1 = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV()));

        iconGenerator.setContentView(view2);
        iconGenerator.setBackground(null);

        Marker marker2= mMap.addMarker(new MarkerOptions().position(new LatLng(placeModel.getLat(),placeModel.getLng())).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).anchor(iconGenerator.getAnchorU(),iconGenerator.getAnchorV()));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marker1.getPosition());
        builder.include(marker2.getPosition());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(),200);

        mMap.moveCamera(cameraUpdate);


    }





}
