package com.yxkj.facexradix.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.idl.face.main.FaceManager;
import com.baidu.idl.face.main.api.FaceApi;
import com.baidu.idl.face.main.model.User;
import com.baidu.idl.face.main.utils.ImageUtils;
import com.baidu.idl.face.main.utils.LogUtils;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.utils.FileUitls;
import com.yxdz.commonlib.util.SPUtils;
import com.yxdz.commonlib.util.ScreenUtils;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.netty.Message;
import com.yxkj.facexradix.netty.util.ClientMain;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import io.netty.channel.Channel;

public class LoadFaceActivity extends AppCompatActivity {

    private Channel channel;
    private boolean isGettingImg;
    private String TAG = "FaceRegitster";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_face);
        ProgressBar pb_load = findViewById(R.id.pb_load);
        pb_load.setVisibility(View.VISIBLE);
        ScreenUtils.setFullScreen(this);

        JSONArray jsonArray = JSON.parseArray(getIntent().getStringExtra("data"));
        DownloadTask mTask = new DownloadTask(getIntent().getIntExtra("msgid", 1), getIntent().getIntExtra("cmd", 1));
        if (jsonArray.size() > 1) {
            mTask.execute(jsonArray);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    onReceive(getIntent());
                }
            }).start();

        }

    }

    public void onReceive(Intent intent) {
        this.sendBroadcast(new Intent(Constants.BROADCAST_RECEIVER_STARTLOADFACE));
        HashMap<String, Object> objmap = new HashMap<>();
        JSONArray jsonArray = JSON.parseArray(intent.getStringExtra("data"));
        if (jsonArray.size() == 1) {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String userId = jsonObject.getString("userId");
            String faceUrl = jsonObject.getString("faceUrl");
            if (faceUrl == null || faceUrl.equals("")) {
                FaceManager.deleteFace(userId);
            } else {
                List<User> facex = FaceApi.getInstance().getUserList("facex");
                for (User user : facex) {
                    if (user.getUserId().equals(userId)) {
                        String serverIp = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS, Constants.DEFAULT_SERVER_ADDRESS);
                        String serverPort = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS_IMAGE_PORT, Constants.DEFAULT_SERVER_ADDRESS_IMAGE_PORT);
                        isGettingImg = true;
                        Bitmap urLimage = getBitmap("http://" + serverIp + ":" + serverPort + faceUrl);
                        while (true) {
                            if (!isGettingImg) {
                                if (urLimage != null) {
                                    boolean register = initFaceRegitster(user, "facex", urLimage);
                                    setResult(123, new Intent().putExtra("type", 0));
                                    if (register) {
                                        // log.d("OrperationReceiver", "人脸图片录入成功");
                                        objmap.put("success", userId);
                                        objmap.put("fail", null);
                                    } else {
                                        objmap.put("success", null);
                                        objmap.put("fail", userId);
                                    }
                                } else {
                                    objmap.put("success", null);
                                    objmap.put("fail", userId);
                                }
                                FaceApi.getInstance().initDatabases(true);
                                break;
                            }
                        }
                    }
                }
            }
            channel = ClientMain.getChannel();
            Message message = new Message();
            message.setMsgId(intent.getIntExtra("msgid", 1));
            message.setCode(intent.getIntExtra("cmd", 1));
            HashMap<String, Object> map = new HashMap<>();
            map.put("code", 0);
            map.put("msg", "成功");
            map.put("obj", objmap);
            message.setData(map);
            if (channel != null) {
                channel.writeAndFlush(message);
            }
            finish();


        } else if (jsonArray.size() == 0) {
            finish();
            Message message = new Message();
            HashMap<String, Object> map = new HashMap<>();
            map.put("code", 0);
            map.put("msg", "成功");
            map.put("obj", objmap);
            message.setData(map);
            if (channel != null) {
                channel.writeAndFlush(message);
            }

        }
        MainActivity.isLoading = false;
    }

    public Bitmap getBitmap(String imgUrl) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        URL url = null;
        try {
            url = new URL(imgUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(2000);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                //网络连接成功
                inputStream = httpURLConnection.getInputStream();
                outputStream = new ByteArrayOutputStream();
                byte buffer[] = new byte[1024 * 8];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                byte[] bu = outputStream.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bu, 0, bu.length);
                isGettingImg = false;
                return bitmap;
            } else {
                Toast.makeText(this, "图片路径错误", Toast.LENGTH_SHORT).show();
                // log.d("UserControllerService", "网络连接失败----" + httpURLConnection.getResponseCode());
                setResult(123, new Intent().putExtra("type", 1));
                isGettingImg = false;
                return null;
            }
        } catch (Exception e) {
            setResult(123, new Intent().putExtra("type", 1));
            e.printStackTrace();
            isGettingImg = false;
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean initFaceRegitster(User user, String groupName, Bitmap bitmap) {
        try {
            boolean success = false;
            if (bitmap != null) {
                byte[] bytes = new byte[512];
                float ret = -1;
                // 走人脸SDK接口，通过人脸检测、特征提取拿到人脸特征值
                ret = FaceApi.getInstance().getFeature(bitmap, bytes,
                        BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO);

                LogUtils.i(TAG, "live_photo = " + ret);

                if (ret == -1) {
                    LogUtils.e(TAG, "未检测到人脸，可能原因：人脸太小");
                } else if (ret == 128) {
                    FaceApi.getInstance().userDelete(user.getUserId(), groupName);
                    // 将用户信息和用户组信息保存到数据库
                    boolean importDBSuccess = FaceApi.getInstance().registerUserIntoDBmanager(groupName,
                            user.getUserName(), user.getUserId(), user.getUserInfo(), bytes, user.getCard(), user.getPwd());
                    // 保存数据库成功
                    if (importDBSuccess) {
                        // 保存图片到新目录中
                        File facePicDir = FileUitls.getFaceDirectory();
                        if (facePicDir != null) {
                            File savePicPath = new File(facePicDir, user.getUserId());
                            ImageUtils.resize(bitmap, savePicPath, 300, 300);
//                            if (FileUtils.saveBitmap(savePicPath, bitmap)) {
//                                LogUtils.i(TAG, "图片保存成功");
//                                success = true;
//                            } else {
//                                LogUtils.i(TAG, "图片保存失败");
//                            }

                        }
                    } else {
                        LogUtils.e(TAG, user.getUserId() + "：保存到数据库失败");
                    }
                } else {
                    LogUtils.e(TAG, user.getUserId() + "：未检测到人脸");
                }

                // 图片回收
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            } else {
                LogUtils.e(TAG, user.getUserId() + "：该图片转成Bitmap失败");
            }

            // 判断成功与否
            if (success) {
                LogUtils.e(TAG, "导入成功");
                return true;
            } else {
                LogUtils.e(TAG, "导入失败");
                return false;
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "exception = " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    class DownloadTask extends AsyncTask<JSONArray, Void, HashMap<String, Object>> {


        private final int msgid;
        private final int cmd;
        private Channel channel;

        public DownloadTask(int msgid, int cmd) {
            this.msgid = msgid;
            this.cmd = cmd;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> objmap) {
            super.onPostExecute(objmap);
            channel = ClientMain.getChannel();
            if (channel != null) {
                Message message = new Message();
                message.setMsgId(msgid);
                message.setCode(cmd);
                HashMap<String, Object> map = new HashMap<>();
                map.put("code", 0);
                map.put("msg", "成功");
                map.put("obj", objmap);
                message.setData(map);
                channel.writeAndFlush(message);
            }
            MainActivity.isLoading = false;
            finish();
        }

        @Override
        protected HashMap<String, Object> doInBackground(JSONArray... strings) {
            String serverIp = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS, Constants.DEFAULT_SERVER_ADDRESS);
            String serverPort = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS_IMAGE_PORT, Constants.DEFAULT_SERVER_ADDRESS_IMAGE_PORT);
            HashMap<String, Object> objmap = new HashMap<>();

            for (Object object : strings[0]) {
                MainActivity.operateTime = System.currentTimeMillis();
                JSONObject jsonObject = (JSONObject) object;
                String userId = jsonObject.getString("userId");
                String faceUrl = jsonObject.getString("faceUrl");
                if (faceUrl == null || faceUrl.equals("")) {
                    FaceManager.deleteFace(userId);
                } else {
                    List<User> facex = FaceApi.getInstance().getUserList("facex");
                    for (User user : facex) {
                        if (user.getUserId().equals(userId)) {
                            Bitmap urLimage = getBitmap("http://" + serverIp + ":" + serverPort + faceUrl);
                            if (urLimage != null) {
                                boolean register = initFaceRegitster(user, "facex", urLimage);
                                setResult(123, new Intent().putExtra("type", 0));
                                if (register) {
                                    // log.d("OrperationReceiver", "人脸图片录入成功");
                                    objmap.put("success", user.getUserId());
                                    objmap.put("fail", null);
                                } else {
                                    objmap.put("success", null);
                                    objmap.put("fail", user.getUserId());
                                }
                            } else {
                                objmap.put("success", null);
                                objmap.put("fail", user.getUserId());
                            }
                        }
                    }
                }
            }
            FaceApi.getInstance().initDatabases(true);
            return objmap;
        }
    }
}
