package com.wy.arialena.modulecore.base;

/**
 * @author wuyan
 * @param <T>
 */
public interface IContractView<T> {
    /**
     * show Loading
     */
    void showLoading(boolean cancleable,String loadingTips);

    /**
     * dismiss Loading
     */
    void dismissLoading();
}
