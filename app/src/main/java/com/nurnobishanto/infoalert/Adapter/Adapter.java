package com.nurnobishanto.infoalert.Adapter;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.nurnobishanto.infoalert.Model.Model;
import com.nurnobishanto.infoalert.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<Object> mList;
    private MyRecyclerViewItemClickListener mItemClickListener;

    public Adapter(ArrayList<Object> listItems, MyRecyclerViewItemClickListener itemClickListener)
    {
        this.mList = listItems;
        this.mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate RecyclerView row
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_itm, parent, false);

        //Create View Holder
        final ViewHolder viewHolder = new ViewHolder(view);

        //Item Clicks
        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mItemClickListener.onItemClicked((Model) mList.get(viewHolder.getLayoutPosition()));
            }
        });

        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Model model = (Model) mList.get(position);


        viewHolder.title.setText(model.getTitle());
        viewHolder.id.setText("Complain ID: "+model.getId());
        viewHolder.complain.setText(model.getComplain());
        viewHolder.category.setText(model.getCategory());

        switch (model.getStatus()) {
            case "SUBMITTED":
                viewHolder.submit.setText(model.getStatus());
                viewHolder.submit.setVisibility(View.VISIBLE);
                break;
            case "PENDING":
                viewHolder.pending.setText(model.getStatus());
                viewHolder.pending.setVisibility(View.VISIBLE);
                break;
            case "SOLVED":
                viewHolder.solved.setText(model.getStatus());
                viewHolder.solved.setVisibility(View.VISIBLE);
                break;
        }



        String date_s = model.getDate();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dt.parse(date_s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.date.setText(dt.format(date));



    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView title,complain,date,submit,pending,solved,category,id;

        ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            complain = itemView.findViewById(R.id.complain);
            date = itemView.findViewById(R.id.date);
            submit = itemView.findViewById(R.id.submit);
            pending = itemView.findViewById(R.id.pending);
            solved = itemView.findViewById(R.id.solved);
            category = itemView.findViewById(R.id.category);
            id = itemView.findViewById(R.id.id);


        }
    }
    public interface MyRecyclerViewItemClickListener
    {
        void onItemClicked(Model model);
    }
}
