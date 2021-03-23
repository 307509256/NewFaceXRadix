package com.yxkj.facexradix.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yxdz.commonlib.base.BaseActivity;
import com.yxdz.commonlib.util.ShellUtils;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.utils.Helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;

public class UpdataActivity extends BaseActivity {


    private TextView progress_number;
    private TextView cancel;
    private ProgressBar progress_bar;
    private Thread thread;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_updata;
    }

    @Override
    public void onModel() {
        isUpdating = true;
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdating = false;
                finish();
            }
        });
        progress_bar = findViewById(R.id.progress_bar);
        progress_number = findViewById(R.id.progress_number);
    }

    @Override
    public void onData(Bundle savedInstanceState) {
        String url = getIntent().getStringExtra("url");
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                downloadFile1(url);
            }
        });
        thread.start();

    }

    boolean isUpdating;

    public void downloadFile1(String url) {
        try {

            String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            final long startTime = System.currentTimeMillis();
            Log.i("DOWNLOAD", "startTime=" + startTime);
            //下载函数
            String filename = url.substring(url.lastIndexOf("/") + 1);
            //获取文件名
            URL myURL = new URL(url);
            URLConnection conn = myURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            int fileSize = conn.getContentLength();//根据响应获取文件大小
            progress_bar.setMax(fileSize);
            if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
            if (is == null) throw new RuntimeException("stream is null");
            File file1 = new File(path);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            //把数据存入路径+文件名
            FileOutputStream fos = new FileOutputStream(path + "/" + filename);
            byte buf[] = new byte[1024];
            int downLoadFileSize = 0;

            do {
                //循环读取
                int numread = is.read(buf);
                if (numread == -1) {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;
                //更新进度条
                progress_bar.setProgress(downLoadFileSize);
                int finalDownLoadFileSize = downLoadFileSize;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 创建一个数值格式化对象
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        // 设置精确到小数点后2位
                        numberFormat.setMaximumFractionDigits(2);
                        String result = numberFormat.format((float) finalDownLoadFileSize / (float) fileSize * 100);
                        progress_number.setText(result + "%");
                    }
                });

            } while (isUpdating);
            Log.i("DOWNLOAD", "download success");
            Log.i("DOWNLOAD", "totalTime=" + (System.currentTimeMillis() - startTime));

            try {
                if (isUpdating) {
                    Helper.restartAPP();
                    ShellUtils.CommandResult commandResult = ShellUtils.execCmd("pm install -r " + path + "/" + filename, true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            is.close();
        } catch (Exception ex) {
            Log.e("DOWNLOAD", "error: " + ex.getMessage(), ex);
        }
    }
}
