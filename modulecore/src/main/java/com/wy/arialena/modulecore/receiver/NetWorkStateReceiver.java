package com.wy.arialena.modulecore.receiver;
/**
 * 7.0的变化
 * Project Svelte：后台优化
 * Android 7.0 移除了三项隐式广播，以帮助优化内存使用和电量消耗。此项变更很有必要，因为隐式广播会在后台频繁启动已注册侦听这些广播的应用。
 * 删除这些广播可以显著提升设备性能和用户体验。
 * <p>
 * 移动设备会经历频繁的连接变更，例如在 WLAN 和移动数据之间切换时。目前，可以通过在应用清单中注册一个接收器来侦听隐式 CONNECTIVITY_ACTION 广播，
 * 让应用能够监控这些变更。由于很多应用会注册接收此广播，因此单次网络切换即会导致所有应用被唤醒并同时处理此广播。
 * <p>
 * 同理，在之前版本的 Android 中，应用可以注册接收来自其他应用（例如相机）的隐式 ACTION_NEW_PICTURE 和 ACTION_NEW_VIDEO 广播。
 * 当用户使用相机应用拍摄照片时，这些应用即会被唤醒以处理广播。
 * <p>
 * 为缓解这些问题，Android 7.0 应用了以下优化措施：
 * <p>
 * 面向 Android 7.0 开发的应用不会收到 CONNECTIVITY_ACTION 广播，即使它们已有清单条目来请求接受这些事件的通知。
 * 在前台运行的应用如果使用 BroadcastReceiver 请求接收通知，则仍可以在主线程中侦听 CONNECTIVITY_CHANGE。
 * 应用无法发送或接收 ACTION_NEW_PICTURE 或 ACTION_NEW_VIDEO 广播。此项优化会影响所有应用，而不仅仅是面向 Android 7.0 的应用。
 * 如果您的应用使用任何 intent，您仍需要尽快移除它们的依赖关系，以正确适配 Android 7.0 设备。
 * Android 框架提供多个解决方案来缓解对这些隐式广播的需求。
 * 例如，JobScheduler API 提供了一个稳健可靠的机制来安排满足指定条件（例如连入无限流量网络）时所执行的网络操作。您甚至可以使用 JobScheduler 来适应内容提供程序变化。
 * <p>
 * 如需了解有关 Android N 中后台优化以及如何改写应用的详细信息，请参阅后台优化。
 */

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.wy.arialena.modulecore.rxbus.NetChangeObser;
import com.wy.arialena.modulecore.rxbus.RxBus;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 检测API是不是小于21，因为到了API21之后getNetworkInfo(int networkType)方法被弃用
 * 动态注册
 * IntentFilter filter = new IntentFilter();
 * filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
 * registerReceiver(netWorkStateReceiver, filter);
 * 会出现一打开APP就提示网络已连接的提示，根据实际应用场景 blnFirst deal
 * <p>
 * android 7.0后开始禁止/屏蔽静态广播的注册了。
 * 故请使用动态注册广播的方式注册
 * <p>
 * 使用注意：若使用getNetworkInfo
 * 1：此处暂未处理API < 21
 * 2：请使用动态注册
 * 3：如果不想一打开App 注册广播就收到网络状态 callback 。可以尝试使用needFirst来判断。
 * 您有其他好的建议或使用方法，请多多交流，深表感激。
 * <p>
 * usage : 请先（在Application中）进行初始化
 */
public class NetWorkStateReceiver extends BroadcastReceiver {
    private String TAG = NetWorkStateReceiver.class.getSimpleName();
    private boolean needFirst = false;
    private int recvCount = 0;
    private Application context;

    private static class NetWorkStateReceiverHolder {
        private static final NetWorkStateReceiver INSTANCE = new NetWorkStateReceiver();
    }

    public static NetWorkStateReceiver getInstance() {
        return NetWorkStateReceiverHolder.INSTANCE;
    }

    /**
     * @param context              please use Application .otherwise maybe cause outofmemory
     * @param isNeedFirstNetChange
     * @return
     */
    public NetWorkStateReceiver init(Application context, boolean isNeedFirstNetChange) {
        this.context = context;
        this.needFirst = isNeedFirstNetChange;
        return this;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null){return;}
        recvCount++;
        Log.i(TAG, "网络onReceive：" + needFirst + " recvCount: " + recvCount);
        if (!needFirst && recvCount == 1) {
            return;
        }

        boolean blnConnected = false;
        NetChangeObser netChangeObser = null;
        String connectTypeName = "";
        int connectType = -1;
        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                blnConnected = true;
                connectType = ni.getType();//@Link eg: ConnectivityManager.TYPE_WIFI
                connectTypeName = ni.getTypeName();
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                blnConnected = false;
            }
        }
        Log.i(TAG, "网络state blnConnected:" + blnConnected + " connectType:" + connectType + " connectName:" + connectTypeName);
        if (netChangedCallback != null) {
            netChangedCallback.netChangedCallback(blnConnected, connectType, connectTypeName);
        }
        // if you want to RxBus post this netChangeObser
        netChangeObser = new NetChangeObser(blnConnected, connectType, connectTypeName);
        RxBus.getDefault().post(netChangeObser);

    }

    /**
     * 请先初始化配置
     * 在你的MainPresenter中注册网络服务
     * 若要回调，请注册后即添加netChangedCallback
     * @return
     */
    public NetWorkStateReceiver registerReceiver() {
        checkNotNull(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(this, filter);
        return this;
    }

    public void unRegisterReceiver() {
        checkNotNull(context);
        this.recvCount = 0;
        try {
            this.context.unregisterReceiver(this);
        }catch (Exception e){}
    }

    private NetChangedCallback netChangedCallback;

    public  interface NetChangedCallback {
        void netChangedCallback(boolean connect, int connectType, String connectName);
    }

    public NetWorkStateReceiver addNetChangedCallback(NetChangedCallback netChangedCallback) {
        this.netChangedCallback = netChangedCallback;
        return this;
    }
}
