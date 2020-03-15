package com.endpoint.Jack.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.Jack.R;
import com.endpoint.Jack.models.WatingOrderData;
import com.endpoint.Jack.tags.Tags;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class WaitOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int ITEM_LOAD = 2;


    private List<WatingOrderData.WaitOrder> waitOrderList;
    private Context context;
    public WaitOrderAdapter(List<WatingOrderData.WaitOrder> waitOrderList, Context context) {

        this.waitOrderList = waitOrderList;
        this.context = context;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.wait_orders_row, parent, false);
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
            WatingOrderData.WaitOrder waitOrder = waitOrderList.get(myHolder.getAdapterPosition());
            myHolder.BindData(waitOrder);


        }else
            {
                LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
                loadMoreHolder.progBar.setIndeterminate(true);
            }
    }

    @Override
    public int getItemCount() {
        return waitOrderList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private TextView tv_name, tv_phone,tv_delivery_time,tv_order_num;

        public MyHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            tv_delivery_time = itemView.findViewById(R.id.tv_delivery_time);
            tv_order_num = itemView.findViewById(R.id.tv_order_num);



        }

        public void BindData(WatingOrderData.WaitOrder waitOrder) {
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL+waitOrder.getDriver_user_image())).fit().placeholder(R.drawable.logo_only).into(image);
            tv_name.setText(waitOrder.getDriver_user_full_name());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy , hh:mm aa", Locale.ENGLISH);
            String date = dateFormat.format(new Date(Long.parseLong(waitOrder.getOrder_time_arrival())*1000));
            tv_delivery_time.setText(date);
            tv_phone.setText(waitOrder.getDriver_user_phone());
            tv_order_num.setText(" #"+waitOrder.getOrder_id());

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
        WatingOrderData.WaitOrder waitOrder = waitOrderList.get(position);
        if (waitOrder ==null)
        {
            return ITEM_LOAD;
        }else
            {
                return ITEM_DATA;
            }

    }
}
