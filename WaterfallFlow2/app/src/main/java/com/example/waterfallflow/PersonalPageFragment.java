package com.example.waterfallflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class PersonalPageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_page, container, false);

        // 设置工具栏
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("个人页面");
        toolbar.setNavigationOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        // 设置页面内容
        TextView contentText = view.findViewById(R.id.content_text);
        contentText.setText("这是个人页面");

        return view;
    }
}