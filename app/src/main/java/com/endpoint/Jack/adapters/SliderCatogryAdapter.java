package com.endpoint.Jack.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.endpoint.Jack.R;
import com.endpoint.Jack.models.SingleCategoryModel;
import com.endpoint.Jack.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderCatogryAdapter extends PagerAdapter {
    private List<SingleCategoryModel.Data.Menus> imgList;
    private Context context;

    public SliderCatogryAdapter(List<SingleCategoryModel.Data.Menus> imgList, Context context) {
        this.imgList = imgList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.slider,container,false);
        ImageView image = view.findViewById(R.id.image);
        Uri path = Uri.parse(Tags.IMAGE_URL+imgList.get(position).getImage());
        Picasso.with(context).load(path).fit().into(image);
        container.addView(view);
        return view;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
