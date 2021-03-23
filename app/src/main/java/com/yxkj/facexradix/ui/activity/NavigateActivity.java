package com.yxkj.facexradix.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.face.main.activity.FaceAuthActicity;
import com.baidu.idl.face.main.listener.SdkInitListener;
import com.baidu.idl.face.main.manager.FaceSDKManager;
import com.baidu.idl.face.main.utils.ConfigUtils;
import com.baidu.idl.face.main.utils.ToastUtils;
import com.yxdz.commonlib.base.BaseActivity;
import com.yxdz.commonlib.util.SPUtils;
import com.yxdz.commonlib.util.ShellUtils;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.MyApplication;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.room.FacexDatabase;
import com.yxkj.facexradix.room.bean.Time;
import com.yxkj.facexradix.tts.SpeechX;

import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class NavigateActivity extends BaseActivity {


    private TextView tvTip;
    private ImageView ivIcon;
    private int flag = 0;
    private long currentTime;
    private Handler handler;
    private Timer timer;
    private Timer rebootTimer;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_navigate;
    }

    @Override
    public void onModel() {
        tvTip = findViewById(R.id.tvTip);
        ivIcon = findViewById(R.id.ivIcon);
        long license_time = SPUtils.getInstance().getLong("License_time", 0);
        if (license_time != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(license_time));
            MyApplication.SetTime(calendar.getTimeInMillis());
        } else {
            Toast.makeText(this, "请重设系统时间", Toast.LENGTH_SHORT).show();
            Time time = new Time();
            time.setSun1Fr("00:00"); time.setSun1To("23:59");
            time.setMon1Fr("00:00"); time.setMon1To("23:59");
            time.setTue1Fr("00:00"); time.setTue1To("23:59");
            time.setWed1Fr("00:00"); time.setWed1To("23:59");
            time.setThu1Fr("00:00"); time.setThu1To("23:59");
            time.setSat1Fr("00:00"); time.setSat1To("23:59");
            FacexDatabase.getInstance(this).getTimeDao().insert(time);
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                getNetTime();
            }
        }).start();


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getNetTime();
            }
        }, 0, 3600000);


        rebootTimer = new Timer();
        rebootTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ShellUtils.CommandResult commandResult = ShellUtils.execCmd("reboot", true);
                MyApplication.getJldManager().jldSetLcdBackLight_onoff(1);
            }
        }, 300000);
    }

    private void getNetTime() {
        URL url = null;//取得资源对象
        try {
            String serverIp = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS, null);
            String serverPort = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS_IMAGE_PORT, null);
            if (serverIp == null || serverPort == null) {
                return;
            }
            //            url = new URL("http://www.baidu.com");
            url = new URL("http://" + serverIp + ":" + serverPort + "/surpass");
            //            url = new URL("http://www.bjtime.cn");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            long ld = uc.getDate(); //取得网站日期时间
            if (ld == 0) {
                return;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ld);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SPUtils.getInstance().put("License_time", calendar.getTimeInMillis());
                    MyApplication.SetTime(calendar.getTimeInMillis());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public   static  void main(String arg,String[] args[]){

    }


    @Override
    protected void onDestroy() {
        if (rebootTimer != null) {
            rebootTimer.cancel();
            rebootTimer.purge();
            rebootTimer = null;
        }
        super.onDestroy();
    }

    @Override
    public void onData(Bundle savedInstanceState) {
        tvTip.setText("正在初始化数据。。。");
        ivIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(MainActivity.class);
                finish();
                return false;
            }
        });

        handler = new Handler();
        onSpeakInit();
        boolean isConfigExit = ConfigUtils.isConfigExit();
        Boolean isInitConfig = ConfigUtils.initConfig();
        if (isInitConfig && isConfigExit) {
            Toast.makeText(this, "初始配置加载成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "初始配置失败,将重置文件内容为默认配置", Toast.LENGTH_SHORT).show();
            ConfigUtils.modityJson();
        }
        onFaceInit();
    }

   private void onSpeakInit() {
        tvTip.setText("正在初始化语音引擎。。。");
        SpeechX.getInstance();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SpeechX.getInstance().bOpen) {
                    checkGo();
                } else {
                    tvTip.setText(SpeechX.getInstance().errorMessage);
                }
            }
        }, 2500);
    }

    public void onFaceInit() {
        tvTip.setText("初始化人脸识别引擎。。。");

        FaceSDKManager.getInstance().init(this, new SdkInitListener() {
            @Override
            public void initStart() {
                // log.d("NavigateActivity", "开始认证");
//                timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                    }
//                }, 2000);
            }

            @Override
            public void initLicenseSuccess() {
                SPUtils.getInstance().put("LICENSE_IS_ACTIVATION", true);
                // log.d("NavigateActivity", "认证成功");
                checkGo();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getNetTime();
                    }
                }).start();

            }

            @Override
            public void initLicenseFail(int errorCode, String msg) {
                // 如果授权失败，跳转授权页面
                SPUtils.getInstance().put("LICENSE_IS_ACTIVATION", false);
                ToastUtils.toast(NavigateActivity.this, errorCode + msg);
                startActivityForResult(new Intent(NavigateActivity.this, FaceAuthActicity.class), 123);
            }

            @Override
            public void initModelSuccess() {
                SPUtils.getInstance().put("LICENSE_IS_ACTIVATION", true);
                // log.d("NavigateActivity", "模块初始化成功");
            }

            @Override
            public void initModelFail(int errorCode, String msg) {
                // log.d("NavigateActivity", "模块初始化失败");
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        checkGo();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkGo() {
        flag++;
        if (flag == 2) {
            startActivity(MainActivity.class);
            finish();
        }
    }


    @Override
    protected boolean setFullScreen() {
        return true;
    }
}