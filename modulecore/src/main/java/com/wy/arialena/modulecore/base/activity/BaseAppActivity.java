package com.wy.arialena.modulecore.base.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.wy.arialena.modulecore.base.ActivityLifecycleable;
import com.wy.arialena.modulecore.base.IBaseActivity;
import com.wy.arialena.modulecore.base.IContractView;
import com.wy.arialena.modulecore.base.presenter.BasePresenter;
import com.wy.arialena.modulecore.rxbus.NetChangeObser;
import com.wy.arialena.modulecore.rxbus.RxBus;
import com.wy.arialena.modulecore.utils.helper.ActivityManageHelper;
import com.wy.arialena.modulecore.utils.WeakHandler;
import com.wy.arialena.modulecore.utils.helper.StatusBarHelper;
import com.wy.arialena.modulecore.widget.LoadingDialog;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * @author wuyan
 */
public abstract class BaseAppActivity<P extends BasePresenter, V extends IContractView> extends RxAppCompatActivity implements ActivityLifecycleable, IBaseActivity<P>,IContractView {

    public Activity activity;
    public Context context;
    private Unbinder mUnbinder;
    protected CompositeDisposable mCompositeDisposable;
    private final BehaviorSubject<ActivityEvent> mLifecycleSubject = BehaviorSubject.create();
    //如果当前页面逻辑简单, Presenter 可以为 null
    protected P mPresenter;

    private LoadingDialog loadingDialog = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContent();
        setContentView(getLayoutId(savedInstanceState));
        activity = this;
        context = this;
        mUnbinder = ButterKnife.bind(this);
        ActivityManageHelper.getInstance().addActivity(this);

        mPresenter = initPresenter();
        initView(savedInstanceState);
        render();
        subscribeRxbusEvent();
    }

    /**
     * 默认禁用横屏。
     * 不需要时，请不要super
     */
    protected void beforeSetContent() {
        //全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//使用横屏
    }

    private void netChangedCallback(boolean connect, int connectType, String connectName) {
        if (connect) {
            netReConnected(connectType, connectName);
        } else {
            netDisConnected();
        }
    }

    @NonNull
    @Override
    public final Subject<ActivityEvent> provideLifecycleSubject() {
        return mLifecycleSubject;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public <T extends View> T findViewById(@IdRes int id) {
        return super.findViewById(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManageHelper.getInstance().finshActivity(this);
        unSubCribeRxbusEvent();
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
            mUnbinder.unbind();
        }
        this.mUnbinder = null;
        if (mPresenter != null) {
            mPresenter.onDestroy();//释放资源
            mPresenter.detachView();
            this.mPresenter = null;
        }
        //移除消息队列中所有消息和所有的Runnable
        if (weakHandler != null) {
            weakHandler.removeCallbacksAndMessages(null);
            weakHandler = null;
        }
    }

    /**
     * Events Message Queue
     *
     * @param msg
     */
    protected void onHandleMessage(final Message msg) {

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
     * 订阅rxbus事件总线
     * 使用compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))方法指定在onDestroy方法被调用时取消订阅
     * 注意compose方法需要在subscribeOn方法之后使用 @Link https://www.jianshu.com/p/7fae42861b8d
     */
    private void subscribeRxbusEvent() {
        unSubCribeRxbusEvent();
        Disposable mDisposable = RxBus.getDefault().toObservable(Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(object -> {
                    // do recv events
                    recvRxEvents(object);
                    if (object instanceof NetChangeObser) {
                        NetChangeObser netChangeObser = (NetChangeObser) object;
                        netChangedCallback(netChangeObser.connect, netChangeObser.connectType, netChangeObser.connectTypeName);
                    }
                }, throwable -> {
                    //ERROR 常规的Rxbus发生错误后，会取消订阅。但此时的Rxbus基于jakson的，避免了这一问题
                });
        addDispose(mDisposable);
    }
    /**
     * 解除rxbus订阅事件
     * 取消网络访问
     */
    protected void unSubCribeRxbusEvent() {
        unDispose();
    }

    public void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);//将所有 Disposable 放入集中处理
    }

    /**
     * 停止集合中正在执行的 RxJava 任务
     */
    protected void unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();//保证 Activity 结束时取消所有正在执行的订阅
        }
    }

    /**
     * root view
     *
     * @return
     */
    public View getRootView() {
        return activity.getWindow().getDecorView().findViewById(android.R.id.content);
    }


    // ------------   Dialog start ----------------

    /**
     * 显示正在Loading Dialog
     *
     * @param loadingTips
     */
    @Override
    public void showLoading(boolean cancleable,String loadingTips) {
        dismissLoading();
        loadingDialog = new LoadingDialog.Builder(context)
                .setTips(loadingTips)
                .setCancelable(cancleable)
                .setCanceledOnTouchOutside(cancleable)
                .create();
        loadingDialog.show();
    }

    /**
     * dis SmileLoading Dialog
     */
    @Override
    public void dismissLoading() {
        if (loadingDialog != null && loadingDialog.isShowing() && !activity.isFinishing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * @param statuTips
     */
    public void showStatuTips(String statuTips) {
        if (loadingDialog != null && !TextUtils.isEmpty(statuTips)) {
            loadingDialog.setStatuTip(statuTips);
        }
    }

    /**
     * @return
     */
    public LoadingDialog getLoadingDialog() {
        if (loadingDialog == null) {
            throw new RuntimeException("you yet show smileLoading Dialog ! ");
        }
        return loadingDialog;
    }
    // ------------   Dialog end  ----------------


    // ------------ 状态栏 start ------------------------
    public void setTransparentForWindow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置状态栏颜色
     *
     * @param statuBarColor  状态栏颜色
     * @param statusBarAlpha 状态栏颜色透明度
     * @param useDart        图标文字是否使用深色调
     */
    protected void setStatuBarStyle(@ColorRes int statuBarColor, @IntRange(from = 0, to = 255) int statusBarAlpha, boolean useDart) {
        StatusBarHelper.setColor(this, ContextCompat.getColor(this, statuBarColor), statusBarAlpha);
        StatusBarHelper.setStatusTextColor(useDart, this);
    }
    // ---------- 状态栏 end ----------------------------
}
