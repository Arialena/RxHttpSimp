package com.wy.arialena.modulecore.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.wy.arialena.modulecore.R;


/**
 * @author wuyan
 * view出入动画
 */
public class AnimationUtil {
    private static AnimationUtil mInstance;


    public static AnimationUtil getInstance() {
        if (mInstance == null) {
            synchronized (AnimationUtil.class) {
                if (mInstance == null) {
                    mInstance = new AnimationUtil();
                }
            }
        }
        return mInstance;
    }

    public void viewVisibility(Context context, View view){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha_in);
        view.startAnimation(animation);
    }

    public void viewVisibility(final View v, long Duration){
        if (v.getVisibility() != View.VISIBLE) {
            return;
        }
        ScaleAnimation scaleAni = new ScaleAnimation(0.2f, 3.0f, 0.2f, 3.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        //设置动画执行的时间，单位是毫秒
        scaleAni.setDuration(Duration);

        // 设置动画重复次数
        // -1或者Animation.INFINITE表示无限重复，正数表示重复次数，0表示不重复只播放一次
        scaleAni.setRepeatCount(0);

        // 设置动画模式（Animation.REVERSE设置循环反转播放动画,Animation.RESTART每次都从头开始）
        scaleAni.setRepeatMode(Animation.REVERSE);

        // 启动动画
        v.startAnimation(scaleAni);
    }

    public void viewGone(final View v, long Duration){
        if (v.getVisibility() != View.VISIBLE) {
            return;
        }
        TranslateAnimation mHiddenAction   = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mHiddenAction  .setDuration(Duration);
        v.startAnimation(mHiddenAction );
        v.setVisibility(View.GONE);
    }
}
