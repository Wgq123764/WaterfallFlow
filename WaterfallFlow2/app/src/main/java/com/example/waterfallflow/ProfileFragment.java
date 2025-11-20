package com.example.waterfallflow;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProfileAdapter adapter;
    private List<ProfileOption> optionList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Log.d("ProfileFragment", "=== ProfileFragment onCreateView ===");

        initView(view);
        initData();
        setupRecyclerView();
        setupClickListeners();

        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.profile_recycler_view);

        if (recyclerView == null) {
            Log.e("ProfileFragment", "❌ recyclerView 为 null");
        } else {
            Log.d("ProfileFragment", "✅ recyclerView 初始化成功");
        }

        // 设置顶部个人信息点击事件 - 添加更多调试
        View userInfoCard = view.findViewById(R.id.user_info_card);
        if (userInfoCard != null) {
            Log.d("ProfileFragment", "✅ 找到 user_info_card");

            userInfoCard.setOnClickListener(v -> {
                Log.d("ProfileFragment", "🎯 点击了个人信息卡片！！！");

                // 测试简单的Toast先
                try {
                    PersonalPageFragment personalPageFragment = new PersonalPageFragment();

                    if (getParentFragmentManager() != null) {
                        getParentFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .addToBackStack("personal_page")
                                .replace(R.id.fragment_container, personalPageFragment)
                                .commit();
                        Log.d("ProfileFragment", "✅ 已提交个人页面事务");
                    } else {
                        Log.e("ProfileFragment", "❌ getParentFragmentManager() 为 null");
                    }
                } catch (Exception e) {
                    Log.e("ProfileFragment", "❌ 跳转异常: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            // 测试点击区域
            userInfoCard.setOnLongClickListener(v -> {
                Log.d("ProfileFragment", "🎯 长按了个人信息卡片！！！");
                return true;
            });

        } else {
            Log.e("ProfileFragment", "❌ 未找到 user_info_card 视图");
        }
    }

    private void initData() {
        optionList = new ArrayList<>();

        // 作品管理分组
        optionList.add(new ProfileOption(ProfileOption.TYPE_SECTION, "", "作品管理"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "投稿作品"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "作品管理"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "收藏"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "浏览记录"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "书签"));

        // 社交分组
        optionList.add(new ProfileOption(ProfileOption.TYPE_SECTION, "", "社交"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "积分"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "关联"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "关注"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "粉丝"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "好P友"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "其他"));

        // 设置分组
        optionList.add(new ProfileOption(ProfileOption.TYPE_SECTION, "", "设置"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "屏蔽设定"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "帮助与反馈"));
        optionList.add(new ProfileOption(ProfileOption.TYPE_OPTION, "关于本APP"));

        Log.d("ProfileFragment", "✅ 初始化了 " + optionList.size() + " 个选项");
    }

    private void setupRecyclerView() {
        if (recyclerView == null) {
            Log.e("ProfileFragment", "❌ recyclerView 为 null，无法设置布局管理器");
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProfileAdapter(optionList);
        recyclerView.setAdapter(adapter);

        Log.d("ProfileFragment", "✅ RecyclerView 设置完成");
    }

    private void setupClickListeners() {
        if (adapter == null) {
            Log.e("ProfileFragment", "❌ adapter 为 null，无法设置点击监听器");
            return;
        }

        adapter.setOnOptionClickListener((option, position) -> {
            Log.d("ProfileFragment", "🎯 选项点击事件: " + option.getTitle() + ", 位置: " + position);
            if (option.getType() == ProfileOption.TYPE_OPTION) {
                openFragment(option.getTitle(), "无法找到搜索内容");
            }
        });

        Log.d("ProfileFragment", "✅ 点击监听器设置完成");
    }

    private void openFragment(String title, String content) {
        Log.d("ProfileFragment", "🔄 准备打开Fragment: " + title);

        try {
            SimpleTextFragment fragment = SimpleTextFragment.newInstance(title, content);

            if (getParentFragmentManager() != null) {
                getParentFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .addToBackStack("profile_detail")
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                Log.d("ProfileFragment", "✅ 已提交Fragment事务: " + title);
            } else {
                Log.e("ProfileFragment", "❌ getParentFragmentManager() 为 null");
            }
        } catch (Exception e) {
            Log.e("ProfileFragment", "❌ 打开Fragment异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}