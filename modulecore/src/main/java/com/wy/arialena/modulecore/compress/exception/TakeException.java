package com.wy.arialena.modulecore.compress.exception;


/**
 * @author : wuyan
 * createDate   : 2019-10-1713:15
 * desc   :
 */
public class TakeException extends Exception {
    private @TakeExceptionType
    int exceptopnType;


    public TakeException(@TakeExceptionType int takeExceptionType, String msg) {
        super(msg);
        this.exceptopnType = takeExceptionType;
    }

    public int getExceptopnType() {
        return exceptopnType;
    }
}
