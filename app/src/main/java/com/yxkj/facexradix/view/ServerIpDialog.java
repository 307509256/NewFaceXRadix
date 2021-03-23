package com.yxkj.facexradix.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.PopupWindowCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.yxdz.commonlib.util.LogUtils;
import com.yxdz.commonlib.util.RegexUtils;
import com.yxdz.commonlib.util.SPUtils;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.netty.util.ClientMain;
import com.yxkj.facexradix.ui.activity.MainActivity;

import java.util.regex.Pattern;

public class ServerIpDialog extends Dialog implements View.OnClickListener, View.OnFocusChangeListener {


    private final TextView tvServerIp;
    private EditText server_ip;
    private EditText server_port;
    private EditText image_port;
    private View viewById;
    private Switch isReConnect;
    private boolean isChecked;


    public ServerIpDialog(@NonNull Context context, TextView tvServerIp) {
        super(context);
        this.tvServerIp = tvServerIp;
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
        setContentView(R.layout.layout_serverdialog);

        server_ip = findViewById(R.id.server_ip);
        server_ip.setOnClickListener(this);
        server_port = findViewById(R.id.server_port);
        server_port.setOnClickListener(this);
        image_port = findViewById(R.id.image_port);
        image_port.setOnClickListener(this);
        server_ip.setInputType(InputType.TYPE_NULL);
        server_port.setInputType(InputType.TYPE_NULL);

        image_port.setInputType(InputType.TYPE_NULL);

        viewById = findViewById(R.id.networkconnect);
        viewById.setOnClickListener(this);
        findViewById(R.id.networkcancel).setOnClickListener(this);

        server_ip.setOnFocusChangeListener(this);
        server_ip.setShowSoftInputOnFocus(false);
        server_port.setOnFocusChangeListener(this);
        server_port.setShowSoftInputOnFocus(false);
        image_port.setOnFocusChangeListener(this);
        image_port.setShowSoftInputOnFocus(false);

        String serverIp = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS, Constants.DEFAULT_SERVER_ADDRESS);
        String serverPort = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS_PORT, Constants.DEFAULT_SERVER_ADDRESS_PORT);
        String imagePort = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS_IMAGE_PORT, Constants.DEFAULT_SERVER_ADDRESS_IMAGE_PORT);
        String iceserverIp = SPUtils.getInstance().getString(Constants.ICE_SERVER_ADDRESS, Constants.DEFAULT_ICE_SERVER_ADDRESS);
        String iceserverPort = SPUtils.getInstance().getString(Constants.ICE_SERVER_ADDRESS_PORT, Constants.DEFAULT_ICE_SERVER_ADDRESS_PORT);

        server_ip.setText(serverIp);
        server_port.setText(serverPort);
        image_port.setText(imagePort);


        isReConnect = findViewById(R.id.isReConnect);
        isReConnect.setChecked(SPUtils.getInstance().getBoolean("isReconnect", isChecked));
        isReConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ServerIpDialog.this.isChecked = isChecked;
            }
        });

    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.networkconnect:
                if (!TextUtils.isEmpty(server_ip.getText().toString()) && !TextUtils.isEmpty(server_port.getText().toString())) {
                    if (!RegexUtils.isIP(server_ip.getText())) {
                        Toast.makeText(getContext(), "服务器ip 不是有效的ip地址", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!isNumeric(server_port.getText().toString())) {
                        Toast.makeText(getContext(), "端口只能为数字", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!isNumeric(image_port.getText().toString())) {
                        Toast.makeText(getContext(), "端口只能为数字", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    SPUtils.getInstance().put(Constants.SERVER_ADDRESS, server_ip.getText().toString());
                    SPUtils.getInstance().put(Constants.SERVER_ADDRESS_PORT, server_port.getText().toString());
                    SPUtils.getInstance().put(Constants.SERVER_ADDRESS_IMAGE_PORT, image_port.getText().toString());
                    //                    SPUtils.getInstance().put("TCPCLIENT_COUNT",0);
                    if (ClientMain.getChannel() != null && ClientMain.getChannel().isActive()) {
                        ClientMain.getChannel().close();
                    }
                    tvServerIp.setText(server_ip.getText().toString() + ":" + server_port.getText().toString());
                    SPUtils.getInstance().put("isReconnect", isChecked);
                    this.dismiss();
                } else {
                    Toast.makeText(getContext(), "ip地址和端口不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.networkcancel:
                this.dismiss();
                break;
            default:
                PopupKeyboard popupKeyboard = new PopupKeyboard(getContext(), (EditText) v);
                popupKeyboard.getContentView().measure(0, 0);
                PopupWindowCompat.showAsDropDown(popupKeyboard, viewById, 0, 330, Gravity.BOTTOM);
                break;
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            PopupKeyboard popupKeyboard = new PopupKeyboard(getContext(), (EditText) v);
            popupKeyboard.getContentView().measure(0, 0);
            PopupWindowCompat.showAsDropDown(popupKeyboard, viewById, 0, 330, Gravity.BOTTOM);

        }
    }
}

