package com.yxkj.facexradix.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yxkj.facexradix.R;


/**
 * @PackageName: com.yxdz.facex.view
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/2/14 15:04
 */
public class XKeyBoard extends LinearLayout implements View.OnClickListener {

    private Context context;
    private OnXKeyListener onXKeyListener;
    private int mode=1;
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

    public XKeyBoard(Context context) {
        super(context);
        this.context=context;
        initView();
    }

    public XKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView();
    }

    public XKeyBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initView();
    }


    private void initView() {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.layout_xkeyboard, this, false);
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
    }

    public void setKeyListener(OnXKeyListener onXKeyListener){
       this.onXKeyListener=onXKeyListener;
    }

    /**
     * 设置键盘模式
     * @param mode 1,数字；2.字母
     */
    public void setMode(int mode){
        this.mode=mode;
        setKeyBoard();
    }

    private void setKeyBoard() {
        //数字
        if (this.mode==1){
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
        }else if (this.mode==2){
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

    public int getMode(){
        return this.mode;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1A:
               deal('1','A');
                break;
            case R.id.btn2B:
                deal('2','B');
                break;
            case R.id.btn3C:
                deal('3','C');
                break;
            case R.id.btn4D:
                deal('4','D');
                break;
            case R.id.btn5E:
                deal('5','E');
                break;
            case R.id.btn6F:
                deal('6','F');
                break;
            case R.id.btn7G:
                deal('7','G');
                break;
            case R.id.btn8H:
                deal('8','H');
                break;
            case R.id.btn9I:
                deal('9','I');
                break;
            case R.id.btn0J:
                deal('0','J');
                break;
        }
    }

    public void deal(char one1,char one2){
        if (onXKeyListener!=null){
            if (mode==1){
                onXKeyListener.onKey(one1);
            }else if(mode==2){
                onXKeyListener.onKey(one2);
            }

        }
    }

}
