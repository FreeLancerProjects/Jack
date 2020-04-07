package com.endpoint.Jack.activities_fragments.activity_reviews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.adapters.ReviewsAdapter;
import com.endpoint.Jack.adapters.ReviewsCategoryAdapter;
import com.endpoint.Jack.language.Language_Helper;
import com.endpoint.Jack.models.CategoryModel;
import com.endpoint.Jack.models.NotificationModel;
import com.endpoint.Jack.models.PlaceDetailsModel;
import com.endpoint.Jack.models.ReviewsCategoryModel;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.preferences.Preferences;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.tags.Tags;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsActivity extends AppCompatActivity {
    private CategoryModel.Data data;
private List<ReviewsCategoryModel.Data> rDataList;
    private ImageView arrow;
    private LinearLayout ll_back;
    private String current_lang;
    private RecyclerView recView;
    private PlaceDetailsModel.PlaceDetails placeDetails;
    private ReviewsAdapter adapter;
    private Preferences preference;
    private UserModel userModel;
    private TextView tv_rate;
    private ReviewsCategoryAdapter reviewsCategoryAdapter;
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
        else if (getIntent().getSerializableExtra("datas") != null) {
            data = (CategoryModel.Data) getIntent().getSerializableExtra("datas");
        }
    }

    private void initView() {
        rDataList=new ArrayList<>();
        preference=Preferences.getInstance();
        userModel=preference.getUserData(this);
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
        tv_rate=findViewById(R.id.tv_rate);

        recView.setLayoutManager(new LinearLayoutManager(this));
        if(data!=null){
            if(userModel!=null){
            tv_rate.setVisibility(View.VISIBLE);}
            reviewsCategoryAdapter=new ReviewsCategoryAdapter(rDataList,this);
            recView.setAdapter(reviewsCategoryAdapter);
            getRevuews();
        }
        else {
        adapter = new ReviewsAdapter(placeDetails.getReviews(),this);
        recView.setAdapter(adapter);}
        tv_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAddRateAlertDialog();
            }
        });
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
    public void getRevuews() {
        //   Common.CloseKeyBoard(homeActivity, edt_name);
        rDataList.clear();
reviewsCategoryAdapter.notifyDataSetChanged();
        // rec_sent.setVisibility(View.GONE);
        // progBar.setVisibility(View.VISIBLE);
        Api.getService(Tags.base_url)
                .getreview(data.getCategory_id()+"")
                .enqueue(new Callback<ReviewsCategoryModel>() {
                    @Override
                    public void onResponse(Call<ReviewsCategoryModel>  call, Response<ReviewsCategoryModel> response) {
                        //  progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            rDataList.clear();
                            rDataList.addAll(response.body().getData());
                            //     Log.e("lllll",response.body().getData().size()+"");
                            if (response.body().getData().size() > 0) {
                                // rec_sent.setVisibility(View.VISIBLE);

                                //   ll_no_store.setVisibility(View.GONE);
                                reviewsCategoryAdapter.notifyDataSetChanged();
                                //   total_page = response.body().getMeta().getLast_page();

                            } else {
                                // ll_no_store.setVisibility(View.VISIBLE);

                            }
                        } else {
                            // ll_no_store.setVisibility(View.VISIBLE);

                            //  Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewsCategoryModel> call, Throwable t) {
                        try {

                            //  progBar.setVisibility(View.GONE);
                            //    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });

    }

    public void CreateAddRateAlertDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cat_rate,null);
        ImageView img_close = view.findViewById(R.id.img_close);
        CircleImageView image = view.findViewById(R.id.image);
        TextView tv_name = view.findViewById(R.id.tv_name);
        SimpleRatingBar simpleRatingBar=view.findViewById(R.id.ratingBar);

        final EditText edt_comment = view.findViewById(R.id.edt_comment);
        final TextView tv_rate = view.findViewById(R.id.tv_rate);
        final Button btn_rate = view.findViewById(R.id.btn_rate);
      //  Picasso.with(this).load(Uri.parse(Tags.IMAGE_URL+notificationModel.getFrom_user_image())).fit().into(image);
        tv_name.setText(data.getWord().getTitle());





        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = edt_comment.getText().toString().trim();
               float rate=simpleRatingBar.getRating();
                AddRate(dialog,data,rate,comment);
            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }
    private void AddRate(final AlertDialog alertDialog, CategoryModel.Data notificationModel, float rate, String comment) {

        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .addRate(userModel.getData().getUser_id()+"",notificationModel.getCategory_id()+"",rate,comment)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful())
                        {

                            alertDialog.dismiss();
                            dialog.dismiss();
                            getRevuews();

                            //  RefreshFragment_Notification();

                        }else
                        {
                            try {
                                Log.e("error_code",response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                            Toast.makeText(ReviewsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error",t.getMessage());
                            Toast.makeText(ReviewsActivity.this,getString(R.string.something), Toast.LENGTH_SHORT).show();
                        }catch (Exception re){}
                    }
                });



    }

}
