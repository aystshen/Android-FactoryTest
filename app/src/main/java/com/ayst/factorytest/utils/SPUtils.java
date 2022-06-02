package com.ayst.factorytest.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SPUtils {
    private static final String SP = "history";

    private static SPUtils instance;
    private static SharedPreferences mSp = null;

    private SPUtils(Context context) {
        mSp = context.getSharedPreferences(SP, Context.MODE_PRIVATE);
    }

    public static SPUtils get(Context context) {
        if (instance == null)
            instance = new SPUtils(context);
        return instance;
    }

    public void clear() {
        Editor editor = mSp.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Save data
     *
     * @param key preference key
     * @param value preference value
     */
    public void saveData(String key, String value) {
        Editor editor = mSp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Save data
     *
     * @param key preference key
     * @param value preference value
     */
    public void saveData(String key, boolean value) {
        Editor editor = mSp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Save data
     *
     * @param key preference key
     * @param value preference value
     */
    public void saveData(String key, int value) {
        Editor editor = mSp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Save data
     *
     * @param key preference key
     * @param value preference value
     */
    public void saveData(String key, float value) {
        Editor editor = mSp.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * Get data
     *
     * @param key preference key
     * @param defValue default value
     * @return value
     */
    public String getData(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    /**
     * Get data
     *
     * @param key preference key
     * @param defValue default value
     * @return value
     */
    public boolean getData(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    /**
     * Get data
     *
     * @param key preference key
     * @param defValue default value
     * @return value
     */
    public int getData(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

    /**
     * Get data
     *
     * @param key preference key
     * @param defValue default value
     * @return value
     */
    public float getData(String key, float defValue) {
        return mSp.getFloat(key, defValue);
    }
}
