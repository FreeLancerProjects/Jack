package com.endpoint.Jack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Store;
import com.endpoint.Jack.models.CategoryModel;
import com.endpoint.Jack.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyHolder> {
    private List<CategoryModel.Data> categoryModelList;
    private Context context;
    private Fragment_Client_Store fragment_main;
    private Fragment fragment;
    public CategoryAdapter(List<CategoryModel.Data> categoryModelList, Context context, Fragment fragment) {
        this.categoryModelList = categoryModelList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        final CategoryModel.Data categoryModel=categoryModelList.get(position);
tv_title.setText(categoryModel.getWord().getTitle());
if(categoryModel.getWord().getContent()!=null&&categoryModel.getWord().getContent().length()<=30){
tv_details.setText(categoryModel.getWord().getContent());}
else {
    tv_details.setText(categoryModel.getWord().getContent().substring(0,29)+"...");
}
        Picasso.with(context).load(Tags.IMAGE_URL+categoryModel.getLogo()).fit().into(image);
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
if(fragment instanceof Fragment_Client_Store){
    fragment_main=(Fragment_Client_Store)fragment;
    fragment_main.Displaycatogry(categoryModelList.get(holder.getLayoutPosition()));
}
    }
});
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }
private TextView tv_title,tv_details;
    private CircleImageView image;
    public class MyHolder extends RecyclerView.ViewHolder {

        public MyHolder(View itemView) {
            super(itemView);
            tv_title=itemView.findViewById(R.id.tv_name);
            tv_details=itemView.findViewById(R.id.tv_desc);
            image=itemView.findViewById(R.id.image);
        }
    }
}
