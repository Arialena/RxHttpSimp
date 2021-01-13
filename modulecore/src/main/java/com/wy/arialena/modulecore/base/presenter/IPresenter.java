package com.wy.arialena.modulecore.base.presenter;

import com.wy.arialena.modulecore.base.IContractView;

/**
 * @author wuyan
 * @param <V>
 */
public interface IPresenter<V extends IContractView>{
    /**
     * init operation
     */
    void start();

    /**
     * Activity onDestroy()
     */
    void onDestroy();

    /**
     * presenter和对应的view绑定
     * @param mvpView  目标view
     */
    void attachView(V mvpView);

    /**
     * presenter与view解绑
     */
    void detachView();
}
