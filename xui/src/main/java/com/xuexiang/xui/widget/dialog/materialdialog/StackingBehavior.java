/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.xui.widget.dialog.materialdialog;

/**
 * @author Aidan Follestad (afollestad)
 */
public enum StackingBehavior {
    /**
     * The action buttons are always stacked vertically.
     */
    ALWAYS,
    /**
     * The action buttons are stacked vertically IF it is necessary for them to fit in the dialog.
     */
    ADAPTIVE,
    /**
     * The action buttons are never stacked, even if they should be.
     */
    NEVER
}
