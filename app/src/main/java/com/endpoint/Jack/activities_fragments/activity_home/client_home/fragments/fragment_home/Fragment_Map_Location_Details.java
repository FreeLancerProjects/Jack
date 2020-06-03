package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.models.PlaceDirectionModel;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Map_Location_Details extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks, LocationListener {
    private static final String TAG1 = "LAT";
    private static final String TAG2 = "LNG";
    private static final String TAG3 = "ADDRESS";
    private static final String TAG4 = "CLIENT_LAT";
    private static final String TAG5 = "CLIENT_LNG";
    private final String gps_perm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int gps_req = 22;

    private ClientHomeActivity activity;
    private ImageView arrow;
    private LinearLayout ll_back,ll_map_type;
    private TextView tv_map_type;
    private String current_language;
    private double lat = 0.0, lng = 0.0,client_lat = 0.0, client_lng = 0.0;
    private String address="";
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location location;
    private ProgressDialog dialog = null;
    private TextView tvDistance1,tvDistance2;



    public static Fragment_Map_Location_Details newInstance(double lat, double lng, double client_lat, double client_lng, String address) {
        Fragment_Map_Location_Details fragment_map = new Fragment_Map_Location_Details();
        Bundle bundle = new Bundle();
        bundle.putDouble(TAG1, lat);
        bundle.putDouble(TAG2, lng);
        bundle.putString(TAG3, address);
        bundle.putDouble(TAG4, client_lat);
        bundle.putDouble(TAG5, client_lng);

        fragment_map.setArguments(bundle);
        return fragment_map;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_location_details, container, false);
        initView(view);
        return view;
    }



    private void initView(View view) {
        activity = (ClientHomeActivity) getActivity();
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
        ll_map_type = view.findViewById(R.id.ll_map_type);
        tv_map_type = view.findViewById(R.id.tv_map_type);
        tvDistance1 = view.findViewById(R.id.tvDistance1);
        tvDistance2 = view.findViewById(R.id.tvDistance2);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        ll_map_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (mMap.getMapType())
                {
                    case GoogleMap.MAP_TYPE_NORMAL:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        tv_map_type.setText(getString(R.string.hybrid));
                        break;

                    case GoogleMap.MAP_TYPE_HYBRID:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        tv_map_type.setText(getString(R.string.terrain));


                        break;
                    case GoogleMap.MAP_TYPE_TERRAIN:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        tv_map_type.setText(getString(R.string.normal));

                        break;
                }
            }
        });


        Bundle bundle = getArguments();
        if (bundle != null) {
            lat = bundle.getDouble(TAG1);
            lng = bundle.getDouble(TAG2);
            address = bundle.getString(TAG3);
            client_lat = bundle.getDouble(TAG4);
            client_lng = bundle.getDouble(TAG5);

            updateUI();
        }



    }

    private void initGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    private void updateUI() {

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null) {
            mMap = googleMap;
            tv_map_type.setText(getString(R.string.normal));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            CheckPermission();


        }
    }

    private void AddMarker() {

        getDirection(1);

        View view = LayoutInflater.from(activity).inflate(R.layout.map_shop_icon,null);
        View view2 = LayoutInflater.from(activity).inflate(R.layout.map_client_icon,null);
        View view3 = LayoutInflater.from(activity).inflate(R.layout.map_you_icon,null);

        IconGenerator iconGenerator= new IconGenerator(activity);
        iconGenerator.setContentView(view);
        iconGenerator.setBackground(null);
        Marker marker1 = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV()));

        iconGenerator.setContentView(view2);
        iconGenerator.setBackground(null);
        Marker marker2= mMap.addMarker(new MarkerOptions().position(new LatLng(client_lat,client_lng)).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).anchor(iconGenerator.getAnchorU(),iconGenerator.getAnchorV()));

        iconGenerator.setContentView(view3);
        iconGenerator.setBackground(null);
        Marker marker3= mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).anchor(iconGenerator.getAnchorU(),iconGenerator.getAnchorV()));


        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marker1.getPosition());
        builder.include(marker2.getPosition());
        builder.include(marker3.getPosition());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(),200);

        mMap.animateCamera(cameraUpdate);

    }



    private void getDirection(int type) {



        String origin="";
        String dest= "";
        if (type ==1)
        {
            dialog = Common.createProgressDialog(activity,getString(R.string.wait));
            dialog.show();

            origin = client_lat + "," + client_lng;
            dest = lat + "," + lng;
        }else if (type ==2)
        {
            origin = location.getLatitude() + "," + location.getLongitude();
            dest = lat + "," + lng;
            Log.e("ddd","yyyyy");
        }


        Api.getService(Tags.googleDirectionBase_url)
                .getDirection(origin, dest, "rail", getString(R.string.map_api_key2))
                .enqueue(new Callback<PlaceDirectionModel>() {
                    @Override
                    public void onResponse(Call<PlaceDirectionModel> call, Response<PlaceDirectionModel> response) {
                        if (response.body() != null && response.body().getRoutes().size() > 0) {
                            if (type==1)
                            {
                                drawRoute(PolyUtil.decode(response.body().getRoutes().get(0).getOverview_polyline().getPoints()));
                                getDirection(2);
                            }else if (type==2)
                            {
                                drawRoute(PolyUtil.decode(response.body().getRoutes().get(0).getOverview_polyline().getPoints()));
                                dialog.dismiss();
                                double dist1 = SphericalUtil.computeDistanceBetween(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(lat, lng));
                                double dist2 = SphericalUtil.computeDistanceBetween(new LatLng(lat, lng), new LatLng(client_lat,client_lng));

                                tvDistance1.setText(String.format("%s %s", String.format(Locale.ENGLISH,"%.2f",(dist1/1000)),getString(R.string.km)));
                                tvDistance2.setText(String.format("%s %s", String.format(Locale.ENGLISH,"%.2f",(dist2/1000)),getString(R.string.km)));



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
        options.width(15.0f);
        options.addAll(latLngList);
        mMap.addPolyline(options);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1255)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                startLocationUpdate();
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == gps_req && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            initGoogleApiClient();
        }
    }
    private void CheckPermission()
    {
        if (ActivityCompat.checkSelfPermission(activity, gps_perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{gps_perm}, gps_req);
        } else {

            initGoogleApiClient();
        }
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
                            status.startResolutionForResult(activity,1255);
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
        LocationServices.getFusedLocationProviderClient(activity)
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

    }

    @Override
    public void onLocationChanged(Location location) {

        this.location = location;
        AddMarker();

        if (googleApiClient!=null)
        {
            googleApiClient.disconnect();
        }

        if (locationCallback!=null)
        {
            LocationServices.getFusedLocationProviderClient(activity).removeLocationUpdates(locationCallback);
        }
    }



}
