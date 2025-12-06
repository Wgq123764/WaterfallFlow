package com.example.waterfallflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        标签
        List<String> tabs = Arrays.asList("单列", "双列", "三列");

//        配置viewPager2
        viewPager2 = view.findViewById(R.id.home_view_pager2);
        viewPager2.setAdapter(new ViewPager2Adapter(this, tabs));

//        关联ViewPager2和TabLayout，设置标签
        tabLayout = view.findViewById(R.id.home_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(tabs.get(position));
        }).attach();

//        默认展示双列布局
        viewPager2.setCurrentItem(1, false);
    }

    private class ViewPager2Adapter extends FragmentStateAdapter {
        private List<String> tabs;

        ViewPager2Adapter(@NonNull Fragment fragment, List<String> tabs) {
            super(fragment);
            this.tabs = tabs;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return HomePagerFragment.newInstance(position + 1);
        }

        @Override
        public int getItemCount() {
            return tabs.size();
        }
    }
}