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
    private int singleColumnWidth;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Item item, int position);
    }

    public WaterfallAdapter(List<Item> itemList, int singleColumnWidth) {
        this.itemList = itemList;
        this.singleColumnWidth = singleColumnWidth;
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
        holder.bind(item, singleColumnWidth);

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

        public void bind(Item item, int cardWidth) {
            titleText.setText(item.getTitle());
            descriptionText.setText(item.getDescription());

            // 根据卡片类型计算宽度
            int actualCardWidth = item.isFullWidth() ? getFullWidth() : cardWidth;

            // 计算图片等比例高度
            int imageHeight = item.calculateImageHeight(actualCardWidth);

            // 设置图片高度并加载图片
            setImageWithHeight(item.getImageResId(), imageHeight);

            // 计算CardView总高度
            int titleHeight = measureTextViewHeight(titleText, actualCardWidth);
            int descHeight = measureTextViewHeight(descriptionText, actualCardWidth);
            int padding = getTotalVerticalPadding();

            int cardHeight = imageHeight + titleHeight + descHeight + padding;

            // 设置CardView布局参数
            ViewGroup.LayoutParams cardParams = cardView.getLayoutParams();
            // cardParams.width = actualCardWidth;
            cardParams.height = cardHeight;
            cardView.setLayoutParams(cardParams);
        }

        /**
         * 设置图片并调整高度
         */
        private void setImageWithHeight(int imageResId, int targetHeight) {
            // 设置图片高度
            ViewGroup.LayoutParams imageParams = imageView.getLayoutParams();
            imageParams.height = targetHeight;
            imageView.setLayoutParams(imageParams);

            // 加载图片
            imageView.setImageResource(imageResId);
        }

        /**
         * 获取横跨卡片的宽度
         */
        private int getFullWidth() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) itemView.getContext()).getWindowManager()
                    .getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;

            int horizontalPadding = itemView.getContext().getResources()
                    .getDimensionPixelSize(R.dimen.recycler_view_padding) * 2;
            int cardMargin = itemView.getContext().getResources()
                    .getDimensionPixelSize(R.dimen.card_margin) * 2;

            return screenWidth - horizontalPadding - cardMargin;
        }

        /**
         * 测量TextView在指定宽度下的高度
         */
        private int measureTextViewHeight(TextView textView, int width) {
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            textView.measure(widthMeasureSpec, heightMeasureSpec);

            int marginVertical = 0;
            ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                marginVertical += marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
            }

            return textView.getMeasuredHeight() + marginVertical;
        }

        /**
         * 获取CardView的总垂直内边距
         */
        private int getTotalVerticalPadding() {
            int verticalPadding = cardView.getContentPaddingTop() + cardView.getContentPaddingBottom();

            if (imageView.getParent() instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) imageView.getParent();
                verticalPadding += parent.getPaddingTop() + parent.getPaddingBottom();

                if (titleText.getParent() instanceof ViewGroup) {
                    ViewGroup textParent = (ViewGroup) titleText.getParent();
                    verticalPadding += textParent.getPaddingTop() + textParent.getPaddingBottom();
                }
            }

            return verticalPadding;
        }
    }
}