package com.endpoint.Jack.activities_fragments.activity_fragment_category;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_complete_order.CompleteOrderActivity;
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

public class CategoryActivity extends AppCompatActivity {
    private CategoryModel.Data data;
    private String lang;
    private TextView tv_name, tv_content, tv_rate, tv_addess, tv_address, tv_time, tv_status;
    private CircleImageView imageView;
    private SimpleRatingBar simpleRatingBar;
    private ViewPager pager;
    private TabLayout tab;
    private int current_page = 0, NUM_PAGES;
    private SliderCatogryAdapter sliderCatogryAdapter;
    private LinearLayout ll_change,ll_choose_delivery_time;
    private SelectedLocation selectedLocation;
    private ImageView arrow1, arrow2, arrow3, imback;
    private ProgressBar progressBar;
    private Button btnOrderNow;
    public Location location = null;
    private Fragment_Map fragment_map;
    private FragmentManager fragmentManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Language_Helper.updateResources(base, Language_Helper.getLanguage(base)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getdatafromintent();
        initview();
        getsinglecat();
        change_slide_image();
    }

    private void initview() {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        fragmentManager = getSupportFragmentManager();
        tv_name = findViewById(R.id.tv_name);
        tv_content = findViewById(R.id.tv_content);
        tv_rate = findViewById(R.id.tv_rate);
        tv_status = findViewById(R.id.tv_status);
        tv_time = findViewById(R.id.tv_time);
        simpleRatingBar = findViewById(R.id.rateBar);
        imageView = findViewById(R.id.image);
        pager = findViewById(R.id.pager);
        ll_change = findViewById(R.id.ll_change);
        ll_choose_delivery_time=findViewById(R.id.ll_time);
        tv_addess = findViewById(R.id.tv_address);
        tv_address = findViewById(R.id.tv_address1);
        arrow1 = findViewById(R.id.arrow1);
        arrow2 = findViewById(R.id.arrow2);
        arrow3 = findViewById(R.id.arrow3);
        imback = findViewById(R.id.image_back);
        progressBar = findViewById(R.id.progBarSlider);
        btnOrderNow = findViewById(R.id.btnOrderNow);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        if (lang.equals("en")) {
            arrow1.setRotation(180.0f);
            arrow2.setRotation(180.0f);
            arrow3.setRotation(180.0f);

        } else {
            imback.setRotation(180.0f);
        }
        imback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_choose_delivery_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateTimeDialog();
            }
        });
        tab = findViewById(R.id.tab);
        tab.setupWithViewPager(pager);
        ll_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryActivity.this, MapActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        btnOrderNow.setOnClickListener(view -> {
            Intent intent = new Intent(CategoryActivity.this, CompleteOrderActivity.class);
            startActivity(intent);

        });

    }

    private void getdatafromintent() {
        if (getIntent().getSerializableExtra("data") != null) {
            data = (CategoryModel.Data) getIntent().getSerializableExtra("data");
        }
    }

    public void DisplayFragmentMap(String from) {

        if (location != null) {
            fragment_map = Fragment_Map.newInstance(location.getLatitude(), location.getLongitude(), from);

        } else {
            fragment_map = Fragment_Map.newInstance(0.0, 0.0, from);

        }

        if (fragment_map.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_map).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_map, "fragment_map").addToBackStack("fragment_map").commit();
        }


    }
    private void CreateTimeDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view  = LayoutInflater.from(this).inflate(R.layout.dialog_delivery_time,null);
        Button btn_select = view.findViewById(R.id.btn_select);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        final NumberPicker numberPicker = view.findViewById(R.id.numberPicker);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(timesList.length-1);
        numberPicker.setDisplayedValues(timesList);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(1);
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = timesList[numberPicker.getValue()];
                tv_delivery_time.setText(val);
                setTime(numberPicker.getValue());
                dialog.dismiss();
            }
        });




        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setView(view);
        dialog.show();
    }


    public void getsinglecat() {


        final ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .getsinglecat(lang, data.getCategory_id() + "", 1, 1)
                .enqueue(new Callback<SingleCategoryModel>() {
                    @Override
                    public void onResponse(Call<SingleCategoryModel> call, Response<SingleCategoryModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                            update(response.body());


                        } else {
                            try {
                                Log.e("codesssss", response.code() + "" + response.errorBody().string());
                            } catch (Exception e) {
                                //  e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<SingleCategoryModel> call, Throwable t) {
                        Log.e("codesssss", t.getMessage());


                    }
                });

    }

    private void update(SingleCategoryModel body) {
        if (body.getData().get(0).getWord().getContent() != null)
            tv_content.setText(body.getData().get(0).getWord().getContent() + "");
        tv_name.setText(body.getData().get(0).getWord().getTitle());
        Picasso.with(this).load(Tags.IMAGE_URL + body.getData().get(0).getLogo()).into(imageView);
        progressBar.setVisibility(View.GONE);
        if (body.getData().get(0).getMenus() != null && body.getData().get(0).getMenus().size() > 0) {
            NUM_PAGES = body.getData().get(0).getMenus().size();
            sliderCatogryAdapter = new SliderCatogryAdapter(body.getData().get(0).getMenus(), this);
            pager.setAdapter(sliderCatogryAdapter);
            pager.setVisibility(View.VISIBLE);
        } else {
            pager.setVisibility(View.GONE);
        }
        tv_rate.setText(body.getData().get(0).getRate() + "");
        simpleRatingBar.setIndicator(false);
        simpleRatingBar.setRating(body.getData().get(0).getRate());
        tv_time.setText(body.getData().get(0).getDays().get(0).getFrom_time() + ":" + body.getData().get(0).getDays().get(0).getTo_time());
        tv_status.setText(body.getData().get(0).getDays().get(0).getStatus());

    }

    private void change_slide_image() {
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (current_page == NUM_PAGES) {
                    current_page = 0;
                }
                pager.setCurrentItem(current_page++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            if (data.hasExtra("location")) {
                selectedLocation = (SelectedLocation) data.getSerializableExtra("location");
                if (selectedLocation != null) {
                    tv_address.setText(selectedLocation.getAddress());
                    tv_addess.setText(selectedLocation.getAddress().substring(0, 5));
                }
            }
        }

    }


}
