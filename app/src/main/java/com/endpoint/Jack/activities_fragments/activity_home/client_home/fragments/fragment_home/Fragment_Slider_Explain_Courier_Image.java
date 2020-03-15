package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.squareup.picasso.Picasso;

public class Fragment_Slider_Explain_Courier_Image extends Fragment {

    private static final String TAG ="image";
    private ImageView image;
    private int img_resource;
    private ClientHomeActivity activity;

    public static Fragment_Slider_Explain_Courier_Image newInstance(int img_resource)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(TAG,img_resource);
        Fragment_Slider_Explain_Courier_Image fragment_slider_explain_courier_image = new Fragment_Slider_Explain_Courier_Image();

        fragment_slider_explain_courier_image.setArguments(bundle);
        return fragment_slider_explain_courier_image;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_slider_explain_courier_image,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        activity = (ClientHomeActivity) getActivity();
        image = view.findViewById(R.id.image);
        Bundle bundle = getArguments();
        if(bundle!=null)
        {
            img_resource = bundle.getInt(TAG);
            Picasso.with(activity).load(img_resource).fit().into(image);
        }
    }
}
