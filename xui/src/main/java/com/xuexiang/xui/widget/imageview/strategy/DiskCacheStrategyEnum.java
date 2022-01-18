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

/**
 * 磁盘缓存策略枚举
 *
 * @author xuexiang
 * @since 2019-11-09 10:58
 */
public enum DiskCacheStrategyEnum {
    /**
     * Caches remote data with both {@link #DATA} and {@link #RESOURCE}, and local data with
     * {@link #RESOURCE} only.
     */
    ALL,
    /**
     * Saves no data to cache.
     */
    NONE,
    /**
     * Writes retrieved data directly to the disk cache before it's decoded.
     */
    DATA,
    /**
     * Writes resources to disk after they've been decoded.
     */
    RESOURCE,
    /**
     * Tries to intelligently choose a strategy
     */
    AUTOMATIC,
}
