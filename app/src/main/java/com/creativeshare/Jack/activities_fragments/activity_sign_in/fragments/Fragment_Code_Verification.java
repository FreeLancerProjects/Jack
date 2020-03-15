package com.creativeshare.Jack.activities_fragments.activity_sign_in.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_sign_in.activity.SignInActivity;
import com.creativeshare.Jack.remote.Api;
import com.creativeshare.Jack.share.Common;
import com.creativeshare.Jack.tags.Tags;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Code_Verification extends Fragment {
    private static String TAG1 = "phone_code";
    private static String TAG2 = "phone_number";
    private static String TAG3 = "country_code";

    private EditText edt_code;
    private Button btn_resend,btn_confirm;
    private SignInActivity activity;
    private String phone_code="",phone_number="",country_code="";
    private boolean canResend = true;

    private CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_verification,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Code_Verification newInstance(String phone_code,String phone_number,String country_code)
    {
        Bundle bundle = new Bundle();
        bundle.putString(TAG1,phone_code);
        bundle.putString(TAG2,phone_number);
        bundle.putString(TAG3,country_code);

        Fragment_Code_Verification fragment_code_verification = new Fragment_Code_Verification();
        fragment_code_verification.setArguments(bundle);
        return fragment_code_verification;
    }

    private void initView(View view) {
        activity = (SignInActivity) getActivity();
        edt_code = view.findViewById(R.id.edt_code);
        btn_resend = view.findViewById(R.id.btn_resend);
        btn_confirm = view.findViewById(R.id.btn_confirm);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });

        btn_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (canResend)
                {
                    sendSMSCode(phone_code,phone_number);
                }
            }
        });

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            phone_code = bundle.getString(TAG1);
            phone_number = bundle.getString(TAG2);
            country_code = bundle.getString(TAG3);

        }

        startCounter();

    }

    private void checkData() {
        String code = edt_code.getText().toString().trim();
        if (!TextUtils.isEmpty(code))
        {
            Common.CloseKeyBoard(activity,edt_code);
            ValidateCode(code);
        }else
            {
                edt_code.setError(getString(R.string.field_req));
            }
    }

    private void ValidateCode(String code)
    {
        final ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .validateCode(phone_code,phone_number,code)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        dialog.dismiss();

                        if (response.isSuccessful())
                        {
                            countDownTimer.cancel();
                            activity.signIn("0"+phone_number,country_code,phone_code);
                        }else
                            {
                                if (response.code()==404)
                                {
                                    Common.CreateSignAlertDialog(activity,getString(R.string.inc_code_verification));
                                }else
                                    {
                                        Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    }
                            }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());


                        }catch (Exception e){}
                    }
                });
    }

    private void startCounter()
    {
        countDownTimer = new CountDownTimer(59000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                canResend = false;

                int AllSeconds = (int) (millisUntilFinished / 1000);
                int seconds= AllSeconds%60;


                btn_resend.setText("00:"+seconds);
            }

            @Override
            public void onFinish() {
                canResend = true;
                btn_resend.setText(getString(R.string.resend));
            }
        }.start();
    }

    private void sendSMSCode(String phone_code, final String phone) {
        final ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .getSmsCode(phone_code.replace("+","00"),phone)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        dialog.dismiss();

                        if (response.isSuccessful())
                        {
                            startCounter();

                        }else
                        {
                            try {
                                Log.e("error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code()==404)
                            {
                                Common.CreateSignAlertDialog(activity,getString(R.string.inc_code_verification));
                            }else
                            {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());


                        }catch (Exception e){}
                    }
                });
    }

}
