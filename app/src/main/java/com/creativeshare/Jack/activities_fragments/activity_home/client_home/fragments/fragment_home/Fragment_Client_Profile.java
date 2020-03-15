package com.creativeshare.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.Jack.models.SocialMediaModel;
import com.creativeshare.Jack.models.UserModel;
import com.creativeshare.Jack.remote.Api;
import com.creativeshare.Jack.share.Common;
import com.creativeshare.Jack.singletone.UserSingleTone;
import com.creativeshare.Jack.tags.Tags;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Currency;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Client_Profile extends Fragment {

    private ImageView image_setting, image, arrow, arrow2, image_instagram, image_facebook, image_twitter, img_certified;
    private TextView tv_name, tv_balance, tv_order_count, tv_feedback, tv_certified, tv_coupons;
    private SimpleRatingBar rateBar;
    private ConstraintLayout cons_logout, cons_register_delegate, cons_comment, cons_add_coupon;
    private LinearLayout ll_telegram, ll_certification;
    private String current_language;
    private ClientHomeActivity activity;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private String facebook = "0", twitter = "0", instegram = "0", telegram = "0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_profile, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Client_Profile newInstance() {
        return new Fragment_Client_Profile();
    }

    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        arrow = view.findViewById(R.id.arrow);
        arrow2 = view.findViewById(R.id.arrow2);

        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_left_arrow);
            arrow2.setImageResource(R.drawable.ic_left_arrow);

        } else {
            arrow.setImageResource(R.drawable.ic_right_arrow);
            arrow2.setImageResource(R.drawable.ic_right_arrow);


        }

        image_facebook = view.findViewById(R.id.image_facebook);
        image_twitter = view.findViewById(R.id.image_twitter);
        image_instagram = view.findViewById(R.id.image_instagram);
        img_certified = view.findViewById(R.id.img_certified);

        tv_certified = view.findViewById(R.id.tv_certified);

        image_setting = view.findViewById(R.id.image_setting);
        image = view.findViewById(R.id.image);
        tv_name = view.findViewById(R.id.tv_name);
        tv_balance = view.findViewById(R.id.tv_balance);
        tv_order_count = view.findViewById(R.id.tv_order_count);
        tv_feedback = view.findViewById(R.id.tv_feedback);
        tv_coupons = view.findViewById(R.id.tv_coupons);
        rateBar = view.findViewById(R.id.rateBar);
        cons_add_coupon = view.findViewById(R.id.cons_add_coupon);

        cons_register_delegate = view.findViewById(R.id.cons_register_delegate);
        cons_comment = view.findViewById(R.id.cons_comment);
        cons_logout = view.findViewById(R.id.cons_logout);

        ll_certification = view.findViewById(R.id.ll_certification);

        ll_telegram = view.findViewById(R.id.ll_telegram);

        image_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentSettings();
            }
        });


        cons_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Logout();
            }
        });

        cons_register_delegate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentDocumentation();
            }
        });
        cons_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE)) {
                    activity.DisplayFragmentDelegateComment();

                }
            }
        });

        cons_add_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentAddCoupon();
            }
        });
       /* ll_telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(""));
                startActivity(intent);
            }
        });*/


        image_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!twitter.equals("0")) {
                    ViewSocial(twitter);

                } else {

                }
            }
        });

        image_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!facebook.equals("0")) {
                    ViewSocial(facebook);

                } else {
                    CreateAlertDialog();

                }
            }
        });

        image_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!instegram.equals("0")) {
                    ViewSocial(instegram);

                } else {
                    CreateAlertDialog();

                }
            }
        });

        ll_telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!telegram.equals("0")) {
                    ViewSocial(telegram);

                } else {
                    CreateAlertDialog();
                }
            }
        });

        updateUI(userModel);

        getSocialMedia();

    }

    private void getSocialMedia() {

        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .getSocialMedia()
                .enqueue(new Callback<SocialMediaModel>() {
                    @Override
                    public void onResponse(Call<SocialMediaModel> call, Response<SocialMediaModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            facebook = response.body().getCompany_facebook();
                            twitter = response.body().getCompany_twitter();
                            instegram = response.body().getCompany_instagram();
                            telegram = response.body().getCompany_telegram();

                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SocialMediaModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });

    }

    private void ViewSocial(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        startActivity(intent);

    }

    public void updateUI(UserModel userModel) {
        if (userModel != null) {
            this.userModel = userModel;

            if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT)) {
                ll_certification.setVisibility(View.GONE);
            } else {
                if (userModel.getData().getNum_orders() > 0) {
                    tv_certified.setText(getString(R.string.certified_account));
                    img_certified.setImageResource(R.drawable.checked_certified);
                } else {
                    tv_certified.setText(R.string.not_certified);
                    img_certified.setImageResource(R.drawable.checked_not_certified);

                }
                ll_certification.setVisibility(View.VISIBLE);


            }
            tv_name.setText(userModel.getData().getUser_full_name());
            tv_order_count.setText(String.valueOf(userModel.getData().getNum_orders()));
            tv_coupons.setText(String.valueOf(userModel.getData().getNum_coupon()));
            Picasso.with(activity).load(Uri.parse(Tags.IMAGE_URL + userModel.getData().getUser_image())).placeholder(R.drawable.logo_only).into(image);
            Currency currency = Currency.getInstance(new Locale(current_language, userModel.getData().getUser_country()));
            if (userModel.getData().getAccount_balance() > 0) {
                tv_balance.setTextColor(ContextCompat.getColor(activity, R.color.active));
            } else {
                tv_balance.setTextColor(ContextCompat.getColor(activity, R.color.delete_color));

            }
            tv_balance.setText(String.valueOf(userModel.getData().getAccount_balance()) + " " + currency.getSymbol());

            if (userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE)) {
                tv_feedback.setText(String.valueOf(userModel.getData().getNum_comments()));

                SimpleRatingBar.AnimationBuilder builder = rateBar.getAnimationBuilder()
                        .setRepeatCount(0)
                        .setDuration(1500)
                        .setRatingTarget((float) userModel.getData().getRate());
                builder.start();

            }

        }
    }

    public void updateUserData(UserModel userModel) {
        this.userModel = userModel;
        userSingleTone.setUserModel(userModel);
        updateUI(userModel);
    }

    private void CreateAlertDialog() {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_sign, null);
        Button doneBtn = view.findViewById(R.id.doneBtn);
        TextView tv_msg = view.findViewById(R.id.tv_msg);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.app_name));
        tv_msg.setText(R.string.no_media_available);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }

}
