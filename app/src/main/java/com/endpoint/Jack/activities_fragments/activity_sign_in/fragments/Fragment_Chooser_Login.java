package com.endpoint.Jack.activities_fragments.activity_sign_in.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_sign_in.activity.SignInActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

public class Fragment_Chooser_Login extends Fragment {

    private LinearLayout ll_phone_sign_up,ll_face,signup,ll_google_signup;
    private Button btn_skip,btn_terms;
    private SignInActivity activity;
    private GoogleSignInClient mGoogleSignInClient;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chooser_login,container,false);
        initView(view);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        return view;
    }

    public static Fragment_Chooser_Login newInstance()
    {
        return new Fragment_Chooser_Login();
    }

    private void initView(View view) {
        activity = (SignInActivity) getActivity();
        ll_phone_sign_up = view.findViewById(R.id.ll_phone_sign_up);
        ll_face = view.findViewById(R.id.ll_face_sign_up);
        ll_google_signup = view.findViewById(R.id.ll_google_sign_up);

        btn_skip = view.findViewById(R.id.btn_skip);
        btn_terms = view.findViewById(R.id.btn_terms);
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        loginButton = view.findViewById(R.id.login_button);
        ll_phone_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentPhone();
            }
        });
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("datyaa",loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
                Log.e("hhhhh","gggg");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e("hhhhh","gggg");

                Log.e("datysssaa",e.toString());

            }
        });
//        ll_face.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activity.DisplayFragmentPhone();
//            }
//        });
        ll_google_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
signIn();            }
        });
        btn_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.NavigateToTermsActivity();
            }
        });
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.NavigateToClientHomeActivity();
            }
        });

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 2);
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
            mGoogleSignInClient.signOut();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount o) {
        mGoogleSignInClient.signOut();

        if(o!=null){
        Log.e("datassss",o.getEmail());}
        else {
            Log.e("lflflfllfl","lflflfl");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 2) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        updateUI(account);
    }
}
