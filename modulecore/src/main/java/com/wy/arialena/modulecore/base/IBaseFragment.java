package com.wy.arialena.modulecore.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author wuyan
 * @param <P>
 */
public interface IBaseFragment<P> {
    /**
     * 布局ID
     * @return
     */
    int getLayoutId();

    /**
     * 创建prensenter
     *
     * @return <T extends BasePresenter> 必须是BasePresenter的子类
     */
    P initPresenter();

    /**
     *
     * @param rootView
     * @param savedInstanceState
     */
    void initView(View rootView, @Nullable Bundle savedInstanceState);

    /**
     * render data and UI
     */
    void render();

    /**
     * getClass().getSimpleName() + "  对用户第一次可见"
     */
    void onFragmentFirstVisible() ;


    /**
     * getClass().getSimpleName() + "  对用户可见"
     */
    void onFragmentResume() ;


    /**
     * getClass().getSimpleName() + "  对用户不可见"
     */
    void onFragmentPause();

    /**
     * recv Rxbus events
     *
     * @param rxPostEvent
     */
    void recvRxEvents(Object rxPostEvent);

    /**
     * @param connectType
     * @param connectName
     */
    void netReConnected(int connectType, String connectName);

    /**
     * Net Disconnect
     */
    void netDisConnected();
}
