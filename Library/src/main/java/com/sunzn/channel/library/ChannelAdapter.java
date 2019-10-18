package com.sunzn.channel.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.sunzn.channel.library.hold.KeepHeadViewHolder;
import com.sunzn.channel.library.hold.KeepItemViewHolder;
import com.sunzn.channel.library.hold.MineHeadViewHolder;
import com.sunzn.channel.library.hold.MineItemViewHolder;

import java.util.List;

/**
 * 拖拽排序 + 增删
 * Created by YoKeyword on 15/12/28.
 */
public abstract class ChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemMoveListener {
    // 我的频道 标题部分
    public static final int TYPE_ITEM_MINE_HEAD = 0;
    // 我的频道
    public static final int TYPE_ITEM_MINE_ITEM = 1;
    // 其他频道 标题部分
    public static final int TYPE_ITEM_KEEP_HEAD = 2;
    // 其他频道
    public static final int TYPE_ITEM_KEEP_ITEM = 3;

    // 我的频道之前的header数量  该demo中 即标题部分 为 1
    private static final int COUNT_MINE_HEAD = 1;
    // 其他频道之前的header数量  该demo中 即标题部分 为 COUNT_MINE_HEAD + 1
    private static final int COUNT_KEEP_HEAD = COUNT_MINE_HEAD + 1;

    private static final long ANIM_TIME = 360L;

    // touch 点击开始时间
    private long startTime;
    // touch 间隔时间  用于分辨是否是 "点击"
    private static final long SPACE_TIME = 100;

    private LayoutInflater mInflater;
    private ItemTouchHelper mItemTouchHelper;

    // 是否为 编辑 模式
    private boolean isEditMode;

    private int mFixedNum = 2;

    private List<ChannelBase> mMineChannel, mKeepChannel;

    private GridSpanSizeListener mSpanSizeListener;

    // 我的频道点击事件
    private OnMineChannelItemClickListener mChannelItemClickListener;

    public ChannelAdapter(Context context, ItemTouchHelper helper, List<ChannelBase> mineChannel, List<ChannelBase> keepChannel) {
        this.mInflater = LayoutInflater.from(context);
        this.mItemTouchHelper = helper;
        this.mMineChannel = mineChannel;
        this.mKeepChannel = keepChannel;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {    // 我的频道 标题部分
            return TYPE_ITEM_MINE_HEAD;
        } else if (position == mMineChannel.size() + 1) {    // 其他频道 标题部分
            return TYPE_ITEM_KEEP_HEAD;
        } else if (position > 0 && position < mMineChannel.size() + 1) {
            return TYPE_ITEM_MINE_ITEM;
        } else {
            return TYPE_ITEM_KEEP_ITEM;
        }
    }

    public abstract int resource(int sort);

