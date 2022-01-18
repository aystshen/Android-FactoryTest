package com.xuexiang.xui.widget.shadow;

import android.content.Context;
import androidx.core.view.ViewCompat;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * 可设置阴影的按钮
 *
 * @author XUE
 * @since 2019/3/30 16:15
 */
public class ShadowButton extends AppCompatButton {

    public ShadowButton(Context context) {
        super(context);
        init(context, null);
    }

    public ShadowButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ShadowButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ShadowDrawable drawable = ShadowDrawable.fromAttributeSet(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(this, drawable);
    }
}
