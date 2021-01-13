package com.wy.arialena.modulecore.utils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.HttpException;

import static com.wy.arialena.modulecore.utils.ApiException.HTTPCODE.NOT_FOUND;

/**
 * @author wuyan
 */
public class ApiException extends Exception {
    private int code;
    private String message;


    public ApiException(Throwable throwable, int code){
        super(throwable);
        this.message = throwable.getMessage();
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public static ApiException handleException(Throwable e){
        ApiException ex;
        if (e instanceof HttpException){
            HttpException httpException = (HttpException) e;
            ex = new ApiException(httpException,httpException.code());
            try {
                switch (httpException.code()){
                    case 404:
                        ex.message = "连接不上服务器，稍后重试！code:404";
                        break;
                    default:
                        ex.message = httpException.response().errorBody().toString();
                        break;
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }
//            try {
//                ResponseBody responseBody = httpException.response().errorBody();
//                String json = responseBody.string();
//                JSONObject object = new JSONObject(json);
//                if (object != null && object.has("message") && object.has("code")){
//                    String message = object.getString("message");
//                    int code = object.getInt("code");
//                    ex.code = code;
//                    switch (code){
//                        case THROWABLE_ERROR:
//                        case PARAMETER_ERROR:
//                        case REMOTE_LOGIN:
//                            ex.message = message;
//                            break;
//                        case SERVICE_ERROR:
//                            ex.message = message + code;
//                            break;
//                        case NOT_FOUND:
//                            ex.message = "服务器内部错误";
//                            ex.code = NOT_FOUND;
//                            break;
//                            default:
//                                ex.message = message;
//                            break;
//                    }
//                }else {
//                    switch (httpException.code()){
//                        case SERVICE_ERROR:
//                            ex.message = "服务器内部错误,code:500";
//                            break;
//                        case NOT_FOUND:
//                            ex.message = "连接不上服务器，稍后重试！code:404";
//                            break;
//                        default:
//                            ex.message = httpException.response().body().toString();
//                            break;
//                    }
//                }
//            }catch (Exception e1){
//                e.printStackTrace();
//                ex.message =e.getMessage();
//            }
            return ex;
        }else if (e instanceof SocketTimeoutException){
            ex = new ApiException(e,NOT_FOUND);
            ex.message = "网络连接超时，请检查您的网络状态，稍后重试！";
            return ex;
        }else if (e instanceof ConnectException){
            ex = new ApiException(e,NOT_FOUND);
            ex.message = "网络连接异常，请检查您的网络状态，稍后重试！";
            return ex;
        }else if (e instanceof ConnectTimeoutException){
            ConnectTimeoutException timeoutException = (ConnectTimeoutException) e;
            ex = new ApiException(e,timeoutException.hashCode());
            ex.message = "网络连接超时，请检查您的网络状态，稍后重试！"+ ex.code;
            return ex;
        }else if (e instanceof UnknownHostException){
            UnknownHostException hostException = (UnknownHostException) e;
            ex = new ApiException(e,hostException.hashCode());
            ex.message = "连接不上服务器，稍后重试！" + ex.code;
            return ex;
        }else if (e instanceof NullPointerException){
            NullPointerException pointerException = (NullPointerException) e;
            ex = new ApiException(e,pointerException.hashCode());
            ex.message = "空指针异常";
            return ex;
        }else if (e instanceof SSLHandshakeException){
            SSLHandshakeException handshakeException = (SSLHandshakeException) e;
            ex = new ApiException(e,handshakeException.hashCode());
            ex.message = "证书验证失败";
            return ex;
        }else if (e instanceof ClassCastException){
            ClassCastException castException = (ClassCastException) e;
            ex = new ApiException(e,castException.hashCode());
            ex.message = "类型转换错误";
            return ex;
        }else if (e instanceof JsonParseException || e instanceof JSONException
                || e instanceof JsonSyntaxException || e instanceof JsonSerializer
                || e instanceof NotSerializableException || e instanceof ParseException){
            ex = new ApiException(e,1005);
            ex.message = "解析错误";
            return ex;
        }else if (e instanceof IllegalStateException){
            IllegalStateException stateException = (IllegalStateException) e;
            ex = new ApiException(e,stateException.hashCode());
            ex.message = e.getMessage();
            return ex;
        }else {
            ex = new ApiException(e,e.hashCode());
            ex.message = "网络连接异常，请检查网络是否畅通";
            return ex;
        }
    }

    /**
     * 约定异常
     */
    public static class HTTPCODE {

        /**
         * 成功
         */
        public static final int SUCCESS = 200;
        /**
         * 登录失败
         */
        public static final int LOGIN_FAIL = 199;
        /**
         * 用户信息错误
         */
        public static final int USER_INFO_ERROR = 104;

        /**
         * 定义错误
         */
        public static final int PARAMETER_ERROR = 400;
        public static final int THROWABLE_ERROR = -1;
        public static final int SERVICE_ERROR = 500;

        public static final int NOT_FOUND = 404;
    }
}
