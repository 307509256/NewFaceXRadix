package com.yxkj.facexradix.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yxkj.facexradix.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 自定义标题栏
 */
public class SystemHeadView extends RelativeLayout {

    private Context context;
    private LinearLayout ivHome;
    private TextView tvDate;
    private TextView tvTime;
    private LinearLayout ivHelp;
    private SimpleDateFormat sdf1;
    private SimpleDateFormat sdf2;
    private boolean flag;
    private ImageView ivTcp;
    private LinearLayout ivUpgrade;


    public SystemHeadView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public SystemHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public SystemHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        //设置背景颜色
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.layout_system_head, this, false);
        this.addView(viewGroup);
        ivHome = viewGroup.findViewById(R.id.ivHome);
        tvDate = viewGroup.findViewById(R.id.tvDate);
        tvTime = viewGroup.findViewById(R.id.tvTime);
        ivUpgrade = viewGroup.findViewById(R.id.ivUpgrade);
        ivHelp = viewGroup.findViewById(R.id.ivHelp);
        ivTcp = viewGroup.findViewById(R.id.tcp_connection);
    }

    public void start() {
        //实时更新时间（1秒更新一次）
        sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        sdf2 = new SimpleDateFormat("HH:mm:ss");
        flag = true;
        TimeThread timeThread = new TimeThread(tvDate);//tvDate 是显示时间的控件TextView
        timeThread.start();//启动线程
    }

    public void stop() {
        flag = false;
    }

    public void setHelpListener(OnClickListener listener) {
        if (listener != null) {
            ivHelp.setOnClickListener(listener);
        }
    }

    public void setHelpView(boolean show) {
        if (show) {
            ivHelp.setVisibility(VISIBLE);
        } else {
            ivHelp.setVisibility(GONE);
        }

    }

    public void setHomeView(boolean show) {
        if (show) {
            ivHome.setVisibility(VISIBLE);
        } else {
            ivHome.setVisibility(GONE);
        }

    }

    public void setUpgradeView(boolean show) {
        if (show) {
            ivUpgrade.setVisibility(VISIBLE);
        } else {
            ivUpgrade.setVisibility(GONE);
        }

    }

    public void setHomeListener(OnClickListener listener) {
        if (listener != null) {
            ivHome.setOnClickListener(listener);
        }
    }


    public void setUpgradeListener(OnClickListener listener) {
        if (listener != null) {
            ivUpgrade.setOnClickListener(listener);
        }
    }

    public void toggleTcp(boolean type) {
        if (type) {
            ivTcp.setVisibility(VISIBLE);
        } else {
            ivTcp.setVisibility(GONE);
        }
    }

    private class TimeThread extends Thread {
        public TextView tvDate;
        private int msgKey1 = 1;

        public TimeThread(TextView tvDate) {
            this.tvDate = tvDate;
        }

        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (flag);
        }

        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        String date = sdf1.format(new Date());
                        String time = sdf2.format(new Date());
                        tvTime.setText(time);
                        tvDate.setText(date);
                        break;
                    default:
                        break;
                }
            }
        };
    }


}
