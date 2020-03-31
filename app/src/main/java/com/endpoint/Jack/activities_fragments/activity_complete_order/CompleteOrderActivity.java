package com.endpoint.Jack.activities_fragments.activity_complete_order;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.endpoint.Jack.R;
import com.endpoint.Jack.language.Language_Helper;

import java.util.Locale;

import io.paperdb.Paper;

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
        overridePendingTransition(R.anim.slide_up, R.anim.slide_up);

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
