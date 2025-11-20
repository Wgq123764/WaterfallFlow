package com.example.waterfallflow;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProfileOption> optionList;
    private OnOptionClickListener onOptionClickListener;

    public interface OnOptionClickListener {
        void onOptionClick(ProfileOption option, int position);
    }

    public ProfileAdapter(List<ProfileOption> optionList) {
        this.optionList = optionList;
    }

    public void setOnOptionClickListener(OnOptionClickListener listener) {
        this.onOptionClickListener = listener;
        Log.d("ProfileAdapter", "✅ 设置点击监听器");
    }

    @Override
    public int getItemViewType(int position) {
        return optionList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_profile_option, parent, false);

        if (viewType == ProfileOption.TYPE_SECTION) {
            return new SectionViewHolder(view);
        } else {
            return new OptionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProfileOption option = optionList.get(position);

        if (holder instanceof SectionViewHolder) {
            ((SectionViewHolder) holder).bind(option);
        } else if (holder instanceof OptionViewHolder) {
            ((OptionViewHolder) holder).bind(option, position);
        }
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    // 分组标题ViewHolder
    static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;
        View optionItem;

        SectionViewHolder(View view) {
            super(view);
            sectionTitle = view.findViewById(R.id.section_title);
            optionItem = view.findViewById(R.id.option_item);
        }

        void bind(ProfileOption option) {
            sectionTitle.setVisibility(View.VISIBLE);
            optionItem.setVisibility(View.GONE);
            sectionTitle.setText(option.getSectionTitle());

            // 分组标题不可点击
            itemView.setClickable(false);
        }
    }

    // 选项ViewHolder
    class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;
        TextView optionText;
        View optionItem;

        OptionViewHolder(View view) {
            super(view);
            sectionTitle = view.findViewById(R.id.section_title);
            optionText = view.findViewById(R.id.option_text);
            optionItem = view.findViewById(R.id.option_item);

            // 直接设置点击监听器
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ProfileOption option = optionList.get(position);
                    Log.d("ProfileAdapter", "🎯 RecyclerView项点击: " + option.getTitle());
                    if (onOptionClickListener != null) {
                        onOptionClickListener.onOptionClick(option, position);
                    } else {
                        Log.e("ProfileAdapter", "❌ onOptionClickListener 为 null");
                    }
                }
            });
        }

        void bind(ProfileOption option, int position) {
            sectionTitle.setVisibility(View.GONE);
            optionItem.setVisibility(View.VISIBLE);
            optionText.setText(option.getTitle());

            Log.d("ProfileAdapter", "✅ 绑定选项: " + option.getTitle() + ", 位置: " + position);
        }
    }
}