package com.wy.arialena.modulecore.base.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wy.arialena.modulecore.base.FragmentLifecycleable;
import com.wy.arialena.modulecore.base.IBaseFragment;
import com.wy.arialena.modulecore.base.IContractView;
import com.wy.arialena.modulecore.base.presenter.BasePresenter;
import com.wy.arialena.modulecore.rxbus.NetChangeObser;
import com.wy.arialena.modulecore.rxbus.RxBus;
import com.wy.arialena.modulecore.utils.WeakHandler;
import com.wy.arialena.modulecore.widget.LoadingDialog;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.List;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
 * @param <P>
 * @param <V>
 */
public abstract class BaseLazyLoadFragment<P extends BasePresenter, V extends IContractView> extends RxFragment
        implements FragmentLifecycleable, IBaseFragment<P>,IContractView {
    private String TAG = BaseLazyLoadFragment.class.getSimpleName();
    protected View rootView = null;
    private boolean mIsFirstVisible = true;
    private boolean isViewCreated = false;
    private boolean currentVisibleState = false;
    public Context context;
    public Activity activity;
    private Unbinder unbinder;
    protected CompositeDisposable mCompositeDisposable;
    //如果当前页面逻辑简单, Presenter 可以为 null
    protected P mPresenter;
    private LoadingDialog loadingDialog = null;

    private final BehaviorSubject<FragmentEvent> mLifecycleSubject = BehaviorSubject.create();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);
        }
        context = getContext();
        activity = getActivity();
        unbinder = ButterKnife.bind(this, rootView);
        mPresenter = initPresenter();
        initView(rootView, savedInstanceState);
        subscribeRxbusEvent();
        render();
        return rootView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 对于默认 tab 和 间隔 checked tab 需要等到 isViewCreated = true 后才可以通过此通知用户可见
        // 这种情况下第一次可见不是在这里通知 因为 isViewCreated = false 成立,等从别的界面回到这里后会使用 onFragmentResume 通知可见
        // 对于非默认 tab mIsFirstVisible = true 会一直保持到选择则这个 tab 的时候，因为在 onActivityCreated 会返回 false
        if (isViewCreated) {
            if (isVisibleToUser && !currentVisibleState) {
                dispatchUserVisibleHint(true);
            } else if (!isVisibleToUser && currentVisibleState) {
                dispatchUserVisibleHint(false);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        isViewCreated = true;
        // !isHidden() 默认为 true  在调用 hide show 的时候可以使用
        if (!isHidden() && getUserVisibleHint()) {
            // 这里的限制只能限制 A - > B 两层嵌套
            dispatchUserVisibleHint(true);
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(TAG,getClass().getSimpleName() + "  onHiddenChanged dispatchChildVisibleState  hidden " + hidden);

        if (hidden) {
            dispatchUserVisibleHint(false);
        } else {
            dispatchUserVisibleHint(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!mIsFirstVisible) {
            if (!isHidden() && !currentVisibleState && getUserVisibleHint()) {
                dispatchUserVisibleHint(true);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 当前 Fragment 包含子 Fragment 的时候 dispatchUserVisibleHint 内部本身就会通知子 Fragment 不可见
        // 子 fragment 走到这里的时候自身又会调用一遍 ？
        if (currentVisibleState && getUserVisibleHint()) {
            dispatchUserVisibleHint(false);
        }
    }

    private boolean isFragmentVisible(Fragment fragment) {
        return !fragment.isHidden() && fragment.getUserVisibleHint();
    }


    /**
     * 统一处理 显示隐藏
     *
     * @param visible
     */
    private void dispatchUserVisibleHint(boolean visible) {
        //当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment getUserVisibleHint = true
        //但当父 fragment 不可见所以 currentVisibleState = false 直接 return 掉
        //LogUtils.e(getClass().getSimpleName() + "  dispatchUserVisibleHint isParentInvisible() " + isParentInvisible());
        // 这里限制则可以限制多层嵌套的时候子 Fragment 的分发
        if (visible && isParentInvisible()) {
            return;
        }

        //此处是对子 Fragment 不可见的限制，因为 子 Fragment 先于父 Fragment回调本方法 currentVisibleState 置位 false
        // 当父 dispatchChildVisibleState 的时候第二次回调本方法 visible = false 所以此处 visible 将直接返回
        if (currentVisibleState == visible) {
            return;
        }

        currentVisibleState = visible;

        if (visible) {
            if (mIsFirstVisible) {
                mIsFirstVisible = false;
                onFragmentFirstVisible();
            }
            onFragmentResume();
            dispatchChildVisibleState(true);
        } else {
            dispatchChildVisibleState(false);
            onFragmentPause();
        }
    }

    /**
     * 用于分发可见时间的时候父获取 fragment 是否隐藏
     *
     * @return true fragment 不可见， false 父 fragment 可见
     */
    private boolean isParentInvisible() {
        BaseLazyLoadFragment fragment = (BaseLazyLoadFragment) getParentFragment();
        return fragment != null && !fragment.isSupportVisible();

    }

    private boolean isSupportVisible() {
        return currentVisibleState;
    }

    /**
     * 当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment 的唯一或者嵌套 VP 的第一 fragment 时 getUserVisibleHint = true
     * 但是由于父 Fragment 还进入可见状态所以自身也是不可见的， 这个方法可以存在是因为庆幸的是 父 fragment 的生命周期回调总是先于子 Fragment
     * 所以在父 fragment 设置完成当前不可见状态后，需要通知子 Fragment 我不可见，你也不可见，
     * <p>
     * 因为 dispatchUserVisibleHint 中判断了 isParentInvisible 所以当 子 fragment 走到了 onActivityCreated 的时候直接 return 掉了
     * <p>
     * 当真正的外部 Fragment 可见的时候，走 setVisibleHint (VP 中)或者 onActivityCreated (hide show) 的时候
     * 从对应的生命周期入口调用 dispatchChildVisibleState 通知子 Fragment 可见状态
     *
     * @param visible
     */
    private void dispatchChildVisibleState(boolean visible) {
        FragmentManager childFragmentManager = getChildFragmentManager();
        List<Fragment> fragments = childFragmentManager.getFragments();
        if (!fragments.isEmpty()) {
            for (Fragment child : fragments) {
                if (child instanceof BaseLazyLoadFragment && !child.isHidden() && child.getUserVisibleHint()) {
                    ((BaseLazyLoadFragment) child).dispatchUserVisibleHint(visible);
                }
            }
        }
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
    public void unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();//保证 Activity 结束时取消所有正在执行的订阅
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        mIsFirstVisible = true;
        if (unbinder != null && unbinder != Unbinder.EMPTY) {
            unbinder.unbind();
        }
        unbinder = null;
        unSubCribeRxbusEvent();
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
     * @param id
     * @param <VT>
     * @return
     */
    protected <VT extends View> VT findViewById(int id) {
        if (rootView == null) {
            throw new NullPointerException("Fragment content view is null.");
        }
        VT view = (VT) rootView.findViewById(id);
        if (view == null) {
            throw new NullPointerException("This resource id is invalid.");
        }
        return view;
    }


    /**
     * Fragment中初始化Toolbar
     *
     * @param toolbar
     * @param title             标题
     * @param isDisplayHomeAsUp 是否显示返回箭头
     */
    public void initFragmentToolbar(Toolbar toolbar, String title, boolean isDisplayHomeAsUp) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(isDisplayHomeAsUp);
        }
    }

    /**
     * Fragment中初始化Toolbar
     *
     * @param toolbar
     * @param title             标题
     * @param isDisplayHomeAsUp 是否显示返回箭头
     *                          Note:请不要使用 //appCompatActivity.setSupportActionBar(toolbar);
     */
    public void initFragmentToolbar(Toolbar toolbar, @MenuRes int menu, String title, boolean isDisplayHomeAsUp) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        toolbar.inflateMenu(menu);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(isDisplayHomeAsUp);
        }
    }

    /**
     * 订阅rxbus事件总线
     * 使用compose(this.<Long>bindUntilEvent(FragmentEvent.DESTROY))方法指定在onDestroy方法被调用时取消订阅
     * 注意compose方法需要在subscribeOn方法之后使用 @Link https://www.jianshu.com/p/7fae42861b8d
     */
    private void subscribeRxbusEvent() {
        unSubCribeRxbusEvent();
        Disposable mDisposable = RxBus.getDefault().toObservable(Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY))
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
     * @解除rxbus订阅事件
     * @取消网络访问
     */
    private void unSubCribeRxbusEvent() {
        unDispose();
    }

    private void netChangedCallback(boolean connect, int connectType, String connectName) {
        if (connect) {
            netReConnected(connectType, connectName);
        } else {
            netDisConnected();
        }
    }

    /**
     * 绑定生命周期
     *
     * @param <T>
     * @return
     */
    public <T> LifecycleTransformer<T> bindToLife() {
        return bindUntilEvent(FragmentEvent.DESTROY);
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



    // ------------   Dialog start ----------------

    /**
     * 显示正在Loading Dialog
     *@param cancleable
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
            loadingDialog = null;
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

    @NonNull
    @Override
    public Subject<FragmentEvent> provideLifecycleSubject() {
        return mLifecycleSubject;
    }
}
