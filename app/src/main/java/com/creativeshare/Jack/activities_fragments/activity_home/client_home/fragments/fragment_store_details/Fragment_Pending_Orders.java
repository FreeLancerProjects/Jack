package com.creativeshare.Jack.activities_fragments.activity_home.client_home.fragments.fragment_store_details;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.Jack.adapters.WaitOrderAdapter;
import com.creativeshare.Jack.models.PlaceModel;
import com.creativeshare.Jack.models.WatingOrderData;
import com.creativeshare.Jack.remote.Api;
import com.creativeshare.Jack.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Pending_Orders extends Fragment {

    private static final String TAG = "Data";
    private TextView tv_no_orders;
    private ClientHomeActivity activity;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private List<WatingOrderData.WaitOrder> waitOrderList;
    private WaitOrderAdapter adapter;
    private ProgressBar progBar;
    private int current_page = 1;
    private PlaceModel placeModel;
    private boolean isLoading = false;

    public static Fragment_Pending_Orders newInstance(PlaceModel placeModel)
    {
        Fragment_Pending_Orders fragment_pending_orders = new Fragment_Pending_Orders();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,placeModel);

        fragment_pending_orders.setArguments(bundle);
        return fragment_pending_orders;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_order,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        waitOrderList = new ArrayList<>();
        tv_no_orders = view.findViewById(R.id.tv_no_orders);
        recView = view.findViewById(R.id.recView);
        recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recView.setDrawingCacheEnabled(true);
        recView.setItemViewCacheSize(25);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        adapter = new WaitOrderAdapter(waitOrderList, activity);
        recView.setAdapter(adapter);
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {

                    int lastVisibleItemPos = manager.findLastCompletelyVisibleItemPosition();
                    int total_item = recView.getAdapter().getItemCount();

                    if (lastVisibleItemPos >= (total_item-5)&&!isLoading)
                    {
                        int next_page = current_page+1;
                        waitOrderList.add(null);
                        adapter.notifyItemInserted(waitOrderList.size()-1);
                        isLoading = true;
                        loadMore(placeModel.getPlace_id(),next_page);
                    }
                }
            }
        });

        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            placeModel = (PlaceModel) bundle.getSerializable(TAG);
            updateUI(placeModel);

        }
    }



    private void updateUI(PlaceModel placeModel) {

        Log.e("place_id",placeModel.getPlace_id());
        getOrders(placeModel.getPlace_id());
    }

    private void getOrders(String place_id) {
        Api.getService(Tags.base_url)
                .getWaitingOrders(place_id,1)
                .enqueue(new Callback<WatingOrderData>() {
                    @Override
                    public void onResponse(Call<WatingOrderData> call, Response<WatingOrderData> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {

                            if (response.body()!=null&&response.body().getData().size()>0)
                            {
                                tv_no_orders.setVisibility(View.GONE);
                                waitOrderList.addAll(response.body().getData());
                                activity.AddWaitOrderCount(response.body().getMeta().getTotal_orders());
                            }else
                                {
                                    tv_no_orders.setVisibility(View.VISIBLE);

                                    activity.AddWaitOrderCount(0);
                                    adapter.notifyDataSetChanged();

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
                    public void onFailure(Call<WatingOrderData> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    private void loadMore(String place_id,int next_page)
    {
        Log.e("next_page",next_page+"");
        Api.getService(Tags.base_url)
                .getWaitingOrders(place_id,next_page)
                .enqueue(new Callback<WatingOrderData>() {
                    @Override
                    public void onResponse(Call<WatingOrderData> call, Response<WatingOrderData> response) {
                        if (response.isSuccessful())
                        {
                            waitOrderList.remove(waitOrderList.size()-1);
                            adapter.notifyDataSetChanged();
                            if (response.body()!=null&&response.body().getData().size()>0)
                            {
                                Log.e("next_page",current_page+"");

                                int old_lastPos = waitOrderList.size()-1;


                                waitOrderList.addAll(response.body().getData());
                                current_page = response.body().getMeta().getCurrent_page();

                                adapter.notifyItemRangeInserted(old_lastPos,waitOrderList.size()-1);


                            }
                            isLoading = false;

                        }else
                        {
                            isLoading = false;
                            waitOrderList.remove(waitOrderList.size()-1);
                            adapter.notifyDataSetChanged();

                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<WatingOrderData> call, Throwable t) {
                        try {
                            isLoading = false;
                            waitOrderList.remove(waitOrderList.size()-1);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

}
