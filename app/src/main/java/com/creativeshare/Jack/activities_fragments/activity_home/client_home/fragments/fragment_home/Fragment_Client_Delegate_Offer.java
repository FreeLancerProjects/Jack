package com.creativeshare.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.Jack.models.NotificationModel;
import com.creativeshare.Jack.models.UserModel;
import com.creativeshare.Jack.singletone.UserSingleTone;
import com.creativeshare.Jack.tags.Tags;
import com.google.android.material.appbar.AppBarLayout;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.util.Currency;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Fragment_Client_Delegate_Offer extends Fragment {

    private final static  String TAG = "Data";
    private ImageView image_back,order_image;
    private LinearLayout ll_back,ll_action_container,ll_container;
    private CircleImageView image;
    private AppBarLayout app_bar;
    private TextView tv_delegate_name,tv_order_details,tv_order_address,tv_delivery_cost,tv_rate;
    private Button btn_accept,btn_refused;
    private NotificationModel notificationModel;
    private SimpleRatingBar rateBar;
    private String current_language;
    private ClientHomeActivity activity;
    private UserSingleTone userSingleTone;
    private UserModel userModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_delegate_offer,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Client_Delegate_Offer newInstance(NotificationModel notificationModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,notificationModel);

        Fragment_Client_Delegate_Offer fragment_client_delegate_offer = new Fragment_Client_Delegate_Offer();
        fragment_client_delegate_offer.setArguments(bundle);
        return fragment_client_delegate_offer;
    }

    private void initView(View view)
    {
        activity = (ClientHomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        image_back = view.findViewById(R.id.image_back);

        if (current_language.equals("ar"))
        {
            image_back.setImageResource(R.drawable.ic_right_arrow);
        }else
        {

            image_back.setImageResource(R.drawable.ic_left_arrow);
        }
        ll_action_container = view.findViewById(R.id.ll_action_container);
        ll_container = view.findViewById(R.id.ll_container);
        app_bar = view.findViewById(R.id.app_bar);

        order_image = view.findViewById(R.id.order_image);
        ll_back = view.findViewById(R.id.ll_back);
        image = view.findViewById(R.id.image);
        tv_delegate_name = view.findViewById(R.id.tv_delegate_name);
        tv_order_details = view.findViewById(R.id.tv_order_details);
        tv_order_address = view.findViewById(R.id.tv_order_address);
        tv_delivery_cost = view.findViewById(R.id.tv_delivery_cost);
        btn_accept = view.findViewById(R.id.btn_accept);
        btn_refused = view.findViewById(R.id.btn_refused);
        rateBar = view.findViewById(R.id.rateBar);
        tv_rate = view.findViewById(R.id.tv_rate);


        Bundle bundle = getArguments();

        if (bundle!=null)
        {
            notificationModel = (NotificationModel) bundle.getSerializable(TAG);
            UpdateUI(notificationModel);
        }

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.clientAcceptRefuseOffer(notificationModel.getDriver_id(),notificationModel.getClient_id(),notificationModel.getOrder_id(),"accept",Double.parseDouble(notificationModel.getPlace_lat()),Double.parseDouble(notificationModel.getPlace_long()));
            }
        });

        btn_refused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.clientAcceptRefuseOffer(notificationModel.getDriver_id(),notificationModel.getClient_id(),notificationModel.getOrder_id(),"refuse",Double.parseDouble(notificationModel.getPlace_lat()),Double.parseDouble(notificationModel.getPlace_long()));

            }
        });


        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int totalRange= appBarLayout.getTotalScrollRange();
                if ((totalRange+verticalOffset)<=50)
                {
                    ll_container.setVisibility(View.GONE);
                }else
                {
                    ll_container.setVisibility(View.VISIBLE);

                }
            }
        });


    }



    private void UpdateUI(NotificationModel notificationModel) {
        if (notificationModel!=null)
        {
            if (notificationModel.getOrder_status().equals(String.valueOf(Tags.STATE_DELEGATE_SEND_OFFER)))
            {
                ll_action_container.setVisibility(View.VISIBLE);
            }else
                {
                    ll_action_container.setVisibility(View.GONE);

                }

            if (notificationModel.getOrder_image().equals("0"))
            {
                order_image.setVisibility(View.GONE);
            }else
            {
                Picasso.with(activity).load(Uri.parse(Tags.base_url+notificationModel.getOrder_image())).fit().into(order_image);
                order_image.setVisibility(View.VISIBLE);

            }
            Currency currency = Currency.getInstance(new Locale(current_language,userModel.getData().getUser_country()));
            Picasso.with(activity).load(Tags.IMAGE_URL+notificationModel.getFrom_user_image()).placeholder(R.drawable.logo_only).fit().into(image);
            tv_delegate_name.setText(notificationModel.getFrom_user_full_name());
            tv_order_details.setText(notificationModel.getOrder_details());
            tv_order_address.setText(notificationModel.getClient_address());
            tv_delivery_cost.setText(notificationModel.getDriver_offer()+" "+currency.getSymbol());
            tv_rate.setText("("+notificationModel.getRate()+")");
            SimpleRatingBar.AnimationBuilder builder = rateBar.getAnimationBuilder();
            builder.setDuration(1500);
            builder.setRepeatCount(0);
            builder.setRatingTarget((float) notificationModel.getRate());
            builder.setInterpolator(new AccelerateInterpolator());
            builder.start();
        }

    }




}
