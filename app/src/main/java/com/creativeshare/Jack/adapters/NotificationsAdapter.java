package com.creativeshare.Jack.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Notifications;
import com.creativeshare.Jack.models.NotificationModel;
import com.creativeshare.Jack.share.TimeAgo;
import com.creativeshare.Jack.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int ITEM_LOAD = 2;
    private List<NotificationModel> notificationModelList;
    private Context context;
    private Fragment_Client_Notifications fragment;
    private String user_type;

    public NotificationsAdapter(List<NotificationModel> notificationModelList, Context context, Fragment_Client_Notifications fragment, String user_type) {

        this.notificationModelList = notificationModelList;
        this.context = context;
        this.fragment = fragment;
        this.user_type = user_type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA) {
            View view = LayoutInflater.from(context).inflate(R.layout.notifications_row, parent, false);
            return new MyHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.load_more_row, parent, false);
            return new LoadMoreHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder) {

            final MyHolder myHolder = (MyHolder) holder;
            NotificationModel notificationModel = notificationModelList.get(myHolder.getAdapterPosition());
            if (notificationModel.getOrder_status().equals(String.valueOf(Tags.STATE_ORDER_NEW)))
            {
                if (user_type.equals(Tags.TYPE_DELEGATE))
                {
                    myHolder.BindData(notificationModel);

                }
            }else
                {
                    myHolder.BindData(notificationModel);

                }
            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotificationModel notificationModel = notificationModelList.get(myHolder.getAdapterPosition());
                    //delegate send price offer client accept or refused
                    fragment.setItemData(notificationModel,myHolder.getAdapterPosition());


                }

            });

        } else {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
            loadMoreHolder.progBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private ImageView image_state;
        private TextView tv_name, tv_order_num, tv_notification_date, tv_order_state,tv_add_rate;

        public MyHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            image_state = itemView.findViewById(R.id.image_state);
            tv_notification_date = itemView.findViewById(R.id.tv_notification_date);
            tv_order_num = itemView.findViewById(R.id.tv_order_num);
            tv_order_state = itemView.findViewById(R.id.tv_order_state);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_add_rate = itemView.findViewById(R.id.tv_add_rate);


        }

        public void BindData(NotificationModel notificationModel) {
            tv_order_num.setText("#" + notificationModel.getOrder_id());
            tv_notification_date.setText(TimeAgo.getTimeAgo(Long.parseLong(notificationModel.getDate_notification()) * 1000, context));


            if (notificationModel.getOrder_status().equals(String.valueOf(Tags.STATE_ORDER_NEW)))
            {
                if (user_type.equals(Tags.TYPE_CLIENT))
                {
                    tv_order_state.setText(R.string.not_approved);

                }else
                    {
                        tv_order_state.setText(context.getString(R.string.new_order_sent2));

                    }
                image_state.setVisibility(View.GONE);
                image_state.setVisibility(View.GONE);
                tv_add_rate.setVisibility(View.GONE);
                Picasso.with(context).load(R.drawable.logo_only).fit().into(image);
                tv_name.setText(notificationModel.getOrder_details());




            }
             else if (notificationModel.getOrder_status().equals(String.valueOf(Tags.STATE_DELEGATE_SEND_OFFER)) || notificationModel.getOrder_status().equals(String.valueOf(Tags.STATE_CLIENT_ACCEPT_OFFER))) {
                image_state.setBackgroundResource(R.drawable.wait_bg);
                image_state.setImageResource(R.drawable.ic_time_left);
                image_state.setVisibility(View.VISIBLE);
                image_state.setVisibility(View.VISIBLE);
                tv_add_rate.setVisibility(View.GONE);

                if (user_type.equals(Tags.TYPE_CLIENT)) {
                    tv_order_state.setText(context.getString(R.string.del_sent_offer));

                    Picasso.with(context).load(R.drawable.logo_only).fit().into(image);
                    tv_name.setText(notificationModel.getOrder_details());


                } else {
                    tv_order_state.setText(context.getString(R.string.offer_accepted));
                    Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL + notificationModel.getFrom_user_image())).placeholder(R.drawable.logo_only).fit().into(image);
                    tv_name.setText(notificationModel.getFrom_user_full_name());

                }

            }
            else if (notificationModel.getOrder_status().equals(String.valueOf(Tags.STATE_DELEGATE_REFUSE_ORDER)) || notificationModel.getOrder_status().equals(String.valueOf(Tags.STATE_CLIENT_REFUSE_OFFER))) {


                image_state.setBackgroundResource(R.drawable.delete_bg);
                image_state.setImageResource(R.drawable.ic_delete_state);

                image_state.setVisibility(View.VISIBLE);
                image_state.setVisibility(View.VISIBLE);
                tv_add_rate.setVisibility(View.GONE);

                if (user_type.equals(Tags.TYPE_CLIENT))
                {
                    tv_order_state.setText(R.string.order_refused_send_again);

                }else

                {
                    Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL + notificationModel.getFrom_user_image())).placeholder(R.drawable.logo_only).fit().into(image);
                    tv_name.setText(notificationModel.getFrom_user_full_name());
                    tv_order_state.setText(R.string.offer_refused);

                }
            }else if (notificationModel.getOrder_status().equals(String.valueOf(Tags.STATE_DELEGATE_DELIVERED_ORDER)))
            {
                if (user_type.equals(Tags.TYPE_CLIENT))
                {
                    Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL + notificationModel.getFrom_user_image())).placeholder(R.drawable.logo_only).fit().into(image);
                    tv_name.setText(notificationModel.getFrom_user_full_name());
                    tv_order_state.setText(context.getString(R.string.done));

                    image_state.setBackgroundResource(R.drawable.finish_bg);
                    image_state.setImageResource(R.drawable.ic_correct);
                    tv_add_rate.setVisibility(View.VISIBLE);
                    image_state.setVisibility(View.VISIBLE);
                    image_state.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    public class LoadMoreHolder extends RecyclerView.ViewHolder {
        private ProgressBar progBar;

        public LoadMoreHolder(View itemView) {
            super(itemView);
            progBar = itemView.findViewById(R.id.progBar);
            progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public int getItemViewType(int position) {
        NotificationModel notificationModel = notificationModelList.get(position);
        if (notificationModel == null) {
            return ITEM_LOAD;
        } else {
            return ITEM_DATA;
        }

    }
}
