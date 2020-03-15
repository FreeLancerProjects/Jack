package com.creativeshare.Jack.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Delegates_Result;
import com.creativeshare.Jack.models.NotificationModel;
import com.creativeshare.Jack.tags.Tags;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Delegates_Result_Adapter extends RecyclerView.Adapter<Delegates_Result_Adapter.MyHolder> {



    private List<NotificationModel.Drivers> delegateModelList;
    private Context context;
    private Fragment_Delegates_Result fragment;
    private String money_sembole;

    public Delegates_Result_Adapter(List<NotificationModel.Drivers> delegateModelList, Context context, Fragment_Delegates_Result fragment,String money_sembole) {

        this.delegateModelList = delegateModelList;
        this.context = context;
        this.fragment = fragment;
        this.money_sembole = money_sembole;

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.delegate_result_row, parent, false);
        return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        NotificationModel.Drivers drivers = delegateModelList.get(position);
        holder.BindData(drivers);
        holder.btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationModel.Drivers drivers = delegateModelList.get(holder.getAdapterPosition());
                fragment.setItemData(drivers);
            }
        });

    }

    @Override
    public int getItemCount() {
        return delegateModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private TextView tv_name, tv_offer,tv_distance;
        private SimpleRatingBar rateBar;
        private Button btn_send;

        public MyHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_offer = itemView.findViewById(R.id.tv_offer);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            rateBar = itemView.findViewById(R.id.rateBar);
            btn_send = itemView.findViewById(R.id.btn_send);



        }

        public void BindData(NotificationModel.Drivers drivers) {

            Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL+drivers.getUser_image())).fit().placeholder(R.drawable.logo_only).into(image);
            tv_name.setText(drivers.getUser_full_name());
            tv_distance.setText(String.format("%.2f",drivers.getDistance())+" "+context.getString(R.string.km));
            tv_offer.setText(drivers.getDriver_offer()+" "+money_sembole);
            rateBar.setRating((float)drivers.getRate());
        }
    }



}
