package com.yxkj.facexradix.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.yxdz.commonlib.base.BaseFragment;
import com.yxdz.commonlib.util.NoDoubleClick;
import com.yxdz.commonlib.util.SPUtils;
import com.yxdz.commonlib.util.ToastUtils;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.utils.FragmentUtil;
import com.yxkj.facexradix.view.Keyboard;
import com.yxkj.facexradix.view.KeyboardListener;
import com.yxkj.facexradix.view.XEditText;


import java.util.List;

/**
 * @PackageName: com.yxdz.facex.ui.fragment
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/2/14 14:58
 */
public class PasswordFragment extends BaseFragment implements View.OnClickListener {

    private ImageButton ibtnBack;
    private ImageButton ibtnSure;
    private XEditText xEditText;
    private InputMethodManager inputManager;
    private Keyboard keyboard;


    @Override
    public int getLayoutRes() {
        return R.layout.fragment_password;
    }

    @Override
    public void onModel() {
        ibtnBack = rootView.findViewById(R.id.ibtnBack);
        ibtnSure = rootView.findViewById(R.id.ibtnSure);
        xEditText = rootView.findViewById(R.id.xEditText);
        keyboard = rootView.findViewById(R.id.keyboard);
    }

    @Override
    public void onData(Bundle savedInstanceState) {
        ibtnBack.setOnClickListener(this);
        ibtnSure.setOnClickListener(this);
        List<Fragment> fragments = FragmentUtil.getFragments(getFragmentManager());
        fragments.size();
        keyboard.setOnkeyboardListener(new KeyboardListener() {
            @Override
            public void onKey(CharSequence key) {
                xEditText.addTvTip(key);
            }

            @Override
            public void onDelete() {
                xEditText.deleteTip();
            }

            @Override
            public void onDone() {
                if (!NoDoubleClick.isFastDoubleClick(500)) {
                    String password = xEditText.getData();
                    String pass = SPUtils.getInstance().getString(Constants.DEVICE_ACCESS_PASSWORD, Constants.DEFAULT_DEVICE_ACCESS_PASSWORD);
                    if (pass.equals(password)) {
                        FragmentUtil.remove(PasswordFragment.this);
                        ConfigFragment configFragment = new ConfigFragment();
                        FragmentUtil.add(getFragmentManager(), configFragment, R.id.flContainer);
                    } else {
                        ToastUtils.showShortToast("认证密码错误！");
                    }
                }
            }
        });


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibtnBack:
                if(!NoDoubleClick.isFastDoubleClick(500)){
                    FragmentUtil.remove(this);
                }
                break;
            case R.id.ibtnSure:
                if(!NoDoubleClick.isFastDoubleClick(500)){
                    String password = xEditText.getData();
                    String pass=SPUtils.getInstance().getString(Constants.DEVICE_ACCESS_PASSWORD,Constants.DEFAULT_DEVICE_ACCESS_PASSWORD);
                    if (pass.equals(password)){
                        FragmentUtil.remove(this);
                        ConfigFragment configFragment=new ConfigFragment();
                        FragmentUtil.add(getFragmentManager(),configFragment,R.id.flContainer);
//                        hideSoftKeyboard(getContext());
                    }else{
                        ToastUtils.showShortToast("认证密码错误！");
                    }
                }
                break;
        }
    }

}

