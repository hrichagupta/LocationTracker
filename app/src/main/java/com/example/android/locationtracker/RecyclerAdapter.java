package com.example.android.locationtracker;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    ArrayList<SuitCaseContact> arrayList;
    Context context;
    int lastPosition;


    public RecyclerAdapter(Context context, ArrayList<SuitCaseContact> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.custom_recycle_row, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, final int position) {
        holder.txtView.setText(arrayList.get(position).txtPlace);
        holder.imageIcon.setImageResource(arrayList.get(position).imgIcon);
        setAnimation(position, holder.relativeLayout);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrayList.size(); i++) {
                    Intent intent = new Intent(context, MapsActivityPlaces.class);
                    intent.putExtra("checkPosition", position);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
        });
    }

    private void setAnimation(int position, RelativeLayout relativeLayout) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.translate_left);
            relativeLayout.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageIcon;
        TextView txtView;
        RelativeLayout relativeLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.imgIcon);
            txtView = itemView.findViewById(R.id.txtPlace);
            relativeLayout = itemView.findViewById(R.id.rl);
        }
    }
}
