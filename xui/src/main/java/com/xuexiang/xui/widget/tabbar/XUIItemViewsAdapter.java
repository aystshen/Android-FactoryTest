/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xuexiang.xui.widget.tabbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.core.util.Pools;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.xui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个带 cache 功能的“列表型数据-View”的适配器，适用于自定义 {@link View} 需要显示重复单元 {@link android.widget.ListView} 的情景，
 * cache 功能主要是保证在需要多次刷新数据或布局的情况下（{@link ListView} 或 {@link RecyclerView} 的 itemView）
 * 复用已存在的 {@link View}。
 * XUI 用于 {@link TabSegment} 中 {@link TabSegment.Tab} 与数据的适配。
 *
 * @author xuexiang
 * @since 2018/12/26 下午4:18
 */
public abstract class XUIItemViewsAdapter<T, V extends View> {
    private Pools.Pool<V> mCachePool;
    private List<T> mItemData = new ArrayList<>();
    // 不能简单的用mParentView的子views，因为可能mParentView有一些装饰子view,不应该归adapter管理
    private List<V> mViews = new ArrayList<>();
    private ViewGroup mParentView;

    public XUIItemViewsAdapter(ViewGroup parentView) {
        mParentView = parentView;
    }

    public void detach(int count) {
        int childCount = mViews.size();
        while (childCount > 0 && count > 0) {
            V view = mViews.remove(childCount - 1);
            if (mCachePool == null) {
                mCachePool = new Pools.SimplePool<>(12);
            }

            // 做简单cache，如果V需要动态添加子view，则业务保证不做cache
            Object notCacheTag = view.getTag(R.id.xui_view_can_not_cache_tag);
            if (notCacheTag == null || !(boolean) notCacheTag) {
                try {
                    mCachePool.release(view);
                } catch (Exception ignored) {
                }
            }

            mParentView.removeView(view);
            childCount--;
            count--;
        }
    }

    public void clear() {
        mItemData.clear();
        detach(mViews.size());
    }

    private V getView() {
        V v = mCachePool != null ? mCachePool.acquire() : null;
        if (v == null) {
            v = createView(mParentView);
        }
        return v;
    }

    protected abstract V createView(ViewGroup parentView);

    public XUIItemViewsAdapter<T, V> addItem(T item) {
        mItemData.add(item);
        return this;
    }

    public void setup() {
        int itemCount = mItemData.size();
        int childCount = mViews.size();
        int i;

        if (childCount > itemCount) {
            detach(childCount - itemCount);
        } else if (childCount < itemCount) {
            for (i = 0; i < itemCount - childCount; i++) {
                V view = getView();
                mParentView.addView(view);
                mViews.add(view);
            }
        }

        for (i = 0; i < itemCount; i++) {
            V view = mViews.get(i);
            T item = mItemData.get(i);
            bind(item, view, i);
        }
        mParentView.invalidate();
        mParentView.requestLayout();

    }

    public T getItem(int position) {
        if (mItemData == null) {
            return null;
        }
        if (position < 0 || position >= mItemData.size()) {
            return null;
        }
        return mItemData.get(position);
    }

    public void replaceItem(int position, T data) throws IllegalAccessException {
        if (position < mItemData.size() && position >= 0) {
            mItemData.set(position, data);
        } else {
            throw new IllegalAccessException("替换数据不存在");
        }

    }

    protected abstract void bind(T item, V view, int position);

    public List<V> getViews() {
        return mViews;
    }

    public int getSize() {
        if (mItemData == null) {
            return 0;
        }
        return mItemData.size();
    }
}
