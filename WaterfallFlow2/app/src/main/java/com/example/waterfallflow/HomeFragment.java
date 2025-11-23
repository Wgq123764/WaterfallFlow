package com.example.waterfallflow;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
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

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private WaterfallAdapter adapter;
    private List<Item> itemList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RadioGroup layoutRadioGroup;

    // 当前布局列数和分页相关
    private int currentSpanCount = 2;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isRefreshing = false;

    // 优化设置
    private static final int INITIAL_PAGES = 3;
    private static final int ITEMS_PER_PAGE = 15;
    private static final int PRELOAD_THRESHOLD = 6;
    private static final int MAX_PAGES = 8;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);
        initData();
        setupRecyclerView();
        setupLayoutSwitcher();
        setupRefresh();
        setupLoadMore();

        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        layoutRadioGroup = view.findViewById(R.id.layout_radio_group);
    }

    private void initData() {
        itemList = new ArrayList<>();
        // 初始加载多页数据
        for (int i = 1; i <= INITIAL_PAGES; i++) {
            loadHomeData(i);
        }
        currentPage = INITIAL_PAGES;
    }

    private void setupRecyclerView() {
        updateLayoutManager(currentSpanCount);

        adapter = new WaterfallAdapter(itemList);

        // 设置点击监听器
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

    private void setupLayoutSwitcher() {
        // 设置默认选中双列布局
        layoutRadioGroup.check(R.id.radio_double_column);

        layoutRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_single_column) {
                currentSpanCount = 1;
                updateLayoutManager(currentSpanCount);
                Toast.makeText(getContext(), "已切换到单列布局", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.radio_double_column) {
                currentSpanCount = 2;
                updateLayoutManager(currentSpanCount);
                Toast.makeText(getContext(), "已切换到双列布局", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.radio_three_column) {
                currentSpanCount = 3;
                updateLayoutManager(currentSpanCount);
                Toast.makeText(getContext(), "已切换到三列布局", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isRefreshing) {
                isRefreshing = true;
                new Handler().postDelayed(() -> {
                    currentPage = 1;
                    itemList.clear();
                    loadHomeData(1);
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

                if (currentPage >= MAX_PAGES) {
                    return;
                }

                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && !isRefreshing) {
                    int[] lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null);
                    int lastVisibleItem = getLastVisibleItem(lastVisibleItemPositions);
                    int totalItemCount = layoutManager.getItemCount();

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

    private void updateLayoutManager(int spanCount) {
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * 获取主页专用的图片资源（以"home_"开头的图片）
     */
    private List<Integer> getAllHomeImageDrawables() {
        List<Integer> drawableList = new ArrayList<>();

        try {
            Field[] fields = R.drawable.class.getFields();

            for (Field field : fields) {
                String fieldName = field.getName();

                // 只加载以 "home_" 开头的图片，排除系统图标
                if (fieldName.startsWith("home_") &&
                        !fieldName.startsWith("ic_launcher") &&
                        !fieldName.startsWith("ic_menu")) {

                    try {
                        int resId = field.getInt(null);

                        // 验证资源是否存在
                        try {
                            if (getResources().getResourceName(resId) != null) {
                                drawableList.add(resId);
                                Log.d("HomeDrawableLoader", "Found home image: " + fieldName);
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e("HomeDrawableLoader", "Resource not found: " + fieldName);
                        }

                    } catch (Exception e) {
                        Log.e("HomeDrawableLoader", "Error accessing field: " + fieldName, e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("HomeDrawableLoader", "Error accessing R.drawable class: " + e.getMessage());
        }

        Log.d("HomeDrawableLoader", "Total home images found: " + drawableList.size());
        return drawableList;
    }

    /**
     * 获取备用图片资源（当没有主页专用图片时使用）
     */
    private List<Integer> getAllImageDrawables() {
        List<Integer> drawableList = new ArrayList<>();

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
                    drawableList.add(resId);
                    Log.d("DrawableLoader", "Found image: " + fieldName);
                } catch (Exception e) {
                    // 忽略无法访问的字段
                }
            }
        } catch (Exception e) {
            Log.e("DrawableLoader", "Error accessing R.drawable class: " + e.getMessage());
        }

        Log.d("DrawableLoader", "Total backup images found: " + drawableList.size());
        return drawableList;
    }

    private void loadHomeData(int page) {
        Random random = new Random();

        // 首先尝试获取主页专用图片
        List<Integer> imageResources = getAllHomeImageDrawables();

        // 如果没有找到主页专用图片，使用备用图片
        if (imageResources.isEmpty()) {
            Log.w("HomeFragment", "No home images found, using backup images");
            imageResources = getAllImageDrawables();

            // 如果备用图片也没有，使用默认图标
            if (imageResources.isEmpty()) {
                imageResources.add(R.drawable.ic_launcher_foreground);
                Log.w("HomeFragment", "No backup images found, using default icon");
            }
        }

        // 主页特有的标题和描述
        String[] titles = {
                "布局展示 - 单列效果",
                "布局展示 - 双列效果",
                "布局展示 - 三列效果",
                "瀑布流演示",
                "图片浏览功能",
                "布局切换演示",
                "应用功能介绍",
                "使用指南"
        };

        String[] descriptions = {
                "展示单列布局下的图片排列效果",
                "展示双列布局下的瀑布流效果",
                "展示三列布局下的紧凑排列",
                "体验不同布局的视觉差异",
                "点击图片可查看大图详情",
                "实时切换单列、双列、三列布局",
                "了解应用的各项功能和特性",
                "学习如何使用布局切换功能"
        };

        // 使用固定的每页项目数
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int index = (page - 1) * ITEMS_PER_PAGE + i;

            // 从可用图片列表中随机选择
            int randomIndex = random.nextInt(imageResources.size());
            int imageRes = imageResources.get(randomIndex);

            String title = titles[i % titles.length] + (page > 1 ? " " + index : "");
            String description = descriptions[i % descriptions.length];
            int height = 500 + random.nextInt(400); // 随机高度

            itemList.add(new Item(imageRes, title, description, height));
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadMoreData() {
        // 检查是否达到最大页数
        if (currentPage >= MAX_PAGES) {
            Toast.makeText(getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
            isLoading = false;
            return;
        }

        // 模拟网络请求延迟
        new Handler().postDelayed(() -> {
            currentPage++;
            loadHomeData(currentPage);
            isLoading = false;
            Toast.makeText(getContext(), "加载了第" + currentPage + "页数据", Toast.LENGTH_SHORT).show();
        }, 1500);
    }
}