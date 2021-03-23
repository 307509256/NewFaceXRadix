package com.yxkj.facexradix.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yxdz.commonlib.util.SPUtils;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.Constants;


/**
 * @PackageName: com.yxdz.facex.view
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/2/14 15:04
 */
public class XKeyBoard2 extends LinearLayout implements View.OnClickListener {

    private Context context;
    private OnXKeyListener onXKeyListener;
    private int mode = 1;
    private Button btn1A;
    private Button btn2B;
    private Button btn3C;
    private Button btn4D;
    private Button btn5E;
    private Button btn6F;
    private Button btn7G;
    private Button btn8H;
    private Button btn9I;
    private Button btn0J;
    private Button btnClear;
    private Button btnSure;

    private int[] arr;
    private Button[] btnArr;
    private int[] brr;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public XKeyBoard2(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public XKeyBoard2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public XKeyBoard2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }


    private void initView() {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.layout_disorder_keyboard, this, false);
        this.addView(viewGroup);
        btn1A = viewGroup.findViewById(R.id.btn1A);
        btn2B = viewGroup.findViewById(R.id.btn2B);
        btn3C = viewGroup.findViewById(R.id.btn3C);
        btn4D = viewGroup.findViewById(R.id.btn4D);
        btn5E = viewGroup.findViewById(R.id.btn5E);
        btn6F = viewGroup.findViewById(R.id.btn6F);
        btn7G = viewGroup.findViewById(R.id.btn7G);
        btn8H = viewGroup.findViewById(R.id.btn8H);
        btn9I = viewGroup.findViewById(R.id.btn9I);
        btn0J = viewGroup.findViewById(R.id.btn0J);
        btnClear = viewGroup.findViewById(R.id.btnClear);
        btnSure = viewGroup.findViewById(R.id.btnSure);
        btn1A.setOnClickListener(this);
        btn2B.setOnClickListener(this);
        btn3C.setOnClickListener(this);
        btn4D.setOnClickListener(this);
        btn5E.setOnClickListener(this);
        btn6F.setOnClickListener(this);
        btn7G.setOnClickListener(this);
        btn8H.setOnClickListener(this);
        btn9I.setOnClickListener(this);
        btn0J.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        arr = new int[]{4, 3, 1, 0, 5, 9, 2, 6, 8, 7};
        btnArr = new Button[]{btn1A, btn2B, btn3C, btn4D, btn5E, btn6F, btn7G,
                btn8H, btn9I, btn0J};
        // 乱序键盘值数组
        brr = new int[10];
        setDisOrder();
        changeColor();
    }

    public void changeColor(){
        String passMode = SPUtils.getInstance().getString(Constants.DEVICE_OPEATOR_MODE, "faceOrCardOrPassword");
//        switch (passMode) {
//            case "face":
//                changeColor( R.drawable.selector_btn_circle_orange,ContextCompat.getColor(context, R.color.orange));
//                break;
//            case "card":
//                changeColor( R.drawable.selector_btn_circle_orange,ContextCompat.getColor(context, R.color.orange));
//                break;
//            case "cardAndFace":
//                changeColor( R.drawable.selector_btn_circle_orange,ContextCompat.getColor(context, R.color.orange));
//                break;
//            default:
                changeColor( R.drawable.selector_btn_circle,ContextCompat.getColor(context, R.color.white));
//                break;
//        }
    }
//
//
    public void changeColor(int drawable, int color) {
        btn1A.setBackgroundResource(drawable);
        btn1A.setTextColor(color);
        btn2B.setBackgroundResource(drawable);
        btn2B.setTextColor(color);
        btn3C.setBackgroundResource(drawable);
        btn3C.setTextColor(color);
        btn4D.setBackgroundResource(drawable);
        btn4D.setTextColor(color);
        btn5E.setBackgroundResource(drawable);
        btn5E.setTextColor(color);
        btn6F.setBackgroundResource(drawable);
        btn6F.setTextColor(color);
        btn7G.setBackgroundResource(drawable);
        btn7G.setTextColor(color);
        btn8H.setBackgroundResource(drawable);
        btn8H.setTextColor(color);
        btn9I.setBackgroundResource(drawable);
        btn9I.setTextColor(color);
        btn0J.setBackgroundResource(drawable);
        btn0J.setTextColor(color);
        btnClear.setBackgroundResource(drawable);
        btnClear.setTextColor(color);
        btnSure.setBackgroundResource(drawable);
        btnSure.setTextColor(color);
    }

    public void setKeyListener(OnXKeyListener onXKeyListener) {
        this.onXKeyListener = onXKeyListener;
    }

    /**
     * 设置键盘模式
     *
     * @param mode 1,数字；2.字母
     */
    public void setMode(int mode) {
        this.mode = mode;
        setKeyBoard();
    }

    public void setDisOrder() {
        int c = (int) (Math.random() * 10);// 0-9的随机数
        // 赋值于乱序键盘值数组
        for (int i = 0; i < brr.length; i++) {
            if (c + i < brr.length) {
                brr[i] = arr[c + i];
            } else {
                brr[i] = arr[i - brr.length + c];// 比如随机数为7，把a[7]赋给b[0],a[]赋完以后，再从a【0】开始赋值给b【】
            }
        }
        // ========================================================================给按钮赋值===================
        for (int i = 0; i < btnArr.length; i++) {
            btnArr[i].setText(String.valueOf(brr[i]));
        }
    }

    private void setKeyBoard() {
        //数字
        if (this.mode == 1) {
            btn1A.setText("1");
            btn2B.setText("2");
            btn3C.setText("3");
            btn4D.setText("4");
            btn5E.setText("5");
            btn6F.setText("6");
            btn7G.setText("7");
            btn8H.setText("8");
            btn9I.setText("9");
            btn0J.setText("0");
        } else if (this.mode == 2) {
            //字母
            btn1A.setText("A");
            btn2B.setText("B");
            btn3C.setText("C");
            btn4D.setText("D");
            btn5E.setText("E");
            btn6F.setText("F");
            btn7G.setText("G");
            btn8H.setText("H");
            btn9I.setText("I");
            btn0J.setText("J");
        }
    }

    public int getMode() {
        return this.mode;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1A:
                deal(btn1A.getText().charAt(0));
                break;
            case R.id.btn2B:
                deal(btn2B.getText().charAt(0));
                break;
            case R.id.btn3C:
                deal(btn3C.getText().charAt(0));
                break;
            case R.id.btn4D:
                deal(btn4D.getText().charAt(0));
                break;
            case R.id.btn5E:
                deal(btn5E.getText().charAt(0));
                break;
            case R.id.btn6F:
                deal(btn6F.getText().charAt(0));
                break;
            case R.id.btn7G:
                deal(btn7G.getText().charAt(0));
                break;
            case R.id.btn8H:
                deal(btn8H.getText().charAt(0));
                break;
            case R.id.btn9I:
                deal(btn9I.getText().charAt(0));
                break;
            case R.id.btn0J:
                deal(btn0J.getText().charAt(0));
                break;
            case R.id.btnClear:
                deal(btnClear.getText().charAt(0));
                break;
            case R.id.btnSure:
                deal(btnSure.getText().charAt(0));
                break;
        }
    }


    public void deal(char one1) {
        if (onXKeyListener != null) {
            onXKeyListener.onKey(one1);
        }
    }

}
