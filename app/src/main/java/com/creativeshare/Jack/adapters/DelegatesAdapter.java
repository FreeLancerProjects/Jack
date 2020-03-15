package com.creativeshare.Jack.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Delegates;
import com.creativeshare.Jack.models.NearDelegateDataModel;
import com.creativeshare.Jack.tags.Tags;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DelegatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int ITEM_LOAD = 2;


    private List<NearDelegateDataModel.DelegateModel> delegateModelList;
    private Context context;
    private Fragment_Delegates fragment;
    public DelegatesAdapter(List<NearDelegateDataModel.DelegateModel> delegateModelList, Context context, Fragment_Delegates fragment) {

        this.delegateModelList = delegateModelList;
        this.context = context;
        this.fragment = fragment;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.delegate_row, parent, false);
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
            NearDelegateDataModel.DelegateModel delegateModel = delegateModelList.get(myHolder.getAdapterPosition());
            myHolder.BindData(delegateModel);

            myHolder.btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NearDelegateDataModel.DelegateModel delegateModel = delegateModelList.get(myHolder.getAdapterPosition());
                    fragment.setItemData(delegateModel);
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
        return delegateModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private TextView tv_name, tv_phone,tv_distance;
        private SimpleRatingBar rateBar;
        private Button btn_send;

        public MyHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            rateBar = itemView.findViewById(R.id.rateBar);
            btn_send = itemView.findViewById(R.id.btn_send);



        }

        public void BindData(NearDelegateDataModel.DelegateModel delegateModel) {
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL+delegateModel.getUser_image())).fit().placeholder(R.drawable.logo_only).into(image);
            tv_name.setText(delegateModel.getUser_full_name());
            tv_distance.setText(String.format("%.2f",delegateModel.getDistance())+" "+context.getString(R.string.km));
            tv_phone.setText(delegateModel.getUser_phone());

            SimpleRatingBar.AnimationBuilder builder = rateBar.getAnimationBuilder()
                    .setRepeatCount(0)
                    .setDuration(1500)
                    .setRatingTarget((float) delegateModel.getRate());
            builder.start();
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
        NearDelegateDataModel.DelegateModel delegateModel = delegateModelList.get(position);
        if (delegateModel ==null)
        {
            return ITEM_LOAD;
        }else
            {
                return ITEM_DATA;
            }

    }
}
