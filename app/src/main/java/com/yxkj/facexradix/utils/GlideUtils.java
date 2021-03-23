package com.yxkj.facexradix.utils;

import android.content.Context;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class GlideUtils {

    /**
     * @param context
     * @param imageView
     * @param url       仅用于圆形图片——头像显示
     */
    public static void displayCropCircle(Context context, ImageView imageView, File url) {
//        Glide.with(context).load(url).into(imageView);
        RequestOptions requestOptions = new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .transform(new GlideRoundTransformation());

        Glide.with(context).load(url).apply(requestOptions).into(imageView);

    }

    /**
     * @param context
     * @param imageView
     * @param url       默认图片
     */
    public static void displayDefault(Context context, ImageView imageView, String url) {
//        Glide.with(context).load(pathUrl).placeholder(R.drawable.person_img_head_no_data)
//                .error(R.drawable.person_img_head_no_data).into(view);
//        Glide.with(context).load(url).into(imageView);
        RequestOptions requestOptions = new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        Glide.with(context).load(url).apply(requestOptions).into(imageView);


    }

    /**
     * @param context
     * @param imageView
     */
    public static void displayDefault(Context context, ImageView imageView, byte[] data) {
//        Glide.with(context).load(pathUrl).placeholder(R.drawable.person_img_head_no_data)
//                .error(R.drawable.person_img_head_no_data).into(view);
//        Glide.with(context).load(data).into(imageView);
        RequestOptions requestOptions = new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        Glide.with(context).load(data).apply(requestOptions).into(imageView);


    }


}
