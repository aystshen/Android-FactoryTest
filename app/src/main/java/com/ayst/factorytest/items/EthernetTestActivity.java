package com.ayst.factorytest.items;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ayst.factorytest.App;
import com.ayst.factorytest.R;
import com.ayst.factorytest.base.ChildTestActivity;
import com.ayst.factorytest.model.TestItem;

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

    private static boolean ipCheck(String str) {
        if (!TextUtils.isEmpty(str)) {
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            return str.matches(regex);
        }
        return false;
    }

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

        mSuccessBtn.setVisibility(View.GONE);

        String mac = App.getTBManager().getEthMac();
        String ip = App.getTBManager().getIp("eth0");
        String netmask = App.getTBManager().getNetmask("eth0");
        String gateway = App.getTBManager().getGateway("eth0");
        String mode = App.getTBManager().getIpAssignment("eth0");
        String dns1 = App.getTBManager().getDns1("eth0");
        String dns2 = App.getTBManager().getDns2("eth0");

        mMacTv.setText(mac);
        mIpTv.setText(ip);
        mNetmaskTv.setText(netmask);
        mGatewayTv.setText(gateway);
        mDns1Tv.setText(dns1);
        mDns2Tv.setText(dns2);
        mModeTv.setText(mode);

        updateResult(String.format("{'mac':'%s', 'ip':'%s', 'netmask':'%s', 'gateway':'%s', 'dns1':'%s', 'dns2':'%s', 'mode':'%s'}",
                mac, ip, netmask, gateway, dns1, dns2, mode));

        if (!TextUtils.isEmpty(mac) && ipCheck(ip) && ipCheck(netmask) && ipCheck(gateway)) {
            finish(TestItem.STATE_SUCCESS);
        } else {
            finish(TestItem.STATE_FAILURE);
        }
    }
}