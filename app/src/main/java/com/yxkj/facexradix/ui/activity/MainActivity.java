package com.yxkj.facexradix.ui.activity;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Gpio;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.idl.face.main.camera.CameraPreviewManager;
import com.google.gson.Gson;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;
import com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity;
import com.tencent.liteav.demo.trtc.debug.GenerateTestUserSig;
import com.tencent.trtc.TRTCCloudDef;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.proxy.impl.DefaultUpdateChecker;
import com.yxdz.commonlib.base.BaseActivity;
import com.yxdz.commonlib.util.LogUtils;
import com.yxdz.commonlib.util.NoDoubleClick;
import com.yxdz.commonlib.util.SPUtils;
import com.yxdz.commonlib.util.ShellUtils;
import com.yxdz.commonlib.util.ToastUtils;
import com.yxdz.commonlib.view.LoadingDialog;
import com.yxdz.serialport.listener.SerialPortListener;
import com.yxdz.serialport.service.SerialPortService;
import com.yxkj.facexradix.BeepManager;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.MyApplication;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.netty.controller.DeviceControllerService;
import com.yxkj.facexradix.netty.controller.UserControllerService;
import com.yxkj.facexradix.netty.util.ClientMain;
import com.yxkj.facexradix.netty.util.ClientUtil;
import com.yxkj.facexradix.receive.NetworkStatuReceiver;
import com.yxkj.facexradix.receive.UsbCameraReceiver;
import com.yxkj.facexradix.room.FacexDatabase;
import com.yxkj.facexradix.room.bean.IosToken;
import com.yxkj.facexradix.room.dao.IosTokenDao;
import com.yxkj.facexradix.rtc.TRTCActivity;
import com.yxkj.facexradix.star.MessageBean;
import com.yxkj.facexradix.star.database.CoreDB;
import com.yxkj.facexradix.star.database.HistoryBean;
import com.yxkj.facexradix.star.demo.MLOC;
import com.yxkj.facexradix.star.utils.AEvent;
import com.yxkj.facexradix.star.utils.IEventListener;
import com.yxkj.facexradix.tts.SpeechX;
import com.yxkj.facexradix.ui.fragment.CallFragment;
import com.yxkj.facexradix.ui.fragment.HelperFragment;
import com.yxkj.facexradix.ui.fragment.HomeFragment;
import com.yxkj.facexradix.update.CProgressDialogUtils;
import com.yxkj.facexradix.update.CustomUpdateParser;
import com.yxkj.facexradix.update.CustomUpdatePrompter;
import com.yxkj.facexradix.utils.BrightnessTools;
import com.yxkj.facexradix.utils.FragmentUtil;
import com.yxkj.facexradix.utils.Helper;
import com.yxkj.facexradix.view.SystemHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @PackageName: com.yxdz.fadox.ui.activity
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/1/29 10:18
 */
