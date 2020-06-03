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
import com.endpoint.Jack.models.PlaceDetailsModel;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyHolder> {

    private List<PlaceDetailsModel.Reviews> list;
    private Context context;

    public ReviewsAdapter(List<PlaceDetailsModel.Reviews> list, Context context) {
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

        PlaceDetailsModel.Reviews reviews = list.get(position);
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

        public void BindData(PlaceDetailsModel.Reviews reviews) {

            Picasso.with(context).load(Uri.parse(reviews.getProfile_photo_url())).fit().into(image);
            tv_name.setText(reviews.getAuthor_name());
            tvDate.setText(reviews.getRelative_time_description());
            tvRate.setText("("+reviews.getRating()+")");
            ratingBar.setRating((float) reviews.getRating());

            tvComment.setText(reviews.getText());

        }
    }
}
