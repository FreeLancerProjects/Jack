package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_orders;

import android.graphics.PorterDuff;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.adapters.OrdersAdapter;
import com.endpoint.Jack.adapters.ViewPagerAdapter;
import com.endpoint.Jack.models.OrderDataModel;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Client_Orders extends Fragment {

    private TabLayout tab;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private List<Fragment> fragmentList;
    private List<String> titleList;
    private ArrayAdapter spinnerArrayAdapter;
private Spinner sptype;
    private ArrayList<String> spinnerArray;
private ClientHomeActivity clientHomeActivity;

    private ProgressBar progBar;
    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private ClientHomeActivity activity;
    private TextView tv_no_orders;
    private List<OrderDataModel.OrderModel> orderModelList;
    private OrdersAdapter ordersAdapteradapter;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private boolean isLoading = false;
    private int current_page = 1;
    private Call<OrderDataModel> call;
    private boolean isFirstTime = true;
    private String type="current";
    @Override
    public void onStart() {
        super.onStart();
        if (!isFirstTime&&ordersAdapteradapter!=null)
        {
            ordersAdapteradapter.notifyDataSetChanged();
        }
    }
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
        clientHomeActivity=(ClientHomeActivity)getActivity();
        tab = view.findViewById(R.id.tab);
        pager = view.findViewById(R.id.pager);
        sptype=view.findViewById(R.id.sp_type);
   spinnerArray = new ArrayList<String>();
   spinnerArray.add(clientHomeActivity.getResources().getString(R.string.active1));
        spinnerArray.add(clientHomeActivity.getResources().getString(R.string.inactive1));

        spinnerArrayAdapter = new ArrayAdapter(clientHomeActivity,
                R.layout.spinner_item,
                spinnerArray);
        sptype.setAdapter(spinnerArrayAdapter);
      //  pager.setVisibility(View.GONE);
       // tab.setupWithViewPager(pager);
//        pager.setOffscreenPageLimit(3);
//        fragmentList = new ArrayList<>();
//        titleList = new ArrayList<>();
//
//        fragmentList.add(Fragment_Client_New_Orders.newInstance());
//        fragmentList.add(Fragment_Client_Current_Orders.newInstance());
//        fragmentList.add(Fragment_Client_Previous_Orders.newInstance());
//
//       titleList.add(getString(R.string.my_orders));
//        titleList.add(getString(R.string.current));
//        titleList.add(getString(R.string.previous));
//
//        adapter = new ViewPagerAdapter(getChildFragmentManager());
//        adapter.AddFragments(fragmentList);
//        adapter.AddTitles(titleList);
//        pager.setAdapter(adapter);
        orderModelList = new ArrayList<>();
        activity = (ClientHomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        tv_no_orders = view.findViewById(R.id.tv_no_orders);

        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        ordersAdapteradapter = new OrdersAdapter(orderModelList,activity,userModel.getData().getUser_type(),this);
        recView.setAdapter(ordersAdapteradapter);

        recView.setDrawingCacheEnabled(true);
        recView.setItemViewCacheSize(25);
        recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager); recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy>0)
            {
                int lastVisibleItem = ((LinearLayoutManager)manager).findLastCompletelyVisibleItemPosition();
                int totalItems = manager.getItemCount();

                if (lastVisibleItem>=(totalItems-5)&&!isLoading)
                {
                    isLoading = true;
                    orderModelList.add(null);
                    ordersAdapteradapter.notifyItemInserted(orderModelList.size()-1);
                    int next_page = current_page+1;
                    loadMore(next_page);
                }
            }
        }
    });
        getOrders();
sptype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position==0){
            if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT)){
            type="current";}
            else {
                type="new";
            }
        }
        else {
            type="old";
        }
        getOrders();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
});
    }

    public void getOrders()
    {

        if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT))
        {
            call  = Api.getService(Tags.base_url).getClientOrders(userModel.getData().getUser_id(),type, 1);
        }else if (userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE))
        {
            call  = Api.getService(Tags.base_url).getDelegateOrders(userModel.getData().getUser_id(),type, 1);

        }

        progBar.setVisibility(View.VISIBLE);
