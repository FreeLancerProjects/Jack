package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Documentation_Data extends Fragment {

    private LinearLayout ll_back;
    private ImageView arrow;
    private Button btn_next;
    private ClientHomeActivity activity;
    private String current_language;


    public static Fragment_Documentation_Data newInstance()
    {
        return new Fragment_Documentation_Data();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_documentation_data,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        arrow = view.findViewById(R.id.arrow);

        if (current_language.equals("ar"))
        {
            arrow.setImageResource(R.drawable.ic_right_arrow);
        }else
        {
            arrow.setImageResource(R.drawable.ic_left_arrow);

        }

        ll_back = view.findViewById(R.id.ll_back);
        btn_next = view.findViewById(R.id.btn_next);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentExplainCourier();
            }
        });
    }
}
