package com.sunzn.channel.library.hold;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.sunzn.channel.library.R;

public class KeepItemViewHolder extends RecyclerView.ViewHolder {

    private AppCompatTextView nameView;

    public KeepItemViewHolder(@NonNull View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.keep_item_name);
    }

    public AppCompatTextView getNameView() {
        return nameView;
    }

}
