package com.wy.arialena.modulecore.update_app;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wy.arialena.modulecore.R;
import com.wy.arialena.modulecore.update_app.utils.DownLoadApkUtils;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * app 更新
 * @author wuyan
 */
public class UpdateDownloadDialog extends DialogFragment implements Serializable {
    public static final String TAG = UpdateDownloadDialog.class.getSimpleName();

    private Builder                      builder;
    private static UpdateDownloadDialog instance = new UpdateDownloadDialog();

    public static UpdateDownloadDialog getInstance() {
        return instance;
    }

    private AppCompatImageView image;
    private TextView title, subTitle, body;
    private Button positive, negative;
    private LinearLayout buttonsPanel;
    /**
     * 存储地址
     */
    private String downloadStorePath;
    /**
     * 下载地址
     */
    private String DOWNLOAD_URL;

    private Activity activity;
    private boolean isForce = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置返回键无效
        setCancelable(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.CENTER;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
//        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = (int) (dm.widthPixels * 0.8);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_dialog_progress, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
//        Aria.download(this).register();
        if (builder != null) {
            if (builder.getTextTitle() != null) {
                title.setText(builder.getTextTitle());
            } else {
                title.setVisibility(View.GONE);
            }
            if (builder.getTitleColor() != 0) {
                title.setTextColor(ContextCompat.getColor(getActivity(), builder.getTitleColor()));
            }
            if (builder.getTextSubTitle() != null) {
                subTitle.setVisibility(View.VISIBLE);
                subTitle.setText(builder.getTextSubTitle());
            } else {
                subTitle.setVisibility(View.GONE);
            }
            if (builder.getSubtitleColor() != 0) {
                subTitle.setTextColor(ContextCompat.getColor(getActivity(), builder.getSubtitleColor()));
            }
            if (builder.getBody() != null) {
                body.setText(builder.getBody());
            } else {
                body.setVisibility(View.GONE);
            }
            body.setText(builder.getBody());
            if (builder.getBodyColor() != 0) {
                body.setTextColor(ContextCompat.getColor(getActivity(), builder.getBodyColor()));
            }

            if (builder.getPositiveButtonText() != null) {
                positive.setText(builder.getPositiveButtonText());
                if (builder.getPositiveTextColor() != 0) {
                    positive.setTextColor(ContextCompat.getColor(getActivity(), builder.getPositiveTextColor()));
                }
                if (builder.getOnPositiveClicked() != null) {
                    positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // builder.getDownloadPath();
                            builder.getOnPositiveClicked().OnClick(v, getDialog());
                        }
                    });
                }
            } else {
                positive.setVisibility(View.GONE);
            }
            if (builder.getNegativeButtonText() != null) {
                negative.setText(builder.getNegativeButtonText());
                if (builder.getNegativeColor() != 0) {
                    negative.setTextColor(ContextCompat.getColor(getActivity(), builder.getNegativeColor()));
                }
                if (builder.getOnNegativeClicked() != null) {
                    negative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.getOnNegativeClicked().OnClick(v, getDialog());
                        }
                    });
                }
            } else {
                negative.setVisibility(View.GONE);
            }


            if (builder.getImageRecourse() != 0) {
                Drawable imageRes = ContextCompat.getDrawable(getContext(), builder.getImageRecourse());
                image.setImageDrawable(imageRes);
            }

            if (builder.isAutoHide()) {
                int time = builder.getTimeToHide() != 0 ? builder.getTimeToHide() : 10000;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded() && getActivity() != null) {
                            dismiss();
                        }
                    }
                }, time);
            }

            if (builder.isTouchDismiss()) {
                if (getDialog() != null) {
                    getDialog().setCanceledOnTouchOutside(true);
                }
            } else {
                if (getDialog() != null) {
                    getDialog().setCanceledOnTouchOutside(false);
                }
            }


            if (builder.getSubTitleFont() != null) {
                subTitle.setTypeface(builder.getSubTitleFont());
            }

            if (builder.getBodyFont() != null) {
                body.setTypeface(builder.getBodyFont());
            }

            if (builder.getPositiveButtonFont() != null) {
                positive.setTypeface(builder.getPositiveButtonFont());
            }
            if (builder.getNegativeButtonFont() != null) {
                negative.setTypeface(builder.getNegativeButtonFont());
            }

            if (builder.getAlertFont() != null) {
                title.setTypeface(builder.getAlertFont());
                subTitle.setTypeface(builder.getAlertFont());
                body.setTypeface(builder.getAlertFont());
                positive.setTypeface(builder.getAlertFont());
                negative.setTypeface(builder.getAlertFont());
            }

            if (builder.getButtonsGravity() != null) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                switch (builder.getButtonsGravity()) {
                    case LEFT:
                        params.gravity = Gravity.LEFT;
                        break;
                    case RIGHT:
                        params.gravity = Gravity.RIGHT;
                        break;
                    case CENTER:
                        params.gravity = Gravity.CENTER;
                        break;
                }
                if (buttonsPanel != null) {
                    buttonsPanel.setLayoutParams(params);
                }
            }
        }
    }

    private void initViews(View view) {
        image = view.findViewById(R.id.img_view);
        title = view.findViewById(R.id.title_view);
        subTitle = view.findViewById(R.id.sub_title_view);
        body = view.findViewById(R.id.body_view);
        positive = view.findViewById(R.id.down_btn);
        negative = view.findViewById(R.id.cancle_btn);
        buttonsPanel = view.findViewById(R.id.buttons_panel);
    }

    private RoundedBitmapDrawable rectRoundBitmap(Bitmap bitmap){
        //创建RoundedBitmapDrawable对象
        RoundedBitmapDrawable roundImg = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        //抗锯齿
        roundImg.setAntiAlias(true);
        //设置圆角半径
        roundImg.setCornerRadius(15);
        return roundImg;
    }

    private Dialog show(Activity activity, Builder builder) {
        this.builder = builder;
        show(((AppCompatActivity) activity).getSupportFragmentManager(), TAG);
        return getDialog();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            Field dismissed = DialogFragment.class.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field shown = DialogFragment.class.getDeclaredField("mShownByMe");
            shown.setAccessible(true);
            shown.set(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public static class Builder implements Serializable {
        private String positiveButtonText;
        private String negativeButtonText;
        private String textTitle;
        private String textSubTitle;
        private String body;
        private String downloadPath;
        private OnPositiveClicked onPositiveClicked;
        private OnNegativeClicked onNegativeClicked;
        private boolean autoHide;
        private boolean touchDismiss = false;
        private int timeToHide;
        private int positiveTextColor;
        private int backgroundColor;
        private int negativeColor;
        private int titleColor;
        private int subtitleColor;
        private int bodyColor;

        public int getImageRecourse() {
            return imageRecourse;
        }

        private int imageRecourse;

        public String getPositiveButtonText() {
            return positiveButtonText;
        }

        public String getNegativeButtonText() {
            return negativeButtonText;
        }

        public String getTextTitle() {
            return textTitle;
        }

        public String getTextSubTitle() {
            return textSubTitle;
        }

        public String getBody() {
            return body;
        }

        public String getDownloadPath() {
            return downloadPath;
        }

        public OnPositiveClicked getOnPositiveClicked() {
            return onPositiveClicked;
        }

        public OnNegativeClicked getOnNegativeClicked() {
            return onNegativeClicked;
        }

        public int getTimeToHide() {
            return timeToHide;
        }

        public int getPositiveTextColor() {
            return positiveTextColor;
        }

        public void setPositiveTextColor(int positiveTextColor) {
            this.positiveTextColor = positiveTextColor;
        }

        public int getBackgroundColor() {
            return backgroundColor;
        }

        public int getNegativeColor() {
            return negativeColor;
        }

        public int getTitleColor() {
            return titleColor;
        }

        public int getSubtitleColor() {
            return subtitleColor;
        }

        public int getBodyColor() {
            return bodyColor;
        }


        public Typeface getSubTitleFont() {
            return subTitleFont;
        }

        public void setSubTitleFont(Typeface subTitleFont) {
            this.subTitleFont = subTitleFont;
        }

        public Typeface getBodyFont() {
            return bodyFont;
        }

        public void setBodyFont(Typeface bodyFont) {
            this.bodyFont = bodyFont;
        }

        private Typeface subTitleFont;
        private Typeface bodyFont;
        private Typeface positiveButtonFont;
        private Typeface negativeButtonFont;
        private Typeface alertFont;

        private Activity activity;

        private PanelGravity buttonsGravity;

        public PanelGravity getButtonsGravity() {
            return buttonsGravity;
        }

        public Builder setButtonsGravity(PanelGravity buttonsGravity) {
            this.buttonsGravity = buttonsGravity;
            return this;
        }

        public Typeface getAlertFont() {
            return alertFont;
        }

        public Builder setAlertFont(String alertFont) {
            this.alertFont = Typeface.createFromAsset(activity.getAssets(), alertFont);
            return this;
        }

        public Typeface getPositiveButtonFont() {
            return positiveButtonFont;
        }

        public Builder setPositiveButtonFont(String positiveButtonFont) {
            this.positiveButtonFont = Typeface.createFromAsset(activity.getAssets(), positiveButtonFont);
            return this;
        }

        public Typeface getNegativeButtonFont() {
            return negativeButtonFont;
        }

        public Builder setNegativeButtonFont(String negativeButtonFont) {
            this.negativeButtonFont = Typeface.createFromAsset(activity.getAssets(), negativeButtonFont);
            return this;
        }


        public Builder setSubTitleFont(String subTitleFontPath) {
            this.subTitleFont = Typeface.createFromAsset(activity.getAssets(), subTitleFontPath);
            return this;
        }

        public Builder setBodyFont(String bodyFontPath) {
            this.bodyFont = Typeface.createFromAsset(activity.getAssets(), bodyFontPath);
            return this;
        }


        public Builder setTimeToHide(int timeToHide) {
            this.timeToHide = timeToHide;
            return this;
        }

        public boolean isAutoHide() {
            return autoHide;
        }

        public Builder setAutoHide(boolean autoHide) {
            this.autoHide = autoHide;
            return this;
        }

        public Builder(Activity context) {
            this.activity = context;
        }

        public Builder setPositiveColor(int positiveTextColor) {
            this.positiveTextColor = positiveTextColor;
            return this;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }


        public Builder setNegativeColor(int negativeColor) {
            this.negativeColor = negativeColor;
            return this;
        }

        public Builder setTitleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public Builder setSubtitleColor(int subtitleColor) {
            this.subtitleColor = subtitleColor;
            return this;
        }

        public Builder setBodyColor(int bodyColor) {
            this.bodyColor = bodyColor;
            return this;
        }

        public Builder setImageRecourse(int imageRecourse) {
            this.imageRecourse = imageRecourse;
            return this;
        }

        public Builder setPositiveButtonText(int positiveButtonText) {
            this.positiveButtonText = activity.getString(positiveButtonText);
            return this;
        }

        public Builder setPositiveButtonText(String positiveButtonText) {
            this.positiveButtonText = positiveButtonText;
            return this;
        }

        public Builder setNegativeButtonText(String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            return this;
        }

        public Builder setNegativeButtonText(int negativeButtonText) {
            this.negativeButtonText = activity.getString(negativeButtonText);
            return this;
        }

        public Builder setTextTitle(String textTitle) {
            this.textTitle = textTitle;
            return this;
        }

        public Builder setTextTitle(int textTitle) {
            this.textTitle = activity.getString(textTitle);
            return this;
        }


        public Builder setTextSubTitle(String textSubTitle) {
            this.textSubTitle = textSubTitle;
            return this;
        }

        public Builder setTextSubTitle(int textSubTitle) {
            this.textSubTitle = activity.getString(textSubTitle);
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setBody(int body) {
            this.body = activity.getString(body);
            return this;
        }

        public Builder setOnPositiveClicked(OnPositiveClicked onPositiveClicked) {
            this.onPositiveClicked = onPositiveClicked;
            return this;
        }

        public Builder setOnNegativeClicked(OnNegativeClicked onNegativeClicked) {
            this.onNegativeClicked = onNegativeClicked;
            return this;
        }


        public Builder build() {
            return this;
        }

        public Dialog show() {
            return UpdateDownloadDialog.getInstance().show(activity, this);
        }


        public void download(String DOWNLOAD_URL, String filename) {
            UpdateDownloadDialog.getInstance().startDownload(DOWNLOAD_URL, filename);
        }

        public boolean isTouchDismiss() {
            return touchDismiss;
        }

        public Builder setTouchDismiss(boolean touchDismiss) {
            this.touchDismiss = touchDismiss;
            return this;
        }

        public Builder setDownloadPath(String downloadPath) {
            this.downloadPath = downloadPath;
            return this;
        }

    }

    /**
     * 开始下载
     *
     * @param DOWNLOAD_URL 文件下载地址
     * @return
     */
    public UpdateDownloadDialog startDownload(String DOWNLOAD_URL, String filename) {
        this.DOWNLOAD_URL = DOWNLOAD_URL;
        if (downloadStorePath == null) {
            downloadStorePath = DownLoadApkUtils.downloadPath;
        }
        String fineName = downloadStorePath + filename;
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
//        Aria.download(this)
//                .load(DOWNLOAD_URL)     //读取下载地址
//                .setFilePath(fineName) //设置文件保存的完整路径
//                .start();  //启动下载

        return this;
    }

    public interface OnPositiveClicked {
        void OnClick(View view, Dialog dialog);
    }

    public interface OnNegativeClicked {
        void OnClick(View view, Dialog dialog);
    }

    public enum PanelGravity implements Serializable {
        LEFT,
        RIGHT,
        CENTER
    }

    @Override
    public void onPause() {
        super.onPause();
//        this.builder = null;
//        dismiss();
    }
}
