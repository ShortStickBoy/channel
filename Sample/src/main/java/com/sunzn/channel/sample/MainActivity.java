package com.sunzn.channel.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.sunzn.channel.library.ChannelAdapter;
import com.sunzn.channel.library.ChannelBase;
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
        final List<ChannelBase> mine = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            ChannelBean entity = new ChannelBean();
            switch (i) {
                case 0:
                    entity.setName("关注");
                    break;
                case 1:
                    entity.setName("推荐");
                    break;
                default:
                    entity.setName("频道" + i);
                    break;
            }
            mine.add(entity);
        }
        final List<ChannelBase> keep = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ChannelBean entity = new ChannelBean();
            entity.setName("其他" + i);
            keep.add(entity);
        }

        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRecy.setLayoutManager(manager);

        ItemTouchHelperCallback callback = new ItemTouchHelperCallback();
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecy);

        Adapter adapter = new Adapter(this, helper, mine, keep);
        mRecy.setAdapter(adapter);

        adapter.setMineChannelItemClickListener(new ChannelAdapter.OnMineChannelItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(MainActivity.this, mine.get(position).getValue(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
