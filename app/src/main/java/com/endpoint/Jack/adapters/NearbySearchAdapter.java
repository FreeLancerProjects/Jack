package com.endpoint.Jack.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Search;
import com.endpoint.Jack.models.NearbyModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NearbySearchAdapter extends RecyclerView.Adapter<NearbySearchAdapter.MyHolder> {

    private List<NearbyModel> nearbyModelList;
    private Context context;
    private Fragment_Search fragment;
    private double user_lat=0.0,user_lng=0.0;
    public NearbySearchAdapter(List<NearbyModel> nearbyModelList, Context context, Fragment_Search fragment, double user_lat, double user_lng) {
        this.nearbyModelList = nearbyModelList;
        this.context = context;
        this.fragment = fragment;
        this.user_lat = user_lat;
        this.user_lng = user_lng;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.nearby_row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        NearbyModel nearbyModel = nearbyModelList.get(position);
        holder.BindData(nearbyModel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NearbyModel nearbyModel= nearbyModelList.get(holder.getAdapterPosition());
                fragment.setItemData(nearbyModel);

            }
        });
    }

    @Override
    public int getItemCount() {
        return nearbyModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView tv_name, tv_address, tv_rate, tv_state,tv_distance;

        public MyHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_rate = itemView.findViewById(R.id.tv_rate);
            tv_state = itemView.findViewById(R.id.tv_state);
            tv_distance = itemView.findViewById(R.id.tv_distance);


        }

        public void BindData(NearbyModel nearbyModel) {
            if (nearbyModel.getPhotos().size()>0)
            {
                String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+nearbyModel.getPhotos().get(0).getPhoto_reference()+"&key=AIzaSyCbc2Y5AIwZ8uUeHRUXiozGN3CnpjKT0oI";
                Picasso.with(context).load(Uri.parse(url)).fit().into(image);

            }else
            {
                Picasso.with(context).load(Uri.parse(nearbyModel.getIcon())).fit().into(image);

            }            tv_name.setText(nearbyModel.getName());
            tv_address.setText(nearbyModel.getVicinity());
            tv_rate.setText(String.valueOf(nearbyModel.getRating()));
            double distance = SphericalUtil.computeDistanceBetween(new LatLng(user_lat,user_lng),new LatLng(nearbyModel.getGeometry().getLocation().getLat(),nearbyModel.getGeometry().getLocation().getLng()));
            tv_distance.setText(String.format("%.2f",(distance/1000))+" "+context.getString(R.string.km));

            if (nearbyModel.getOpening_hours()!=null&&nearbyModel.getOpening_hours().isOpen_now()) {

                tv_state.setText(R.string.active);
                tv_state.setTextColor(ContextCompat.getColor(context, R.color.black));
            }else
            {
                tv_state.setText(R.string.inactive);
                tv_state.setTextColor(ContextCompat.getColor(context, R.color.gray4));
            }
        }
    }
}
