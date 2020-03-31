package com.endpoint.Jack.activities_fragments.activity_reviews;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.endpoint.Jack.R;
import com.endpoint.Jack.adapters.ReviewsAdapter;
import com.endpoint.Jack.language.Language_Helper;
import com.endpoint.Jack.models.PlaceDetailsModel;

import io.paperdb.Paper;

public class ReviewsActivity extends AppCompatActivity {

    private ImageView arrow;
    private LinearLayout ll_back;
    private String current_lang;
    private RecyclerView recView;
    private PlaceDetailsModel.PlaceDetails placeDetails;
    private ReviewsAdapter adapter;

    @Override
    protected void attachBaseContext(Context base) {

        super.attachBaseContext(Language_Helper.updateResources(base, Language_Helper.getLanguage(base)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_up);
        setContentView(R.layout.activity_reviews);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("data"))
        {
            placeDetails = (PlaceDetailsModel.PlaceDetails) intent.getSerializableExtra("data");
        }
    }

    private void initView() {

        Paper.init(this);
        current_lang = Paper.book().read("lang","ar");
        arrow = findViewById(R.id.arrow);
        if (current_lang.equals("ar"))
        {
            arrow.setImageResource(R.drawable.ic_right_arrow);
            arrow.setColorFilter(ContextCompat.getColor(this,R.color.black), PorterDuff.Mode.SRC_IN);

        }else
        {
            arrow.setImageResource(R.drawable.ic_left_arrow);
            arrow.setColorFilter(ContextCompat.getColor(this,R.color.black), PorterDuff.Mode.SRC_IN);


        }

        ll_back = findViewById(R.id.ll_back);

        recView = findViewById(R.id.recView);
        recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReviewsAdapter(placeDetails.getReviews(),this);
        recView.setAdapter(adapter);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (current_lang.equals("ar"))
                {
                    overridePendingTransition(R.anim.from_left,R.anim.to_right);

                }else
                {
                    overridePendingTransition(R.anim.from_right,R.anim.to_left);

                }
            }
        });
    }
}
