package com.yxkj.facexradix;

import android.app.AlarmManager;
import android.app.glonger.JldManager;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.baidu.idl.main.facesdk.utils.PreferencesUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;
import com.yxdz.commonlib.util.ToastUtils;
import com.yxdz.commonlib.util.Utils;
import com.yxkj.facexradix.update.OKHttpUpdateHttpService;
import com.yxkj.facexradix.utils.EthernetUtil;

import static com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION;
import static com.yxkj.facexradix.utils.EthernetUtil.getIpConfigurationEnum;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @ClassName: MyApplication
 * @Desription:
 * @author: Dreamcoding
 * @date: 2018/12/6
 */
public class MyApplication extends MultiDexApplication {

    private static MyApplication sInstance;
    private static JldManager jldManager;


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        Utils.init(getAppContext());
        jldManager = JldManager.create(getApplicationContext());

        PreferencesUtil.initPrefs(this);
        CrashHandler.getInstance().init(getAppContext());
        CrashReport.initCrashReport(getApplicationContext(), "ec0891bb03", true);
        initXupdate();

    }

    public static JldManager getJldManager() {
        return jldManager;
    }


    public static Context getAppContext() {
        return sInstance;
    }


    public static void SetTime(long timestamp) {
        AlarmManager systemService = (AlarmManager) getAppContext().getSystemService(Context.ALARM_SERVICE);
        if (timestamp / 1000 < Integer.MAX_VALUE)
            systemService.setTime(timestamp);
    }



    public static void SetDHCP()  {
        EthernetUtil.setDynamicIp(getAppContext());
    }




    public void  initXupdate(){
        XUpdate.get()
                .debug(true)
                .isWifiOnly(true)                                               //??????????????????wifi?????????????????????
                .isGet(true)                                                    //??????????????????get??????????????????
                .isAutoMode(false)                                              //?????????????????????????????????????????????????????????
                .param("versionCode", UpdateUtils.getVersionCode(this))         //??????????????????????????????
                .param("appKey", getPackageName())
                .setOnUpdateFailureListener(new OnUpdateFailureListener() {     //?????????????????????????????????
                    @Override
                    public void onFailure(UpdateError error) {
                        if (error.getCode() != CHECK_NO_NEW_VERSION) {          //???????????????????????????
                            ToastUtils.showShortToast(error.toString());
                        }
                    }
                })
                .supportSilentInstall(true)                                     //??????????????????????????????????????????true
                .setIUpdateHttpService(new OKHttpUpdateHttpService())           //????????????????????????????????????????????????
                .init(this);
    }






}
