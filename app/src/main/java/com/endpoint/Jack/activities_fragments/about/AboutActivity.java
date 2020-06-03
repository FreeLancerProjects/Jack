package com.endpoint.Jack.activities_fragments.about;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.endpoint.Jack.R;
import com.endpoint.Jack.language.Language_Helper;
import com.endpoint.Jack.models.AppDataModel;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.tags.Tags;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutActivity extends AppCompatActivity {

    private TextView tv_title;
    private ImageView arrow;
    private LinearLayout ll_back;
    private TextView tv_content;
    private SmoothProgressBar smoothprogressbar;
    private String current_lang;


    @Override
    protected void attachBaseContext(Context base) {

        super.attachBaseContext(Language_Helper.updateResources(base,Language_Helper.getLanguage(base)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_up);

        setContentView(R.layout.activity_about);
        initView();
        getDataFromIntent();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            int type = intent.getIntExtra("type",-1);
            UpdateUI(type);
        }
    }



    private void initView() {
        Paper.init(this);
        current_lang = Paper.book().read("lang","ar");
        arrow = findViewById(R.id.arrow);
        if (current_lang.equals("ar"))
        {
            arrow.setImageResource(R.drawable.ic_right_arrow);
            arrow.setColorFilter(ContextCompat.getColor(this,R.color.white), PorterDuff.Mode.SRC_IN);

        }else
        {
            arrow.setImageResource(R.drawable.ic_left_arrow);
            arrow.setColorFilter(ContextCompat.getColor(this,R.color.white), PorterDuff.Mode.SRC_IN);


        }
        tv_title = findViewById(R.id.tv_title);

        ll_back = findViewById(R.id.ll_back);
        tv_content = findViewById(R.id.tv_content);
        smoothprogressbar = findViewById(R.id.smoothprogressbar);

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


    private void UpdateUI(int type) {

        if (type == Tags.APPTERMS)
        {
            tv_title.setText(getString(R.string.terms_and_conditions));


        }else if (type == Tags.APPPRIVACY)
        {
            tv_title.setText(getString(R.string.privacy_policy));

        }else if (type == Tags.APPABOUT)
        {
            tv_title.setText(getString(R.string.about_tour));

        }
        getAppData(type);
    }

    private void getAppData(int type) {

        Api.getService(Tags.base_url)
                .getAppData(type)
                .enqueue(new Callback<AppDataModel>() {
                    @Override
                    public void onResponse(Call<AppDataModel> call, Response<AppDataModel> response) {
                        smoothprogressbar.setVisibility(View.GONE);

                        if (response.isSuccessful()&&response.body()!=null)
                        {
                            updateTermsContent(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<AppDataModel> call, Throwable t) {
                        try {
                            smoothprogressbar.setVisibility(View.GONE);
                            Toast.makeText(AboutActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });

    }





    private void updateTermsContent(AppDataModel appDataModel) {
        if (current_lang.equals("ar"))
        {
            tv_content.setText(appDataModel.getData().getAr_content());

        }else
        {
            tv_content.setText(appDataModel.getData().getEn_content());

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (current_lang.equals("ar"))
        {
            overridePendingTransition(R.anim.from_left,R.anim.to_right);

        }else
        {
            overridePendingTransition(R.anim.from_right,R.anim.to_left);

        }
    }
}
