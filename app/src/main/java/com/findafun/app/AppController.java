package com.findafun.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import java.io.*;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.findafun.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class AppController extends android.support.multidex.MultiDexApplication  {

    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;
    com.nostra13.universalimageloader.core.ImageLoader universalImageLoader;
    private DisplayImageOptions logoDisplayOptions;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    public com.nostra13.universalimageloader.core.ImageLoader getUniversalImageLoader() {
        if (universalImageLoader == null) {
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true).showImageOnLoading(R.drawable.placeholder)
                    .showImageForEmptyUri(R.drawable.placeholder)
                    .build();
            // Initialize ImageLoader with configuration.
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                    .defaultDisplayImageOptions(options)
                    .threadPoolSize(3)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .memoryCacheSize(1500000) // 1.5 Mb
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .build();
            universalImageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
            universalImageLoader.init(config);
        }
        return universalImageLoader;
    }
    public DisplayImageOptions getLogoDisplayOptions() {
        if (logoDisplayOptions == null) {
            logoDisplayOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true).showImageOnLoading(R.drawable.placeholder_small)
                    .showImageForEmptyUri(R.drawable.placeholder_small)
                    .showImageOnFail(R.drawable.placeholder_small)
                    .build();
        }
        return logoDisplayOptions;
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}