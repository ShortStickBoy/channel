package com.sunzn.channel.sample;

import android.content.Context;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.sunzn.channel.library.ChannelAdapter;
import com.sunzn.channel.library.ChannelBase;

import java.util.List;

public class Adapter extends ChannelAdapter {

    public Adapter(Context context, ItemTouchHelper helper, List<ChannelBase> mineChannel, List<ChannelBase> keepChannel) {
        super(context, helper, mineChannel, keepChannel);
    }

    @Override
    public int resource(int sort) {
        switch (sort) {
            case TYPE_ITEM_MINE_HEAD:
                return R.layout.item_mine_head;
            case TYPE_ITEM_MINE_ITEM:
                return R.layout.item_mine_item;
            case TYPE_ITEM_KEEP_HEAD:
                return R.layout.item_keep_head;
            case TYPE_ITEM_KEEP_ITEM:
                return R.layout.item_keep_item;
            default:
                return 0;
        }
    }

}
