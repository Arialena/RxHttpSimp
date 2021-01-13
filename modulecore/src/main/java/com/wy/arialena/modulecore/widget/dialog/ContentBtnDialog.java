package com.wy.arialena.modulecore.widget.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.wy.arialena.modulecore.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ContentBtnDialog extends DialogFragment {
    private TextView message;
    private TextView title;
    private Button cancel;
    private Button sure;
    private CallBack callBack;
    String titlStre="温馨提示";
    String msgStr = "请开启存储权限，及安装权限，否则无法正常下载升级哦";
    String cancelStr = "取消";
    String sureStr = "确定";

    public ContentBtnDialog(CallBack callBack){
        this.callBack = callBack;
    }

    public ContentBtnDialog( String msgStr, CallBack callBack){
        this.callBack = callBack;
        this.msgStr = msgStr;
    }
    public ContentBtnDialog(String msgStr, String cancelStr, String sureStr,CallBack callBack){
        this.callBack = callBack;
        this.msgStr = msgStr;
        this.cancelStr = cancelStr;
        this.sureStr = sureStr;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_button_dialog, container, false);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        if (win != null) {
            // 一定要设置Background，如果不设置，window属性设置无效
            win.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

            WindowManager.LayoutParams params = win.getAttributes();
            params.gravity = Gravity.CENTER;
            // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
//        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;// (int) (dm.widthPixels * 0.8);
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            win.setAttributes(params);
        }
    }

    private void initView(View view) {
        message = view.findViewById(R.id.context_text);
        title = view.findViewById(R.id.title_text);
        cancel = view.findViewById(R.id.cancle_btn);
        sure = view.findViewById(R.id.sure_btn);
        title.setText(titlStre);
        message.setText(msgStr);
        cancel.setText(cancelStr);
        sure.setText(sureStr);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null){
                    callBack.cancel();
                }
                dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null){
                    callBack.sure();
                }
                dismiss();
            }
        });
    }
}
