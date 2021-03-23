package com.yxkj.facexradix.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yxdz.commonlib.util.SPUtils;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.Constants;

import java.util.Arrays;
import java.util.List;


public class ModeSettingDialog extends Dialog implements View.OnClickListener {


    TextView tvDeviceMode;
    private LinearLayout compositelayout;
    private CheckBox face;
    private CheckBox card;
    private CheckBox password;
    private CheckBox qrcode;

    public ModeSettingDialog(Context context, TextView tvDeviceMode) {
        super(context);
        this.tvDeviceMode = tvDeviceMode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_modesetting);
        RelativeLayout cardAndFace = findViewById(R.id.cardAndFace);
        cardAndFace.setOnClickListener(this);

        RelativeLayout cardAndPassword = findViewById(R.id.cardAndPassword);
        cardAndPassword.setOnClickListener(this);

        RelativeLayout cardAndPasswordAndFace = findViewById(R.id.cardAndPasswordAndFace);
        cardAndPasswordAndFace.setOnClickListener(this);

        RelativeLayout faceAndPassword = findViewById(R.id.faceAndPassword);
        faceAndPassword.setOnClickListener(this);


        findViewById(R.id.Composite).setOnClickListener(this);
        findViewById(R.id.submit_mode).setOnClickListener(this);

        compositelayout = findViewById(R.id.compositelayout);
        face = findViewById(R.id.box1);
        card = findViewById(R.id.box2);
        password = findViewById(R.id.box3);
        qrcode = findViewById(R.id.box4);


        try {
            Integer.parseInt(SPUtils.getInstance().getString(Constants.DEVICE_OPEATOR_MODE));
            switch (Integer.parseInt(SPUtils.getInstance().getString(Constants.DEVICE_OPEATOR_MODE))) {
                case 11:
                case 12:
                case 13:
                case 14:
                    compositelayout.setVisibility(View.GONE);
                    break;
                default:
                    compositelayout.setVisibility(View.VISIBLE);
                    break;

            }
        } catch (NumberFormatException e) {
            compositelayout.setVisibility(View.VISIBLE);
        }

        checkCode();
    }

    private void checkCode() {
        String[] codes = SPUtils.getInstance().getString(Constants.DEVICE_OPEATOR_MODE).split("-");
        List<String> list = Arrays.asList(codes);
        if (list.contains("0")) {
            card.setChecked(true);
        } else {
            card.setChecked(false);
        }

        if (list.contains("1")) {
            face.setChecked(true);
        } else {
            face.setChecked(false);
        }

        if (list.contains("2")) {
            qrcode.setChecked(true);
        } else {
            qrcode.setChecked(false);
        }

        if (list.contains("3")) {
            password.setChecked(true);
        } else {
            password.setChecked(false);
        }
    }


    private String getCode() {
        String code = "";
        if (face.isChecked()) {
            code += 1;
        }
        if (card.isChecked()) {
            code += 0;
        }
        if (password.isChecked()) {
            code += 3;
        }
        if (qrcode.isChecked()) {
            code += 2;
        }
        String s2 = code.replaceAll("(.{1})", "$1-");
        if (s2.isEmpty()){
            s2 = "1";
        }
        String sop2 = s2.substring(0, s2.length() - 1);
        return sop2;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent("WAKE_UP_MODE");
        intent.putExtra("type", SPUtils.getInstance().getInt("DEVICE_WAKE_MODE", 1));
        switch (v.getId()) {
            case R.id.Composite:
                if (compositelayout.getVisibility() == View.GONE) {
                    compositelayout.setVisibility(View.VISIBLE);
                } else {
                    compositelayout.setVisibility(View.GONE);
                }
                break;
            case R.id.submit_mode:
                SPUtils.getInstance().put(Constants.DEVICE_OPEATOR_MODE, getCode());
                // log.d("ModeSettingDialog", "getCode():" + getCode());
                getContext().sendBroadcast(intent);
                this.dismiss();
                tvDeviceMode.setText("多选模式");
                break;
            case R.id.cardAndFace:
                SPUtils.getInstance().put(Constants.DEVICE_OPEATOR_MODE, "11");
                getContext().sendBroadcast(intent);
                this.dismiss();
                tvDeviceMode.setText("卡+人脸");
                break;
            case R.id.cardAndPassword:
                SPUtils.getInstance().put(Constants.DEVICE_OPEATOR_MODE, "12");
                getContext().sendBroadcast(intent);
                this.dismiss();
                tvDeviceMode.setText("卡+密码");
                break;
            case R.id.cardAndPasswordAndFace:
                SPUtils.getInstance().put(Constants.DEVICE_OPEATOR_MODE, "13");
                getContext().sendBroadcast(intent);
                this.dismiss();
                tvDeviceMode.setText("卡+人脸+密码");
                break;
            case R.id.faceAndPassword:
                SPUtils.getInstance().put(Constants.DEVICE_OPEATOR_MODE, "14");
                getContext().sendBroadcast(intent);
                this.dismiss();
                tvDeviceMode.setText("人脸+密码");
                break;
        }

    }
}
