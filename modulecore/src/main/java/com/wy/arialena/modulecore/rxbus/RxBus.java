package com.wy.arialena.modulecore.rxbus;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.Observable;

/**
 * @author wuyan
 */
public class RxBus {
    private static volatile RxBus instance;
    /**
     * 主题
     */
    private final Relay<Object> mBus;

    /**
     * PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
     */
    private RxBus() {
        this.mBus = PublishRelay.create().toSerialized();
    }

    /**
     * 单列
     *
     * @return
     */
    public static RxBus getDefault() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = Holder.BUS;
                }
            }
        }
        return instance;
    }

    /**
     * 提供了一个新的事件
     * 发布
     *
     * @param obj
     */
    public void post(Object obj) {
        mBus.accept(obj);
    }

    public <T> Observable<T> toObservable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    public Observable<Object> toObservable() {
        return mBus;
    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }

    private static class Holder {
        private static final RxBus BUS = new RxBus();
    }
}
