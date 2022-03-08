package com.example.smarthomeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    String[][] dataset;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvId, tvUser, tvTipo, tvValor, tvFecha;
        ImageView ivEdit, ivDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvValor = itemView.findViewById(R.id.tvValor);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }

    public MyAdapter(String[][] dataset){
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_sensores, parent, false);
        return  new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvId.setText(this.dataset[position][0]);
        holder.tvUser.setText(this.dataset[position][1]);
        holder.tvTipo.setText(this.dataset[position][2]);
        holder.tvValor.setText(this.dataset[position][3]);
        holder.tvFecha.setText(this.dataset[position][4]);

    }

    @Override
    public int getItemCount() {
        return this.dataset.length;
    }
}

