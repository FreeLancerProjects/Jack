package com.creativeshare.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.Jack.adapters.BankAdapter;
import com.creativeshare.Jack.models.BankDataModel;
import com.creativeshare.Jack.remote.Api;
import com.creativeshare.Jack.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Bank_Account extends Fragment {

    private LinearLayout ll_back;
    private ImageView arrow;
    private String current_language;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private ProgressBar progBar;
    private BankAdapter adapter;
    private List<BankDataModel.BankModel> bankModelList;
    private ClientHomeActivity activity;

    public static Fragment_Bank_Account newInstance()
    {
        return new Fragment_Bank_Account();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bank_account,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        bankModelList = new ArrayList<>();
        activity = (ClientHomeActivity) getActivity();
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        arrow = view.findViewById(R.id.arrow);

        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);
        } else {
            arrow.setImageResource(R.drawable.ic_left_arrow);

        }

        ll_back = view.findViewById(R.id.ll_back);

        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        adapter  = new BankAdapter(bankModelList,activity);
        recView.setAdapter(adapter);

        ll_back = view.findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        getBankAccount();
    }

    private void getBankAccount() {
        Api.getService(Tags.base_url)
                .getBankAccount()
                .enqueue(new Callback<BankDataModel>() {
                    @Override
                    public void onResponse(Call<BankDataModel> call, Response<BankDataModel> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()&&response.body()!=null)
                        {
                            bankModelList.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();
                        }else
                            {
                                try {
                                    Log.e("Error",response.code()+"_"+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(activity,getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                    }

                    @Override
                    public void onFailure(Call<BankDataModel> call, Throwable t) {
                        try {

                            progBar.setVisibility(View.GONE);
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_LONG).show();
                        }catch (Exception e)
                        {
                        }
                    }
                });
    }
}
