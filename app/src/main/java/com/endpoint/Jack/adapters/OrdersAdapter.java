package com.endpoint.Jack.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_orders.Fragment_Client_Current_Orders;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_orders.Fragment_Client_New_Orders;
import com.endpoint.Jack.models.OrderDataModel;
import com.endpoint.Jack.share.TimeAgo;
import com.endpoint.Jack.tags.Tags;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int ITEM_LOAD = 2;
    private String user_type;
    private List<OrderDataModel.OrderModel> orderModelList;
    private Context context;
    private Fragment fragment;

    public OrdersAdapter(List<OrderDataModel.OrderModel> orderModelList, Context context,String user_type,Fragment fragment) {

        this.orderModelList = orderModelList;
        this.context = context;
        this.user_type = user_type;
        this.fragment = fragment;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.orders_row, parent, false);
            return new MyHolder(view);
        }else
            {
                View view = LayoutInflater.from(context).inflate(R.layout.load_more_row, parent, false);
                return new LoadMoreHolder(view);
            }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder)
        {

            final MyHolder myHolder = (MyHolder) holder;
            OrderDataModel.OrderModel orderModel = orderModelList.get(myHolder.getAdapterPosition());
            myHolder.BindData(orderModel);

            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderDataModel.OrderModel orderModel = orderModelList.get(myHolder.getAdapterPosition());

                    if (fragment instanceof Fragment_Client_Current_Orders)
                    {
                        Fragment_Client_Current_Orders fragment_client_current_orders = (Fragment_Client_Current_Orders) fragment;
                        fragment_client_current_orders.setItemData(orderModel);
                    }else if (fragment instanceof Fragment_Client_New_Orders)
                    {
                        Fragment_Client_New_Orders fragment_client_new_orders = (Fragment_Client_New_Orders) fragment;
                        fragment_client_new_orders.setItemData(orderModel);
                    }
                }
            });


        }else
            {
                LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
                loadMoreHolder.progBar.setIndeterminate(true);
            }
    }

    @Override
    public int getItemCount() {
        return orderModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private ImageView image_state;
        private TextView tv_delivery_time,tv_order_num,tv_order_date,tv_order_state,tv_details;

        public MyHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            image_state = itemView.findViewById(R.id.image_state);
            tv_order_date = itemView.findViewById(R.id.tv_order_date);
            tv_delivery_time = itemView.findViewById(R.id.tv_delivery_time);
            tv_order_num = itemView.findViewById(R.id.tv_order_num);
            tv_order_state = itemView.findViewById(R.id.tv_order_state);
            tv_details = itemView.findViewById(R.id.tv_details);



        }

        public void BindData(OrderDataModel.OrderModel orderModel) {
            if (orderModel.getOrder_status().equals(String.valueOf(Tags.STATE_ORDER_NEW))||orderModel.getOrder_status().equals(String.valueOf(Tags.STATE_DELEGATE_SEND_OFFER)))
            {
                Picasso.with(context).load(R.drawable.logo_only).fit().into(image);
                image_state.setBackgroundResource(R.drawable.wait_bg_gray);
                image_state.setImageResource(R.drawable.ic_time_left);
                tv_order_state.setText(context.getString(R.string.new_order_2));

            }else
                {
                    if (user_type.equals(Tags.TYPE_CLIENT))
                    {
                        Picasso.with(context).load(Tags.IMAGE_URL+orderModel.getDriver_user_image()).fit().placeholder(R.drawable.logo_only).into(image);

                    }else
                        {
                            Picasso.with(context).load(Tags.IMAGE_URL+orderModel.getClient_user_image()).fit().placeholder(R.drawable.logo_only).into(image);

                        }

                        if (orderModel.getOrder_status().equals(String.valueOf(Tags.STATE_CLIENT_ACCEPT_OFFER))||orderModel.getOrder_status().equals(String.valueOf(Tags.STATE_DELEGATE_COLLECTING_ORDER))||orderModel.getOrder_status().equals(String.valueOf(Tags.STATE_DELEGATE_DELIVERING_ORDER)))
                        {
                            image_state.setBackgroundResource(R.drawable.wait_bg);
                            image_state.setImageResource(R.drawable.ic_time_left);
                            tv_order_state.setText(R.string.pending);
                        }else if (orderModel.getOrder_status().equals(String.valueOf(Tags.STATE_DELEGATE_DELIVERED_ORDER)))
                            {
                                image_state.setBackgroundResource(R.drawable.finish_bg);
                                image_state.setImageResource(R.drawable.ic_correct);
                                tv_order_state.setText(R.string.done);
                                tv_details.setVisibility(View.GONE);
                            }

                }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy , hh:mm aa", Locale.ENGLISH);
            String deliveryTime = dateFormat.format(new Date(Long.parseLong(orderModel.getOrder_time_arrival())*1000));
            tv_delivery_time.setText(deliveryTime);
            tv_order_num.setText("#"+orderModel.getOrder_id());
            tv_order_date.setText(TimeAgo.getTimeAgo(Long.parseLong(orderModel.getOrder_date())*1000,context));

        }
    }

    public class LoadMoreHolder extends RecyclerView.ViewHolder {
        private ProgressBar progBar;
        public LoadMoreHolder(View itemView) {
            super(itemView);
            progBar = itemView.findViewById(R.id.progBar);
            progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public int getItemViewType(int position) {
        OrderDataModel.OrderModel orderModel = orderModelList.get(position);
        if (orderModel ==null)
        {
            return ITEM_LOAD;
        }else
            {
                return ITEM_DATA;
            }

    }
}
