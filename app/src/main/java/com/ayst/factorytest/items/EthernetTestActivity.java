package com.ayst.factorytest.items;

import android.os.Bundle;
import android.widget.TextView;

import com.ayst.factorytest.App;
import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;

import butterknife.BindView;

public class EthernetTestActivity extends ChildTestActivity {

    @BindView(R.id.tv_mac)
    TextView mMacTv;
    @BindView(R.id.tv_ip)
    TextView mIpTv;
    @BindView(R.id.tv_netmask)
    TextView mNetmaskTv;
    @BindView(R.id.tv_gateway)
    TextView mGatewayTv;
    @BindView(R.id.tv_dns1)
    TextView mDns1Tv;
    @BindView(R.id.tv_dns2)
    TextView mDns2Tv;
    @BindView(R.id.tv_mode)
    TextView mModeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentLayout() {
        return R.layout.content_ethernet_test;
    }

    @Override
    public int getFullscreenLayout() {
        return 0;
    }

    @Override
    public void initViews() {
        super.initViews();

        mMacTv.setText(App.getTBManager().getEthMac());
        mIpTv.setText(App.getTBManager().getIp("eth0"));
        mNetmaskTv.setText(App.getTBManager().getNetmask("eth0"));
        mGatewayTv.setText(App.getTBManager().getGateway("eth0"));
        mDns1Tv.setText(App.getTBManager().getDns1("eth0"));
        mDns2Tv.setText(App.getTBManager().getDns2("eth0"));
        mModeTv.setText(App.getTBManager().getIpAssignment("eth0"));
    }
}