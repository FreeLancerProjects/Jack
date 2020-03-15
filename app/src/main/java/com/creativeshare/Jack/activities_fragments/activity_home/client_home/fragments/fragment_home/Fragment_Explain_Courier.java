package com.creativeshare.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

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
import androidx.viewpager.widget.ViewPager;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.Jack.adapters.SliderExplainCourierAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Explain_Courier extends Fragment {

    private LinearLayout ll_back;
    private ImageView arrow;
    private ViewPager pager;
    private TabLayout tab;
    private Button btn_prev,btn_next;
    private ClientHomeActivity activity;
    private String current_language;
    private boolean isFinish = false;
    private SliderExplainCourierAdapter adapter;
    private List<Fragment> fragmentList;

    public static Fragment_Explain_Courier newInstance()
    {
        return new Fragment_Explain_Courier();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explain_courier,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        fragmentList = new ArrayList<>();
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
        pager = view.findViewById(R.id.pager);
        tab = view.findViewById(R.id.tab);
        btn_prev = view.findViewById(R.id.btn_prev);
        btn_next = view.findViewById(R.id.btn_next);

        tab.setupWithViewPager(pager);


        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinish)
                {
                    activity.DisplayFragmentRegisterDelegate();
                }else
                    {
                        pager.setCurrentItem(pager.getCurrentItem()+1,true);

                    }
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem()- 1,true);
            }
        });


        fragmentList.add(Fragment_Slider_Explain_Courier_Image.newInstance(R.drawable.slider1));
        fragmentList.add(Fragment_Slider_Explain_Courier_Image.newInstance(R.drawable.slider2));
        fragmentList.add(Fragment_Slider_Explain_Courier_Image.newInstance(R.drawable.slider3));
        fragmentList.add(Fragment_Slider_Explain_Courier_Image.newInstance(R.drawable.slider4));

        adapter = new SliderExplainCourierAdapter(getChildFragmentManager(),activity);
        adapter.AddFragment(fragmentList);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(pager.getAdapter().getCount());

        btn_prev.setVisibility(View.INVISIBLE);
        pager.setCurrentItem(0);


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
       {
           @Override
           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

           }

           @Override
           public void onPageSelected(int position) {
               if (position==0)
               {
                   isFinish = false;
                   btn_prev.setVisibility(View.INVISIBLE);
                   btn_next.setText(R.string.next);
               }else
                   {
                       btn_prev.setVisibility(View.VISIBLE);
                       if (position == pager.getAdapter().getCount()-1)
                       {
                           isFinish =true;
                           btn_next.setText(R.string.finish);


                       }else
                           {
                               btn_next.setText(R.string.next);
                               isFinish = false;
                           }

                   }
           }

           @Override
           public void onPageScrollStateChanged(int state) {

           }
       });

        for (int i = 0 ; i<tab.getTabCount();i++)
        {
            View tabView = ((ViewGroup)tab.getChildAt(0)).getChildAt(i);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
            params.setMargins(8,0,8,0);

        }

    }
}
