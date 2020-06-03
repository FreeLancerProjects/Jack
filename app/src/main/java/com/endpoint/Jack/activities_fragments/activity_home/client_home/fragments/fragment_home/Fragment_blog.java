package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.models.SocialMediaModel;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_blog extends Fragment {

    private ClientHomeActivity activity;
    private ImageView arrow_back;
    private CardView imgFacebook,imgTwitter,imgEmail,imgWhats,imgSnapchat,imgInstgram;
    private String current_language,facebook,telegram,twitter,instegram,snapchat,whats,email;
    private ConstraintLayout cons_back;
    private UserSingleTone userSingleTone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, container, false);
        initView(view);
        return view;
    }

    public static Fragment_blog newInstance() {
        return new Fragment_blog();
    }

    private void initView(View view) {

        userSingleTone = UserSingleTone.getInstance();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        arrow_back = view.findViewById(R.id.arrow_back);
        imgFacebook = view.findViewById(R.id.imgfacebook);
        imgInstgram = view.findViewById(R.id.imginstgram);
        imgSnapchat = view.findViewById(R.id.imgSnapchat);
        imgEmail = view.findViewById(R.id.imgEmail);
        imgWhats = view.findViewById(R.id.imgWhats);
        imgTwitter = view.findViewById(R.id.imgTwitter);

        cons_back = view.findViewById(R.id.cons_back);

        if (current_language.equals("ar")) {
            arrow_back.setImageResource(R.drawable.ic_right_arrow);


        } else {
            arrow_back.setImageResource(R.drawable.ic_left_arrow);

        }

        cons_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });
        getSocialMedia();
        imgEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                i.putExtra(Intent.EXTRA_SUBJECT, "Fast One");
                i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgSnapchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://snapchat.com/add/" + snapchat));
                    intent.setPackage("com.snapchat.android");
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://snapchat.com/add/" + snapchat)));
                }
            }
        });

        imgTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (twitter!=null){

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter));
                    startActivity(intent);
                }else
                {
                    Toast.makeText(getContext(), R.string.not_av, Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (facebook!=null){

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebook));
                    startActivity(intent);
                }else
                {
                    Toast.makeText(getContext(), R.string.not_av, Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgInstgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (instegram!=null){

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(instegram));
                    startActivity(intent);
                }else
                {
                    Toast.makeText(getContext(), R.string.not_av, Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgWhats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (whats!=null){

                     whats = whats.replace("(","").replace(")","").replaceAll("_","");

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="+whats));
                    startActivity(intent);
                }else
                {
                    Toast.makeText(getContext(), R.string.not_av, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                            snapchat = response.body().getCompany_snapchat();
                            whats = response.body().getCompany_whatsapp();
                            email=response.body().getCompany_emails();


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


}