orderModelList.clear();
ordersAdapteradapter.notifyDataSetChanged();
        call.enqueue(new Callback<OrderDataModel>() {
            @Override
            public void onResponse(Call<OrderDataModel> call, Response<OrderDataModel> response) {
                progBar.setVisibility(View.GONE);
                if (response.isSuccessful())
                {
                    orderModelList.clear();

                    if (response.body()!=null&&response.body().getData().size()>0)
                    {
                        tv_no_orders.setVisibility(View.GONE);
                        orderModelList.addAll(response.body().getData());
                        ordersAdapteradapter.notifyDataSetChanged();
                        isFirstTime =false;

                    }else
                    {
                        ordersAdapteradapter.notifyDataSetChanged();

                        tv_no_orders.setVisibility(View.VISIBLE);

                    }
                }else
                {

                    Toast.makeText(activity,R.string.failed, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderDataModel> call, Throwable t) {
                try {
                    progBar.setVisibility(View.GONE);
                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }catch (Exception e){}
            }
        });
    }

    private void loadMore(int page_index)
    {



        if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT))
        {
            call  = Api.getService(Tags.base_url).getClientOrders(userModel.getData().getUser_id(),type, page_index);
        }else if (userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE))
        {
            call  = Api.getService(Tags.base_url).getDelegateOrders(userModel.getData().getUser_id(),type, page_index);

        }


        call.enqueue(new Callback<OrderDataModel>() {
            @Override
            public void onResponse(Call<OrderDataModel> call, Response<OrderDataModel> response) {
                orderModelList.remove(orderModelList.size()-1);
                ordersAdapteradapter.notifyDataSetChanged();
                isLoading = false;

                if (response.isSuccessful())
                {

                    if (response.body()!=null&&response.body().getData().size()>0)
                    {
                        orderModelList.addAll(response.body().getData());
                        ordersAdapteradapter.notifyDataSetChanged();
                        current_page = response.body().getMeta().getCurrent_page();

                    }
                }else
                {


                    Toast.makeText(activity,R.string.failed, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderDataModel> call, Throwable t) {
                try {
                    isLoading = false;
                    if (orderModelList.get(orderModelList.size()-1)==null)
                    {
                        orderModelList.remove(orderModelList.size()-1);
                        ordersAdapteradapter.notifyDataSetChanged();
                    }
                    progBar.setVisibility(View.GONE);
                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }catch (Exception e){}
            }
        });
    }
    public void NavigateToFragmentRefresh(int pos)
    {
//        pager.setCurrentItem(pos,true);
//        if (pos==0)
//        {
//            Fragment_Client_New_Orders fragment_client_new_orders = (Fragment_Client_New_Orders) fragmentList.get(0);
//            fragment_client_new_orders.getOrders();
//
//        }else if (pos==1)
//        {
//            Fragment_Client_Current_Orders  fragment_client_current_orders = (Fragment_Client_Current_Orders) fragmentList.get(1);
//            fragment_client_current_orders.getOrders();
//
//        }else if (pos ==2)
//        {
//            Fragment_Client_Previous_Orders fragment_client_previous_orders = (Fragment_Client_Previous_Orders) fragmentList.get(2);
//            fragment_client_previous_orders.getOrders();
//
//        }
    }

    public void RefreshOrderFragments()
    {
        if(fragmentList!=null&&fragmentList.size()>0) {
            Fragment_Client_New_Orders fragment_client_new_orders = (Fragment_Client_New_Orders) fragmentList.get(0);
            Fragment_Client_Current_Orders fragment_client_current_orders = (Fragment_Client_Current_Orders) fragmentList.get(1);
            Fragment_Client_Previous_Orders fragment_client_previous_orders = (Fragment_Client_Previous_Orders) fragmentList.get(2);

            fragment_client_new_orders.getOrders();
            fragment_client_current_orders.getOrders();
            fragment_client_previous_orders.getOrders();
        }

    }
    public void setItemData(OrderDataModel.OrderModel orderModel) {

        if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT))
        {
            activity.DisplayFragmentClientOrderDetails(orderModel);

        }else
        {
            activity.DisplayFragmentDelegateCurrentOrderDetails(orderModel);
        }
    }
}
