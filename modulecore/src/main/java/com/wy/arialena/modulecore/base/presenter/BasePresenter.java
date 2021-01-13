package com.wy.arialena.modulecore.base.presenter;

import android.app.Service;
import android.os.Message;

import com.wy.arialena.modulecore.base.IContractView;
import com.wy.arialena.modulecore.utils.WeakHandler;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import com.google.common.base.Preconditions;

/**
 * @author wuyan
 * @param <M>
 * @param <V>
 */
public abstract class BasePresenter<M extends IModel, V extends IContractView> implements IPresenter<V>, LifecycleObserver {

    protected final String TAG = this.getClass().getSimpleName();
    protected CompositeDisposable mCompositeDisposable;
    protected M mModel;
    protected V mContractView;

    /**
     * 如果当前页面同时需要 Model 层和 View 层,则使用此构造函数(默认)
     *
     * @param model
     * @param contractView
     */

    public BasePresenter(M model, V contractView) {
        Preconditions.checkNotNull(model, "%s cannot be null", IModel.class.getName());
        Preconditions.checkNotNull(contractView, "%s cannot be null", IContractView.class.getName());
        this.mModel = model;
        this.mContractView = contractView;
        regLifecycleObserver();

    }

    /**
     * 如果当前页面不需要操作数据,只需要 View 层,则使用此构造函数
     *
     * @param contractView
     */

    public BasePresenter(V contractView) {
        Preconditions.checkNotNull(contractView, "%s cannot be null", IContractView.class.getName());
        this.mContractView = contractView;
        regLifecycleObserver();

    }


    @Override
    public  abstract  void start();

    /**
     * 将 LifecycleObserver 注册给 LifecycleOwner 后 @OnLifecycleEvent 才可以正常使用
     */
    private void regLifecycleObserver() {
        if (mContractView != null && mContractView instanceof LifecycleOwner) {
            ((LifecycleOwner) mContractView).getLifecycle().addObserver(this);
            if (mModel != null && mModel instanceof LifecycleObserver) {
                ((LifecycleOwner) mContractView).getLifecycle().addObserver((LifecycleObserver) mModel);
            }
        }
    }

    /**
     * 在框架中 { Activity#onDestroy()} 时会默认调用 {@link IPresenter#onDestroy()}
     */
    @Override
    public void onDestroy() {
        unDispose();//解除订阅
        if (mModel != null) {
            mModel.onDestroy();
        }
        this.mModel = null;
        this.mContractView = null;
        this.mCompositeDisposable = null;
        weakHandler.removeCallbacksAndMessages(null);
        weakHandler=null;
    }

    @Override
    public void attachView(V mvpView) {
        this.mContractView = mvpView;
    }

    @Override
    public void detachView() {
        mContractView = null;
    }

    /**
     * 判断 view是否为空
     *
     * @return
     */
    public boolean isAttachView() {
        return mContractView != null;
    }

    /**
     * 检查view和presenter是否连接
     */
    public void checkViewAttach() {
        if (!isAttachView()) {
            throw new MvpViewNotAttachedException();
        }
    }

    /**
     * 自定义异常
     */
    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("请求数据前请先调用 attachView(MvpView) 方法与View建立连接");
        }
    }

    /**
     * 只有当 {@code mRootView} 不为 null, 并且 {@code mRootView} 实现了 {@link LifecycleOwner} 时, 此方法才会被调用
     * 所以当您想在 {@link Service} 以及一些自定义 { View} 或自定义类中使用 {@code Presenter} 时
     * 您也将不能继续使用 {@link OnLifecycleEvent} 绑定生命周期
     *
     * @param owner link { SupportActivity} and {@link Fragment}
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(LifecycleOwner owner) {
        /**
         * 注意, 如果在这里调用了 {@link #onDestroy()} 方法, 会出现某些地方引用 {@code mModel} 或 {@code mRootView} 为 null 的情况
         * 比如在 {@link RxLifecycle} 终止 {@link Observable} 时, 在 {@link io.reactivex.Observable#doFinally(Action)} 中却引用了 {@code mRootView} 做一些释放资源的操作, 此时会空指针
         * 或者如果你声明了多个 @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY) 时在其他 @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
         * 中引用了 {@code mModel} 或 {@code mRootView} 也可能会出现此情况
         */
        owner.getLifecycle().removeObserver(this);
    }


    /**
     * 将 {@link Disposable} 添加到 {@link CompositeDisposable} 中统一管理
     * 可在 { Activity#onDestroy()} 中使用 {@link #unDispose()} 停止正在执行的 RxJava 任务,避免内存泄漏
     * 目前框架已使用 {  RxLifecycle} 避免内存泄漏,此方法作为备用方案
     * @param disposable
     */

    public void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);//将所有 Disposable 放入集中处理
    }

    /**
     * 停止集合中正在执行的 RxJava 任务
     */
    public void unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();//保证 Activity 结束时取消所有正在执行的订阅
        }
    }

    /**
     * Avoid memory leaks
     * WeakHandler
     */
    public WeakHandler weakHandler = new WeakHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            onHandleMessage(msg);
        }
    };

    /**
     * Events Message Queue
     * if you want to use weakhandler to deal message
     * please override onHandleMessage to del msg
     * @param msg
     */
    protected void onHandleMessage(final Message msg) {

    }

}
