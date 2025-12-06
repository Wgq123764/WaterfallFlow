// 修改WaterfallAdapter.java
package com.example.waterfallflow;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaterfallAdapter extends RecyclerView.Adapter<WaterfallAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onImageClick(Item item);
    }
    private OnItemClickListener mListener;
    private Context mContext;
    private List<Item> mItemList;
    private static final Random mRandom = new Random();
    private List<Integer> mSampleImageList;
    private final int mRecommendItems = 3;

    public WaterfallAdapter(Context context, List<Item> itemList) {
        mContext = context;
        mItemList = itemList;
        mSampleImageList = new ArrayList<>();
        loadSamples();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_waterfall, parent, false);
        return new ViewHolder(view, mListener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = mItemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        // 设置横跨全宽属性
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams staggeredParams =
                    (StaggeredGridLayoutManager.LayoutParams) layoutParams;

            Item item = mItemList.get(holder.getAdapterPosition());
            staggeredParams.setFullSpan(item.isFullWidth());
        }
    }

    public void updateData(List<Item> newItemList) {
        this.mItemList = newItemList;
        notifyDataSetChanged();
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.inflate(R.menu.menu_item_options);

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_recommend) {
                recommendMore(position);
                return true;
            } else if (item.getItemId() == R.id.action_dislike) {
                removeItem(position);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void recommendMore(int position) {
        for (int i = 0; i < mRecommendItems; i++) {
            int randomIndex = mRandom.nextInt(mSampleImageList.size());
            int randomImage = mSampleImageList.get(randomIndex);
            mItemList.add(position + 1 + i, new Item(
                    Item.TYPE_NORMAL,
                    randomImage,
                    "卡片标题 " + (position + 1 + i),
                    "这是第" + (position + 1 + i) + "个卡片的描述文字。"
            ));
        }
        notifyItemRangeInserted(position + 1, mRecommendItems);
        Toast.makeText(mContext, "已推荐更多内容", Toast.LENGTH_SHORT).show();
    }

    private void removeItem(int position) {
        mItemList.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(mContext, "已删除该内容", Toast.LENGTH_SHORT).show();
    }

    private void loadSamples() {
        try {
            Field[] fields = R.drawable.class.getFields();

            for (Field field : fields) {
                String fieldName = field.getName();

                // 排除系统自带的图标和主页专用图标
                if (fieldName.startsWith("ic_launcher") ||
                        fieldName.startsWith("ic_menu") ||
                        fieldName.startsWith("home_") ||
                        fieldName.equals("ic_launcher_foreground") ||
                        fieldName.equals("ic_launcher_background")) {
                    continue;
                }

                try {
                    int resId = field.getInt(null);
                    mSampleImageList.add(resId);
                    Log.d("DrawableLoader", "Found image: " + fieldName);
                } catch (Exception e) {
                    // 忽略无法访问的字段
                }
            }
        } catch (Exception e) {
            Log.e("DrawableLoader", "Error accessing R.drawable class: " + e.getMessage());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private TextView mTitle;
        private TextView mDescription;
        private Item mCurItem;
        private WeakReference<OnItemClickListener> mListenerRef;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener, WaterfallAdapter adapter) {
            super(itemView);
            mListenerRef = new WeakReference<>(listener);

            mImage = itemView.findViewById(R.id.item_image);
            mTitle = itemView.findViewById(R.id.item_title);
            mDescription = itemView.findViewById(R.id.item_description);

            mImage.setClickable(true);
            mImage.setOnClickListener(v -> {
                if (mListenerRef.get() != null && mCurItem != null) {
                    mListenerRef.get().onImageClick(mCurItem);
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    adapter.showPopupMenu(itemView, position);
                    return true;
                }
                return false;
            });
        }

        public void bind(Item item) {
            mCurItem = item;
            mImage.setImageResource(item.getImageResId());
            mTitle.setText(item.getTitle());
            mDescription.setText(item.getDescription());
        }
    }
}