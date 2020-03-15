package com.creativeshare.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.Jack.models.OrderDataModel;
import com.creativeshare.Jack.models.UserModel;
import com.creativeshare.Jack.share.Common;
import com.creativeshare.Jack.singletone.UserSingleTone;
import com.creativeshare.Jack.tags.Tags;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Fragment_Delegate_Add_Offer extends Fragment {

    private final static  String TAG = "Data";
    private ImageView image_back,order_image;
    private LinearLayout ll_back,ll_client_container,ll_address,ll_shipment;
    private CircleImageView image;
    private TextView tv_client_name,tv_order_details,tv_order_address,tv_location_pickup,tv_location_dropoff;
    private EditText edt_delivery_cost;
    private Button btn_accept,btn_refused;
    private OrderDataModel.OrderModel orderModel;
    private String current_language;
    private ClientHomeActivity activity;
    private AppBarLayout app_bar;
    private UserSingleTone userSingleTone;
    private UserModel userModel;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegate_add_offer,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Delegate_Add_Offer newInstance(OrderDataModel.OrderModel orderModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,orderModel);

        Fragment_Delegate_Add_Offer fragment_delegate_add_offer = new Fragment_Delegate_Add_Offer();
        fragment_delegate_add_offer.setArguments(bundle);
        return fragment_delegate_add_offer;
    }

    private void initView(View view)
    {
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        activity = (ClientHomeActivity) getActivity();
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
        order_image = view.findViewById(R.id.order_image);

        ll_back = view.findViewById(R.id.ll_back);
        image = view.findViewById(R.id.image);
        tv_client_name = view.findViewById(R.id.tv_client_name);
        tv_order_details = view.findViewById(R.id.tv_order_details);
        tv_order_address = view.findViewById(R.id.tv_order_address);
        edt_delivery_cost = view.findViewById(R.id.edt_delivery_cost);
        btn_accept = view.findViewById(R.id.btn_accept);
        btn_refused = view.findViewById(R.id.btn_refused);
        ll_client_container = view.findViewById(R.id.ll_client_container);

        ll_address = view.findViewById(R.id.ll_address);

        ll_shipment = view.findViewById(R.id.ll_shipment);
        tv_location_pickup = view.findViewById(R.id.tv_location_pickup);
        tv_location_dropoff = view.findViewById(R.id.tv_location_dropoff);

        app_bar = view.findViewById(R.id.app_bar);


        Bundle bundle = getArguments();

        if (bundle!=null)
        {
            orderModel = (OrderDataModel.OrderModel) bundle.getSerializable(TAG);
            UpdateUI(orderModel);
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
                CheckData();
            }
        });

        btn_refused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.delegateRefuse_FinishOrder(userModel.getData().getUser_id(),orderModel.getClient_id(),orderModel.getOrder_id(),"refuse");

            }
        });


        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int totalRange= appBarLayout.getTotalScrollRange();
                if ((totalRange+verticalOffset)<=50)
                {
                    ll_client_container.setVisibility(View.GONE);
                }else
                    {
                        ll_client_container.setVisibility(View.VISIBLE);

                    }
            }
        });


    }



    private void UpdateUI(OrderDataModel.OrderModel orderModel) {
        if (orderModel!=null)
        {
            Picasso.with(activity).load(Tags.IMAGE_URL+orderModel.getClient_user_image()).placeholder(R.drawable.logo_only).fit().into(image);
            tv_client_name.setText(orderModel.getClient_user_full_name());
            tv_order_details.setText(orderModel.getOrder_details());

            if (orderModel.getOrder_image().equals("0"))
            {
                order_image.setVisibility(View.GONE);
            }else
            {
                Picasso.with(activity).load(Uri.parse(Tags.IMAGE_URL+orderModel.getOrder_image())).fit().into(order_image);
                order_image.setVisibility(View.VISIBLE);

            }



            if (orderModel.getOrder_type().equals("1"))
            {
                tv_order_address.setText(orderModel.getClient_address());
                tv_order_address.setVisibility(View.VISIBLE);
                ll_address.setVisibility(View.VISIBLE);

                ll_shipment.setVisibility(View.GONE);
            }else if (orderModel.getOrder_type().equals("2"))
            {
                tv_order_address.setVisibility(View.GONE);
                ll_address.setVisibility(View.GONE);

                tv_location_pickup.setText(orderModel.getPlace_address());
                tv_location_dropoff.setText(orderModel.getClient_address());
                ll_shipment.setVisibility(View.VISIBLE);

            }

        }

    }

    private void CheckData() {
        String m_cost = edt_delivery_cost.getText().toString().trim();
        if (!TextUtils.isEmpty(m_cost))
        {
            edt_delivery_cost.setError(null);
            Common.CloseKeyBoard(activity,edt_delivery_cost);
            activity.delegateAcceptOrder(userModel.getData().getUser_id(),orderModel.getClient_id(),orderModel.getOrder_id(),m_cost);
        }else
            {
                edt_delivery_cost.setError(getString(R.string.field_req));
            }
    }


}
