package org.anyrtc.weight;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

import org.anyrtc.tools.SoundPlayUtils;
import org.anyrtc.wawa.R;

import butterknife.BindView;
import butterknife.OnClick;


public class DialogReady extends AppBaseDialogFragment implements DialogInterface.OnKeyListener{

    @BindView(R.id.tv_fangqi)
    TextView tvFangqi;
    @BindView(R.id.tv_begin)
    TextView tvBegin;
    private ImmersionBar mImmersionBar;
    private TimeCount mTimeCount;
    private onReadyButtonListener onReadyButtonListener;
    public static boolean isShow=false;

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if (onReadyButtonListener!=null){
                onReadyButtonListener.onFangqi();
            }
            dismiss();
            return true;
        }else {
            //这里注意当不是返回键时需将事件扩散，否则无法处理其他点击事件
            return false;
        }
    }

    public interface onReadyButtonListener{
        void onFangqi();
        void onStart();
    }


    public void setOnReadyButtonListener(DialogReady.onReadyButtonListener onReadyButtonListener) {
        this.onReadyButtonListener = onReadyButtonListener;
    }

    public DialogReady() {
        // Required empty public constructor
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
    }


    @Override
    protected void setLayout() {
        Window window = getDialog().getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setBackgroundDrawable(new ColorDrawable(0));//背景透明
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.dimAmount = 0;
        setCancelable(false);
        window.setAttributes(lp);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.dialog_reaady;
    }

    @Override
    protected void initData() {
        mTimeCount = new TimeCount(10000, 1000);
        mTimeCount.start();
        mImmersionBar=ImmersionBar.with(this,getDialog());
        mImmersionBar.init();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        isShow=false;
        if (mTimeCount != null) {
            mTimeCount.cancel();
            mTimeCount = null;
        }
    }



    @OnClick({R.id.tv_fangqi, R.id.tv_begin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_fangqi:
                if (onReadyButtonListener!=null){
                    onReadyButtonListener.onFangqi();
                }
                dismiss();
                break;
            case R.id.tv_begin:
                if (onReadyButtonListener!=null){
                    onReadyButtonListener.onStart();
                }
                dismiss();
                break;
        }
    }


    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (onReadyButtonListener!=null){
                onReadyButtonListener.onFangqi();
                dismiss();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (tvFangqi!=null) {
                tvFangqi.setText(String.format(getString(R.string.had_send), millisUntilFinished / 1000 + ""));
            }
        }
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        SoundPlayUtils.play(3);
        isShow=true;
    }
}
