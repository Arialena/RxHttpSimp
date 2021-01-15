package com.jytc.rxhttpsimp.ui;

import com.wy.arialena.modulecore.base.IContractView;
import com.wy.arialena.modulecore.base.presenter.IModel;

public interface MainContract {

    interface View extends IContractView{
        void successOrFail(int code,String msg);
    }
    interface Presenter extends IModel{
        void testHttp();
    }
}