public class MainActivity extends BaseActivity implements SerialPortListener, Camera.PreviewCallback, IEventListener {


    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
    public static long operateTime;
    public static String versionReader = "";
    public static boolean isLoading = false;
    private static ArrayList<String> snList;
    private static ScheduledExecutorService screenControlService;
    public SerialPortService.SerialPortBinder serialPortBinder;
    public LinearLayout blackScreen;
    public Runnable screenPowerTask = new Runnable() {
        @Override
        public void run() {
            LogUtils.d(TAG, "screenPowerTask:" + (System.currentTimeMillis() - operateTime));
            if (blackScreen.getVisibility() == View.VISIBLE)
                return;
            if ((System.currentTimeMillis() - operateTime) > 30000) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (SPUtils.getInstance().getInt("ISONCALL", 0) == 1) {
                            return;
                        }
                        List<Fragment> fragments = FragmentUtil.getFragments(getSupportFragmentManager());
                        LogUtils.d(TAG, "fragments:" + fragments.size());
                        int size = fragments.size();
                        if (size > 1) {
                            LogUtils.d(TAG, "回到主页");
                            for (int i = size - 1; i > 0; i--) {
                                FragmentUtil.remove(fragments.get(i));
                            }
                            FragmentUtil.show(fragments.get(0));
                        } else {
                            if (blackScreen.getVisibility() == View.GONE) {
                                blackScreen.clearAnimation();
                                blackScreen.setVisibility(View.VISIBLE);
                                Calendar instance = Calendar.getInstance();
                                instance.setTime(new Date());
                                SPUtils.getInstance().put("License_time", instance.getTimeInMillis());
                                BrightnessTools.setBrightness(MainActivity.this, 0);
                                Gpio.Set_Led(0);
                            }
                        }
                    }
                });
            }
        }
    };

    //升级提示
    BroadcastReceiver updateTips = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra("type", 1) == 1) {
                ToastUtils.showLongToast("更新失败");
            } else if (intent.getIntExtra("type", 1) == 2) {
                ToastUtils.showLongToast("更新失败,版本号太低");
            } else {
                String url = intent.getStringExtra("url");
                startActivity(new Intent(MainActivity.this, UpdataActivity.class).putExtra("url", url));
            }
        }
    };
    //复位从机
    BroadcastReceiver resetSlaveRevciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            outputresetSlave();
        }
    };
    //重置从机
    BroadcastReceiver resetReaderRevciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            // 设置参数
            builder.setTitle("是否重置从机")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            outputResetReader();
                        }
                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog,
                                    int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    };
    //服务端连接状态
    BroadcastReceiver serverstatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status", 0);
            if (status == 1)
                Toast.makeText(context, "服务端连接成功", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "服务端连接失败", Toast.LENGTH_SHORT).show();
        }
    };
    //开门
    BroadcastReceiver openDoor = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            outputCardNo(Constants.DEFAULT_CARD);
        }
    };
    int wbStatus = 1;
    //自继电器模式远程开门
    BroadcastReceiver openDoor_VIA_TRTC = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SPUtils.getInstance().getInt(Constants.DEVICE_CTRL_MODE, 0) == 1) {
                outputCardNo("75BCD15");
                ToastUtils.showShortToast("正在开门中");
            } else {
                sendBroadcast(new Intent(Constants.BROADCAST_RECEIVER_OPEN_DOOR));
            }
        }
    };
    private SystemHeadView systemHeadView;
    //切换图标显示是否连接上surpass
    BroadcastReceiver tcpConnection = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            systemHeadView.toggleTcp(intent.getBooleanExtra("type", false));
        }
    };
    private NetworkStatuReceiver networkStatuReceiver;
    private UsbBroadcast usbBroadcast;
    private OpenDoorReceiver openDoorReceiver;
    private UsbCameraReceiver usbCameraReceiver;
    private HomeFragment homeFragment;
    BroadcastReceiver startBlackActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isLoading) {
//                stopReconnectTimer();
                isLoading = true;
                Log.e("MainActivity", "starttoLoadFace");
                Intent intent1 = new Intent(context, LoadFaceActivity.class);
                intent1.putExtra("data", intent.getStringExtra("data"));
                intent1.putExtra("msgid", intent.getIntExtra("msgid", 1));
                intent1.putExtra("cmd", intent.getIntExtra("cmd", 1));
                stopSceenControlService();
                homeFragment.stopFace();
                startActivityForResult(intent1, 123);
            }
            Gpio.Set_Led(0);
        }
    };
    BroadcastReceiver openScreen = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            homeFragment.checkMode();
            BrightnessTools.setBrightness(MainActivity.this, 500);
            blackScreen.setVisibility(View.GONE);
            operateTime = System.currentTimeMillis();
        }
    };
    private RestartRecever restartRecever;
    private Intent deviceServiceIntent;
    private Intent userServiceIntent;
    private Timer reconnect;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serialPortBinder = (SerialPortService.SerialPortBinder) service;
            serialPortBinder.setSerialPortListener(MainActivity.this);
            serialPortBinder.resetSlave();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    serialPortBinder.getVersion();
                }
            }, 2000);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private BeepManager beepManager;
    private Bitmap oldBitmap;
    private Camera mCamera;
    BroadcastReceiver changeWakeModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", 0);
            if (type == 1) {
                String mode = SPUtils.getInstance().getString(Constants.DEVICE_OPEATOR_MODE, "1");
                switch (mode) {
                    case "11":
                    case "12":
                    case "13":
                        releaseCamera();
                        initCamera(1, 0);
                        break;
                    case "14":
                        releaseCamera();
                        break;
                    default:
                        String[] codes = mode.split("-");
                        List<String> list = Arrays.asList(codes);
                        if (list.contains("1")) {
                            releaseCamera();
                        } else {
                            releaseCamera();
                            initCamera(1, 0);
                        }
                        break;
                }
            } else {
                releaseCamera();
                initCamera(1, 0);
            }
        }
    };
    private byte[] rawImage;
    private Bitmap bitmap;
    private int frameCount = 0;
    private int lastSimilarity = 0;

    public static void setSnList(ArrayList<String> snList) {
        MainActivity.snList = snList;
    }

    public static void resetOperateTime() {
        operateTime = System.currentTimeMillis();
        LogUtils.d("MainActivity", "resetoperateTime");
    }

    public static void stopSceenControlService() {
        if (screenControlService != null) {
            screenControlService.shutdown();
            screenControlService = null;
        }
    }

    public static Camera getCameraInstance(int i) {
        Camera c = null;
        try {
            c = Camera.open(i); // 试图获取Camera实例
        } catch (Exception e) {
            // 摄像头不可用（正被占用或不存在）
        }
        return c; // 不可用则返回null
    }

    public static int similarity(Bitmap b, Bitmap viewBt) {
        //把图片转换为Bitmap
        int t = 0;
        int f = 0;
        Bitmap bm_one = b;
        Bitmap bm_two = viewBt;
        //保存图片所有像素个数的数组，图片宽×高
        int[] pixels_one = new int[bm_one.getWidth() * bm_one.getHeight()];
        int[] pixels_two = new int[bm_two.getWidth() * bm_two.getHeight()];
        //获取每个像素的RGB值
        bm_one.getPixels(pixels_one, 0, bm_one.getWidth(), 0, 0, bm_one.getWidth(), bm_one.getHeight());
        bm_two.getPixels(pixels_two, 0, bm_two.getWidth(), 0, 0, bm_two.getWidth(), bm_two.getHeight());
        //如果图片一个像素大于图片2的像素，就用像素少的作为循环条件。避免报错
        if (pixels_one.length >= pixels_two.length) {
            //对每一个像素的RGB值进行比较
            for (int i = 0; i < pixels_two.length; i++) {
                int clr_one = pixels_one[i];
                int clr_two = pixels_two[i];
                //RGB值一样就加一（以便算百分比）
                if (clr_one == clr_two) {
                    t++;
                } else {
                    f++;
                }
            }
        } else {
            for (int i = 0; i < pixels_one.length; i++) {
                int clr_one = pixels_one[i];
                int clr_two = pixels_two[i];
                if (clr_one == clr_two) {
                    t++;
                } else {
                    f++;
                }
            }
        }
        return myPercent(t, t + f);
    }

    /**
     * 百分比的计算
     */
    public static int myPercent(int y, int z) {
        double baiy = y * 1.0;
        double baiz = z * 1.0;
        double fen = (baiy / baiz) * 1000;
        return (Double.valueOf(fen)).intValue();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void onModel() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        SPUtils.getInstance().put("License_time", instance.getTimeInMillis());
        systemHeadView = findViewById(R.id.shvTitle);
    }

    @Override
    public void onData(Bundle savedInstanceState) {
        blackScreen = findViewById(R.id.blackScreen);
        SpeechX.getInstance();
        systemHeadView.start();
        homeFragment = new HomeFragment();
        FragmentUtil.add(getSupportFragmentManager(), homeFragment, R.id.flContainer);
        systemHeadView.setHelpListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperFragment helperFragment = new HelperFragment();
                helperFragment.setBackFragment(homeFragment);
                FragmentUtil.hide(homeFragment);
                FragmentUtil.add(getSupportFragmentManager(), helperFragment, R.id.flContainer);
            }
        });

        systemHeadView.setHomeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NoDoubleClick.isFastDoubleClick(800)) {
                    CallFragment callFragment = new CallFragment();
                    callFragment.setBackFragment(homeFragment);
                    FragmentUtil.hide(homeFragment);
                    FragmentUtil.add(getSupportFragmentManager(), callFragment, R.id.flContainer);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(systemHeadView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        systemHeadView.setUpgradeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpgrade();
            }
        });

        //网络状态
        initNetWorkReciver();
        //可视对讲服务
        initTrtcOpenDoorReciver();
        //串口服务
        initSerialPort();

        initOpenReceiver();
        initUsbCameraReceiver();
        initUsbUpate();
        initNettyService();
        initScreenControlService();
        initServerStatus();
        initResetSlave();
        initResetReader();
        initStartLoadFace();
        registerReceiver(changeWakeModeReceiver, new IntentFilter("WAKE_UP_MODE"));

        updateFaild();
        initTcpConnectionReceiver();
        initRebootTimer(3, 0);


        //每小时重置读卡器
