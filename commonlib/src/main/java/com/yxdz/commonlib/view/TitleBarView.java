package com.yxdz.commonlib.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yxdz.commonlib.R;


/**
 * 自定义标题栏
 */
public class TitleBarView extends RelativeLayout implements View.OnClickListener {

    private Context context;
    //自定义的属性
    private String mTitleBarText;//标题
    private int mTitleBarTextColor = 0;//标题颜色
    private int mTitleBarBackground = -1;//标题栏背景颜色
    private float mTitleBarTextSize = 0;//标题大小
    private boolean mTitleBarBackVisible;//标题栏返回按钮是否显示

    //控件
    private TextView tvBack;
    private TextView tvTitle;
    private ImageView rightImageView;
    private TextView tvRight;

    public TitleBarView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
        initView();
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        initView();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = this.getContext().obtainStyledAttributes(attrs, R.styleable.titleBar);
        if (typedArray != null) {
            mTitleBarText = typedArray.getString(R.styleable.titleBar_titleBarText);//titleBarText
            mTitleBarTextColor = typedArray.getColor(R.styleable.titleBar_titleBarTextColor, 0);
            mTitleBarBackground = typedArray.getColor(R.styleable.titleBar_titleBarBackground, -1);
//            mBarLeftVisible = typedArray.getBoolean(R.styleable.titleBar_headLeftVisible, true);
            mTitleBarTextSize = typedArray.getDimension(R.styleable.titleBar_titleBarTextSize, 40);
            mTitleBarBackVisible = typedArray.getBoolean(R.styleable.titleBar_titleBarBackVisible, true);
            typedArray.recycle();

        }
    }

    private void initView() {
        //设置背景颜色
        setBackgroundColor(getResources().getColor(R.color.colorPrimaryBase));
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.common_titlebar_layout, this, false);
        this.addView(viewGroup);

        tvBack = viewGroup.findViewById(R.id.common_titlebar_back);
        tvTitle = viewGroup.findViewById(R.id.common_titlebar_title);
        rightImageView = viewGroup.findViewById(R.id.common_titlebar_right_image);

        if (mTitleBarBackground != -1) {
            setBackgroundColor(mTitleBarBackground);
        }

        tvTitle.setTextSize(mTitleBarTextSize);

        if (!TextUtils.isEmpty(mTitleBarText)) {
            tvTitle.setText(mTitleBarText);
        }
        if (mTitleBarTextColor != 0) {
            tvTitle.setTextColor(mTitleBarTextColor);
        }
        if (!mTitleBarBackVisible) {
            tvBack.setVisibility(INVISIBLE);
        }

        ////////////定义属性的处理 end ///////////
        tvBack.setOnClickListener(this);

    }

    public void setTitleBarBackgroundColor(int color) {
        this.setBackgroundColor(getResources().getColor(color));
    }

    public void setTitleBarText(String title) {
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
    }

    public ImageView getRightImageView() {
        return rightImageView;
    }

    /**
     * 返回
     * @param onClickListener
     */
    public void setBackOnClickListener(OnClickListener onClickListener) {
        tvBack.setOnClickListener(onClickListener);
    }

    public void setRightImageViewOnClickListener(final RightImageViewOnClickListener rightImageViewOnClickListener) {

        if (rightImageView.getVisibility() == View.GONE) {
            rightImageView.setVisibility(VISIBLE);
        }

        rightImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rightImageViewOnClickListener.onRightOnClick(v);
            }
        });
    }

    public interface RightImageViewOnClickListener {
        public void onRightOnClick(View v);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.common_titlebar_back) {
            ((Activity) (context)).finish();

        }
    }


}
