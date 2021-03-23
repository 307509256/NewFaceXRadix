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
                .isWifiOnly(true)                                               //默认设置只在wifi下检查版本更新
                .isGet(true)                                                    //默认设置使用get请求检查版本
                .isAutoMode(false)                                              //默认设置非自动模式，可根据具体使用配置
                .param("versionCode", UpdateUtils.getVersionCode(this))         //设置默认公共请求参数
                .param("appKey", getPackageName())
                .setOnUpdateFailureListener(new OnUpdateFailureListener() {     //设置版本更新出错的监听
                    @Override
                    public void onFailure(UpdateError error) {
                        if (error.getCode() != CHECK_NO_NEW_VERSION) {          //对不同错误进行处理
                            ToastUtils.showShortToast(error.toString());
                        }
                    }
                })
                .supportSilentInstall(true)                                     //设置是否支持静默安装，默认是true
                .setIUpdateHttpService(new OKHttpUpdateHttpService())           //这个必须设置！实现网络请求功能。
                .init(this);
    }






}
