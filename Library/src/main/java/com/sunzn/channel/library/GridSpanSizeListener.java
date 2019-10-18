package com.sunzn.channel.library;

import androidx.recyclerview.widget.GridLayoutManager;

public interface GridSpanSizeListener {

    int onGetSpanCount(int viewType, GridLayoutManager manager);

}
