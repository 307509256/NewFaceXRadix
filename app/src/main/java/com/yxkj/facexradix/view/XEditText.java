package com.yxkj.facexradix.view;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.yxkj.facexradix.R;


/**
 * @PackageName: com.yxdz.facex.view
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/2/14 15:04
 */
public class XEditText extends LinearLayout implements View.OnClickListener, View.OnFocusChangeListener {

    private Context context;
    private EditText tvTip;
    private ImageButton ibtnDelete;
    private StringBuilder sb;
    private int length;
    private boolean hasFocus;

    public XEditText(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public XEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public XEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }


    private void initView() {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.layout_xedittext, this, false);
        this.addView(viewGroup);
        tvTip = viewGroup.findViewById(R.id.tvTip);
        tvTip.setShowSoftInputOnFocus(false);
        ibtnDelete = viewGroup.findViewById(R.id.ibtnDelete);
        ibtnDelete.setOnClickListener(this);
        sb = new StringBuilder();
        setOnFocusChangeListener(this);
    }

    public void setPasswordMode(boolean flag) {
        if (flag) {
            tvTip.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            tvTip.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }


    public String getData() {
        return tvTip.getText().toString();
    }

    public void clear() {
        sb.delete(0, sb.length());
        tvTip.setText("");
    }


    public void addTvTip(CharSequence tip) {
        sb.append(tip);
        tvTip.setText(sb.toString());
    }

    public void addTvTipLimit(char tip) {
        if (tvTip.getText().toString().length() <= 10) {
            sb.append(tip);
            tvTip.setText(sb.toString());
        }
    }


    public void deleteTip() {
        if(sb.length() != 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        Editable text = tvTip.getText();
        if (text.length() - 1 >= 0) {
            Editable delete = text.delete(text.length() - 1, text.length());
            tvTip.setText(delete);
            tvTip.setSelection(delete.length());
        }

    }

    public void setHint(String tip) {
        tvTip.setHint(tip);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtnDelete:
                clear();
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        tvTip.setFocusable(hasFocus);
        tvTip.setFocusableInTouchMode(hasFocus);
        tvTip.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) tvTip.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(tvTip, 0);
        if (tvTip != null && hasFocus == false){
            inputManager.hideSoftInputFromWindow(tvTip.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }


}
