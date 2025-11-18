package com.example.waterfallflow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WaterfallAdapter adapter;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initRecyclerView();
    }

    private void initData() {
        itemList = new ArrayList<>();
        Random random = new Random();

        // 添加一些示例数据，使用随机高度模拟瀑布流效果
        int[] imageResources = {
                R.drawable.ic_launcher_foreground, // 使用默认图标
                // 您可以添加更多图片资源...
        };

        String[] titles = {"美丽风景", "城市风光", "自然奇观", "人文建筑", "动物世界"};
        String[] descriptions = {
                "这是一段描述文字，展示瀑布流布局的效果",
                "另一段描述，展示不同高度的卡片",
                "瀑布流布局让内容展示更加生动",
                "双列布局充分利用屏幕空间",
                "随机高度创造视觉上的变化"
        };

        // 生成20个示例项
        for (int i = 0; i < 20; i++) {
            int imageRes = imageResources[0]; // 实际项目中可以使用不同图片
            String title = titles[i % titles.length] + " " + (i + 1);
            String description = descriptions[i % descriptions.length];
            int height = 400 + random.nextInt(300); // 随机高度在400-700之间

            itemList.add(new Item(imageRes, title, description, height));
        }
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);

        // 使用StaggeredGridLayoutManager实现瀑布流
        // 参数1: 列数，参数2: 方向（VERTICAL或HORIZONTAL）
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new WaterfallAdapter(itemList);
        recyclerView.setAdapter(adapter);
    }
}