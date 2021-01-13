package com.wy.arialena.modulecore.http.requestCallback;

import com.wy.arialena.modulecore.utils.ApiException;

/**
 * @author wuyan
 * @param <T>
 */
public interface HttpRequestCallback<T> {
    void requestSuccess(T t);

    void requestError(ApiException apiException);
}
