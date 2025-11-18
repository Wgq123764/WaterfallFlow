package com.example.waterfallflow;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private WaterfallAdapter adapter;
    private List<Item> itemList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int currentPage = 1;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);
        initData();
        setupRecyclerView();
        setupRefresh();
        setupLoadMore();

        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
    }

    private void initData() {
        itemList = new ArrayList<>();
        loadData(1);
    }

    private void setupRecyclerView() {
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new WaterfallAdapter(itemList);
        recyclerView.setAdapter(adapter);
    }

    private void setupRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // 模拟网络请求延迟
            new Handler().postDelayed(() -> {
                currentPage = 1;
                itemList.clear();
                loadData(currentPage);
                swipeRefreshLayout.setRefreshing(false);
            }, 1000);
        });
    }

    private void setupLoadMore() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                int[] lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null);
                int lastVisibleItem = getLastVisibleItem(lastVisibleItemPositions);
                int totalItemCount = layoutManager.getItemCount();

                if (!isLoading && lastVisibleItem >= totalItemCount - 5) {
                    loadMoreData();
                    isLoading = true;
                }
            }
        });
    }

    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int max = lastVisibleItemPositions[0];
        for (int value : lastVisibleItemPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private void loadData(int page) {
        Random random = new Random();
        int[] imageResources = {R.drawable.ic_launcher_foreground};
        String[] titles = {"美丽风景", "城市风光", "自然奇观", "人文建筑", "动物世界"};
        String[] descriptions = {
                "这是一段描述文字，展示瀑布流布局的效果",
                "另一段描述，展示不同高度的卡片",
                "瀑布流布局让内容展示更加生动",
                "双列布局充分利用屏幕空间",
                "随机高度创造视觉上的变化"
        };

        int itemsPerPage = 10;
        for (int i = 0; i < itemsPerPage; i++) {
            int index = (page - 1) * itemsPerPage + i;
            int imageRes = imageResources[0];
            String title = titles[i % titles.length] + " " + index;
            String description = descriptions[i % descriptions.length];
            int height = 400 + random.nextInt(300);

            itemList.add(new Item(imageRes, title, description, height));
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadMoreData() {
        // 模拟网络请求延迟
        new Handler().postDelayed(() -> {
            currentPage++;
            loadData(currentPage);
            isLoading = false;
        }, 1500);
    }
}