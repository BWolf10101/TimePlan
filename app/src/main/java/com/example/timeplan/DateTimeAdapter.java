package com.example.timeplan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DateTimeAdapter extends RecyclerView.Adapter<DateTimeAdapter.ViewHolder> {

    private final List<DateTimeItem> dateTimeList;
    private final OnItemDeleteListener deleteListener;

    // Konstruktor dengan listener untuk penghapusan item
    public DateTimeAdapter(List<DateTimeItem> dateTimeList, OnItemDeleteListener deleteListener) {
        this.dateTimeList = dateTimeList;
        this.deleteListener = deleteListener;
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date_time, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateTimeItem item = dateTimeList.get(position);
        holder.tvDateTime.setText(item.getFormattedDateTime());
        holder.tvDescription.setText(item.getDescription());

        // Menambahkan aksi klik panjang untuk menghapus item
        holder.itemView.setOnLongClickListener(v -> {
            Toast.makeText(v.getContext(), "Item removed: " + item.getFormattedDateTime(), Toast.LENGTH_SHORT).show();
            deleteListener.onItemDelete(holder.getAdapterPosition());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return dateTimeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDateTime;
        private final TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
