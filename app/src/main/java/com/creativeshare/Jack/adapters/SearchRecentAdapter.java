package com.creativeshare.Jack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Search;
import com.creativeshare.Jack.models.QueryModel;

import java.util.List;

public class SearchRecentAdapter extends RecyclerView.Adapter<SearchRecentAdapter.MyHolder> {

    private List<QueryModel> queryModelList;
    private Context context;
    private Fragment_Search fragment;
    public SearchRecentAdapter(List<QueryModel> queryModelList, Context context, Fragment_Search fragment) {
        this.queryModelList = queryModelList;
        this.context = context;
        this.fragment = fragment;


    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.query_recent_row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        QueryModel queryModel = queryModelList.get(position);
        holder.BindData(queryModel);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryModel queryModel = queryModelList.get(holder.getAdapterPosition());

                fragment.setQueryItemData(queryModel);

            }
        });
    }

    @Override
    public int getItemCount() {
        return queryModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv_query;

        public MyHolder(View itemView) {
            super(itemView);

            tv_query = itemView.findViewById(R.id.tv_query);



        }

        public void BindData(QueryModel queryModel) {

            tv_query.setText(queryModel.getKeyword());
        }
    }
}
