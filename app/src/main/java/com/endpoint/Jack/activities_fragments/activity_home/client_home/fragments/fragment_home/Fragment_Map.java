package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import com.endpoint.Jack.models.Favourite_location;
import com.endpoint.Jack.models.PlaceGeocodeData;
import com.endpoint.Jack.models.PlaceMapDetailsData;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Map extends Fragment implements OnMapReadyCallback {
    private static final String TAG1 = "FROM";
    private static final String TAG2 = "LAT";
    private static final String TAG3 = "LNG";
    private ClientHomeActivity activity;
    private ImageView arrow, image_pin;
    private LinearLayout ll_back;
    private EditText edt_search, edt_floor;
    private ProgressBar progBar;
    private TextView tv_address;
    private FloatingActionButton fab;
    private String current_language;
    private double lat = 0.0, lng = 0.0;
    private String address="",place_id;
    private GoogleMap mMap;
    private Marker marker;
    private float zoom = 15.6f;
    private boolean stop = false;
    private String from="";



    public static Fragment_Map newInstance(double lat, double lng,String from) {
        Fragment_Map fragment_map = new Fragment_Map();
        Bundle bundle = new Bundle();
        bundle.putString(TAG1, from);
        bundle.putDouble(TAG2, lat);
        bundle.putDouble(TAG3, lng);
        fragment_map.setArguments(bundle);
        return fragment_map;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
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
        image_pin = view.findViewById(R.id.image_pin);
        ll_back = view.findViewById(R.id.ll_back);
        edt_search = view.findViewById(R.id.edt_search);
        edt_floor = view.findViewById(R.id.edt_floor);
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        tv_address = view.findViewById(R.id.tv_address);
        fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(address))
                {
                    Common.CloseKeyBoard(activity,edt_search);
                    String floor = edt_floor.getText().toString().trim();
                    Favourite_location favourite_location = new Favourite_location(place_id,"",floor,address,lat,lng);
                    activity.getAddressFromMapListener(favourite_location,from);
                }
            }
        });
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = edt_search.getText().toString();
                    if (!TextUtils.isEmpty(query)) {
                        Search(query);
                        return true;
                    }
                }
                return false;
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            from = bundle.getString(TAG1);
            lat = bundle.getDouble(TAG2);
            lng = bundle.getDouble(TAG3);
            updateUI();
        }


    }



    private void updateUI() {

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null) {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            Log.e(";;;;",lat+":"+lng);
            getGeoData(lat,lng);

            AddMarker(lat, lng,true);



            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {


                    if (!stop)
                    {
                        edt_search.setText("");
                        double lat = mMap.getCameraPosition().target.latitude;
                        double lng = mMap.getCameraPosition().target.longitude;
                        getGeoData(lat,lng);
                    }

                    stop = false;





                }
            });



            mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    lat = mMap.getCameraPosition().target.latitude;
                    lng =  mMap.getCameraPosition().target.longitude;
                    AddMarker(lat,lng,true);



                }
            });

        }
    }

    private void Search(String query) {

        image_pin.setVisibility(View.GONE);
        progBar.setVisibility(View.VISIBLE);

        String fields = "id,place_id,name,geometry,formatted_address";

        Api.getService("https://maps.googleapis.com/maps/api/")
                .searchOnMap("textquery", query, fields, current_language, getString(R.string.map_api_key2))
                .enqueue(new Callback<PlaceMapDetailsData>() {
                    @Override
                    public void onResponse(Call<PlaceMapDetailsData> call, Response<PlaceMapDetailsData> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            image_pin.setVisibility(View.VISIBLE);
                            progBar.setVisibility(View.GONE);

                            if (response.body().getCandidates().size() > 0) {

                                address = response.body().getCandidates().get(0).getFormatted_address().replace("Unnamed Road,","");
                                place_id = response.body().getCandidates().get(0).getPlace_id();
                                tv_address.setText(address+"");
                                AddMarker(response.body().getCandidates().get(0).getGeometry().getLocation().getLat(),response.body().getCandidates().get(0).getGeometry().getLocation().getLng(),false);
                            }
                        }
                        else
                        {

                            image_pin.setVisibility(View.VISIBLE);
                            progBar.setVisibility(View.GONE);
                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceMapDetailsData> call, Throwable t) {
                        try {


                            image_pin.setVisibility(View.VISIBLE);
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void getGeoData(final double lat, double lng)
    {
        image_pin.setVisibility(View.GONE);
        progBar.setVisibility(View.VISIBLE);

        String location = lat+","+lng;
        Api.getService("https://maps.googleapis.com/maps/api/")
                .getGeoData(location,current_language,getString(R.string.map_api_key2))
                .enqueue(new Callback<PlaceGeocodeData>() {
                    @Override
                    public void onResponse(Call<PlaceGeocodeData> call, Response<PlaceGeocodeData> response) {
                        Log.e("kkkkkk",response.code()+""+response.body().getResults().size());
                        if (response.isSuccessful() && response.body() != null) {

                            image_pin.setVisibility(View.VISIBLE);
                            progBar.setVisibility(View.GONE);

                            if (response.body().getResults().size()>0)
                            {
                                address =response.body().getResults().get(0).getFormatted_address().replace("Unnamed Road,","");
                                place_id = response.body().getResults().get(0).getPlace_id();
                                tv_address.setText(address+"");
                                stop = true;
                                AddMarker(response.body().getResults().get(0).getGeometry().getLocation().getLat(),response.body().getResults().get(0).getGeometry().getLocation().getLng(),true);
                            }
                        }
                        else
                        {

                            image_pin.setVisibility(View.VISIBLE);
                            progBar.setVisibility(View.GONE);
                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<PlaceGeocodeData> call, Throwable t) {
                        try {


                            image_pin.setVisibility(View.VISIBLE);
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void AddMarker(double lat, double lng,boolean isMove) {

        this.lat = lat;
        this.lng = lng;

        if (marker == null) {
            IconGenerator iconGenerator = new IconGenerator(activity);
            iconGenerator.setBackground(null);
            View view = LayoutInflater.from(activity).inflate(R.layout.search_map_icon, null);
            iconGenerator.setContentView(view);
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),zoom));
        } else {
            marker.setPosition(new LatLng(lat, lng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),zoom));


        }
    }



}
