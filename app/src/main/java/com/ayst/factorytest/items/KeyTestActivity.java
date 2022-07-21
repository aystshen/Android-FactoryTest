package com.ayst.factorytest.items;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.ayst.factorytest.R;
import com.ayst.factorytest.adapter.KeyItemAdapter;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.KeyItem;
import com.ayst.factorytest.model.TestItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.xui.utils.WidgetUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import butterknife.BindView;

/**
 * param:
 * "[{'name':'BACK', 'code': 4, 'state': 0},
 * {'name':'MENU', 'code': 82, 'state': 0},
 * {'name':'MUTE', 'code': 164, 'state': 0},
 * {'name':'VOL+', 'code': 24, 'state': 0},
 * {'name':'VOL-', 'code': 25, 'state': 0}]"
 */
public class KeyTestActivity extends ChildTestActivity {
    private static final String TAG = "KeyTestActivity";

    private static final String PARAM_DEFAULT = "[{'name':'BACK', 'code': 4, 'state': 0}, " +
            "{'name':'MENU', 'code': 82, 'state': 0}, " +
            "{'name':'MUTE', 'code': 164, 'state': 0}, " +
            "{'name':'VOL+', 'code': 24, 'state': 0}, " +
            "{'name':'VOL-', 'code': 25, 'state': 0}]";

    @BindView(R.id.rv_items)
    RecyclerView mItemsRv;

    private KeyItemAdapter mKeyItemAdapter;
    private ArrayList<KeyItem> mKeyItems;
    private Gson mGson = new Gson();
    private HashMap<Integer, Boolean> mClicked = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_key_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    public void initViews() {
        super.initViews();

        mSuccessBtn.setVisibility(View.GONE);

        if (TextUtils.isEmpty(mTestItem.getParam())) {
            mTestItem.setParam(PARAM_DEFAULT);
        }

        Log.i(TAG, "initViews, param: " + mTestItem.getParam());

        int spanCount = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 1;
        }
        WidgetUtils.initGridRecyclerView(mItemsRv, spanCount, 1, getResources().getColor(R.color.gray));
        mKeyItems = parseParam(mTestItem.getParam());
        for (KeyItem key : mKeyItems) {
            key.setState(KeyItem.STATE_UNKNOWN);
        }
        mKeyItemAdapter = new KeyItemAdapter();
        mKeyItemAdapter.setList(mKeyItems);
        mItemsRv.setAdapter(mKeyItemAdapter);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        for (int i = 0; i < mKeyItems.size(); i++) {
            if (mKeyItems.get(i).getCode() == event.getKeyCode()) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mKeyItems.get(i).setState(KeyItem.STATE_SUCCESS);
                    mKeyItemAdapter.notifyDataSetChanged();
                    mClicked.put(event.getKeyCode(), true);

                    updateResult(mGson.toJson(mKeyItems));
                    if (mClicked.size() >= mKeyItems.size()) {
                        finish(TestItem.STATE_SUCCESS);
                    }
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private ArrayList<KeyItem> parseParam(String param) {
        Type collectionType = new TypeToken<Collection<KeyItem>>() {
        }.getType();
        return mGson.fromJson(param, collectionType);
    }
}