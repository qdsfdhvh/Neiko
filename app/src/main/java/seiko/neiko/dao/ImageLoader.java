package seiko.neiko.dao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import seiko.neiko.R;
import seiko.neiko.app.App;
import seiko.neiko.dao.db.DbApi;
import seiko.neiko.models.SizeModel;
import seiko.neiko.utils.ColorUtil;
import seiko.neiko.widget.ScaleImageView;
import seiko.neiko.widget.TextDrawable;

/**
 * Created by Seiko on 2016/10/23. YiKu
 */

public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private int cut_pic;

    private static ImageLoader instance;

    public static ImageLoader getDefault() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }
    //读取设置，是否对半切
    public void loadSave() {cut_pic = DbApi.getCutPic();}

    public void display( Context context, ImageView iv, String url, String name) {
        display(context, iv, url, name, null);
    }

    public void display(Context context, ImageView iv, String url, String name, final String refererUrl) {
        //占位图(必须有)
        TextDrawable drawable = getTextDrawable(name);
        iv.setImageDrawable(drawable);
        //链接是否存在
        if (TextUtils.isEmpty(url)) return;
        //开始加载图片
        getRequest(context, url, refererUrl)
                .asBitmap()
                .dontAnimate()
                .dontTransform()
                .placeholder(drawable)
                .into(iv);

    }

    public void display(Context context, ScaleImageView iv, String url, String name) {
        display(context, iv, url, name, null);
    }

    public void display(Context context, ScaleImageView iv, String url, String name, String refererUrl) {
        //占位图(必须有)
        TextDrawable drawable = getTextDrawable(name);
        iv.setImageDrawable(drawable);
        //链接是否存在
        if (TextUtils.isEmpty(url)) return;
        //开始加载图片
        getRequest(context, url, refererUrl)
                .asBitmap()
                .dontAnimate()
                .dontTransform()
                .placeholder(drawable)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        iv.setInitSize(resource.getWidth(), resource.getHeight());
                        iv.setImageBitmap(resource);
                    }
                });
    }

    //===========================================================
    /** Section系列专用 */
    public void display9(final View v, final ScaleImageView iv1, final String url, final String refererUrl) {
        //占位图(必须有)
        TextDrawable drawable = getTextDrawable(null);
        iv1.setImageDrawable(drawable);
        //链接是否存在
        if(TextUtils.isEmpty(url)) return;
        //开始加载图片
        getRequest(v.getContext(), url, refererUrl)
                .asBitmap()
                .dontAnimate()
                .dontTransform()
                .placeholder(drawable)
                .into(new DriverViewTarget(v, iv1));
    }


    private class DriverViewTarget extends SimpleTarget<Bitmap> {

        private LinearLayout layout;
        private ScaleImageView iv;
        private int width;
        private int height;
        private float ratio;

        DriverViewTarget(View v, ScaleImageView view) {
            layout = (LinearLayout) v.findViewById(R.id.section_linear);
            this.iv = view;
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            width  = resource.getWidth();
            height = resource.getHeight();
            ratio = (float) width / height;

            if (height <= 4096)
                isOrdImg(resource, iv);
            else
                isLogImg(resource, iv);
        }

        /** 处理：普通或者需要对半切的图片 */
        private float isOrdImg(Bitmap bitmap, ScaleImageView iv1) {
//            Log.d(TAG, "width=" + width + ";height=" + height + ";ratio=" + ratio);
            switch (cut_pic) {
                case 0:
                    iv1.setInitSize(width, height);
                    iv1.setImageBitmap(bitmap);
                    break;
                case 1:
                case 2:
                    if (width < 1000 || ratio < 1.15 || ratio >1.66 || width > 2000) {
                        iv1.setInitSize(width, height);
                        iv1.setImageBitmap(bitmap);
                    } else {
                        Bitmap H1 = Bitmap.createBitmap(bitmap, 0, 0, width/2, height);        //左边
                        Bitmap H2 = Bitmap.createBitmap(bitmap, width/2, 0, width/2, height);  //右边
                        ScaleImageView iv2 = addImageView(true);

                        iv1.setInitSize(width/2, height);
                        iv2.setInitSize(width/2, height);

                        switch (cut_pic) {
                            case 1:
                                iv1.setImageBitmap(H2);
                                iv2.setImageBitmap(H1);
                                break;
                            case 2:
                                iv1.setImageBitmap(H1);
                                iv2.setImageBitmap(H2);
                                break;
                        }
                    }
                    break;
            }
            return ratio;
        }

        /** 处理：长度大于4096的图片 */
        private float isLogImg(Bitmap bitmap, ScaleImageView iv1) {
            Bitmap V1 = Bitmap.createBitmap(bitmap, 0 ,0, width, 4096);
            iv1.setImageBitmap(V1);

            int a = height / 4096;
//        Log.d(TAG, "图片切割个数=" + a);
            for (int i=1;i<a+1;i++) {
                Bitmap Vqie2;
                if (i == a) {
                    Vqie2 = Bitmap.createBitmap(bitmap, 0, 4096 * i, width, height - 4096 * i);
                } else {
                    Vqie2 = Bitmap.createBitmap(bitmap, 0, 4096 * i, width, 4096);
                }

                ScaleImageView iv2 = addImageView(false);
                iv2.setImageBitmap(Vqie2);

            }
            iv1.requestLayout();
            return ratio;
        }

        private ScaleImageView addImageView(boolean isTwo) {
            ScaleImageView iv = new ScaleImageView (layout.getContext());
            iv.setScaleType(ImageView.ScaleType.FIT_START);
            iv.setAdjustViewBounds(true);

            if (isTwo) {
                //根据回收策略 如果已经添加过iv则删除（暂时）
                if (layout.getChildCount() >= 2) {  //不超过4096的图片才运行
                    layout.removeView(layout.getChildAt(1));
                }
            }

            layout.addView(iv); //添加到layout
            return iv;
        }
    }


    //===============================================================
    /** 生成请求 */
    private DrawableTypeRequest getRequest(Context context, String url, String refererUrl) {
        //本地
        if (url.indexOf("/Neiko/") > 0) {
            return Glide.with(context).load(url);
        }

        //网络
        LazyHeaders.Builder builder = new LazyHeaders.Builder();
        if (refererUrl != null) {
            builder.addHeader("Referer", refererUrl);
        }

        return Glide.with(context).load(new GlideUrl(url, builder.build()));
    }


    //===============================================================
    /** 生成占位图 */
    private static TextDrawable getTextDrawable(final String name) {
        if (name != null) {
            return TextDrawable.builder()
                    .buildRect(getFirstCharacter(name), ColorUtil.MATERIAL.getColor(name));
        }

        return TextDrawable.builder()
                .beginConfig()
                .fontSize(65)
                .bold()
                .textColor(Color.WHITE)
                .endConfig()
                .buildRect("正在加载", Color.parseColor("#424242"));
    }

    @Nullable
    private static String getFirstCharacter(String sentence) {
        for (int i = 0; i < sentence.length(); i++) {
            String s = sentence.substring(i, i+1);
            if (s.equals("[") || s.equals("]")) continue;
            if (s.equals("{") || s.equals("}")) continue;
            if (s.equals("(") || s.equals(")")) continue;
            if (s.equals(",") || s.equals(".")) continue;
            if (s.equals("<") || s.equals(">")) continue;
            if (s.equals("《") || s.equals("》")) continue;
            if (s.equals("【") || s.equals("】")) continue;
            if (s.equals("｛") || s.equals("｝")) continue;
            return s;
        }
        return null;
    }
}
