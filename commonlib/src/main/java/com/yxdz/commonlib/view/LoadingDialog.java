package com.yxdz.commonlib.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yxdz.commonlib.R;


/**
 * 加载转圈
 * Created by huang on 2018/6/5.
 */

public class LoadingDialog extends Dialog {

    private Context context;
    private String text = "加载中...";

    private ProgressBar progressBar;
    private TextView tvText;

    public LoadingDialog(Context context) {
        super(context, R.style.common_load_dialog);
        this.context = context;
    }

    public LoadingDialog(Context context, String text) {
        super(context, R.style.common_load_dialog);
        this.context = context;
        if (text != null) {
            this.text = text;
        }
    }

    public void setTip(String tip){
        if (tvText!=null){
            tvText.setText(tip);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog_loading);
        initView();

    }

    private void initView() {

        progressBar = findViewById(R.id.common_load_dialog_progressbar);
        tvText = findViewById(R.id.common_load_dialog_text);

        setCanceledOnTouchOutside(false);

        tvText.setText(text);
        setOnKeyListener(keylistener);
    }

    /**
     * 禁止返回键点击
     */
    OnKeyListener keylistener = new OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            return keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0;
        }
    };

}
