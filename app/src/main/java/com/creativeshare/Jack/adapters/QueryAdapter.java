package com.creativeshare.Jack.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.Jack.R;
import com.creativeshare.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Store;
import com.creativeshare.Jack.models.QuerySearchModel;

import java.util.List;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.MyHolder> {

    private List<QuerySearchModel> querySearchModelList;
    private Context context;
    private Fragment_Client_Store fragment;
    private int lastSelectedPos = 0;
    private SparseBooleanArray booleanArray;
    public QueryAdapter(List<QuerySearchModel> querySearchModelList, Context context, Fragment_Client_Store fragment) {
        this.querySearchModelList = querySearchModelList;
        this.context = context;
        this.fragment = fragment;
        booleanArray = new SparseBooleanArray();
        booleanArray.put(0,true);

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.query_row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        QuerySearchModel querySearchModel = querySearchModelList.get(position);
        holder.BindData(querySearchModel);

        if (booleanArray.get(position,false))
        {
            holder.ll_container.setBackgroundResource(R.drawable.tv_query_selected_bg);
        }else
            {
                holder.ll_container.setBackgroundResource(R.drawable.tv_query_unselected_bg);
            }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelectedPos = holder.getAdapterPosition();
                booleanArray.clear();
                booleanArray.put(lastSelectedPos,true);
                fragment.setQueryItemData(holder.getAdapterPosition());
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return querySearchModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv_query;
        private ImageView image;
        private LinearLayout ll_container;

        public MyHolder(View itemView) {
            super(itemView);

            tv_query = itemView.findViewById(R.id.tv_query);
            image = itemView.findViewById(R.id.image);
            ll_container = itemView.findViewById(R.id.ll_container);



        }

        public void BindData(QuerySearchModel querySearchModel) {

            tv_query.setText(querySearchModel.getQuery());
            image.setImageResource(querySearchModel.getImage_resource());
        }
    }
}
