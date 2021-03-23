package com.yxkj.facexradix.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.yxdz.commonlib.util.ShellUtils;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.MyApplication;
import com.yxkj.facexradix.R;

public class RestartDialog   extends AlertDialog implements View.OnClickListener, View.OnFocusChangeListener {

    private final Context mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.restart_dialog);


        View reset_salve = findViewById(R.id.reset_salve);
        reset_salve.setOnClickListener(this);
        View restart_salve = findViewById(R.id.restart_salve);
        restart_salve.setOnClickListener(this);
        View restart_app = findViewById(R.id.restart_app);
        restart_app.setOnClickListener(this);
        View restart_device = findViewById(R.id.restart_device);
        restart_device.setOnClickListener(this);


    }

    public RestartDialog(@NonNull Context context) {
        super(context);
        mActivity = context;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reset_salve:
                 mActivity.sendBroadcast(new Intent(Constants.BROADCAST_RECEIVER_RESTART_SLAVE));
                break;
            case R.id.restart_salve:
                mActivity.sendBroadcast(new Intent(Constants.BROADCAST_RECEIVER_RESTART_READER));
                break;
            case R.id.restart_app:
                restartApplication(mActivity);
                break;
            case R.id.restart_device:
                ShellUtils.CommandResult commandResult = ShellUtils.execCmd("reboot", true);
                MyApplication.getJldManager().jldSetLcdBackLight_onoff(1);
                break;
            default:
                break;
        }
    }


    public void restartApplication(Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
}
