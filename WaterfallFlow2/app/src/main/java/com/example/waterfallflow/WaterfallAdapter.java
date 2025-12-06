// 修改WaterfallAdapter.java
package com.example.waterfallflow;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.List;

public class WaterfallAdapter extends RecyclerView.Adapter<WaterfallAdapter.ViewHolder> {
    private List<Item> itemList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Item item, int position);
    }

    public WaterfallAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_waterfall, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.bind(item);

//        // 设置图片资源
//        holder.imageView.setImageResource(item.getImageResId());
//
//        // 设置标题和描述
//        holder.titleText.setText(item.getTitle());
//        holder.descriptionText.setText(item.getDescription());
//
//        // 设置随机高度，实现瀑布流效果
//        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
//        layoutParams.height = item.getHeight();
//        holder.itemView.setLayoutParams(layoutParams);

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        // 设置横跨全宽属性
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams staggeredParams =
                    (StaggeredGridLayoutManager.LayoutParams) layoutParams;

            Item item = itemList.get(holder.getAdapterPosition());
            staggeredParams.setFullSpan(item.isFullWidth());
        }
    }

    public void updateData(List<Item> newItems) {
        this.itemList = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView imageView;
        public TextView titleText;
        public TextView descriptionText;
        public View container;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.item_card);
            imageView = view.findViewById(R.id.item_image);
            titleText = view.findViewById(R.id.item_title);
            descriptionText = view.findViewById(R.id.item_description);
            container = view.findViewById(R.id.item_container);
        }

        public void bind(Item item) {
            titleText.setText(item.getTitle());
            descriptionText.setText(item.getDescription());
            imageView.setImageResource(item.getImageResId());
        }

    }
}