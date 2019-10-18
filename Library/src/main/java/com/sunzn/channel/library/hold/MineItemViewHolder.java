package com.sunzn.channel.library.hold;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.sunzn.channel.library.OnDragVHListener;
import com.sunzn.channel.library.R;

public class MineItemViewHolder extends RecyclerView.ViewHolder implements OnDragVHListener {

    private final AppCompatTextView nameView;
    private final AppCompatImageView execView;

    public MineItemViewHolder(@NonNull View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.mine_item_name);
        execView = itemView.findViewById(R.id.mine_item_exec);
    }

    public AppCompatTextView getNameView() {
        return nameView;
    }

    public AppCompatImageView getExecView() {
        return execView;
    }

    /**
     * item 被选中时
     */
    @Override
    public void onItemSelected() {
        nameView.setBackgroundResource(R.drawable.cxt_mine_keep_push);
    }

    /**
     * item 取消选中时
     */
    @Override
    public void onItemFinish() {
        nameView.setBackgroundResource(R.drawable.ctx_mine_keep_item);
    }

}