    @NonNull
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view;
        switch (viewType) {
            case TYPE_ITEM_MINE_HEAD:
                view = mInflater.inflate(resource(TYPE_ITEM_MINE_HEAD), parent, false);
                final MineHeadViewHolder holder = new MineHeadViewHolder(view);
                holder.getEditView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isEditMode) {
                            startEditMode((RecyclerView) parent);
                            holder.getEditView().setText(R.string.warn_done);
                            holder.getWarnView().setText(R.string.warn_sort);
                        } else {
                            cancelEditMode((RecyclerView) parent);
                            holder.getEditView().setText(R.string.warn_edit);
                            holder.getWarnView().setText(R.string.warn_into);
                        }
                    }
                });
                return holder;

            case TYPE_ITEM_MINE_ITEM:
                view = mInflater.inflate(resource(TYPE_ITEM_MINE_ITEM), parent, false);
                final MineItemViewHolder myHolder = new MineItemViewHolder(view);

                myHolder.getNameView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        int position = myHolder.getAdapterPosition();
                        if (isEditMode) {
                            if (position > mFixedNum) {
                                RecyclerView recyclerView = ((RecyclerView) parent);
                                View targetView = recyclerView.getLayoutManager().findViewByPosition(mMineChannel.size() + COUNT_KEEP_HEAD);
                                View currentView = recyclerView.getLayoutManager().findViewByPosition(position);
                                // 如果targetView不在屏幕内,则indexOfChild为-1  此时不需要添加动画,因为此时notifyItemMoved自带一个向目标移动的动画
                                // 如果在屏幕内,则添加一个位移动画
                                if (recyclerView.indexOfChild(targetView) >= 0) {
                                    int targetX, targetY;

                                    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                                    int spanCount = ((GridLayoutManager) manager).getSpanCount();

                                    // 移动后 高度将变化 (我的频道Grid 最后一个item在新的一行第一个)
                                    if ((mMineChannel.size() - COUNT_MINE_HEAD) % spanCount == 0) {
                                        View preTargetView = recyclerView.getLayoutManager().findViewByPosition(mMineChannel.size() + COUNT_KEEP_HEAD - 1);
                                        targetX = preTargetView.getLeft();
                                        targetY = preTargetView.getTop();
                                    } else {
                                        targetX = targetView.getLeft();
                                        targetY = targetView.getTop();
                                    }

                                    moveMineToKeep(myHolder);
                                    startAnimation(recyclerView, currentView, targetX, targetY);

                                } else {
                                    moveMineToKeep(myHolder);
                                }
                            }
                        } else {
                            mChannelItemClickListener.onItemClick(v, position - COUNT_MINE_HEAD);
                        }
                    }
                });

                myHolder.getNameView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        int position = myHolder.getAdapterPosition();
                        if (position > mFixedNum) {
                            if (!isEditMode) {
                                RecyclerView recyclerView = ((RecyclerView) parent);
                                startEditMode(recyclerView);

                                // header 按钮文字 改成 "完成"
                                View view = recyclerView.getChildAt(0);
                                if (view == recyclerView.getLayoutManager().findViewByPosition(0)) {
                                    AppCompatTextView warnView = view.findViewById(R.id.mine_head_warn);
                                    warnView.setText(R.string.warn_sort);
                                    AppCompatTextView editView = view.findViewById(R.id.mine_head_edit);
                                    editView.setText(R.string.warn_done);
                                }
                            }

                            mItemTouchHelper.startDrag(myHolder);
                            return true;
                        } else {
                            RecyclerView recyclerView = ((RecyclerView) parent);
                            startEditMode(recyclerView);

                            // header 按钮文字 改成 "完成"
                            View view = recyclerView.getChildAt(0);
                            if (view == recyclerView.getLayoutManager().findViewByPosition(0)) {
                                AppCompatTextView warnView = view.findViewById(R.id.mine_head_warn);
                                warnView.setText(R.string.warn_sort);
                                AppCompatTextView editView = view.findViewById(R.id.mine_head_edit);
                                editView.setText(R.string.warn_done);
                            }
                            return true;
                        }
                    }
                });

                myHolder.getNameView().setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int position = myHolder.getAdapterPosition();
                        if (isEditMode) {
                            if (position > mFixedNum) {
                                switch (event.getActionMasked()) {
                                    case MotionEvent.ACTION_DOWN:
                                        startTime = System.currentTimeMillis();
                                        break;
                                    case MotionEvent.ACTION_MOVE:
                                        if (System.currentTimeMillis() - startTime > SPACE_TIME) {
                                            mItemTouchHelper.startDrag(myHolder);
                                        }
                                        break;
                                    case MotionEvent.ACTION_CANCEL:
                                    case MotionEvent.ACTION_UP:
                                        startTime = 0;
                                        break;
                                }
                            } else {
                                return true;
                            }
                        }
                        return false;
                    }
                });
                return myHolder;

            case TYPE_ITEM_KEEP_HEAD:
                view = mInflater.inflate(resource(TYPE_ITEM_KEEP_HEAD), parent, false);
                return new KeepHeadViewHolder(view);

            case TYPE_ITEM_KEEP_ITEM:
                view = mInflater.inflate(resource(TYPE_ITEM_KEEP_ITEM), parent, false);
                final KeepItemViewHolder otherHolder = new KeepItemViewHolder(view);
                otherHolder.getNameView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecyclerView recyclerView = ((RecyclerView) parent);
                        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                        int currentPosition = otherHolder.getAdapterPosition();
                        // 如果RecyclerView滑动到底部,移动的目标位置的y轴 - height
                        View currentView = manager.findViewByPosition(currentPosition);
                        // 目标位置的前一个item  即当前MyChannel的最后一个
                        View preTargetView = manager.findViewByPosition(mMineChannel.size() - 1 + COUNT_MINE_HEAD);

                        // 如果targetView不在屏幕内,则为-1  此时不需要添加动画,因为此时notifyItemMoved自带一个向目标移动的动画
                        // 如果在屏幕内,则添加一个位移动画
                        if (recyclerView.indexOfChild(preTargetView) >= 0) {
                            int targetX = preTargetView.getLeft();
                            int targetY = preTargetView.getTop();

                            int targetPosition = mMineChannel.size() - 1 + COUNT_KEEP_HEAD;

                            GridLayoutManager gridLayoutManager = ((GridLayoutManager) manager);
                            int spanCount = gridLayoutManager.getSpanCount();
                            // target 在最后一行第一个
                            if ((targetPosition - COUNT_MINE_HEAD) % spanCount == 0) {
                                View targetView = manager.findViewByPosition(targetPosition);
                                targetX = targetView.getLeft();
                                targetY = targetView.getTop();
                            } else {
                                targetX += preTargetView.getWidth();

                                // 最后一个item可见
                                if (gridLayoutManager.findLastVisibleItemPosition() == getItemCount() - 1) {
                                    // 最后的item在最后一行第一个位置
                                    if ((getItemCount() - 1 - mMineChannel.size() - COUNT_KEEP_HEAD) % spanCount == 0) {
                                        // RecyclerView实际高度 > 屏幕高度 && RecyclerView实际高度 < 屏幕高度 + item.height
                                        int firstVisiblePostion = gridLayoutManager.findFirstVisibleItemPosition();
                                        if (firstVisiblePostion == 0) {
                                            // FirstCompletelyVisibleItemPosition == 0 即 内容不满一屏幕 , targetY值不需要变化
                                            // // FirstCompletelyVisibleItemPosition != 0 即 内容满一屏幕 并且 可滑动 , targetY值 + firstItem.getTop
                                            if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                                                int offset = (-recyclerView.getChildAt(0).getTop()) - recyclerView.getPaddingTop();
                                                targetY += offset;
                                            }
                                        } else { // 在这种情况下 并且 RecyclerView高度变化时(即可见第一个item的 position != 0),
                                            // 移动后, targetY值  + 一个item的高度
                                            targetY += preTargetView.getHeight();
                                        }
                                    }
                                } else {
                                    System.out.println("current--No");
                                }
                            }

                            // 如果当前位置是otherChannel可见的最后一个
                            // 并且 当前位置不在grid的第一个位置
                            // 并且 目标位置不在grid的第一个位置

                            // 则 需要延迟250秒 notifyItemMove , 这是因为这种情况 , 并不触发ItemAnimator , 会直接刷新界面
                            // 导致我们的位移动画刚开始,就已经notify完毕,引起不同步问题
                            if (currentPosition == gridLayoutManager.findLastVisibleItemPosition()
                                    && (currentPosition - mMineChannel.size() - COUNT_KEEP_HEAD) % spanCount != 0
                                    && (targetPosition - COUNT_MINE_HEAD) % spanCount != 0) {
                                moveOtherToMyWithDelay(otherHolder);
                            } else {
                                moveKeepToMine(otherHolder);
                            }
                            startAnimation(recyclerView, currentView, targetX, targetY);

                        } else {
                            moveKeepToMine(otherHolder);
                        }
                    }
                });
                return otherHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MineItemViewHolder) {
            MineItemViewHolder myHolder = (MineItemViewHolder) holder;
            myHolder.getNameView().setText(mMineChannel.get(position - COUNT_MINE_HEAD).getValue());
            if (isEditMode) {
                myHolder.getExecView().setVisibility(View.VISIBLE);
            } else {
                myHolder.getExecView().setVisibility(View.INVISIBLE);
            }
        } else if (holder instanceof KeepItemViewHolder) {
            ((KeepItemViewHolder) holder).getNameView().setText(mKeepChannel.get(position - mMineChannel.size() - COUNT_KEEP_HEAD).getValue());
        } else if (holder instanceof MineHeadViewHolder) {
            MineHeadViewHolder headerHolder = (MineHeadViewHolder) holder;
            if (isEditMode) {
                headerHolder.getEditView().setText(R.string.warn_done);
                headerHolder.getWarnView().setText(R.string.warn_sort);
            } else {
                headerHolder.getEditView().setText(R.string.warn_edit);
                headerHolder.getWarnView().setText(R.string.warn_into);
            }
        }
    }

    @Override
    public int getItemCount() {
        // 我的频道  标题 + 我的频道.size + 其他频道 标题 + 其他频道.size
        return mMineChannel.size() + mKeepChannel.size() + COUNT_KEEP_HEAD;
    }

    /**
     * 开始增删动画
     */
    private void startAnimation(RecyclerView recyclerView, final View currentView, float targetX, float targetY) {
        final ViewGroup viewGroup = (ViewGroup) recyclerView.getParent();
        final ImageView mirrorView = addMirrorView(viewGroup, recyclerView, currentView);

        Animation animation = getTranslateAnimator(targetX - currentView.getLeft(), targetY - currentView.getTop());
        currentView.setVisibility(View.INVISIBLE);
        mirrorView.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewGroup.removeView(mirrorView);
                if (currentView.getVisibility() == View.INVISIBLE) {
                    currentView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 我的频道 移动到 其他频道
     *
     * @param holder
     */
    private void moveMineToKeep(MineItemViewHolder holder) {
        int position = holder.getAdapterPosition();

        int startPosition = position - COUNT_MINE_HEAD;
        if (startPosition > mMineChannel.size() - 1) {
            return;
        }
        ChannelBase item = mMineChannel.get(startPosition);
        mMineChannel.remove(startPosition);
        mKeepChannel.add(0, item);

        notifyItemMoved(position, mMineChannel.size() + COUNT_KEEP_HEAD);
    }

    /**
     * 其他频道 移动到 我的频道
     *
     * @param holder
     */
    private void moveKeepToMine(KeepItemViewHolder holder) {
        int position = processItemRemoveAdd(holder);
        if (position == -1) {
            return;
        }
        notifyItemMoved(position, mMineChannel.size() - 1 + COUNT_MINE_HEAD);
    }

    /**
     * 其他频道 移动到 我的频道 伴随延迟
     *
     * @param otherHolder
     */
    private void moveOtherToMyWithDelay(KeepItemViewHolder otherHolder) {
        final int position = processItemRemoveAdd(otherHolder);
        if (position == -1) {
            return;
        }
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyItemMoved(position, mMineChannel.size() - 1 + COUNT_MINE_HEAD);
            }
        }, ANIM_TIME);
    }

    private Handler delayHandler = new Handler();

    private int processItemRemoveAdd(KeepItemViewHolder otherHolder) {
        int position = otherHolder.getAdapterPosition();

        int startPosition = position - mMineChannel.size() - COUNT_KEEP_HEAD;
        if (startPosition > mKeepChannel.size() - 1) {
            return -1;
        }
        ChannelBase item = mKeepChannel.get(startPosition);
        mKeepChannel.remove(startPosition);
        mMineChannel.add(item);
        return position;
    }


    /**
     * 添加需要移动的 镜像View
     */
    private ImageView addMirrorView(ViewGroup parent, RecyclerView recyclerView, View view) {
        /* *
         我们要获取cache首先要通过setDrawingCacheEnable方法开启cache，然后再调用getDrawingCache方法就可以获得view的cache图片了。
         buildDrawingCache方法可以不用调用，因为调用getDrawingCache方法时，若果cache没有建立，系统会自动调用buildDrawingCache方法生成cache。
         若想更新cache, 必须要调用destoryDrawingCache方法把旧的cache销毁，才能建立新的。
         当调用setDrawingCacheEnabled方法设置为false, 系统也会自动把原来的cache销毁。
         */
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        final ImageView mirrorView = new ImageView(recyclerView.getContext());
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        mirrorView.setImageBitmap(bitmap);
        view.setDrawingCacheEnabled(false);
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        int[] parenLocations = new int[2];
        recyclerView.getLocationOnScreen(parenLocations);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
        params.setMargins(locations[0], locations[1] - parenLocations[1], 0, 0);
        parent.addView(mirrorView, params);
        return mirrorView;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        ChannelBase item = mMineChannel.get(fromPosition - COUNT_MINE_HEAD);
        mMineChannel.remove(fromPosition - COUNT_MINE_HEAD);
        mMineChannel.add(toPosition - COUNT_MINE_HEAD, item);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * 开启编辑模式
     *
     * @param parent
     */
    private void startEditMode(RecyclerView parent) {
        isEditMode = true;

        int visibleChildCount = parent.getChildCount();
        for (int i = 0; i < visibleChildCount; i++) {
            if (i > mFixedNum) {
                View view = parent.getChildAt(i);
                AppCompatImageView exec = view.findViewById(R.id.mine_item_exec);
                if (exec != null) {
                    exec.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 完成编辑模式
     *
     * @param parent
     */
    private void cancelEditMode(RecyclerView parent) {
        isEditMode = false;

        int visibleChildCount = parent.getChildCount();
        for (int i = 0; i < visibleChildCount; i++) {
            View view = parent.getChildAt(i);
            AppCompatImageView exec = view.findViewById(R.id.mine_item_exec);
            if (exec != null) {
                exec.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 获取位移动画
     */
    private TranslateAnimation getTranslateAnimator(float targetX, float targetY) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetX,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetY);
        // RecyclerView默认移动动画250ms 这里设置360ms 是为了防止在位移动画结束后 remove(view)过早 导致闪烁
        translateAnimation.setDuration(ANIM_TIME);
        translateAnimation.setFillAfter(true);
        return translateAnimation;
    }

    public void setSpanSizeListener(GridSpanSizeListener listener) {
        mSpanSizeListener = listener;
    }

    public interface OnMineChannelItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setMineChannelItemClickListener(OnMineChannelItemClickListener listener) {
        this.mChannelItemClickListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager GridManager = (GridLayoutManager) manager;
            GridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position < getItemCount()) {
                        return mSpanSizeListener == null ? 1 : mSpanSizeListener.onGetSpanCount(getItemViewType(position), GridManager);
                    } else {
                        return GridManager.getSpanCount();
                    }
                }
            });
        }
    }

}
