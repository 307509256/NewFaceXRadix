package com.yxkj.facexradix.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.PopupWindowCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.yxdz.commonlib.util.LogUtils;
import com.yxkj.facexradix.MyApplication;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.ui.activity.MainActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yxkj.facexradix.MyApplication.getAppContext;


public class XDiaLog extends Dialog implements View.OnClickListener, View.OnFocusChangeListener {


    private RadioGroup type;
    private EditText ip;
    private EditText gateway;
    private EditText netmask;
    private EditText dns;
    private Button cancel;
    private Button connect;
    private LinearLayout networksettingview;
    TextView tvDeviceIp;

    public XDiaLog(@NonNull Context context, TextView tvDeviceIp) {
        super(context);
        this.tvDeviceIp = tvDeviceIp;
    }


    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_UP) {
            LogUtils.d("touce", "事件分发");
            MainActivity.resetOperateTime();

        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_xdialog);
        type = findViewById(R.id.networktype);
        ip = findViewById(R.id.networkip);
        ip.setOnClickListener(this);
        gateway = findViewById(R.id.networkgateway);
        gateway.setOnClickListener(this);
        netmask = findViewById(R.id.networknetmask);
        netmask.setOnClickListener(this);
        dns = findViewById(R.id.networkdns);
        dns.setOnClickListener(this);
        cancel = findViewById(R.id.networkcancel);
        connect = findViewById(R.id.networkconnect);
        networksettingview = findViewById(R.id.networksettingview);
        ip.clearFocus();
        gateway.clearFocus();
        netmask.clearFocus();
        dns.clearFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getAppContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(ip.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        ip.setOnFocusChangeListener(this);
        ip.setShowSoftInputOnFocus(false);
        gateway.setOnFocusChangeListener(this);
        gateway.setShowSoftInputOnFocus(false);
        netmask.setOnFocusChangeListener(this);
        netmask.setShowSoftInputOnFocus(false);
        dns.setOnFocusChangeListener(this);
        dns.setShowSoftInputOnFocus(false);


        EthernetManager ethernet = (EthernetManager) getAppContext().getSystemService(Context.ETHERNET_SERVICE);
        if (ethernet.getConfiguration().getIpAssignment() == IpConfiguration.IpAssignment.DHCP) {
            type.check(R.id.type1);
            networksettingview.setVisibility(View.GONE);
        } else {
            type.check(R.id.type2);
        }


        type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.type1:
                        networksettingview.setVisibility(View.GONE);
                        break;
                    case R.id.type2:
                        networksettingview.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });


        cancel.setOnClickListener(this);
        connect.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.networkcancel:
                this.dismiss();
                break;
            case R.id.networkconnect:
                if (type.getCheckedRadioButtonId() == R.id.type1) {
                    MyApplication.SetDHCP();
                    this.dismiss();
                }
                if (type.getCheckedRadioButtonId() == R.id.type2) {
                    if (isIP(ip.getText().toString()) && isIP(netmask.getText().toString()) && isIP(gateway.getText().toString()) && isIP(dns.getText().toString())) {
                        MyApplication.getJldManager().jldSetEthStaticIPAddress(
                                ip.getText().toString(),
                                netmask.getText().toString(),
                                gateway.getText().toString(),
                                dns.getText().toString());
                        this.dismiss();

                    } else {
                        Toast.makeText(getContext(), "请输入正确的地址", Toast.LENGTH_SHORT).show();

                    }
                }
                break;
            default:
                PopupKeyboard popupKeyboard = new PopupKeyboard(getContext(), (EditText) v);
                popupKeyboard.getContentView().measure(0, 0);
                PopupWindowCompat.showAsDropDown(popupKeyboard, connect, 0, 210, Gravity.BOTTOM);
                break;
        }
    }


    public boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean ipAddress = mat.find();

        return ipAddress;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            PopupKeyboard popupKeyboard = new PopupKeyboard(getContext(), (EditText) v);
            popupKeyboard.getContentView().measure(0, 0);
            PopupWindowCompat.showAsDropDown(popupKeyboard, connect, 0, 265, Gravity.BOTTOM);
        }
    }
}
