package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.adapters.Delegates_Result_Adapter;
import com.endpoint.Jack.models.NotificationModel;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.singletone.UserSingleTone;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Delegates_Result extends Fragment {
    private static final String TAG1 = "DATA";
    private ClientHomeActivity activity;
    private ImageView arrow;
    private LinearLayout ll_back;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private List<NotificationModel.Drivers> driversList;
    private Delegates_Result_Adapter adapter;
    private NotificationModel notificationModel;
    private String current_language;
    private UserSingleTone userSingleTone;
    private UserModel userModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegates_results, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Delegates_Result newInstance(NotificationModel notificationModel) {
        Fragment_Delegates_Result fragment_delegates_result = new Fragment_Delegates_Result();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG1,  notificationModel);
        fragment_delegates_result.setArguments(bundle);
        return fragment_delegates_result;
    }

    private void initView(View view) {
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        driversList = new ArrayList<>();



        arrow = view.findViewById(R.id.arrow);

        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);
        } else {
            arrow.setImageResource(R.drawable.ic_right_arrow);

        }

        ll_back = view.findViewById(R.id.ll_back);

        recView = view.findViewById(R.id.recView);
        recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recView.setDrawingCacheEnabled(true);
        recView.setItemViewCacheSize(25);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        recView.setAdapter(adapter);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {

            notificationModel = (NotificationModel) bundle.getSerializable(TAG1);
            updateUi(notificationModel);

        }
    }

    private void updateUi(NotificationModel notificationModel) {
        driversList.addAll(notificationModel.getDriver_list());
        Currency currency = Currency.getInstance(new Locale(current_language,userModel.getData().getUser_country()));

        adapter = new Delegates_Result_Adapter(driversList,activity,this,currency.getSymbol());
        recView.setAdapter(adapter);
    }


    public void setItemData(NotificationModel.Drivers drivers) {
        activity.clientAcceptOffer(drivers.getDriver_id(),userModel.getData().getUser_id(),notificationModel.getOrder_id(),"accept",notificationModel.getDriver_offer(),"fragment_delegate_result");
        //activity.setDelegate_id(delegateModel.getDriver_id(),client_id,order_id,type);

    }
}
