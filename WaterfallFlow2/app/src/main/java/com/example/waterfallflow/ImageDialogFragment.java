package com.example.waterfallflow;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ImageDialogFragment extends DialogFragment {

    private ImageView mImage;
    private ImageButton mCloseButton;
    private TextView mTitle;

    private static final String ARG_IMAGE_RES_ID = "IMAGE_ID";
    private static final String ARG_TITLE = "TITLE";

    public static ImageDialogFragment newInstance(int imageResId, String title) {
        ImageDialogFragment fragment = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RES_ID, imageResId);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_viewer);

        // 设置全屏
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_image_viewer, container, false);

        mImage = view.findViewById(R.id.fullscreen_image_view);
        mCloseButton = view.findViewById(R.id.close_button);
        mTitle = view.findViewById(R.id.image_title);

        Bundle args = getArguments();
        if (args != null) {
            int imageResId = args.getInt(ARG_IMAGE_RES_ID, 0);
            String title = args.getString(ARG_TITLE, "");
            mImage.setImageResource(imageResId);
            mTitle.setText(title);
        }

        // 点击图片关闭
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 点击关闭按钮
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
