package com.wy.arialena.modulecore.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * @author wuyan
 * @param <P>
 */
public interface IBaseActivity<P>{
    /**
     * 初始化 View, 如果 {@link #getLayoutId(Bundle)} 返回 0, 则不会调用 {@link android.app.Activity#setContentView(int)}
     * @param savedInstanceState
     * @return 布局layout ID ,The layout id that's gonna be the activity view.
     */
    int getLayoutId(@Nullable Bundle savedInstanceState);

    /**
     * 创建prensenter
     * @return <T extends BasePresenter> 必须是BasePresenter的子类
     */
    P initPresenter();


    /**
     * 初始化 View 视图组件
     * 如：列表的adapter初始化
     *     manager视图初始化
     *
     * @param savedInstanceState
     */
    void initView(@Nullable Bundle savedInstanceState);

    /**
     * 渲染静态数据
     */
    void render();

    /**
     * recv Rxbus events
     * 接收Rxbus消息总线分发
     * @param rxPostEvent
     */
    void recvRxEvents(Object rxPostEvent);

    /**
     * 网络已连接
     * @param connectType
     * @param connectName
     */
    void netReConnected(int connectType, String connectName);

    /**
     * 网络断开
     */
    void netDisConnected();
}
