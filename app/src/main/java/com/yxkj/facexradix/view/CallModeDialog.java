package com.yxkj.facexradix.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.PopupWindowCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yxdz.commonlib.util.RegexUtils;
import com.yxdz.commonlib.util.SPUtils;
import com.yxkj.facexradix.R;

public class CallModeDialog extends Dialog implements View.OnClickListener , View.OnFocusChangeListener {

    private final TextView textView;
    private RadioGroup type;
    private View callnumber;
    private View connect;
    private View cancel;
    private EditText number;

    public CallModeDialog(@NonNull Context context, TextView textView) {
        super(context);
        this.textView = textView;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_callmodedialog);
        callnumber = findViewById(R.id.callnumber);
        type = findViewById(R.id.calltype);
        type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.type1:
                        callnumber.setVisibility(View.GONE);
                        break;
                    case R.id.type2:
                        callnumber.setVisibility(View.VISIBLE);
                        number.setText(SPUtils.getInstance().getString("DEFAULT_CALL_NUMBER"));
                        break;
                    case R.id.type3:
                        callnumber.setVisibility(View.GONE);
                        break;
                }
            }
        });


        connect = findViewById(R.id.connect);
        connect.setOnClickListener(this);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        number = findViewById(R.id.number);
        number.setOnClickListener(this);
        number.clearFocus();
        number.setInputType(InputType.TYPE_NULL);
        number.setOnFocusChangeListener(this);
        number.setShowSoftInputOnFocus(false);

        if (SPUtils.getInstance().getInt("CALL_MODE",0) == 0){
            type.check(R.id.type1);
            callnumber.setVisibility(View.GONE);
        }else if(SPUtils.getInstance().getInt("CALL_MODE",0) == 1){
            type.check(R.id.type2);
            number.setText(SPUtils.getInstance().getString("DEFAULT_CALL_NUMBER"));
        }else{
            type.check(R.id.type3);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect:
                if(type.getCheckedRadioButtonId() == R.id.type1){
                    SPUtils.getInstance().put("CALL_MODE",0);
                    this.textView.setText("房间号模式");
                    this.dismiss();
                }
                if (type.getCheckedRadioButtonId() == R.id.type3){
                    SPUtils.getInstance().put("CALL_MODE",2);
                    this.textView.setText("社区模式");
                    this.dismiss();
                }
                if (type.getCheckedRadioButtonId() == R.id.type2){
                    if (!TextUtils.isEmpty(number.getText())) {
                        if(RegexUtils.isMobileSimple(number.getText())){
                            SPUtils.getInstance().put("CALL_MODE",1);
                            SPUtils.getInstance().put("DEFAULT_CALL_NUMBER", number.getText().toString());
                            this.textView.setText("门铃模式");
                            this.dismiss();
                        }else{
                            Toast.makeText(getContext(), "号码格式不正确", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(), "电话号码不能为空", Toast.LENGTH_SHORT).show();
                    }

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
}
