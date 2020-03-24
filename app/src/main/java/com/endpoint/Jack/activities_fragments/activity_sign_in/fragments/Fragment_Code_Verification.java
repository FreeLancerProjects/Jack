package com.endpoint.Jack.activities_fragments.activity_sign_in.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_sign_in.activity.SignInActivity;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.tags.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Code_Verification extends Fragment {
    private static String TAG1 = "phone_code";
    private static String TAG2 = "phone_number";
    private static String TAG3 = "country_code";

    private PinEntryEditText edt_code;
    private Button btn_resend, btn_confirm;
    private SignInActivity activity;
    private String phone_code = "", phone_number = "", country_code = "";
    private boolean canResend = true;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String id;
    private String code;
    private FirebaseAuth mAuth;
    private CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_verification, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Code_Verification newInstance(String phone_code, String phone_number, String country_code) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG1, phone_code);
        bundle.putString(TAG2, phone_number);
        bundle.putString(TAG3, country_code);

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

                if (canResend) {
                    // sendSMSCode(phone_code,phone_number);
                    sendverficationcode(phone_number, phone_code.replace("00", "+"));
                    startCounter();
                }
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            phone_code = bundle.getString(TAG1);
            phone_number = bundle.getString(TAG2);
            country_code = bundle.getString(TAG3);

        }

        startCounter();

        authn();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendverficationcode(phone_number, phone_code.replace("00", "+"));
            }
        }, 3);

    }

    private void authn() {

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.e("id", s);
                id = s;
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                Log.e("code",phoneAuthCredential.getSmsCode());
//phoneAuthCredential.getProvider();
                if (phoneAuthCredential.getSmsCode() != null) {
                    code = phoneAuthCredential.getSmsCode();
                    edt_code.setText(code);
                    edt_code.setPinBackground(activity.getResources().getDrawable(R.drawable.edit_shape2));
                    siginwithcredental(phoneAuthCredential);
                } else {
                    siginwithcredental(phoneAuthCredential);
                }


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("llll", e.getMessage());
            }


        };
        edt_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                edt_code.setPinBackground(activity.getResources().getDrawable(R.drawable.edit_shape));

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edt_code.setPinBackground(activity.getResources().getDrawable(R.drawable.edit_shape));

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt_code.setPinBackground(activity.getResources().getDrawable(R.drawable.edit_shape));

            }
        });

    }

    private void checkData() {
        String code = edt_code.getText().toString().trim();
        if (!TextUtils.isEmpty(code)) {
            Common.CloseKeyBoard(activity, edt_code);
            verfiycode(code);
        } else {
            edt_code.setError(getString(R.string.field_req));
        }
    }

    private void ValidateCode(String code) {
        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .validateCode(phone_code, phone_number, code)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        dialog.dismiss();

                        if (response.isSuccessful()) {
                            activity.signIn(phone_number, country_code, phone_code);
                        } else {
                            if (response.code() == 404) {
                                Common.CreateSignAlertDialog(activity, getString(R.string.inc_code_verification));
                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());


                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void verfiycode(String code) {
        // Toast.makeText(register_activity,code,Toast.LENGTH_LONG).show();
        countDownTimer.cancel();

        Log.e("ccc", code);
        if (id != null) {

try {
    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, code);
    siginwithcredental(credential);

}catch (Exception e){

}
        }
    }

    private void siginwithcredental(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    //  Log.e("data",phone);
                    phone_code = phone_code.replace("+","00");
                    mAuth.signOut();
                    //activity.NavigateToClientHomeActivity();
                   // ValidateCode("1234");
                    activity.signIn(phone_number, country_code, phone_code);

                }
                else {
                    Log.e("llllll",";llllll");

                }


            }
        });
    }

    private void sendverficationcode(String phone, String phone_code) {
        Log.e("kkk", phone_code + phone);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone_code + phone, 59, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallbacks);

    }

    private void startCounter() {
        countDownTimer = new CountDownTimer(59000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                canResend = false;

                int AllSeconds = (int) (millisUntilFinished / 1000);
                int seconds = AllSeconds % 60;


                btn_resend.setText("00:" + seconds);
            }

            @Override
            public void onFinish() {
                canResend = true;
                btn_resend.setText(getString(R.string.resend));
            }
        }.start();
    }

    private void sendSMSCode(String phone_code, final String phone) {
        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .getSmsCode(phone_code.replace("+", "00"), phone)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        dialog.dismiss();

                        if (response.isSuccessful()) {
                            startCounter();

                        } else {
                            try {
                                Log.e("error_code", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 404) {
                                Common.CreateSignAlertDialog(activity, getString(R.string.inc_code_verification));
                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());


                        } catch (Exception e) {
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}



