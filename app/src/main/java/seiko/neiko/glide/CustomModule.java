package seiko.neiko.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.target.ViewTarget;

import java.io.File;
import java.io.InputStream;

import seiko.neiko.R;

import static seiko.neiko.dao.mPath.cachePath;

/**
 * Created by Seiko on 2016/11/18. YiKu
 */

public class CustomModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        ViewTarget.setTagId(R.id.glide_tag_id);

        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
        builder.setDiskCache(() -> {
            // Careful: the external cache directory doesn't enforce permissions
            File cacheLocation = new File(cachePath, "/img");

            boolean isCreate = cacheLocation.exists();
            while (!isCreate) {
                isCreate = cacheLocation.mkdirs();
            }

            //104857600 == 100M
            return DiskLruCacheWrapper.get(cacheLocation, 100 * 1024 * 1024);
        });
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }
}
