package com.endpoint.Jack.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Notifications;
import com.endpoint.Jack.models.NotificationModel;
import com.endpoint.Jack.preferences.Preferences;
import com.endpoint.Jack.share.TimeAgo;
import com.endpoint.Jack.tags.Tags;
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
            myHolder.imageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationModelList.remove(0);
                    Preferences.getInstance().saveVisitdelegete(context,0);
                    notifyDataSetChanged();
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
        private ImageView image_state,imageDelete;
        private TextView tv_name, tv_order_num, tv_notification_date, tv_order_state,tv_add_rate,order_num,order_state;

        public MyHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            image_state = itemView.findViewById(R.id.image_state);
            tv_notification_date = itemView.findViewById(R.id.tv_notification_date);
            tv_order_num = itemView.findViewById(R.id.tv_order_num);
            tv_order_state = itemView.findViewById(R.id.tv_order_state);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_add_rate = itemView.findViewById(R.id.tv_add_rate);
            order_num=itemView.findViewById(R.id.order_num);
            order_state=itemView.findViewById(R.id.order_state);
            imageDelete=itemView.findViewById(R.id.imageDelete);
        }

        public void BindData(NotificationModel notificationModel) {
          Log.e("nfnfnfn",notificationModel.getOrder_status());
            if(!notificationModel.getOrder_status().equals("sss")) {
                tv_order_num.setText("#" + notificationModel.getOrder_id());
                tv_notification_date.setText(TimeAgo.getTimeAgo(Long.parseLong(notificationModel.getDate_notification()) * 1000, context));


            }
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
                    tv_add_rate.setVisibility(View.GONE);

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
            }
            else if (notificationModel.getOrder_status().equals(String.valueOf(Tags.STATE_DELEGATE_DELIVERED_ORDER)))

            {

                    Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL + notificationModel.getFrom_user_image())).placeholder(R.drawable.logo_only).fit().into(image);
                    tv_name.setText(notificationModel.getFrom_user_full_name());
                    tv_order_state.setText(context.getString(R.string.done));

                    image_state.setBackgroundResource(R.drawable.finish_bg);
                    image_state.setImageResource(R.drawable.ic_correct);
                    tv_add_rate.setVisibility(View.VISIBLE);
                    image_state.setVisibility(View.VISIBLE);
                    image_state.setVisibility(View.VISIBLE);
                    if(notificationModel.getFrom_user_type().equals(Tags.TYPE_CLIENT)){
                        tv_add_rate.setVisibility(View.GONE);
                    }

            }
            else if (notificationModel.getOrder_status().equals(String.valueOf(Tags.STATE_DELEGATE_COLLECTING_ORDER)) || notificationModel.getOrder_status().equals(String.valueOf(Tags.STATE_DELEGATE_COLLECTED_ORDER))) {



image_state.setVisibility(View.GONE);
                image_state.setVisibility(View.VISIBLE);
                image_state.setVisibility(View.VISIBLE);
                tv_add_rate.setVisibility(View.GONE);

                if (user_type.equals(Tags.TYPE_CLIENT))
                {
                    tv_order_state.setText(notificationModel.getTitle_notification());
                    tv_name.setText(notificationModel.getOrder_details());

                }else

                {
                    Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL + notificationModel.getFrom_user_image())).placeholder(R.drawable.logo_only).fit().into(image);
                    tv_name.setText(notificationModel.getOrder_details());
                    tv_order_state.setText(notificationModel.getTitle_notification());

                }
            }
            else if (notificationModel.getOrder_status().equals(String.valueOf(Tags.STATE_DELEGATE_DELIVERING_ORDER)))
            {

                    Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL + notificationModel.getFrom_user_image())).placeholder(R.drawable.logo_only).fit().into(image);
                tv_name.setText(notificationModel.getOrder_details());
                    tv_order_state.setText(notificationModel.getTitle_notification());

                    image_state.setBackgroundResource(R.drawable.finish_bg);
                    image_state.setImageResource(R.drawable.ic_correct);
                    tv_add_rate.setVisibility(View.VISIBLE);
                    image_state.setVisibility(View.VISIBLE);
                    image_state.setVisibility(View.VISIBLE);

            }

            else {
                tv_name.setText(notificationModel.getTitle_notification());
order_num.setVisibility(View.GONE);
order_state.setVisibility(View.GONE);
tv_notification_date.setVisibility(View.GONE);
tv_add_rate.setVisibility(View.GONE);
tv_order_num.setVisibility(View.GONE);
tv_order_state.setVisibility(View.GONE);
imageDelete.setVisibility(View.VISIBLE);
tv_name.setLineSpacing(1.5f,1.5f);
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
