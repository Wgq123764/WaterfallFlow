package com.example.waterfallflow;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomePagerFragment extends Fragment {
    //    上拉刷新
    private SwipeRefreshLayout mSwipeRefresh;
    private boolean mIsRefreshing = false;
    //    RecyclerView
    private RecyclerView mRecyclerView;
    private WaterfallAdapter mAdapter;
    private List<Item> mItemList;
    private static final String ARG_COLUMN = "COLUMN";
    private int mColumn; // 瀑布流列数
    //    加载更多
    private boolean mIsLoading = false;
    private int mCurrentPage = 1;
    private static final int INITIAL_PAGES = 3;
    private static final int ITEMS_PER_PAGE = 15;
    private static final int PRELOAD_THRESHOLD = 6;
    private static final int MAX_PAGES = 8;

    public static HomePagerFragment newInstance(int column) {
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN, column);
        HomePagerFragment fragment = new HomePagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumn = getArguments().getInt(ARG_COLUMN);
        }

        initData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
//        瀑布流
        setupRecyclerView();
//        上拉刷新
        setupRefresh();
//        下划加载更多
        setupLoadMore();
    }

    private void initData() {
        mItemList = new ArrayList<>();
//        初始化，加载多页数据
        for (int i = 1; i <= INITIAL_PAGES; i++) {
            loadHomeData(i);
        }
        mCurrentPage = INITIAL_PAGES;
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.home_recycler_view);
        mSwipeRefresh = view.findViewById(R.id.home_refresh);
    }

    private void setupRecyclerView() {
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(mColumn, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mAdapter = new WaterfallAdapter(getContext(), mItemList);
        mAdapter.setOnItemClickListener((Item item) -> {
            ImageDialogFragment dialog = ImageDialogFragment.newInstance(item.getImageResId(), item.getTitle());
            dialog.show(getParentFragmentManager(), "image_dialog");
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupRefresh() {
        mSwipeRefresh.setOnRefreshListener(() -> {
            if (!mIsRefreshing) {
                mIsRefreshing = true;
                new Handler().postDelayed(() -> {
                    mCurrentPage = 1;
                    mItemList.clear();
                    loadHomeData(1);
                    mSwipeRefresh.setRefreshing(false);
                    mIsRefreshing = false;
                    Toast.makeText(getContext(), "刷新完成", Toast.LENGTH_SHORT).show();
                }, 1000);
            }
        });
    }

    private void setupLoadMore() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mCurrentPage >= MAX_PAGES) {
                    return;
                }

                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !mIsLoading && !mIsRefreshing) {
                    int[] lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null);
                    int lastVisibleItem = getLastVisibleItem(lastVisibleItemPositions);
                    int totalItemCount = layoutManager.getItemCount();

                    if (lastVisibleItem >= totalItemCount - PRELOAD_THRESHOLD) {
                        loadMoreData();
                        mIsLoading = true;
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

    private void loadMoreData() {
        // 检查是否达到最大页数
        if (mCurrentPage >= MAX_PAGES) {
            Toast.makeText(getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
            mIsLoading = false;
            return;
        }

        // 模拟网络请求延迟
        new Handler().postDelayed(() -> {
            mCurrentPage++;
            loadHomeData(mCurrentPage);
            mIsLoading = false;
            Toast.makeText(getContext(), "加载了第" + mCurrentPage + "页数据", Toast.LENGTH_SHORT).show();
        }, 1500);
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

        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int index = (page - 1) * ITEMS_PER_PAGE + i;

            // 从可用图片列表中随机选择
            int randomIndex = random.nextInt(imageResources.size());
            int imageRes = imageResources.get(randomIndex);

            Item curItem = new Item(
                    Item.TYPE_NORMAL,
                    imageRes,
                    "卡片标题 " + (index + 1),
                    "这是第" + (index + 1) + "个卡片的描述文字。"
            );

            mItemList.add(curItem);
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
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
}
