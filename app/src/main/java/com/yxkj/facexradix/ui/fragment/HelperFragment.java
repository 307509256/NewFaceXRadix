package com.yxkj.facexradix.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yxdz.commonlib.base.BaseFragment;
import com.yxdz.commonlib.util.AppUtils;
import com.yxdz.commonlib.util.DeviceUtil;
import com.yxkj.facexradix.MyApplication;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.ui.activity.MainActivity;
import com.yxkj.facexradix.utils.FragmentUtil;


/**
 * @PackageName: com.yxdz.facex.ui.fragment
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/2/14 14:02
 */
public class HelperFragment extends BaseFragment implements View.OnClickListener {

    private ImageButton ibtnBack;
    private ImageButton ibtnConfig;
    private BaseFragment backFragment;
    private TextView tvCopyright;
    private TextView tvVersion;
    private TextView tvDeviceSn;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_helper;
    }

    @Override
    public void onModel() {
        ibtnBack = rootView.findViewById(R.id.ibtnBack);
        ibtnConfig = rootView.findViewById(R.id.ibtnConfig);
        tvCopyright = rootView.findViewById(R.id.tvCopyright);
        tvVersion = rootView.findViewById(R.id.tvVersion);
        tvDeviceSn = rootView.findViewById(R.id.textView9);
    }

    @Override
    public void onData(Bundle savedInstanceState) {
        ibtnBack.setOnClickListener(this);
        ibtnConfig.setOnClickListener(this);
        tvCopyright.setText(Constants.COPYRIGHT);
        tvVersion.setText("Software v"+AppUtils.getVersionName(MyApplication.getAppContext())+ " - Reader "+MainActivity.versionReader);
        tvDeviceSn.setText("序列号:"+ DeviceUtil.getLocalMacAddress(MyApplication.getAppContext()));
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setUpgradeView(true);
    }


    public void setBackFragment(BaseFragment backFragment){
        this.backFragment=backFragment;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibtnBack:
                FragmentUtil.remove(this);
                FragmentUtil.show(backFragment);
                break;
            case R.id.ibtnConfig:
                PasswordFragment passwordFragment=new PasswordFragment();
                FragmentUtil.add(mActivity.getSupportFragmentManager(),passwordFragment,R.id.flContainer);
                break;
        }
    }




}
