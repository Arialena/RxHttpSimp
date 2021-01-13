package com.wy.arialena.modulecore.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.wy.arialena.modulecore.R;
import com.wang.avi.AVLoadingIndicatorView;

import androidx.core.content.ContextCompat;

public class LoadingDialog extends AlertDialog {

    private AVLoadingIndicatorView avi;
    private TextView contentText;
    private String text = "";
    private boolean clickCancelable = true;
    private boolean CanceledOnTouchOutside = true;
    private int textColor = R.color.whiteColor;

    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context,themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_loading);
        avi = (AVLoadingIndicatorView)this.findViewById(R.id.avi);
        contentText = this.findViewById(R.id.id_tv_loadingmsg);
    }

    public LoadingDialog setStatuTip(String statuTip) {
        contentText.setVisibility(View.VISIBLE);
        contentText.setText(statuTip);
        return this;
    }

    @Override
    public void show() {
        super.show();
        show(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
       if (avi != null){
           avi.hide();
           avi = null;
       }
//        avi.hide();
    }

    private void show(LoadingDialog mLoadingDialog){
        if (!TextUtils.isEmpty(text)){
            mLoadingDialog.contentText.setText(text);
            mLoadingDialog.contentText.setVisibility(View.VISIBLE);
        }else {
            mLoadingDialog.contentText.setVisibility(View.GONE);
        }
        if (mLoadingDialog.textColor > 0){
            mLoadingDialog.contentText.setTextColor(ContextCompat.getColor(getContext(), mLoadingDialog.textColor));
        }else {
            mLoadingDialog.contentText.setTextColor(ContextCompat.getColor(getContext(), textColor));
        }
        mLoadingDialog.setCancelable(mLoadingDialog.clickCancelable);
        mLoadingDialog.setCanceledOnTouchOutside(mLoadingDialog.CanceledOnTouchOutside);
        if (mLoadingDialog.avi != null){
            mLoadingDialog.avi.show();
        }
    }

    public static class Builder {
        private LoadingDialog mLoadingDialog;

        public Builder(Context context) {
            mLoadingDialog = new LoadingDialog(context,R.style.TransparentDialog);
        }

        /**
         * 设置提示
         *
         * @param tips
         * @return
         */
        public Builder setTips(String tips) {
            mLoadingDialog.text = tips;
            return this;
        }

        /**
         * 设置提示字体
         *
         * @param tipsColor
         * @return
         */
        public Builder setTipsColor(int tipsColor) {
            mLoadingDialog.textColor = tipsColor;
            return this;
        }

        /**
         * 设置是否可以点击返回键取消
         *
         * @param cancelable
         * @return
         */
        public Builder setCancelable(boolean cancelable) {
            mLoadingDialog.clickCancelable = cancelable;
            return this;
        }

        /**
         * 设置是否可以点Touch外部取消
         *
         * @param CanceledOnTouchOutside
         * @return
         */
        public Builder setCanceledOnTouchOutside(boolean CanceledOnTouchOutside) {
            mLoadingDialog.CanceledOnTouchOutside = CanceledOnTouchOutside;
            return this;
        }


        /**
         * 通过Builder类设置完属性后构造对话框的方法
         */
        public LoadingDialog create() {
            return mLoadingDialog;
        }
    }
}
