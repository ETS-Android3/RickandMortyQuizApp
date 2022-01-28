package com.atuvwapps.rickandmortyultimatecharacterquiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Upload> uploads;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<Upload> uploads) {
        this.context = context;
        this.uploads = uploads;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Upload currentUpload = uploads.get(position);
        holder.myTextView.setText(currentUpload.getName());
        Picasso.get().load(currentUpload.getImageUrl()).placeholder(R.mipmap.crown).into(holder.myImageView);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return uploads.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myTextView;
        public ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.text_view_name);
            myImageView = itemView.findViewById(R.id.image_view_member);
        }

    }
}
