package com.ayst.factorytest.items;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.ayst.factorytest.App;
import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.utils.AppUtils;

import butterknife.BindView;

public class InfoTestActivity extends ChildTestActivity {

    @BindView(R.id.tv_model)
    TextView mModelTv;
    @BindView(R.id.tv_android_version)
    TextView mAndroidVersionTv;
    @BindView(R.id.tv_firmware_version)
    TextView mFirmwareVersionTv;
    @BindView(R.id.tv_wifi_mac)
    TextView mWifiMacTv;
    @BindView(R.id.tv_bt_mac)
    TextView mBtMacTv;
    @BindView(R.id.tv_eth_mac)
    TextView mEthMacTv;
    @BindView(R.id.tv_sn)
    TextView mSnTv;
    @BindView(R.id.tv_rom_size)
    TextView mRomSizeTv;
    @BindView(R.id.tv_ram_size)
    TextView mRamSizeTv;
    @BindView(R.id.tv_resolution)
    TextView mResolutionTv;
    @BindView(R.id.tv_imei)
    TextView mImeiTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_info_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    public void initViews() {
        super.initViews();

        mModelTv.setText(Build.MODEL);
        mAndroidVersionTv.setText(Build.VERSION.RELEASE);
        mFirmwareVersionTv.setText(App.getTBManager().getFirmwareVersion());
        mWifiMacTv.setText(AppUtils.getWifiMac(this));
        mBtMacTv.setText(AppUtils.getBtMac(this));
        mEthMacTv.setText(AppUtils.getEth0Mac(this));
        mSnTv.setText(AppUtils.getSerialNo());
        mRomSizeTv.setText(App.getTBManager().getInternalStorageSize() / 1024 / 1024 + "MB");
        mRamSizeTv.setText(App.getTBManager().getMemorySize() / 1024 / 1024 + "MB");
        mResolutionTv.setText(App.getTBManager().getScreenWidth(this)
                + "x" + App.getTBManager().getScreenHeight(this));
        mImeiTv.setText(AppUtils.getIMEI(this));

        updateResult(String.format("{'model':'%s', 'android':'%s', 'firmware':'%s', 'wifi_mac':'%s', " +
                "'bt_mac':'%s', 'eth_mac':'%s', 'sn':'%s', 'rom':'%s', 'ram':'%s', 'resolution':'%s', 'imei':'%s'}",
                mModelTv.getText(), mAndroidVersionTv.getText(), mFirmwareVersionTv.getText(),
                mWifiMacTv.getText(), mBtMacTv.getText(), mEthMacTv.getText(), mSnTv.getText(),
                mRomSizeTv.getText(), mRamSizeTv.getText(), mResolutionTv.getText(), mImeiTv.getText()));
    }
}