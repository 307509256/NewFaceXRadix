package com.yxkj.facexradix.netty.controller;

import static com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity.KEY_AUDIO_HANDFREEMODE;
import static com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity.KEY_AUDIO_VOLUMETYOE;
import static com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity.KEY_RECEIVED_AUDIO;
import static com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity.KEY_RECEIVED_VIDEO;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baidu.idl.face.main.FaceManager;
import com.baidu.idl.face.main.api.FaceApi;
import com.baidu.idl.main.facesdk.statistic.DeviceInfoUtil;
import com.google.gson.Gson;
import com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity;
import com.tencent.liteav.demo.trtc.debug.GenerateTestUserSig;
import com.tencent.trtc.TRTCCloudDef;
import com.yxdz.commonlib.util.DeviceUtil;
import com.yxdz.commonlib.util.FileIOUtil;
import com.yxdz.commonlib.util.LogUtils;
import com.yxdz.commonlib.util.SPUtils;
import com.yxdz.commonlib.util.ToastUtils;
import com.yxkj.facexradix.Config;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.MyApplication;
import com.yxkj.facexradix.netty.Message;
import com.yxkj.facexradix.netty.util.ClientMain;
import com.yxkj.facexradix.netty.util.ClientUtil;
import com.yxkj.facexradix.room.FacexDatabase;
import com.yxkj.facexradix.room.bean.Time;
import com.yxkj.facexradix.room.dao.TimeDao;
import com.yxkj.facexradix.rtc.TRTCActivity;
import com.yxkj.facexradix.ui.activity.MainActivity;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import io.netty.channel.Channel;


public class DeviceControllerService extends Service {


    private PasswordReceiver passwordReceiver;
    private SettingsReceiver settingsReceiver;
    private TimeReceiver timeReceiver;
    private NetworkReceiver networkReceiver;
    private RestartReceiver restartReceiver;
    private ResetReceiver resetReceiver;
    private OpendoorReceiver opendoorReceiver;
    private UpdateReceiver updateReceiver;
    private IssuedReceiver issuedReceiver;
    private DeleteReceiver deleteReceiver;
    private HolidayIssuedReceiver holidayIssuedReceiver;
    private HolidayDeleteReceiver holidayDeleteReceiver;
    private StatusReceiver statusReceiver;
    private DeviceVerificationErrorReceiver deviceVerificationErrorReceiver;
    private Channel channel;
    private String TAG = "DeviceControllerService";
    private NotificationManager mNotificationManager;
    private RemoteViews remoteViews;

    public static String getStorageInfo(Context context, int type) {
        String path = getStoragePath(context, type);
        String storageInfo;
        if (isSDCardMount() && path != null && !path.toString().equals("")) {
            File file = new File(path);
            StatFs statFs = new StatFs(file.getPath());
            long blockCount = statFs.getBlockCountLong();
            long blockSize = statFs.getBlockSizeLong();
            long totalSpace = blockCount * blockSize;
            long aviableBlocks = statFs.getAvailableBlocksLong();
            long aviableSpace = aviableBlocks * blockSize;
            storageInfo = Formatter.formatFileSize(context, (totalSpace - aviableSpace)) + "/" + Formatter.formatFileSize(context, totalSpace);
        } else {
            storageInfo = "无外置SD卡";
        }

        return storageInfo;
    }

