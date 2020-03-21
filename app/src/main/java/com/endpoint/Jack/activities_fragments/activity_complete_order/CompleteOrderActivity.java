package com.endpoint.Jack.activities_fragments.activity_complete_order;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Map;
import com.endpoint.Jack.activities_fragments.activity_map.MapActivity;
import com.endpoint.Jack.adapters.SliderCatogryAdapter;
import com.endpoint.Jack.language.Language_Helper;
import com.endpoint.Jack.models.CategoryModel;
import com.endpoint.Jack.models.SelectedLocation;
import com.endpoint.Jack.models.SingleCategoryModel;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.tags.Tags;
import com.google.android.material.tabs.TabLayout;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteOrderActivity extends AppCompatActivity {
    private ImageView  arrow;
    private LinearLayout ll_back;
    private String current_language;
    private EditText edt_order_details,edt_order_more;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Language_Helper.updateResources(base, Language_Helper.getLanguage(base)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_order);
        initview();

    }

    private void initview() {
        Paper.init(this);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        arrow = findViewById(R.id.arrow);

        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);
            arrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        } else {
            arrow.setImageResource(R.drawable.ic_left_arrow);
            arrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);


        }
        ll_back = findViewById(R.id.ll_back);
        edt_order_details = findViewById(R.id.edt_detials);
edt_order_more=findViewById(R.id.edt_more_detials);





        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   activity.Back();
                finish();
            }
        });

    }




}
