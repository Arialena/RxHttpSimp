package com.wy.arialena.modulecore.http;

/**
 * @author wuyan
 */
public class ApiHelper {
    public ApiHelper() {

    }

    private static class ApiHelperHolder {
        private static final ApiHelper INSTANCE = new ApiHelper();
    }

    public static ApiHelper getInstance() {
        return ApiHelperHolder.INSTANCE;
    }

    public  <T> T getAPIService(Class<T> T) {
        T apiSerVice = RetrofitOkhttpClient.getInstance().createApi(T);
        return apiSerVice;
    }
}
