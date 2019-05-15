package org.anyrtc.wawa;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.anyrtc.rtcp.RtcpCore;
import org.anyrtc.tools.AnimUtils;
import org.anyrtc.wawaClient.AnyRTCWaWaClient;
import org.anyrtc.weight.CustomDialog;

public class MainActivity extends BaseActivity {
    private Button start;
    private CustomDialog customDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        start = (Button) findViewById(R.id.btn_Start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimActivity(WaWaListActivity.class);
            }
        });

        findViewById(R.id.tv_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCall();
            }
        });
    }

    public void showCall() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        customDialog = builder.setContentView(R.layout.dialog_base_layout_two_btn)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setAnimId(R.style.default_dialog_style)
                .setBackgroundDrawable(true)
                .show(new CustomDialog.Builder.onInitListener() {
                    @Override
                    public void init(CustomDialog customDialog) {
                        TextView content = (TextView) customDialog.findViewById(R.id.content);
                        content.setText("联系商务:021-65650071-840 ?");
                        TextView tv_no = (TextView) customDialog.findViewById(R.id.tv_no);
                        TextView tv_ok = (TextView) customDialog.findViewById(R.id.tv_ok);
                        tv_ok.setText("商务咨询");
                        tv_no.setOnClickListener(new DialogOnclick());
                        tv_ok.setOnClickListener(new DialogOnclick());
                    }
                });
    }

    public class DialogOnclick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_no:
                    if (customDialog != null) {
                        customDialog.dismiss();
                    }
                    break;
                case R.id.tv_ok:
                    Uri uri = Uri.parse("tel:021-65650071-840");
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                    startActivity(intent);
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.exit(0);
            RtcpCore.Inst().getmRtcpKit().clear();//程序退出时释放
            AnyRTCWaWaClient.getInstance().closeServer();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ObjectAnimator animator = AnimUtils.tada(start);
        animator.start();
    }
}
