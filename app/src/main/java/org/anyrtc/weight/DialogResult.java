package org.anyrtc.weight;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

import org.anyrtc.tools.SoundPlayUtils;
import org.anyrtc.wawa.R;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;


public class DialogResult extends AppBaseDialogFragment {


    @BindView(R.id.iv_result)
    ImageView ivResult;
    @BindView(R.id.tv_again)
    TextView tvAgain;
    public static boolean result;
    @BindView(R.id.tv_fangqi)
    TextView tvFangqi;
    @BindView(R.id.tv_xiuxi)
    TextView tvXiuxi;
    Unbinder unbinder;
    private ImmersionBar mImmersionBar;
    public int voiceID;
    public static boolean isResultShow=false;
    public DialogResult() {
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
        setCancelable(true);
        window.setAttributes(lp);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.dialog_result;
    }

    @Override
    protected void initData() {
        if (result) {
            tvXiuxi.setVisibility(View.VISIBLE);
            ivResult.setImageResource(R.drawable.img_tailihaile);
        } else {
            tvFangqi.setVisibility(View.VISIBLE);
            ivResult.setImageResource(R.drawable.img_lose);
        }
        mImmersionBar = ImmersionBar.with(this, getDialog());
        mImmersionBar.init();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        SoundPlayUtils.stop(voiceID);
        isResultShow=false;
    }



    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        isResultShow=true;
        if (result) {
            voiceID = SoundPlayUtils.play(1);
        } else {
            voiceID = SoundPlayUtils.play(2);
        }
    }


    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }


    @OnClick({R.id.tv_fangqi, R.id.tv_xiuxi, R.id.tv_again})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_fangqi:
                dismiss();
                break;
            case R.id.tv_xiuxi:
                dismiss();
                break;
            case R.id.tv_again:
                dismiss();
                break;
        }
    }
}
