package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.adapters.NotificationsAdapter;
import com.endpoint.Jack.models.NotificationDataModel;
import com.endpoint.Jack.models.NotificationModel;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Client_Notifications extends Fragment {

    private ProgressBar progBar;
    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private ClientHomeActivity activity;
    private LinearLayout ll_not;
    private UserModel userModel;
    private List<NotificationModel> notificationModelList;
    private NotificationsAdapter adapter;
    private UserSingleTone userSingleTone;
    private boolean isLoading = false;
    private int current_page = 1;
    private Call<NotificationDataModel> call;
    private boolean isFirstTime = true;
    private int lastSelectedItem = -1;
    private String current_language;


    @Override
    public void onStart() {
        super.onStart();
        if (!isFirstTime && adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_notifications, container, false);
        initView(view);
        return view;
    }


    public static Fragment_Client_Notifications newInstance() {
        return new Fragment_Client_Notifications();
    }

    private void initView(View view) {
        notificationModelList = new ArrayList<>();

        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang",Locale.getDefault().getLanguage());
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        ll_not = view.findViewById(R.id.ll_not);

        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        adapter = new NotificationsAdapter(notificationModelList, activity, this, userModel.getData().getUser_type());
        recView.setAdapter(adapter);

        recView.setDrawingCacheEnabled(true);
        recView.setItemViewCacheSize(25);
        recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int lastVisibleItem = ((LinearLayoutManager) manager).findLastCompletelyVisibleItemPosition();
                    int totalItems = manager.getItemCount();

                    if (lastVisibleItem >= (totalItems - 5) && !isLoading) {
                        isLoading = true;
                        notificationModelList.add(null);
                        adapter.notifyItemInserted(notificationModelList.size() - 1);
                        int next_page = current_page + 1;
                        loadMore(next_page);
                    }
                }
            }
        });
        getNotification();

    }

    public void getNotification() {



        if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT)) {
            call = Api.getService(Tags.base_url).getNotification(userModel.getData().getUser_id(), "client", 1);
            if (lastSelectedItem!=-1)
            {
                if (notificationModelList.size()>0)
                {
                    notificationModelList.remove(lastSelectedItem);
                    adapter.notifyItemRemoved(lastSelectedItem);
                    lastSelectedItem = -1;


                }
            }
        } else if (userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE)) {
            call = Api.getService(Tags.base_url).getNotification(userModel.getData().getUser_id(), "driver", 1);

        }


        call.enqueue(new Callback<NotificationDataModel>() {
            @Override
            public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
                progBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {

                    notificationModelList.clear();

                    if (response.body() != null && response.body().getData().size() > 0) {
                        ll_not.setVisibility(View.GONE);
                        notificationModelList.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                        isFirstTime = false;
                    } else {
                        ll_not.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();


                    }
                } else {

                    Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationDataModel> call, Throwable t) {
                try {
                    progBar.setVisibility(View.GONE);
                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                    Log.e("Error", t.getMessage());
                } catch (Exception e) {
                }
            }
        });


    }

    private void loadMore(int page_index) {

        if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT)) {
            call = Api.getService(Tags.base_url).getNotification(userModel.getData().getUser_id(), "client", page_index);
        } else if (userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE)) {
            call = Api.getService(Tags.base_url).getNotification(userModel.getData().getUser_id(), "driver", page_index);

        }


        call.enqueue(new Callback<NotificationDataModel>() {
            @Override
            public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
                notificationModelList.remove(notificationModelList.size() - 1);
                adapter.notifyDataSetChanged();
                isLoading = false;

                if (response.isSuccessful()) {

                    if (response.body() != null && response.body().getData().size() > 0) {
                        notificationModelList.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                        current_page = response.body().getMeta().getCurrent_page();


                    }
                } else {


                    Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationDataModel> call, Throwable t) {
                try {
                    isLoading = false;
                    if (notificationModelList.get(notificationModelList.size() - 1) == null) {
                        notificationModelList.remove(notificationModelList.size() - 1);
                        adapter.notifyDataSetChanged();
                    }
                    progBar.setVisibility(View.GONE);
                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                    Log.e("Error", t.getMessage());
                } catch (Exception e) {
                }
            }
        });

    }

    public void setItemData(NotificationModel notificationModel, int pos)
    {
        if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT) && Integer.parseInt(notificationModel.getOrder_status())<Tags.STATE_CLIENT_ACCEPT_OFFER) {
            CreateAlertDialogForDrivers(notificationModel);
            //activity.DisplayFragmentClientDelegateOffer(notificationModel);
            lastSelectedItem = pos;
        }else if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT) && Integer.parseInt(notificationModel.getOrder_status())==Tags.STATE_DELEGATE_DELIVERED_ORDER) {
            activity.CreateAddRateAlertDialog(notificationModel);
        }

        /*else if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT) && notificationModel.getOrder_status().equals(String.valueOf(Tags.STATE_DELEGATE_REFUSE_ORDER)))
        {

            activity.CreateAcceptRefuseDialog(notificationModel.getOrder_id(),Double.parseDouble(notificationModel.getPlace_lat()),Double.parseDouble(notificationModel.getPlace_long()),notificationModel.getClient_id());
        }*/
    }
    private void CreateAlertDialogForDrivers(final NotificationModel notificationModel)
    {
        final NotificationModel.Drivers drivers = notificationModel.getDriver_list().get(0);

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        Currency currency = Currency.getInstance(new Locale(current_language,userModel.getData().getUser_country()));

        View view = LayoutInflater.from(activity).inflate(R.layout.drivers_dialog,null);
        CircleImageView image = view.findViewById(R.id.image);
        ImageView image_certified = view.findViewById(R.id.image_certified);
        SimpleRatingBar rateBar = view.findViewById(R.id.rateBar);
        TextView tv_name = view.findViewById(R.id.tv_name);
        TextView tv_rate = view.findViewById(R.id.tv_rate);
        TextView tv_delivery_cost = view.findViewById(R.id.tv_delivery_cost);
        TextView tv_distance = view.findViewById(R.id.tv_distance);
        TextView tv_offers_count = view.findViewById(R.id.tv_offers_count);
        TextView tv_certified = view.findViewById(R.id.tv_certified);

        CardView cardView = view.findViewById(R.id.cardView);
        Button btn_accept = view.findViewById(R.id.btn_accept);
        Button btn_refuse = view.findViewById(R.id.btn_refuse);

        Picasso.with(activity).load(Uri.parse(Tags.IMAGE_URL+notificationModel.getDriver_list().get(0).getUser_image())).placeholder(R.drawable.logo_only).into(image);
        tv_name.setText(drivers.getUser_full_name());
        tv_rate.setText("("+drivers.getRate()+")");
        rateBar.setRating((float) drivers.getRate());
        tv_delivery_cost.setText(drivers.getDriver_offer()+" "+currency.getSymbol());
        tv_distance.setText(drivers.getDistance()+" "+getString(R.string.km));
        tv_offers_count.setText("("+notificationModel.getDriver_list().size()+")");

        if (drivers.isCertified())
        {
            image_certified.setImageResource(R.drawable.ic_checked);
            image_certified.setColorFilter(ContextCompat.getColor(activity,R.color.active));
            tv_certified.setText(getString(R.string.certified_account));
            tv_certified.setTextColor(ContextCompat.getColor(activity,R.color.active));

        }else
            {
                image_certified.setImageResource(R.drawable.checked_not_certified);
                tv_certified.setText(getString(R.string.not_certified));
                tv_certified.setTextColor(ContextCompat.getColor(activity,R.color.rating));

            }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                if (notificationModel.getDriver_list().size()>0)
                {
                    activity.DisplayFragmentDelegatesResult(notificationModel);
                }

            }
        });

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.clientAcceptOffer(drivers.getDriver_id(),userModel.getData().getUser_id(),notificationModel.getOrder_id(),"accept",notificationModel.getDriver_offer(),"dialog");
            }
        });

        btn_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.clientRefuseOffer(drivers.getId_notification());

            }
        });


        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }
}
