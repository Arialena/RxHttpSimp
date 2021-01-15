package com.jytc.rxhttpsimp;

import com.wy.arialena.modulecore.base.BaseApplication;
import com.wy.arialena.modulecore.http.RetrofitOkhttpClient;
import com.wy.arialena.modulecore.update_app.utils.LogUtil;
import com.wy.arialena.modulecore.utils.AppInfoUtil;

import static com.jytc.rxhttpsimp.net.ApiUrlConfig.BASR_URL;

public class MAppliaction extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.init(this);

        //初始化HTTP请求
        RetrofitOkhttpClient.init(this);
        RetrofitOkhttpClient.getInstance().setBaseUrl(BASR_URL)
                .addLogInterceptor(true, AppInfoUtil.getVerName(this))
                .configGsonConverterFactory();
        registerActivityLifecy();
    }
}
