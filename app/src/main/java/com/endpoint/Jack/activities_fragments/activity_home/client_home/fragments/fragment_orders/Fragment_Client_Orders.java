package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.endpoint.Jack.R;
import com.endpoint.Jack.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Client_Orders extends Fragment {

    private TabLayout tab;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private List<Fragment> fragmentList;
    private List<String> titleList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_orders,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Client_Orders newInstance()
    {
        return new Fragment_Client_Orders();
    }

    private void initView(View view)
    {
        tab = view.findViewById(R.id.tab);
        pager = view.findViewById(R.id.pager);
        tab.setupWithViewPager(pager);
        pager.setOffscreenPageLimit(3);
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();

        fragmentList.add(Fragment_Client_New_Orders.newInstance());
        fragmentList.add(Fragment_Client_Current_Orders.newInstance());
        fragmentList.add(Fragment_Client_Previous_Orders.newInstance());

        titleList.add(getString(R.string.new_order));
        titleList.add(getString(R.string.current));
        titleList.add(getString(R.string.previous));

        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.AddFragments(fragmentList);
        adapter.AddTitles(titleList);
        pager.setAdapter(adapter);

    }
    public void NavigateToFragmentRefresh(int pos)
    {
        pager.setCurrentItem(pos,true);
        if (pos==0)
        {
            Fragment_Client_New_Orders fragment_client_new_orders = (Fragment_Client_New_Orders) fragmentList.get(0);
            fragment_client_new_orders.getOrders();

        }else if (pos==1)
        {
            Fragment_Client_Current_Orders  fragment_client_current_orders = (Fragment_Client_Current_Orders) fragmentList.get(1);
            fragment_client_current_orders.getOrders();

        }else if (pos ==2)
        {
            Fragment_Client_Previous_Orders fragment_client_previous_orders = (Fragment_Client_Previous_Orders) fragmentList.get(2);
            fragment_client_previous_orders.getOrders();

        }
    }

    public void RefreshOrderFragments()
    {
        Fragment_Client_New_Orders fragment_client_new_orders = (Fragment_Client_New_Orders) fragmentList.get(0);
        Fragment_Client_Current_Orders  fragment_client_current_orders = (Fragment_Client_Current_Orders) fragmentList.get(1);
        Fragment_Client_Previous_Orders fragment_client_previous_orders = (Fragment_Client_Previous_Orders) fragmentList.get(2);

        fragment_client_new_orders.getOrders();
        fragment_client_current_orders.getOrders();
        fragment_client_previous_orders.getOrders();


    }

}
