package com.creativeshare.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.Jack.adapters.CommentsAdapter;
import com.creativeshare.Jack.models.CommentDataModel;
import com.creativeshare.Jack.models.UserModel;
import com.creativeshare.Jack.remote.Api;
import com.creativeshare.Jack.singletone.UserSingleTone;
import com.creativeshare.Jack.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Delegate_Comments extends Fragment {

    private TextView tv_no_comments;
    private ClientHomeActivity activity;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private CommentsAdapter adapter;
    private List<CommentDataModel.CommentModel> commentModelList;
    private ProgressBar progBar;
    private int current_page = 1;
    private boolean isLoading = false;
    private UserSingleTone userSingleTone;
    private UserModel userModel;

    public static Fragment_Delegate_Comments newInstance()
    {
        Fragment_Delegate_Comments fragment_delegate_comments = new Fragment_Delegate_Comments();
        return fragment_delegate_comments;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegate_comments,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        commentModelList = new ArrayList<>();
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        tv_no_comments = view.findViewById(R.id.tv_no_comments);
        recView = view.findViewById(R.id.recView);
        recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recView.setDrawingCacheEnabled(true);
        recView.setItemViewCacheSize(25);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        adapter = new CommentsAdapter(commentModelList,activity);
        recView.setAdapter(adapter);
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {

                    int lastVisibleItemPos = manager.findLastCompletelyVisibleItemPosition();
                    int total_item = recView.getAdapter().getItemCount();

                    if (lastVisibleItemPos >= (total_item-5)&&!isLoading)
                    {
                        int next_page = current_page+1;
                        commentModelList.add(null);
                        adapter.notifyItemInserted(commentModelList.size()-1);
                        isLoading = true;
                        loadMore(next_page);
                    }
                }
            }
        });

        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        getComments();

    }


    private void getComments() {
        Api.getService(Tags.base_url)
                .getDelegateComment(userModel.getData().getUser_id(),"driver",1)
                .enqueue(new Callback<CommentDataModel>() {
                    @Override
                    public void onResponse(Call<CommentDataModel> call, Response<CommentDataModel> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {

                            if (response.body()!=null&&response.body().getData().size()>0)
                            {
                                tv_no_comments.setVisibility(View.GONE);
                                commentModelList.addAll(response.body().getData());
                            }else
                                {
                                    tv_no_comments.setVisibility(View.VISIBLE);


                                }
                                adapter.notifyDataSetChanged();
                        }else
                            {

                                Toast.makeText(activity,R.string.failed, Toast.LENGTH_SHORT).show();
                                try {
                                    Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                    }

                    @Override
                    public void onFailure(Call<CommentDataModel> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    private void loadMore(int next_page)
    {
        Api.getService(Tags.base_url)
                .getDelegateComment(userModel.getData().getUser_id(),"driver",next_page)
                .enqueue(new Callback<CommentDataModel>() {
                    @Override
                    public void onResponse(Call<CommentDataModel> call, Response<CommentDataModel> response) {
                        if (response.isSuccessful())
                        {
                            commentModelList.remove(commentModelList.size()-1);
                            adapter.notifyDataSetChanged();
                            if (response.body()!=null&&response.body().getData().size()>0)
                            {

                                int old_lastPos = commentModelList.size()-1;


                                commentModelList.addAll(response.body().getData());
                                current_page = response.body().getMeta().getCurrent_page();

                                adapter.notifyItemRangeInserted(old_lastPos,commentModelList.size()-1);


                            }
                            isLoading = false;

                        }else
                        {
                            isLoading = false;
                            commentModelList.remove(commentModelList.size()-1);
                            adapter.notifyDataSetChanged();

                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommentDataModel> call, Throwable t) {
                        try {
                            isLoading = false;
                            commentModelList.remove(commentModelList.size()-1);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

}
