package com.example.waterfallflow;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.reflect.Field;
import android.util.Log;

public class DashboardFragment extends Fragment {
    private RecyclerView recyclerView;
    private WaterfallAdapter adapter;
    private List<Item> itemList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isRefreshing = false;

    // 优化设置 - 方案四
    private static final int INITIAL_PAGES = 3;     // 初始加载3页
    private static final int ITEMS_PER_PAGE = 18;   // 每页18个项目（推荐页面可以更多）
    private static final int PRELOAD_THRESHOLD = 8; // 提前预加载阈值
    private static final int MAX_PAGES = 8;         // 最大页数

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

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
        // 初始加载多页数据，避免频繁加载
        for (int i = 1; i <= INITIAL_PAGES; i++) {
            loadData(i);
        }
        currentPage = INITIAL_PAGES;
    }

    private void setupRecyclerView() {
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new WaterfallAdapter(itemList);

        adapter.setOnItemClickListener((item, position) -> {
            ImageDetailFragment detailFragment = ImageDetailFragment.newInstance(
                    item.getImageResId(),
                    item.getTitle()
            );

            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack("image_detail")
                    .replace(R.id.fragment_container, detailFragment)
                    .commit();
        });

        recyclerView.setAdapter(adapter);
    }

    private void setupRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isRefreshing) {
                isRefreshing = true;
                new Handler().postDelayed(() -> {
                    currentPage = 1;
                    itemList.clear();
                    // 刷新时只加载第一页，避免刷新时间过长
                    loadData(1);
                    swipeRefreshLayout.setRefreshing(false);
                    isRefreshing = false;
                    Toast.makeText(getContext(), "刷新完成", Toast.LENGTH_SHORT).show();
                }, 1000);
            }
        });
    }

    private void setupLoadMore() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 添加状态检查
                if (isLoading || isRefreshing || currentPage >= MAX_PAGES) {
                    return;
                }

                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int[] lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null);
                    int lastVisibleItem = getLastVisibleItem(lastVisibleItemPositions);
                    int totalItemCount = layoutManager.getItemCount();

                    // 使用预加载阈值，提前加载
                    if (lastVisibleItem >= totalItemCount - PRELOAD_THRESHOLD) {
                        loadMoreData();
                        isLoading = true;
                    }
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

    private List<Integer> getAllDashboardImageDrawables() {
        List<Integer> drawableList = new ArrayList<>();

        try {
            Field[] fields = R.drawable.class.getFields();

            for (Field field : fields) {
                String fieldName = field.getName();

                if (!fieldName.startsWith("ic_launcher") &&
                        !fieldName.startsWith("ic_menu") &&
                        !fieldName.startsWith("home_") &&
                        !fieldName.equals("ic_launcher_foreground") &&
                        !fieldName.equals("ic_launcher_background")) {

                    try {
                        int resId = field.getInt(null);

                        // 验证资源是否存在
                        try {
                            if (getResources().getResourceName(resId) != null) {
                                drawableList.add(resId);
                                Log.d("DashboardDrawableLoader", "Found dashboard image: " + fieldName);
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e("DashboardDrawableLoader", "Resource not found: " + fieldName);
                        }

                    } catch (Exception e) {
                        Log.e("DashboardDrawableLoader", "Error accessing field: " + fieldName, e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("DashboardDrawableLoader", "Error accessing R.drawable class: " + e.getMessage());
        }

        Log.d("DashboardDrawableLoader", "Total dashboard images found: " + drawableList.size());
        return drawableList;
    }

    private List<Integer> getAllImageDrawables() {
        List<Integer> drawableList = new ArrayList<>();

        try {
            Field[] fields = R.drawable.class.getFields();

            for (Field field : fields) {
                String fieldName = field.getName();

                if (fieldName.startsWith("ic_launcher") ||
                        fieldName.startsWith("ic_menu") ||
                        fieldName.equals("ic_launcher_foreground") ||
                        fieldName.equals("ic_launcher_background")) {
                    continue;
                }

                try {
                    int resId = field.getInt(null);
                    drawableList.add(resId);
                    Log.d("DrawableLoader", "Found image: " + fieldName);
                } catch (Exception e) {
                    // 忽略无法访问的字段
                }
            }
        } catch (Exception e) {
            Log.e("DrawableLoader", "Error accessing R.drawable class: " + e.getMessage());
        }

        Log.d("DrawableLoader", "Total images found: " + drawableList.size());
        return drawableList;
    }

    private void loadData(int page) {
        Random random = new Random();

        List<Integer> imageResources = getAllDashboardImageDrawables();

        if (imageResources.isEmpty()) {
            Log.w("DashboardFragment", "No dashboard images found, using all images as backup");
            imageResources = getAllImageDrawables();

            if (imageResources.isEmpty()) {
                imageResources.add(R.drawable.ic_launcher_foreground);
                Log.w("DashboardFragment", "No images found, using default icon");
            }
        }

        String[] titles = {"美丽风景", "城市风光", "自然奇观", "人文建筑", "动物世界"};
        String[] descriptions = {
                "这是一段描述文字，展示瀑布流布局的效果",
                "另一段描述，展示不同高度的卡片",
                "瀑布流布局让内容展示更加生动",
                "双列布局充分利用屏幕空间",
                "随机高度创造视觉上的变化"
        };

        // 使用固定的每页项目数
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int index = (page - 1) * ITEMS_PER_PAGE + i;

            int randomIndex = random.nextInt(imageResources.size());
            int imageRes = imageResources.get(randomIndex);

            String title = titles[i % titles.length] + " " + index;
            String description = descriptions[i % descriptions.length];
            int height = 550 + random.nextInt(450);

            itemList.add(new Item(imageRes, title, description, height));
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadMoreData() {
        if (currentPage >= MAX_PAGES) {
            Toast.makeText(getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
            isLoading = false;
            return;
        }

        new Handler().postDelayed(() -> {
            currentPage++;
            loadData(currentPage);
            isLoading = false;
            Toast.makeText(getContext(), "加载了第" + currentPage + "页数据", Toast.LENGTH_SHORT).show();
        }, 1500);
    }
}