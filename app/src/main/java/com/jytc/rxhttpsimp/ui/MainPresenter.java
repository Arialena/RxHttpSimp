package com.jytc.rxhttpsimp.ui;

import com.jytc.rxhttpsimp.AppApiService;
import com.jytc.rxhttpsimp.net.model.BaseJsonModel;
import com.wy.arialena.modulecore.base.presenter.BasePresenter;
import com.wy.arialena.modulecore.http.ApiHelper;
import com.wy.arialena.modulecore.http.RxSchedulersHelper;
import com.wy.arialena.modulecore.utils.ApiException;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;

public class MainPresenter extends BasePresenter<MainContract.Presenter,MainContract.View>
        implements MainContract.Presenter{

    public MainPresenter(MainContract.View contractView) {
        super(contractView);
    }

    @Override
    public void start() {

    }

    @Override
    public void testHttp() {

        Map<String, Object> map = new HashMap<>();
        Disposable disposable = ApiHelper.getInstance().getAPIService(AppApiService.class)
                .testHttpRequest(map)
                .compose(RxSchedulersHelper.io_main())
                .subscribe(baseJsonModel-> {
                    //todo 进行自己需要的逻辑操作
                    mContractView.successOrFail(baseJsonModel.getCode(),baseJsonModel.getMessage());
                },throwable -> {
                    //todo  异常处理
                    ApiException exception = ApiException.handleException(throwable);//可自己封装异常工具
                    mContractView.successOrFail(exception.getCode(),exception.getMessage());
                });
        addDispose(disposable);

    }
}
