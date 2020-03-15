package com.endpoint.Jack.activities_fragments.activity_sign_in.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.activities_fragments.activity_sign_in.fragments.Fragment_Chooser_Login;
import com.endpoint.Jack.activities_fragments.activity_sign_in.fragments.Fragment_Code_Verification;
import com.endpoint.Jack.activities_fragments.activity_sign_in.fragments.Fragment_Language;
import com.endpoint.Jack.activities_fragments.activity_sign_in.fragments.Fragment_Phone;
import com.endpoint.Jack.activities_fragments.activity_sign_in.fragments.Fragment_Sign_Up;
import com.endpoint.Jack.activities_fragments.terms_conditions.TermsConditionsActivity;
import com.endpoint.Jack.language.Language_Helper;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.preferences.Preferences;
import com.endpoint.Jack.remote.Api;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Fragment_Language fragment_language;
    private Fragment_Chooser_Login fragment_chooser_login;
    private Fragment_Phone fragment_phone;
    private Fragment_Sign_Up fragment_signUp;
    private Fragment_Code_Verification fragment_code_verification;
    private Preferences preferences;
    private String phone = "";
    private String countrycode="",phone_code="";
    private String current_lang;
    private UserSingleTone userSingleTone;
    private int fragment_count=0;


    @Override
    protected void attachBaseContext(Context base)
    {

        super.attachBaseContext(Language_Helper.updateResources(base,Language_Helper.getLanguage(base)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        current_lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        setContentView(R.layout.activity_sign_in);
        fragmentManager = getSupportFragmentManager();
        preferences = Preferences.getInstance();
        userSingleTone = UserSingleTone.getInstance();


        setUpFragment();

    }
    private void setUpFragment()
    {
        int state = preferences.getFragmentState(this);
        switch (state)
        {
            case 0:
                DisplayFragmentLanguage();
                break;
            case 1:
                DisplayFragmentChooserLogin();
                break;

        }
    }
    public void DisplayFragmentLanguage()
    {
        preferences.saveLoginFragmentState(this,0);

        if (fragment_language == null)
        {
            fragment_language = Fragment_Language.newInstance();
        }
        fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container,fragment_language,"fragment_language").addToBackStack("fragment_language").commit();
    }
    public void DisplayFragmentChooserLogin()
    {
        fragment_count +=1;
        this.phone = "";
        this.countrycode = "";
        preferences.saveLoginFragmentState(this,1);

        if (fragment_chooser_login == null)
        {
            fragment_chooser_login = Fragment_Chooser_Login.newInstance();
        }


        if (fragment_chooser_login.isAdded())
        {
            fragmentManager.beginTransaction().show(fragment_chooser_login).commit();
        }else
            {
                fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container,fragment_chooser_login,"fragment_chooser_login").addToBackStack("fragment_chooser_login").commit();

            }

    }
    public void DisplayFragmentPhone()
    {
        fragment_count +=1;

        this.phone = "";
        this.countrycode="";


        if (fragment_phone == null)
        {
            fragment_phone = Fragment_Phone.newInstance("signup");
        }

        if (fragment_phone.isAdded())
        {
            fragmentManager.beginTransaction().show(fragment_phone).commit();
        }else
            {
                fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container,fragment_phone,"fragment_phone").addToBackStack("fragment_phone").commit();

            }



    }
    public void DisplayFragmentSignUp(String phone)
    {
        fragment_count +=1;

        if (fragment_signUp == null)
        {
            fragment_signUp = Fragment_Sign_Up.newInstance();
        }
        if (fragment_signUp.isAdded())
        {
            fragmentManager.beginTransaction().show(fragment_signUp).commit();

        }else
            {
                fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_signUp,"fragment_signUp").addToBackStack("fragment_signUp").commit();

            }


    }

    public void DisplayFragmentCodeVerification(String phone_code,String phone_number,String country_code)
    {
        fragment_count +=1;

        if (fragment_code_verification == null)
        {
            fragment_code_verification = Fragment_Code_Verification.newInstance(phone_code,phone_number,country_code);
        }
        if (fragment_code_verification.isAdded())
        {
            fragmentManager.beginTransaction().show(fragment_code_verification).commit();

        }else
        {
            fragmentManager.beginTransaction().add(R.id.fragment_sign_in_container, fragment_code_verification,"fragment_code_verification").addToBackStack("fragment_code_verification").commit();

        }


    }

    public void NavigateToClientHomeActivity()
    {
        Intent intent = new Intent(this, ClientHomeActivity.class);
        startActivity(intent);
        finish();
        if (current_lang.equals("ar"))
        {
            overridePendingTransition(R.anim.from_right,R.anim.to_left);


        }else
        {
            overridePendingTransition(R.anim.from_left,R.anim.to_right);


        }


    }
    public void NavigateToTermsActivity()
    {
        Intent intent = new Intent(this, TermsConditionsActivity.class);
        intent.putExtra("type",1);
        startActivity(intent);
        if (current_lang.equals("ar"))
        {
            overridePendingTransition(R.anim.from_right,R.anim.to_left);


        }else
        {
            overridePendingTransition(R.anim.from_left,R.anim.to_right);


        }


    }
    public void RefreshActivity(String selected_language)
    {
        Paper.book().write("lang",selected_language);
        Language_Helper.setNewLocale(this,selected_language);
        preferences.saveLoginFragmentState(this,1);

        Intent intent = getIntent();
        finish();
        if (selected_language.equals("ar"))
        {
            overridePendingTransition(R.anim.from_left,R.anim.to_right);


        }else
            {
                overridePendingTransition(R.anim.from_right,R.anim.to_left);

            }
        startActivity(intent);
        if (selected_language.equals("ar"))
        {
            overridePendingTransition(R.anim.from_right,R.anim.to_left);



        }else
        {
            overridePendingTransition(R.anim.from_left,R.anim.to_right);

        }

    }

    public void signIn(String m_phone, String country_code,String phone_code)
    {

        this.phone = m_phone;
        this.countrycode = country_code;
        this.phone_code = phone_code.replace("+","00");
Log.e("llllll",";llllll");
        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .signIn(phone,this.phone_code)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful())
                        {
                            UserModel userModel = response.body();
                            preferences.create_update_userData(SignInActivity.this,userModel);
                            userSingleTone.setUserModel(userModel);

                            Intent intent = new Intent(SignInActivity.this,ClientHomeActivity.class);
                            startActivity(intent);
                            finish();


                        }else
                        {
                            try {
                                Log.e("code",response.code()+""+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 404)
                            {

                                DisplayFragmentSignUp(phone);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {

                    }
                });

    }

    public void signUpWithImage(String m_name, String m_email, int gender, Uri uri, long date_of_birth) {
        RequestBody email_part = Common.getRequestBodyText(m_email);
        RequestBody phone_part = Common.getRequestBodyText(phone);
        RequestBody phone_code_part = Common.getRequestBodyText(this.phone_code);
        RequestBody name_part = Common.getRequestBodyText(m_name);
        RequestBody gender_part = Common.getRequestBodyText(String.valueOf(gender));
        RequestBody country_code_part = Common.getRequestBodyText(countrycode);

        RequestBody date_birth_part = Common.getRequestBodyText(String.valueOf(date_of_birth));

        MultipartBody.Part image_part = Common.getMultiPart(this,uri,"user_image");


        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .signUpWithImage(email_part,phone_part,phone_code_part,name_part,gender_part,country_code_part,date_birth_part,image_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful())
                        {
                            UserModel userModel = response.body();
                            preferences.create_update_userData(SignInActivity.this,userModel);
                            userSingleTone.setUserModel(userModel);

                            Intent intent = new Intent(SignInActivity.this,ClientHomeActivity.class);
                            startActivity(intent);
                            finish();


                        }else
                        {
                            try {
                                Log.e("error",response.code()+""+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 422)
                            {
                                Common.CreateSignAlertDialog(SignInActivity.this,getString(R.string.inc_em_phone));
                            }else
                            {
                                Common.CreateSignAlertDialog(SignInActivity.this,getString(R.string.failed));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {

                    }
                });
    }

    public void signUpWithoutImage(String m_name, String m_email, int gender, long date_of_birth)
    {

        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .signUpWithoutImage(m_email,phone,this.phone_code,m_name,String.valueOf(gender),countrycode,date_of_birth)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful())
                        {
                            UserModel userModel = response.body();
                            preferences.create_update_userData(SignInActivity.this,userModel);
                            userSingleTone.setUserModel(userModel);

                            Intent intent = new Intent(SignInActivity.this,ClientHomeActivity.class);
                            startActivity(intent);
                            finish();


                        }else
                            {
                                try {
                                    Log.e("error",response.code()+""+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (response.code() == 422)
                                {
                                    Common.CreateSignAlertDialog(SignInActivity.this,getString(R.string.inc_em_phone));
                                }else
                                    {
                                        Common.CreateSignAlertDialog(SignInActivity.this,getString(R.string.failed));
                                    }
                            }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {

                    }
                });

    }

    @Override
    public void onBackPressed() {
        Back();
    }

    public void Back()
    {
        if (fragment_count==1)
        {
            finish();

        }else if (fragment_count>1&&fragment_count<3)
        {
            fragment_count-=1;
            super.onBackPressed();

        }

    }



}
