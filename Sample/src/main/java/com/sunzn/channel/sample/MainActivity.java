package com.sunzn.channel.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.sunzn.channel.library.ChannelAdapter;
import com.sunzn.channel.library.ChannelBean;
import com.sunzn.channel.library.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecy = findViewById(R.id.recy);
        init();
    }

    private void init() {
        final List<ChannelBean> items = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            ChannelBean entity = new ChannelBean();
            entity.setName("频道" + i);
            items.add(entity);
        }
        final List<ChannelBean> otherItems = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ChannelBean entity = new ChannelBean();
            entity.setName("其他" + i);
            otherItems.add(entity);
        }

        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRecy.setLayoutManager(manager);

        ItemTouchHelperCallback callback = new ItemTouchHelperCallback();
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecy);

        final ChannelAdapter adapter = new ChannelAdapter(this, helper, items, otherItems);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = adapter.getItemViewType(position);
                return viewType == ChannelAdapter.TYPE_ITEM_MINE_ITEM || viewType == ChannelAdapter.TYPE_TIEM_KEEP_ITEM ? 1 : 4;
            }
        });
        mRecy.setAdapter(adapter);

        adapter.setOnMyChannelItemClickListener(new ChannelAdapter.OnMyChannelItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(MainActivity.this, items.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
