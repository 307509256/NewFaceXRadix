package com.yxkj.facexradix.update;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.xuexiang.xupdate.entity.UpdateEntity;
import com.xuexiang.xupdate.listener.IUpdateParseCallback;
import com.xuexiang.xupdate.proxy.IUpdateParser;
import com.yxdz.commonlib.util.SPUtils;
import com.yxkj.facexradix.Constants;

public class CustomUpdateParser implements IUpdateParser {

    private String serverIp;
    private String serverPort;

    @Override
        public UpdateEntity parseJson(String json) throws Exception {
            serverIp = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS);
            serverPort = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS_IMAGE_PORT);
            UpdateBean updateBean = fromJson(json, UpdateBean.class);
            if (updateBean != null) {
                return new UpdateEntity()
                        .setHasUpdate(true)
                        .setIsIgnorable(true)
                        .setVersionName(updateBean.getData().getVersionNum())
                        .setUpdateContent(updateBean.getData().getRemarks())
                        .setDownloadUrl("http://" + serverIp + ":" + serverPort + updateBean.getData().getFileUrl())
                        .setSize(Long.parseLong((updateBean.getData().getFileSize().substring(0,updateBean.getData().getFileSize().indexOf(".")))));
            }
            return null;
        }

        @Override
        public void parseJson(String json, IUpdateParseCallback callback) throws Exception {
            // log.d("CustomUpdateParser", json);
        }

        @Override
        public boolean isAsyncParser() {
            return false;
        }



    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return new Gson().fromJson(json, classOfT);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}