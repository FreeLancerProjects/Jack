package com.endpoint.Jack.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.Jack.R;
import com.endpoint.Jack.models.ReviewsCategoryModel;
import com.endpoint.Jack.share.TimeAgo;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsCategoryAdapter extends RecyclerView.Adapter<ReviewsCategoryAdapter.MyHolder> {

    private List<ReviewsCategoryModel.Data> list;
    private Context context;

    public ReviewsCategoryAdapter(List<ReviewsCategoryModel.Data> list, Context context) {
        this.list = list;
        this.context = context;


    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        ReviewsCategoryModel.Data reviews = list.get(position);
        holder.BindData(reviews);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private SimpleRatingBar ratingBar;
        private TextView tv_name,tvDate,tvRate,tvComment;


        public MyHolder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvRate = itemView.findViewById(R.id.tvRate);
            tvComment = itemView.findViewById(R.id.tvComment);
            image = itemView.findViewById(R.id.image);
            ratingBar = itemView.findViewById(R.id.ratingBar);



        }

        public void BindData(ReviewsCategoryModel.Data reviews) {

            if(reviews.getUser()!=null){
                Picasso.with(context).load(Uri.parse(reviews.getUser().getUser_image())).placeholder(R.drawable.logo).fit().into(image);
                tv_name.setText(reviews.getUser().getUser_full_name());}
            long d = reviews.getDate()*1000;
            String n_date = TimeAgo.getTimeAgo(d,context);
            tvDate.setText(n_date);
            tvRate.setText("("+reviews.getRate()+")");
            ratingBar.setRating((float) reviews.getRate());

            tvComment.setText(reviews.getComment());

        }
    }
}
