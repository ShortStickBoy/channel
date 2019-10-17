package com.sunzn.channel.library.hold;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.sunzn.channel.library.R;

public class MineHeadViewHolder extends RecyclerView.ViewHolder {

    private final AppCompatTextView editView;
    private final AppCompatTextView warnView;

    public MineHeadViewHolder(@NonNull View itemView) {
        super(itemView);
        warnView = itemView.findViewById(R.id.mine_head_warn);
        editView = itemView.findViewById(R.id.mine_head_edit);
    }

    public AppCompatTextView getWarnView() {
        return warnView;
    }

    public AppCompatTextView getEditView() {
        return editView;
    }

}
