package com.jytc.rxhttpsimp;

import com.jytc.rxhttpsimp.net.model.BaseJsonModel;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface AppApiService {
    @POST("")
    Observable<BaseJsonModel> testHttpRequest(@Body Map<String, Object> body);

    @FormUrlEncoded
    @POST("")
    Observable<BaseJsonModel> testHttpRequest2(@FieldMap Map<String, Object> body);

    @FormUrlEncoded
    Observable<BaseJsonModel> testHttpRequest3(@QueryMap Map<String, Object> body);

    @FormUrlEncoded
    Observable<BaseJsonModel> testHttpRequest4(@Query("str") String str);
}
