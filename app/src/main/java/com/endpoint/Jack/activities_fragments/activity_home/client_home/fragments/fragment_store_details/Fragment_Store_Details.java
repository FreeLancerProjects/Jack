package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_store_details;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.adapters.ViewPagerAdapter;
import com.endpoint.Jack.models.PlaceModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Store_Details extends Fragment {
    private static final String TAG = "Data";
    private static final String TAG2 = "LAT";
    private static final String TAG3 = "LNG";

    private ClientHomeActivity activity;
    private ImageView arrow;
    private ConstraintLayout cons_back;
    private TextView tv_name,tv_distance;
    private TabLayout tab;
    private ViewPager pager;
    private String current_language;
    private PlaceModel placeModel;
    private double lat = 0.0,lng = 0.0;
    private List<String> titleList;
    private List<Fragment> fragmentList;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView tv_counter;



    public static Fragment_Store_Details newInstance(PlaceModel placeModel, double lat , double lng)
    {
        Fragment_Store_Details fragment_store_details = new Fragment_Store_Details();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,placeModel);
        bundle.putDouble(TAG2,  lat);
        bundle.putDouble(TAG3,  lng);

        fragment_store_details.setArguments(bundle);
        return fragment_store_details;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_details,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        titleList = new ArrayList<>();
        fragmentList = new ArrayList<>();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        arrow = view.findViewById(R.id.arrow);
        if (current_language.equals("ar"))
        {
            arrow.setImageResource(R.drawable.ic_right_arrow);
            arrow.setColorFilter(ContextCompat.getColor(activity,R.color.white), PorterDuff.Mode.SRC_IN);

        }else
            {
                arrow.setImageResource(R.drawable.ic_left_arrow);
                arrow.setColorFilter(ContextCompat.getColor(activity,R.color.white), PorterDuff.Mode.SRC_IN);


            }
        cons_back = view.findViewById(R.id.cons_back);
        tv_name = view.findViewById(R.id.tv_name);
        tv_distance = view.findViewById(R.id.tv_distance);
        tab = view.findViewById(R.id.tab);
        pager = view.findViewById(R.id.pager);
        tab.setupWithViewPager(pager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        cons_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            placeModel = (PlaceModel) bundle.getSerializable(TAG);
            lat = bundle.getDouble(TAG2);
            lng = bundle.getDouble(TAG3);

            updateUI(placeModel,lat,lng);

        }



    }



    private void updateUI(PlaceModel placeModel, double lat, double lng) {
        tv_name.setText(placeModel.getName());
        double distance = SphericalUtil.computeDistanceBetween(new LatLng(lat, lng), new LatLng(placeModel.getLat(), placeModel.getLng()));
        tv_distance.setText(String.format("%.2f",(distance/1000))+" "+getString(R.string.away));

        titleList.add(getString(R.string.shop_info));
        titleList.add(getString(R.string.pending_order));
        fragmentList.add(Fragment_Details.newInstance(placeModel,lat,lng));
        fragmentList.add(Fragment_Pending_Orders.newInstance(placeModel));

        viewPagerAdapter.AddFragments(fragmentList);
        viewPagerAdapter.AddTitles(titleList);
        pager.setAdapter(viewPagerAdapter);

        AddBudget();


    }

    private void AddBudget() {
        View view = LayoutInflater.from(activity).inflate(R.layout.tab_budget_layout,null);
        tv_counter = view.findViewById(R.id.tv_counter);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.pending_order));
        tab.getTabAt(1).setCustomView(view);

    }
    public void AddCounter(int counter)
    {
        tv_counter.setText(counter+"");
        tv_counter.setVisibility(View.VISIBLE);
    }

}
