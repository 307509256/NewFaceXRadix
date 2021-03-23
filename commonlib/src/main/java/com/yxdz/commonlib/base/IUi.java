package com.yxdz.commonlib.base;

import android.os.Bundle;

/**
 * @ClassName: IUi.
 * @Desription: 用于规范性编程，界面的接口化编程
 * @author: Dreamcoding
 * @date: 2017/3/4 0004 22:03.
 */

public interface IUi {
    /** 用于获取activity或fragmen的布局文件 */
    int getLayoutRes() ;

    /** 用于初始话监听 */
    void onModel() ;

    void onData( Bundle savedInstanceState);


}
