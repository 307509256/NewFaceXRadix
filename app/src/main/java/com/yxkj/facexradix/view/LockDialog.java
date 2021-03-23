package com.yxkj.facexradix.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.PopupWindowCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yxdz.commonlib.util.RegexUtils;
import com.yxdz.commonlib.util.SPUtils;
import com.yxdz.commonlib.util.ToastUtils;
import com.yxkj.facexradix.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LockDialog  extends Dialog implements View.OnClickListener , View.OnFocusChangeListener  {

    private final TextView textView;
    private EditText lock_time;
    private View connect;


    public LockDialog(@NonNull Context context, TextView textView) {
        super(context);
        this.textView = textView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lock);
        connect = findViewById(R.id.connect);
        lock_time = findViewById(R.id.lock_time);
//        lock_time.setText(SPUtils.getInstance().getInt("Lock_time"));
        connect.setOnClickListener(this);
        View cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect:
                if(isInt(lock_time.getText().toString())){
                    SPUtils.getInstance().put("Lock_time" , Integer.parseInt(lock_time.getText().toString()) * 1000);
                    this.textView.setText(lock_time.getText().toString() + "秒");
                    this.dismiss();
                }else{
                    ToastUtils.showShortToast("请输入数字");
                }

                break;
            case R.id.cancel:
                this.dismiss();
                break;
            default:
                PopupKeyboard popupKeyboard = new PopupKeyboard(getContext(), (EditText) v);
                popupKeyboard.getContentView().measure(0, 0);
                PopupWindowCompat.showAsDropDown(popupKeyboard, connect, 0, 450, Gravity.BOTTOM);
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            PopupKeyboard popupKeyboard = new PopupKeyboard(getContext(), (EditText) v);
            popupKeyboard.getContentView().measure(0, 0);
            PopupWindowCompat.showAsDropDown(popupKeyboard, connect, 0, 450, Gravity.BOTTOM);
        }
    }

    public static boolean isInt(String string) {
        if (string == null)
            return false;

        String regEx1 = "[\\-|\\+]?\\d+";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches())
            return true;
        else
            return false;
    }

}