//        initSlaveRebootTimer();

        //开门接收
        initOpenDoorCtrlReceiver();
        //重启接收
        restartRecever = new RestartRecever();
        registerReceiver(restartRecever, new IntentFilter(Constants.BROADCAST_RECEIVER_RESTART));
        //唤醒接收
        registerReceiver(openScreen, new IntentFilter("OPENSCREEN"));
        beepManager = new BeepManager(this);

        //初始化 唤醒模式
        Intent intent = new Intent("WAKE_UP_MODE");
        intent.putExtra("type", SPUtils.getInstance().getInt("DEVICE_WAKE_MODE", 1));
        sendBroadcast(intent);

        //自动重连
        starReconnectTimer();

        // 连接Tcp服务器
        intTcpServer();
    }

    // 连接Tcp服务器
    private void intTcpServer() {
        // log.d("MainActivity", "服务器连接");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // log.d("MainActivity", "服务器连接");
                ClientMain.doConnect();
            }
        });
        thread.start();
    }

    //主动检测更新
    private void checkUpgrade() {
        String serverIp = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS);
        String serverPort = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS_IMAGE_PORT);
        if (!serverIp.isEmpty() && !serverPort.isEmpty()) {
            XUpdate.newBuild(this)
                    .isWifiOnly(false)
                    .updateUrl("http://" + serverIp + ":" + serverPort + "/access/sys/faceVersion")
//                .isAutoMode(false) //如果需要完全无人干预，自动更新，需要root权限【静默安装需要】
//                .updateChecker(new CustomUpdateChecker()) // 设置自定义的版本更新检查器
                    .updateChecker(new DefaultUpdateChecker() {
                        @Override
                        public void onBeforeCheck() {
                            super.onBeforeCheck();
                            CProgressDialogUtils.showProgressDialog(MainActivity.this, "查询中...");
                        }

                        @Override
                        public void onAfterCheck() {
                            super.onAfterCheck();
                            CProgressDialogUtils.cancelProgressDialog(MainActivity.this);
                        }
                    })
                    .updateParser(new CustomUpdateParser()) //设置自定义的版本更新解析器
                    .updatePrompter(new CustomUpdatePrompter(this))
                    .promptTopResId(R.mipmap.ic_radix)
                    .isGet(false).apkCacheDir("/sdcard/")
                    .update();
        }

    }


    //开门
    private void initOpenDoorCtrlReceiver() {
        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_RECEIVER_OPEN_DOOR_CTRL);
        registerReceiver(openDoor, intentFilter);
    }

    //服务器连接状态
    private void initTcpConnectionReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_RECEIVER_TCP_CONNECTION);
        registerReceiver(tcpConnection, filter);
    }

    //触发导入人脸页面
    private void initStartLoadFace() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("TCP_INTENT_USER_FACE_ORPERATION");
        registerReceiver(startBlackActivity, filter);
    }

    //服务器连接状态
    private void initServerStatus() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_RECEIVER_SERVER_STATUS);
        registerReceiver(serverstatus, filter);
    }


    //人脸导入返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        initScreenControlService();
        blackScreen.setVisibility(View.GONE);
        if (resultCode != 4321) {
            homeFragment.checkMode();
        } else {
            if (data != null && data.getStringExtra("type").equals("timeout")) {
                ToastUtils.showShortToast("对方无应答");
            }
        }
        if (data != null && data.getIntExtra("type", 0) == 1) {
            ToastUtils.showShortToast("图片url路径错误");
        }
        BrightnessTools.setBrightness(this, 500);
        LogUtils.d(TAG, "回到主页");
        super.onActivityResult(requestCode, resultCode, data);
    }

    //主动重连模式
    public void starReconnectTimer() {
        reconnect = new Timer();
        reconnect.schedule(new TimerTask() {
            @Override
            public void run() {
                if (SPUtils.getInstance().getBoolean("isReconnect", false)) {
                    if (ClientMain.getChannel() != null) {
                        ClientMain.getChannel().close();
                    }
                }
            }
        }, 0, 60000);
    }


    //重置从机
    private void initResetSlave() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_RECEIVER_RESTART_SLAVE);
        registerReceiver(resetSlaveRevciver, filter);
    }

    //复位从机
    private void initResetReader() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_RECEIVER_RESTART_READER);
        registerReceiver(resetReaderRevciver, filter);
    }

    //升级失败提示
    private void updateFaild() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("UPDATETIPS");
        registerReceiver(updateTips, filter);
    }

    //初始化nettyTcp服务
    private void initNettyService() {
        deviceServiceIntent = new Intent(MainActivity.this, DeviceControllerService.class);
        startService(deviceServiceIntent);
        userServiceIntent = new Intent(MainActivity.this, UserControllerService.class);
        startService(userServiceIntent);
    }

    private void initUsbUpate() {
        usbBroadcast = new UsbBroadcast();
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        usbFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        usbFilter.addDataScheme("file");
        registerReceiver(usbBroadcast, usbFilter);
    }

    @Override
    protected boolean setFullScreen() {
        return true;
    }

    private void initUsbCameraReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        usbCameraReceiver = new UsbCameraReceiver();
        this.registerReceiver(usbCameraReceiver, filter);
    }

    public void setHomeView(boolean flag) {
        systemHeadView.setHomeView(flag);
    }

    public void setHelpView(boolean flag) {
        systemHeadView.setHelpView(flag);
    }

    public void setUpgradeView(boolean flag) {
        systemHeadView.setUpgradeView(flag);
    }

    private void initOpenReceiver() {
        openDoorReceiver = new OpenDoorReceiver();
        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_RECEIVER_OPEN_DOOR);
        registerReceiver(openDoorReceiver, intentFilter);
    }

    public void outputCardNo(String cardNo) {
        if (serialPortBinder != null) {
            if (!TextUtils.isEmpty(cardNo)) {
                serialPortBinder.outCard((byte) 0x2A, cardNo);
            }
        }
    }

    public void outBeer() {
        beepManager.playBeepSound();
    }

    public void outputHoldWarm(boolean isHold) {
        if (serialPortBinder != null) {
            serialPortBinder.outHoldWarm(isHold);
        }
    }

    public void outputresetSlave() {
        if (serialPortBinder != null) {
            serialPortBinder.resetSlave();
        }
    }

    public void outputResetReader() {
        if (serialPortBinder != null) {
            serialPortBinder.resetReader();
        }
    }

    public void outputGetVerion() {
        if (serialPortBinder != null) {
            serialPortBinder.getVersion();
        }
    }

    public void outputPassword(byte password) {
        if (serialPortBinder != null) {
            serialPortBinder.outPassword((byte) 0x04, password);
        }
    }

    public void outputPassword(byte type, String password) {
        if (serialPortBinder != null) {
            serialPortBinder.outPassword((byte) 0x04, type, password);
        }
    }

    @Override
    public void onPassword(String doorNumber, String key) {

    }

    @Override
    public void onAlarm(String doorNumber, String statu) {

    }

    @Override
    public void onInvalidCard() {
        SpeechX.getInstance().speak("无效输入");
    }

    @Override
    public void onDoorMagnet(String doorNumber, String statu) {

    }

    @Override
    public void onError(Exception e) {

    }

    //网络状态
    private void initNetWorkReciver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkStatuReceiver = new NetworkStatuReceiver();
        registerReceiver(networkStatuReceiver, filter);
    }

    //可视对讲开门
    private void initTrtcOpenDoorReciver() {
        registerReceiver(openDoor_VIA_TRTC, new IntentFilter("OPENDOOR_VIA_TRTC"));
    }

    /**
     * 初始化串口服务
     */
    private void initSerialPort() {
        Intent intentSerialPort = new Intent(this, SerialPortService.class);
        bindService(intentSerialPort, connection, Service.BIND_AUTO_CREATE);
    }

    @Override
    public void onCardNo(String doorNumber, String cardNo) {
        //输出卡号
        // log.d("MainActivity", cardNo);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (homeFragment != null) {
                    beepManager.playBeepSound();
                    homeFragment.haveCardNo(cardNo);
                }
            }
        });
    }

    //获取从机版本
    @Override
    public void onVersion(String verison) {
        versionReader = verison;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //点击唤醒屏幕
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            LogUtils.d(TAG, "事件分发");
            operateTime = System.currentTimeMillis();
            if (blackScreen.getVisibility() == View.VISIBLE) {
                blackScreen.setVisibility(View.GONE);
                BrightnessTools.setBrightness(this, 500);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkStatuReceiver);
        unregisterReceiver(startBlackActivity);
        unregisterReceiver(resetSlaveRevciver);
        unregisterReceiver(usbBroadcast);
        unregisterReceiver(openDoorReceiver);
        unregisterReceiver(usbCameraReceiver);
        unregisterReceiver(restartRecever);
        unregisterReceiver(updateTips);
        unregisterReceiver(tcpConnection);
        unregisterReceiver(openDoor);
        unregisterReceiver(resetReaderRevciver);
        unregisterReceiver(serverstatus);

        stopService(deviceServiceIntent);
        stopService(userServiceIntent);
        unbindService(connection);
        Gpio.Set_Led(0);
        releaseCamera();
        CameraPreviewManager.getInstance().stopPreview();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    public LoadingDialog showDialog(Context context) {
        LoadingDialog dialog = new LoadingDialog(context, "正在更新(请勿断电)...");
        dialog.show();
        return dialog;
    }

    //熄屏/唤醒控制服务
    public void initScreenControlService() {
        int time = SPUtils.getInstance().getInt(Constants.SLEEP_TIME, 60);
        screenControlService = Executors.newScheduledThreadPool(1);
        screenControlService.scheduleWithFixedDelay(screenPowerTask, time, time, TimeUnit.SECONDS);
    }


    //初始化每天重启
    public void initRebootTimer(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();


        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Date date = calendar.getTime(); //第一次执行定时任务的时间
        // log.d("MainActivity", "date:" + date);

        //如果第一次执行定时任务的时间 小于 当前的时间
        //此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。循环执行的周期则以当前时间为准
        if (date.before(new Date())) {
            date = this.addDay(date, 1);
            // log.d("MainActivity", "date:" + date);
        }

        Timer timer = new Timer();


        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // log.d("MainActivity", "rebooting");
                outputresetSlave();
                outputResetReader();
                ShellUtils.CommandResult commandResult = ShellUtils.execCmd("reboot", true);
                MyApplication.getJldManager().jldSetLcdBackLight_onoff(1);
            }
        }, date, PERIOD_DAY);

    }


    //初始化每小时重置读卡器
    public void initSlaveRebootTimer() {
        Timer timer = new Timer();
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // log.d("MainActivity", "rebooting");
                outputresetSlave();
                outputResetReader();
            }
        }, 0, 3600000);

    }

    public Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }

    // 双摄像头唤醒
    public void initCamera(int i, int count) {
        if (count > 20) {
            Toast.makeText(this, "开启双摄像头唤醒失败", Toast.LENGTH_SHORT).show();
            return;
        }
        mCamera = getCameraInstance(i);
        if (mCamera != null) {
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } else {
            releaseCamera();
            count += 1;
            initCamera(i, count);
        }
    }


    //双摄像头唤醒
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        frameCount += 1;
        if (frameCount % 15 == 0) {
            Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            YuvImage yuvimage = new YuvImage(
                    data,
                    ImageFormat.NV21,
                    previewSize.width,
                    previewSize.height,
                    null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 80, baos);// 80--JPG图片的质量[0-100],100最高
            rawImage = baos.toByteArray();
            //将rawImage转换成bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
            if (oldBitmap == null) {
                oldBitmap = bitmap;
            } else {
                int similarity = similarity(oldBitmap, bitmap);
                // log.d("MainActivity", "similarity:" + similarity);

                int similarity1 = getSimilarity(lastSimilarity, similarity);
                // log.d("MainActivity", "similarity1:" + similarity1);
                if (similarity1 > 30) {
                    if (blackScreen.getVisibility() == View.VISIBLE) {
                        homeFragment.checkMode();
                        BrightnessTools.setBrightness(this, 500);
                        blackScreen.setVisibility(View.GONE);
                        operateTime = System.currentTimeMillis();
                    } else {
                        operateTime = System.currentTimeMillis();
                    }

                    oldBitmap = bitmap;
                    lastSimilarity = similarity;
                } else {
                    oldBitmap = bitmap;
                    lastSimilarity = similarity;
                }
            }
        }
    }

    public int getSimilarity(int old, int news) {
        if (old > news) {
            return old - news;
        } else {
            return news - old;
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onResume() {
        addListener();
        super.onResume();
    }

    @Override
    protected void onStop() {
        removeListener();
        super.onStop();
    }

    //   starRtc======================================================================================
    private void addListener() {
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CALLING, this);
        AEvent.addListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING, this);
        AEvent.addListener(AEvent.AEVENT_C2C_REV_MSG, this);
        AEvent.addListener(AEvent.AEVENT_REV_SYSTEM_MSG, this);
        AEvent.addListener(AEvent.AEVENT_GROUP_REV_MSG, this);
        AEvent.addListener(AEvent.AEVENT_USER_ONLINE, this);
        AEvent.addListener(AEvent.AEVENT_USER_OFFLINE, this);
        AEvent.addListener(AEvent.AEVENT_CONN_DEATH, this);
    }

    private void removeListener() {
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CALLING, this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING, this);
        AEvent.removeListener(AEvent.AEVENT_C2C_REV_MSG, this);
        AEvent.removeListener(AEvent.AEVENT_REV_SYSTEM_MSG, this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_REV_MSG, this);
        AEvent.removeListener(AEvent.AEVENT_USER_ONLINE, this);
        AEvent.removeListener(AEvent.AEVENT_USER_OFFLINE, this);
        AEvent.removeListener(AEvent.AEVENT_CONN_DEATH, this);
    }

    //starrtc通信部分
    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        switch (aEventID) {
            case AEvent.AEVENT_VOIP_REV_CALLING:
                break;
            case AEvent.AEVENT_VOIP_P2P_REV_CALLING:
                break;
            case AEvent.AEVENT_C2C_REV_MSG:
            case AEvent.AEVENT_REV_SYSTEM_MSG:
                MLOC.hasNewC2CMsg = true;
                try {
                    XHIMMessage revMsg = (XHIMMessage) eventObj;
                    JSONObject alertData = new JSONObject();
                    alertData.put("listType", 0);
                    alertData.put("farId", revMsg.fromId);
                    alertData.put("msg", "收到一条新消息");
                    MLOC.showDialog(this, alertData);
                    List<HistoryBean> mHistoryList = new ArrayList<>();
                    List<HistoryBean> list = MLOC.getHistoryList(CoreDB.HISTORY_TYPE_C2C);
                    if (list != null && list.size() > 0) {
                        mHistoryList.addAll(list);
                    }
                    for (HistoryBean historyBean : mHistoryList) {
                        if (historyBean.getConversationId().equals(alertData.getString("farId"))) {
                            checkJson(historyBean.getLastMsg(), historyBean.getConversationId());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_GROUP_REV_MSG:
                MLOC.hasNewGroupMsg = true;
                try {
                    XHIMMessage revMsg = (XHIMMessage) eventObj;
                    JSONObject alertData = new JSONObject();
                    alertData.put("listType", 1);
                    alertData.put("farId", revMsg.targetId);
                    alertData.put("msg", "收到一条群消息");
                    MLOC.showDialog(this, alertData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_USER_OFFLINE:
                MLOC.showMsg(this, "服务已断开");
                // log.d("starRtc", "连接中...");
                break;
            case AEvent.AEVENT_USER_ONLINE:
                if (XHClient.getInstance().getIsOnline()) {
                    sendBroadcast(new Intent("TCP_CONNECTION_SUSSESS"));
                } else {
                    sendBroadcast(new Intent("TCP_CONNECTION_FAIL"));
                }
                break;
            case AEvent.AEVENT_CONN_DEATH:
                MLOC.showMsg(this, "服务已断开");
                // log.d("starRtc", "连接异常，请重新登录");
                break;
        }
    }

    //ios设备可视对讲的控制
    private void checkJson(String msg, String sn) {
        // log.d("starRtc", "dispatchEvent: " + msg);
        Gson gson = new Gson();
        try {
//            msg = SecretUtils.decrypt3DES(msg);
            MessageBean messageBean = gson.fromJson(msg, MessageBean.class);
            switch (messageBean.getType()) {
                case "join":
                    break;
                case "close":
                    sendBroadcast(new Intent("CLOSE_TRTCACTIVITY"));
                    break;
                case "open":
                    sendBroadcast(new Intent("OPENDOOR_VIA_TRTC"));
                    break;
                case "token":
                    IosTokenDao iosTokenDao = FacexDatabase.getInstance(this).getIosTokenDao();
                    String token = String.valueOf(messageBean.getData());
                    IosToken iosToken = iosTokenDao.listTokenBySn(sn);
                    if (iosToken != null) {
                        iosTokenDao.delete(iosToken);
                    }
                    IosToken newIosToken = new IosToken();
                    newIosToken.setSn(sn);
                    newIosToken.setTokne(token);
                    iosTokenDao.insert(newIosToken);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //自继电器 远程开门
    private class OpenDoorReceiver extends BroadcastReceiver {

        private boolean isCloseLong;
        private boolean isOpenLong;
        private Timer timer;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (SPUtils.getInstance().getInt(Constants.DEVICE_CTRL_MODE, 0) == 1) {
                outputCardNo(Constants.DEFAULT_CARD);
            } else {
                int type = intent.getIntExtra("type", 0);
                int time = SPUtils.getInstance().getInt("Lock_time", 5000);
                switch (type) {
                    case 0:
                        if (!isCloseLong && !isOpenLong) {
                            Gpio.RelayOnOff(1);
                            if (timer != null) {
                                timer.cancel();
                                timer.purge();
                                timer = null;
                            }
                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Gpio.RelayOnOff(0);
                                    timer = null;
                                }
                            }, time);
                        }
                        break;
                    case 1:
                        isCloseLong = false;
                        isOpenLong = true;
                        Gpio.RelayOnOff(1);
                        break;
                    case 2:
                        isOpenLong = false;
                        isCloseLong = true;
                        Gpio.RelayOnOff(0);
                        break;
                    case 3:
                        isOpenLong = false;
                        isCloseLong = false;
                        Gpio.RelayOnOff(0);
                        break;
                }
            }
        }
    }

    //usb更新
    public class UsbBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case Intent.ACTION_MEDIA_MOUNTED: {
                    try {
                        LoadingDialog dialog = showDialog(context);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Helper.restartAPP();
                                ShellUtils.CommandResult commandResult = ShellUtils.execCmd("pm install -r /mnt/usb_storage/*/*/update/*.apk", true);
                                if (commandResult.successMsg.equals("Success")) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(context, "安装成功", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    });
//                            dialog.dismiss();
                                } else {
                                    runOnUiThread(() -> {
                                        Toast.makeText(context, "安装失败", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    });
//                            dialog.dismiss();
                                }
                            }
                        }).start();

                    } catch (Exception e) {
                        Toast.makeText(context, "安装失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;
                }
                case Intent.ACTION_MEDIA_UNMOUNTED: {
                    Toast.makeText(context, "存储设备已拔出", Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }

    }

    //远程重启
    class RestartRecever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LoadingDialog dialog = new LoadingDialog(context, "正在重启设备(请勿断电)...");
            dialog.show();
        }
    }


}





