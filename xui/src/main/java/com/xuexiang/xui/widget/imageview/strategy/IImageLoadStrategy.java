/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.xui.widget.imageview.strategy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

/**
 * 图片加载策略
 *
 * @author xuexiang
 * @since 2019-07-26 00:06
 */
public interface IImageLoadStrategy {

    /**
     * 加载图片【最常用】
     *
     * @param imageView 图片控件
     * @param path      图片资源的索引
     */
    void loadImage(@NonNull ImageView imageView, Object path);

    /**
     * 加载图片【最常用】
     *
     * @param imageView 图片控件
     * @param path      图片资源的索引
     * @param listener  加载监听
     */
    void loadImage(@NonNull ImageView imageView, Object path, @NonNull ILoadListener listener);

    /**
     * 加载Gif图片【最常用】
     *
     * @param imageView 图片控件
     * @param path      图片资源的索引
     */
    void loadGifImage(@NonNull ImageView imageView, Object path);

    /**
     * 加载图片【最常用】
     *
     * @param imageView 图片控件
     * @param path      图片资源的索引
     * @param listener  加载监听
     */
    void loadGifImage(@NonNull ImageView imageView, Object path, @NonNull ILoadListener listener);

    //=============================================//

    /**
     * 加载图片
     *
     * @param imageView 图片控件
     * @param path      图片资源的索引
     * @param strategy  磁盘缓存策略
     */
    void loadImage(@NonNull ImageView imageView, Object path, DiskCacheStrategyEnum strategy);


    /**
     * 加载图片
     *
     * @param imageView 图片控件
     * @param path      图片资源的索引
     * @param strategy  磁盘缓存策略
     * @param listener  加载监听
     */
    void loadImage(@NonNull ImageView imageView, Object path, DiskCacheStrategyEnum strategy, ILoadListener listener);

    /**
     * 加载Gif图片
     *
     * @param imageView 图片控件
     * @param path      图片资源的索引
     * @param strategy  磁盘缓存策略
     */
    void loadGifImage(@NonNull ImageView imageView, Object path, DiskCacheStrategyEnum strategy);

    /**
     * 加载Gif图片
     *
     * @param imageView 图片控件
     * @param path      图片资源的索引
     * @param strategy  磁盘缓存策略
     * @param listener  加载监听
     */
    void loadGifImage(@NonNull ImageView imageView, Object path, DiskCacheStrategyEnum strategy, ILoadListener listener);

    //=============================================//

    /**
     * 加载图片
     *
     * @param imageView   图片控件
     * @param path        图片资源的索引
     * @param placeholder 占位图片
     * @param strategy    磁盘缓存策略
     */
    void loadImage(@NonNull ImageView imageView, Object path, Drawable placeholder, DiskCacheStrategyEnum strategy);

    /**
     * 加载图片
     *
     * @param imageView   图片控件
     * @param path        图片资源的索引
     * @param placeholder 占位图片
     * @param strategy    磁盘缓存策略
     * @param listener    加载监听
     */
    void loadImage(@NonNull ImageView imageView, Object path, Drawable placeholder, DiskCacheStrategyEnum strategy, ILoadListener listener);

    /**
     * 加载Gif图片
     *
     * @param imageView   图片控件
     * @param path        图片资源的索引
     * @param placeholder 占位图片
     * @param strategy    磁盘缓存策略
     */
    void loadGifImage(@NonNull ImageView imageView, Object path, Drawable placeholder, DiskCacheStrategyEnum strategy);

    /**
     * 加载Gif图片
     *
     * @param imageView   图片控件
     * @param path        图片资源的索引
     * @param placeholder 占位图片
     * @param strategy    磁盘缓存策略
     * @param listener    加载监听
     */
    void loadGifImage(@NonNull ImageView imageView, Object path, Drawable placeholder, DiskCacheStrategyEnum strategy, ILoadListener listener);

    //=============================================//

    /**
     * 加载图片
     *
     * @param imageView  图片控件
     * @param path       图片资源的索引
     * @param loadOption 加载选项
     */
    void loadImage(@NonNull ImageView imageView, Object path, LoadOption loadOption);

    /**
     * 加载图片
     *
     * @param imageView  图片控件
     * @param path       图片资源的索引
     * @param loadOption 加载选项
     * @param listener   加载监听
     */
    void loadImage(@NonNull ImageView imageView, Object path, LoadOption loadOption, ILoadListener listener);

    /**
     * 加载Gif图片
     *
     * @param imageView  图片控件
     * @param path       图片资源的索引
     * @param loadOption 加载选项
     */
    void loadGifImage(@NonNull ImageView imageView, Object path, LoadOption loadOption);

    /**
     * 加载Gif图片
     *
     * @param imageView  图片控件
     * @param path       图片资源的索引
     * @param loadOption 加载选项
     * @param listener   加载监听
     */
    void loadGifImage(@NonNull ImageView imageView, Object path, LoadOption loadOption, ILoadListener listener);

    //=============================================//

    /**
     * 清除缓存【内存和磁盘缓存】
     *
     * @param context
     */
    void clearCache(Context context);

    /**
     * 清除内存缓存
     *
     * @param context
     */
    void clearMemoryCache(Context context);

    /**
     * 清除磁盘缓存
     *
     * @param context
     */
    void clearDiskCache(Context context);

}
