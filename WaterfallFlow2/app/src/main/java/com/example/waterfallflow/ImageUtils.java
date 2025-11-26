package com.example.waterfallflow;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;

public class ImageUtils {

    /**
     * 从 drawable 资源获取图片的原始宽高
     */
    public static Size getImageSize(Context context, int drawableResId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取边界信息，不加载完整图片到内存

        // 获取 drawable 资源
        Resources resources = context.getResources();
        BitmapFactory.decodeResource(resources, drawableResId, options);

        return new Size(options.outWidth, options.outHeight);
    }

    /**
     * 根据目标宽度计算等比例高度
     */
    public static int calculateProportionalHeight(int originalWidth, int originalHeight, int targetWidth) {
        if (originalWidth <= 0 || originalHeight <= 0) {
            return targetWidth; // 默认正方形
        }
        return (int) ((float) originalHeight / originalWidth * targetWidth);
    }

    /**
     * 尺寸类
     */
    public static class Size {
        public int width;
        public int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
