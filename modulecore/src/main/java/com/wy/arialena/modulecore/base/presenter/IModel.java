package com.wy.arialena.modulecore.base.presenter;

/**
 * @author wuayn
 */
public interface IModel {
    /**
     * 在框架中 {@link BasePresenter#onDestroy()} 时会默认调用 {@link IModel#onDestroy()}
     */
    void onDestroy();
}
