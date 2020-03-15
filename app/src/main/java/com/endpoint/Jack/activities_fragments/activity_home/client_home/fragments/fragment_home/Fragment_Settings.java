package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.app.AlertDialog;
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
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;
import com.zcw.togglebutton.ToggleButton;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Settings extends Fragment {

    private ClientHomeActivity activity;
    private ConstraintLayout cons_back,cons_alert,cons_complains,cons_edit_profile,cons_language,cons_terms,cons_privacy,cons_rate,cons_about,cons_banks;
    private ImageView arrow_back,arrow1,arrow2,arrow3,arrow4,arrow5,arrow6,arrow7,arrow8;
    private String current_language;
    private String [] language_array;
    private ToggleButton toggle_btn;
    private View v_alert;
    private UserSingleTone userSingleTone;
    private UserModel userModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Settings newInstance()
    {
        return new Fragment_Settings();
    }
    private void initView(View view) {

        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang",Locale.getDefault().getLanguage());

        arrow_back = view.findViewById(R.id.arrow_back);
        arrow1 = view.findViewById(R.id.arrow1);
        arrow2 = view.findViewById(R.id.arrow2);
        arrow3 = view.findViewById(R.id.arrow3);
        arrow4 = view.findViewById(R.id.arrow4);
        arrow5 = view.findViewById(R.id.arrow5);
        arrow6 = view.findViewById(R.id.arrow6);
        arrow7 = view.findViewById(R.id.arrow7);
        arrow8 = view.findViewById(R.id.arrow8);

        language_array = new String[]{"English","العربية"};

        if (current_language.equals("ar"))
        {
            arrow_back.setImageResource(R.drawable.ic_right_arrow);

            arrow1.setImageResource(R.drawable.ic_left_arrow);
            arrow2.setImageResource(R.drawable.ic_left_arrow);
            arrow3.setImageResource(R.drawable.ic_left_arrow);
            arrow4.setImageResource(R.drawable.ic_left_arrow);
            arrow5.setImageResource(R.drawable.ic_left_arrow);
            arrow6.setImageResource(R.drawable.ic_left_arrow);
            arrow7.setImageResource(R.drawable.ic_left_arrow);
            arrow8.setImageResource(R.drawable.ic_left_arrow);

        }else
            {
                arrow_back.setImageResource(R.drawable.ic_left_arrow);
                arrow1.setImageResource(R.drawable.ic_right_arrow);
                arrow2.setImageResource(R.drawable.ic_right_arrow);
                arrow3.setImageResource(R.drawable.ic_right_arrow);
                arrow4.setImageResource(R.drawable.ic_right_arrow);
                arrow5.setImageResource(R.drawable.ic_right_arrow);
                arrow6.setImageResource(R.drawable.ic_right_arrow);
                arrow7.setImageResource(R.drawable.ic_right_arrow);
                arrow8.setImageResource(R.drawable.ic_right_arrow);


            }

        cons_back = view.findViewById(R.id.cons_back);
        cons_alert = view.findViewById(R.id.cons_alert);
        cons_complains = view.findViewById(R.id.cons_complains);
        cons_edit_profile = view.findViewById(R.id.cons_edit_profile);
        cons_language = view.findViewById(R.id.cons_language);
        cons_terms = view.findViewById(R.id.cons_terms);
        cons_privacy = view.findViewById(R.id.cons_privacy);
        cons_rate = view.findViewById(R.id.cons_rate);
        cons_about = view.findViewById(R.id.cons_about);
        cons_back = view.findViewById(R.id.cons_back);
        cons_banks = view.findViewById(R.id.cons_banks);

        toggle_btn = view.findViewById(R.id.toggle_btn);
        v_alert = view.findViewById(R.id.v_alert);

        cons_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
                }
            }
        });

        cons_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.NavigateToTermsActivity(Tags.APPTERMS);
            }
        });

        cons_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.NavigateToTermsActivity(Tags.APPPRIVACY);
            }
        });

        cons_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.NavigateToTermsActivity(Tags.APPABOUT);
            }
        });

        cons_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        cons_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateLanguageDialog();
            }
        });

        cons_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentEditProfile();
            }
        });
        cons_complains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentDelegateComment();
            }
        });

        toggle_btn.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on)
                {
                    updateState("on");
                }else
                    {
                        updateState("off");

                    }
            }
        });

        cons_banks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentBankAccount();
            }
        });



        if (userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE))
        {
            cons_alert.setVisibility(View.VISIBLE);
            v_alert.setVisibility(View.VISIBLE);
            if (userModel.getData().getAvailable().equals("1"))
            {
                toggle_btn.setToggleOn();
            }else
            {
                toggle_btn.setToggleOff();

            }
        }else
            {
                cons_alert.setVisibility(View.GONE);
                v_alert.setVisibility(View.GONE);
            }

    }

    private void updateState(final String state)
    {
        final ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.show();

        Api.getService(Tags.base_url)
                .updateDelegateAvailable(userModel.getData().getUser_id(),state)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()&&response.body()!=null&&response.body().getData()!=null)
                        {
                            UpdateUserData(response.body());
                        }else
                        {

                            if (state.equals("on"))
                            {
                                toggle_btn.setToggleOff();

                            }else
                            {
                                toggle_btn.setToggleOn();

                            }

                            Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        try {
                            if (state.equals("on"))
                            {
                                toggle_btn.setToggleOff();

                            }else
                            {
                                toggle_btn.setToggleOn();

                            }
                            dialog.dismiss();
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });

    }

    private void UpdateUserData(UserModel userModel) {
        this.userModel = userModel;
        activity.updateUserData(userModel);
    }


    private void CreateLanguageDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view  = LayoutInflater.from(activity).inflate(R.layout.dialog_language,null);
        Button btn_select = view.findViewById(R.id.btn_select);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        final NumberPicker numberPicker = view.findViewById(R.id.numberPicker);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(language_array.length-1);
        numberPicker.setDisplayedValues(language_array);
        numberPicker.setWrapSelectorWheel(false);
        if (current_language.equals("ar"))
        {
            numberPicker.setValue(1);

        }else
            {
                numberPicker.setValue(0);

            }
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                int pos = numberPicker.getValue();
                if (pos == 0)
                {
                    activity.RefreshActivity("en");
                }else
                    {
                        activity.RefreshActivity("ar");

                    }

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
}
