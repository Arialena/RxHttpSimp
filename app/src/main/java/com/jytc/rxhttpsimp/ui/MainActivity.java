package com.jytc.rxhttpsimp.ui;

import androidx.annotation.Nullable;

import android.os.Bundle;

import com.jytc.rxhttpsimp.R;
import com.wy.arialena.modulecore.base.activity.BaseActivity;

public class MainActivity extends BaseActivity<MainPresenter,MainContract.View> implements MainContract.View {


    @Override
    public int getLayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public MainPresenter initPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void render() {

    }

    @Override
    public void recvRxEvents(Object rxPostEvent) {

    }

    @Override
    public void netReConnected(int connectType, String connectName) {

    }

    @Override
    public void netDisConnected() {

    }

    @Override
    public void successOrFail(int code, String msg) {
        // todo  http返回数据view数据展示处理
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}