    public static boolean isSDCardMount() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static String getStoragePath(Context context, int type) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        try {
            Method getPathMethod = storageManager.getClass().getMethod("getVolumePaths", (Class[]) null);
            String[] path = (String[]) ((String[]) getPathMethod.invoke(storageManager, (Object[]) null));
            switch (type) {
                case 0:
                    return path[type];
                case 1:
                    if (path.length > 1) {
                        return path[type];
                    }
                    return null;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        initReceiver();
        registerReceivers();
    }

    private void sendSusses(Intent intent) {
        channel = ClientMain.getChannel();
        if (channel != null) {
            Message message = new Message();
            message.setMsgId(intent.getIntExtra("msgid", 1));
            message.setCode(intent.getIntExtra("cmd", 1));
            HashMap<String, Object> map = new HashMap<>();
            map.put("code", 0);
            map.put("msg", "成功");
            message.setData(map);
            LogUtils.d(TAG, "发送回复：" + message.toString());
            channel.writeAndFlush(message);
        }
    }

    private void initReceiver() {
        passwordReceiver = new PasswordReceiver();
        settingsReceiver = new SettingsReceiver();
        timeReceiver = new TimeReceiver();
        networkReceiver = new NetworkReceiver();
        restartReceiver = new RestartReceiver();
        resetReceiver = new ResetReceiver();
        opendoorReceiver = new OpendoorReceiver();
        updateReceiver = new UpdateReceiver();
        issuedReceiver = new IssuedReceiver();
        deleteReceiver = new DeleteReceiver();
        holidayIssuedReceiver = new HolidayIssuedReceiver();
        holidayDeleteReceiver = new HolidayDeleteReceiver();
        statusReceiver = new StatusReceiver();
        deviceVerificationErrorReceiver = new DeviceVerificationErrorReceiver();
    }

    private void registerReceivers() {
        registerReceiver(passwordReceiver, new IntentFilter("TCP_INTENT_DEVICE_PASSWORD"));
        registerReceiver(settingsReceiver, new IntentFilter("TCP_INTENT_DEVICE_SETTINGS"));
        registerReceiver(timeReceiver, new IntentFilter("TCP_INTENT_DEVICE_TIME"));
        registerReceiver(networkReceiver, new IntentFilter("TCP_INTENT_DEVICE_NETWORK"));
        registerReceiver(restartReceiver, new IntentFilter("TCP_INTENT_DEVICE_RESTART"));
        registerReceiver(resetReceiver, new IntentFilter("TCP_INTENT_DEVICE_RESET"));
        registerReceiver(opendoorReceiver, new IntentFilter("TCP_INTENT_DEVICE_OPENDOOR"));
//        registerReceiver(recordReceiver, new IntentFilter("TCP_INTENT_DEVICE_RECORD"));
        registerReceiver(updateReceiver, new IntentFilter("TCP_INTENT_DEVICE_UPDATE"));
        registerReceiver(issuedReceiver, new IntentFilter("TCP_INTENT_DEVICE_PERIOD_ISSUED"));
        registerReceiver(deleteReceiver, new IntentFilter("TCP_INTENT_DEVICE_PERIOD_DELETE"));
        registerReceiver(holidayIssuedReceiver, new IntentFilter("TCP_INTENT_DEVICE_PERIOD_HOLIDAY_ISSUED"));
        registerReceiver(holidayDeleteReceiver, new IntentFilter("TCP_INTENT_DEVICE_PERIOD_HOLIDAY_DELETE"));
        registerReceiver(statusReceiver, new IntentFilter("TCP_INTENT_DEVICE_STATUS"));
        registerReceiver(deviceVerificationErrorReceiver, new IntentFilter("TCP_DEVICE_VERIFICATION_ERROR"));
        registerReceiver(OnCall,new IntentFilter("TCP_RTC_FOR_MINI_PROGRAM"));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(passwordReceiver);
        unregisterReceiver(settingsReceiver);
        unregisterReceiver(timeReceiver);
        unregisterReceiver(networkReceiver);
        unregisterReceiver(restartReceiver);
        unregisterReceiver(resetReceiver);
        unregisterReceiver(opendoorReceiver);
//        unregisterReceiver(recordReceiver);
        unregisterReceiver(updateReceiver);
        unregisterReceiver(issuedReceiver);
        unregisterReceiver(deleteReceiver);
        unregisterReceiver(holidayIssuedReceiver);
        unregisterReceiver(holidayDeleteReceiver);
        unregisterReceiver(statusReceiver);
        unregisterReceiver(deviceVerificationErrorReceiver);
        unregisterReceiver(OnCall);
        super.onDestroy();
    }

    private class PasswordReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject data = JSON.parseObject(intent.getStringExtra("data"));
            String oldPass = data.getString("oldPass");
            String newPass = data.getString("newPass");
            String adminPasswrod = SPUtils.getInstance().getString(Constants.DEVICE_ACCESS_PASSWORD, Constants.DEFAULT_DEVICE_ACCESS_PASSWORD);
            // log.d("PasswordReceiver", "setPassword");
            // log.d("PasswordReceiver", "oldPass:" + oldPass);
            // log.d("PasswordReceiver", "newPass:" + newPass);

            if (adminPasswrod.equals(oldPass)) {
                try {
                    SPUtils.getInstance().put(Constants.DEVICE_ACCESS_PASSWORD, newPass);
                    sendSusses(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SettingsReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            Config configBean = null;
            Gson gson = new Gson();
            if (intent.getStringExtra("data") != null) {
                configBean = gson.fromJson(intent.getStringExtra("data"), Config.class);

                if (configBean.getVolume() != 0) {
                    AudioManager audioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, configBean.getVolume(), 0);
                }
                if (configBean.getTtsMod() == 1 || configBean.getTtsMod() == 0) {
                    SPUtils.getInstance().put(Constants.TTS_MODE, configBean.getTtsMod());
                }
                if (configBean.getSleepTime() != 0) {
                    SPUtils.getInstance().put(Constants.SLEEP_TIME, configBean.getSleepTime());
                }
                if (configBean.getDoorDelayTimeForClose() > 0 && configBean.getDoorDelayTimeForClose() < 100) {
                    SPUtils.getInstance().put(Constants.DOOR_DALAY_TIME_FOR_CLOSE, configBean.getTtsMod());
                }
                SPUtils.getInstance().put("Lock_time", configBean.getDoorDelayTimeForClose() * 1000);
                SPUtils.getInstance().put(Constants.DEVICE_CTRL_MODE, configBean.getMode());
                SPUtils.getInstance().put(Constants.DEVICE_OUTPUT_MODE, configBean.getOutMode());
                SPUtils.getInstance().put(Constants.DEVICE_OPEATOR_MODE, configBean.getOpenMode());

                SPUtils.getInstance().put(Constants.DEVICE_HOLD_PASSWORD, configBean.getHoldPassword());
                SPUtils.getInstance().put(Constants.DEVICE_COMMON_PASSWORD, configBean.getCommonPassword());
                SPUtils.getInstance().put(Constants.DEVICE_SCENE_TYPE, configBean.getSceneType());
                SPUtils.getInstance().put(Constants.DEVICE_CARD_DECODE, configBean.getCardFormat());
                sendBroadcast(new Intent(Constants.BROADCAST_RECEIVER_CHANGEVIEW));
                Intent wakeIntent = new Intent("WAKE_UP_MODE");
                wakeIntent.putExtra("type", SPUtils.getInstance().getInt("DEVICE_WAKE_MODE", 1));
                sendBroadcast(wakeIntent);
            }
            sendSusses(intent);
        }
    }

    private class TimeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String timestamp = intent.getStringExtra("data");
                    Log.d("TimeReceiver", "setTime");
                    Log.d("TimeReceiver", "timestamp:" + timestamp);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(Long.parseLong(timestamp)));
                    SPUtils.getInstance().put("License_time", calendar.getTimeInMillis());
                    MyApplication.SetTime(calendar.getTimeInMillis());
                    sendSusses(intent);
                }
            }).start();
        }
    }

    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject data = JSON.parseObject(intent.getStringExtra("data"));
            Integer mode = data.getInteger("mode");
            String ip = data.getString("ip");
            String subnetMask = data.getString("subnetMask");
            String DNS = data.getString("DNS");
            String gateway = data.getString("gateway");
            // log.d("NetworkReceiver", "setNetInfo");
            // log.d("NetworkReceiver", "ip:" + ip);
            // log.d("NetworkReceiver", "mode:" + mode);
            // log.d("NetworkReceiver", "subnetMask:" + subnetMask);
            // log.d("NetworkReceiver", "gateway:" + gateway);
            // log.d("NetworkReceiver", "DNS:" + DNS);
            org.json.JSONObject jsonObject = new org.json.JSONObject();
            if (mode == 1) {
                MyApplication.SetDHCP();
            } else if (mode == 2) {
                if (!subnetMask.equals("") && !DNS.equals("") && !gateway.equals("") && !ip.equals("")) {
                    MyApplication.getJldManager().jldSetEthStaticIPAddress(ip, subnetMask, gateway, DNS);
                }
            }
            sendSusses(intent);
        }
    }

    private class RestartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MyApplication.getAppContext().sendBroadcast(new Intent(Constants.BROADCAST_RECEIVER_RESTART));
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            /**
                             *要执行的操作
                             */
                            MyApplication.getJldManager().execSuCmd("reboot");
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 5000);
                    sendSusses(intent);
                }
            }).start();

        }
    }

    private class ResetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int data = Integer.valueOf(intent.getStringExtra("data"));
            FaceApi.getInstance().deleteAll();
            FacexDatabase.getInstance(MyApplication.getAppContext()).getRecord().deleteAll();
            FaceManager faceManager = FaceManager.getFaceManager();
            faceManager.clearAllAsync();
            FileIOUtil.deleteFaceDirectory(FileIOUtil.getFaceDir());
            if (data == 0) {
                AudioManager audioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);
                SPUtils.getInstance().put(Constants.TTS_MODE, 1);
                SPUtils.getInstance().put(Constants.SLEEP_TIME, 30);
                SPUtils.getInstance().put(Constants.DOOR_DALAY_TIME_FOR_CLOSE, 30);
                SPUtils.getInstance().put(Constants.DEVICE_OUTPUT_MODE, 1);
                SPUtils.getInstance().put(Constants.DEVICE_CTRL_MODE, 0);
                SPUtils.getInstance().put(Constants.DEVICE_OPEATOR_MODE, "1");
                SPUtils.getInstance().put(Constants.DEVICE_HOLD_PASSWORD, "123456");
                SPUtils.getInstance().put(Constants.DEVICE_COMMON_PASSWORD, "123456");
                SPUtils.getInstance().put(Constants.DEVICE_SCENE_TYPE, "0");
                SPUtils.getInstance().put("Lock_time", 5000);
            }
            sendSusses(intent);
        }
    }

    private class OpendoorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int data = Integer.valueOf(intent.getStringExtra("data"));
            // log.d("OpendoorReceiver", "openDoorControl");
            Intent open = new Intent(Constants.BROADCAST_RECEIVER_OPEN_DOOR);
            if (SPUtils.getInstance().getInt(Constants.DEVICE_CTRL_MODE, 0) != 1) {
                open.putExtra("type", data);
                MyApplication.getAppContext().sendBroadcast(open);
            }
            sendSusses(intent);

        }
    }

    private class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject data = JSON.parseObject(intent.getStringExtra("data"));
            PackageManager manager = context.getPackageManager();
            PackageInfo info = null;
            try {
                info = manager.getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (info.versionCode <= data.getInteger("versionCode")) {
                String serverIp = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS, Constants.DEFAULT_SERVER_ADDRESS);
                String serverPort = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS_IMAGE_PORT, Constants.DEFAULT_SERVER_ADDRESS_IMAGE_PORT);
                context.sendBroadcast(new Intent("UPDATETIPS").putExtra("type", 0).putExtra("url", "http://" + serverIp + ":" + serverPort + data.getString("url")));
            } else {
                context.sendBroadcast(new Intent("UPDATETIPS").putExtra("type", 2));
            }
            sendSusses(intent);
        }
    }

    private class IssuedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Time data = JSON.parseObject(intent.getStringExtra("data"), Time.class);
            TimeDao timeDao = FacexDatabase.getInstance(context).getTimeDao();
            timeDao.deleteAll();
            timeDao.insert(data);
            sendSusses(intent);
        }
    }

    private class DeleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String string = intent.getStringExtra("data");
            String[] split = string.split(",");
            TimeDao timeDao = FacexDatabase.getInstance(context).getTimeDao();
            for (String i : split) {
                Time time = new Time();
                time.setTzIndex(Integer.valueOf(i));
                timeDao.delete(time);
            }
            sendSusses(intent);
        }
    }

    private class HolidayIssuedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONArray data = JSON.parseArray(intent.getStringExtra("data"));
            for (int i = 0; i < data.size(); i++) {
                SPUtils.getInstance().put("holiday" + i, data.getJSONObject(i).getString("holidayTime"));
                if (i == data.size()) {
                    SPUtils.getInstance().put("holidaySize", i);
                }
            }
            sendSusses(intent);
        }
    }

    private class HolidayDeleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String string = intent.getStringExtra("data");
            String[] split = string.split(",");
            for (String i : split) {
                SPUtils.getInstance().remove("holiday" + i);
            }
            SPUtils.getInstance().put("holidaySize", SPUtils.getInstance().getInt("holidaySize") - split.length);
            sendSusses(intent);
        }
    }

    private class DeviceVerificationErrorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ToastUtils.showLongToast("设备验证失败！");
        }
    }

    private class StatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = null;
            try {
                info = manager.getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("code", 0);
            dataMap.put("msg", "成功");


            HashMap<String, Object> objmap = new HashMap<>();
            objmap.put("deviceType", 1);
            objmap.put("deviceSn", DeviceUtil.getLocalMacAddress(context));
            objmap.put("userStorage", getStorageInfo(context, 0));
            objmap.put("hardwareBoardType", DeviceInfoUtil.getDeviceBoard());
            objmap.put("softwareVersion", info.versionName);
            objmap.put("systemApiVersion", Build.VERSION.SDK_INT + "");

            dataMap.put("obj", objmap);
            Message message = new Message();
            message.setMsgId(intent.getIntExtra("msgid", 1));
            message.setCode(intent.getIntExtra("cmd", 114));
            message.setData(dataMap);
            ClientUtil.requestMessage(message);
        }
    }


    private void saveUserInfo(String roomId, String userId) {
        try {
            SharedPreferences shareInfo = getSharedPreferences("per_data", 0);
            SharedPreferences.Editor editor = shareInfo.edit();
            editor.putString("userId", userId);
            editor.putString("roomId", roomId);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String mVideoFile = "";
    public BroadcastReceiver OnCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent dataIntent) {
            sendSusses(dataIntent);
            com.alibaba.fastjson.JSONObject data = JSON.parseObject(dataIntent.getStringExtra("data"));
            String roomNo = data.getString("roomNo");
            Log.d(TAG, "callroomNo: "+ roomNo);
            final Intent intent = new Intent(context, TRTCActivity.class);
            int sdkAppId = GenerateTestUserSig.SDKAPPID;
            String userSig = GenerateTestUserSig.genTestUserSig(ClientUtil.Sender);
            intent.putExtra(TRTCVideoRoomActivity.KEY_SDK_APP_ID, sdkAppId);
            intent.putExtra(TRTCVideoRoomActivity.KEY_USER_SIG, userSig);
            intent.putExtra(TRTCVideoRoomActivity.KEY_ROOM_ID, Integer.valueOf(roomNo));
            intent.putExtra(TRTCVideoRoomActivity.KEY_USER_ID, ClientUtil.Sender);
            saveUserInfo(roomNo, ClientUtil.Sender);
            intent.putExtra(TRTCVideoRoomActivity.KEY_APP_SCENE, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);
            boolean isCustomVideoCapture = false;
            intent.putExtra(TRTCVideoRoomActivity.KEY_CUSTOM_CAPTURE, isCustomVideoCapture);
            intent.putExtra(TRTCVideoRoomActivity.KEY_VIDEO_FILE_PATH, mVideoFile);
            boolean mReceivedVideo = true;
            boolean mReceivedAudio = true;
            int mAudioVolumeType = TRTCCloudDef.TRTCSystemVolumeTypeVOIP;
            intent.putExtra(KEY_AUDIO_VOLUMETYOE, mAudioVolumeType);
            intent.putExtra(KEY_AUDIO_HANDFREEMODE, true);
            intent.putExtra(KEY_RECEIVED_VIDEO, mReceivedVideo);
            intent.putExtra(KEY_RECEIVED_AUDIO, mReceivedAudio);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startActivity(intent);
        }
    };
}
