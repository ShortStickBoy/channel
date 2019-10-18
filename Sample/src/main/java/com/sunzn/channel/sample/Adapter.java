package com.sunzn.channel.sample;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.sunzn.channel.library.ChannelAdapter;
import com.sunzn.channel.library.ChannelBase;
import com.sunzn.channel.library.GridSpanSizeListener;

import java.util.List;

public class Adapter extends ChannelAdapter implements GridSpanSizeListener {

    public Adapter(Context context, ItemTouchHelper helper, List<ChannelBase> mineChannel, List<ChannelBase> keepChannel) {
        super(context, helper, mineChannel, keepChannel);
        setSpanSizeListener(this);
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

    @Override
    public int onGetSpanCount(int viewType, GridLayoutManager manager) {
        switch (viewType) {
            case TYPE_ITEM_MINE_ITEM:
            case TYPE_ITEM_KEEP_ITEM:
                return manager.getSpanCount() / 4;
            default:
                return manager.getSpanCount();
        }
    }

}
