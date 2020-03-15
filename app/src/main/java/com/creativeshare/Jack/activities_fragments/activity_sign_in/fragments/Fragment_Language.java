package com.creativeshare.Jack.activities_fragments.activity_sign_in.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_sign_in.activity.SignInActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.paperdb.Paper;

public class Fragment_Language extends Fragment {
    private RadioButton rb_en,rb_ar;
    private FloatingActionButton fab;
    private String selected_language = "ar";
    private SignInActivity activity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_language,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Language newInstance()
    {
        return new Fragment_Language();
    }
    private void initView(View view) {
        activity = (SignInActivity) getActivity();
        Paper.init(activity);


        rb_ar = view.findViewById(R.id.rb_ar);
        rb_en = view.findViewById(R.id.rb_en);
        fab = view.findViewById(R.id.fab);

        rb_ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_language = "ar";


            }
        });

        rb_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selected_language = "en";



            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.RefreshActivity(selected_language);

            }
        });
    }
}
