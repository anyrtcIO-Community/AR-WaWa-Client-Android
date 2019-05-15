package org.anyrtc.wawa;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.anyrtc.rtcp.RTCVideoView;
import org.anyrtc.rtcp.RtcpCore;
import org.anyrtc.rtcp_kit.AnyRTCRTCPEngine;
import org.anyrtc.rtcp_kit.AnyRTCRTCPEvent;
import org.anyrtc.rtcp_kit.RtcpKit;
import org.anyrtc.tools.AnimUtils;
import org.anyrtc.tools.SoundPlayUtils;
import org.anyrtc.wawaClient.AnyRTCWaWaClient;
import org.anyrtc.wawaClient.WaWaServerListener;
import org.anyrtc.weight.CustomDialog;
import org.anyrtc.weight.DialogReady;
import org.anyrtc.weight.DialogResult;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class WaWaActivity extends BaseActivity implements WaWaServerListener {

    @BindView(R.id.rl_video)
    RelativeLayout rlVideo;
    @BindView(R.id.view_space)
    View viewSpace;
    @BindView(R.id.tv_wawa_num)
    TextView tvWawaNum;
    @BindView(R.id.tv_people_num)
    TextView tvPeopleNum;
    @BindView(R.id.camera)
    ImageView camera;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.up)
    ImageButton up;
    @BindView(R.id.down)
    ImageButton down;
    @BindView(R.id.left)
    ImageButton left;
    @BindView(R.id.right)
    ImageButton right;
    @BindView(R.id.action)
    TextView action;
    @BindView(R.id.ll_cmd)
    RelativeLayout llCmd;
    @BindView(R.id.book)
    RadioButton book;
    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.kefu)
    ImageButton kefu;
    @BindView(R.id.now_people)
    TextView nowPeople;
    @BindView(R.id.fl_camera)
    FrameLayout flCamera;
    private RoomBean.DataBean.RoomlistBean roomBean;
    RtcpKit rtcpKit;
    RTCVideoView rtcVideoView;
    private String peerid;
    private boolean isBook = false;
    private TimeCount timeCount;
    DialogResult dialogResult;
    DialogReady dialogReady;
    private String userid = "";
    private CustomDialog customDialog;
    private boolean isFirst = true;
    private ResultListener resultListener;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wa_wa;
    }

    @Override
    public void initView(Bundle savedInstanceState) {


        mImmersionBar.titleBar(viewSpace).init();
        AnyRTCWaWaClient.getInstance().setServerListener(this);
        roomBean = (RoomBean.DataBean.RoomlistBean) getIntent().getSerializableExtra("room");
        if (roomBean != null) {
            tvWawaNum.setText(roomBean.getRoom_anyrtcid());
            tvPeopleNum.setText(roomBean.getRoom_member() + "正在围观");
        }
        userid = "android" + (int) ((Math.random() * 9 + 1) * 100000) + "";
        //获取RTCP对象
        rtcpKit = RtcpCore.Inst().getmRtcpKit();
        //设置回调监听
        rtcpKit.setRtcpEvent(anyRTCRTCPEvent);
        //实例化视频窗口管理对象
        rtcVideoView = new RTCVideoView(rlVideo, this, AnyRTCRTCPEngine.Inst().Egl());
        //订阅媒体
        try {
            JSONObject jsonObject = new JSONObject(roomBean.getRoom_url());
            peerid = jsonObject.getString("RtcpUrl");
            rtcpKit.subscribe(peerid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        resultListener = new ResultListener(15000, 1000);
    }

    private AnyRTCRTCPEvent anyRTCRTCPEvent = new AnyRTCRTCPEvent() {
        /**
         * 发布成功
         * @param strRtcpId 发布媒体id
         */
        @Override
        public void onPublishOK(final String strRtcpId, String strLiveInfo) {
        }

        /**
         * 发布媒体失败
         * @param nCode 状态码
         */
        @Override
        public void onPublishFailed(final int nCode) {

        }

        /**
         * 订阅媒体成功
         * @param strRtcpId 订阅的媒体的id
         */
        @Override
        public void onSubscribeOK(final String strRtcpId) {

            WaWaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RTCP", "onSubscribeOK:" + strRtcpId);
                    AnyRTCWaWaClient.getInstance().joinRoom(roomBean.getRoom_anyrtcid(), userid, "安卓用户" + userid, "");
                }
            });
        }

        /**
         * 订阅失败
         * @param strRtcpId 订阅的媒体的id
         * @param nCode 状态码
         */
        @Override
        public void onSubscribeFailed(final String strRtcpId, final int nCode) {
            WaWaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RTCP", "onSubscribeFailed:" + nCode);
                    Toast.makeText(WaWaActivity.this, "Error:SubscribeFailed " + nCode, Toast.LENGTH_SHORT).show();
                    exit();
                }
            });
        }

        /**
         * 订阅的媒体视频即将显示
         * @param strLivePeerId 订阅的媒体的视频像id
         */
        @Override
        public void onRTCOpenVideoRender(final String strLivePeerId) {
            WaWaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RTCP", "OnRTCOpenVideoRender:" + strLivePeerId);
                    long renderPointer = rtcVideoView.OnRtcOpenRemoteRender(strLivePeerId).GetRenderPointer();
                    rtcpKit.setRTCVideoRender(strLivePeerId, renderPointer);
                    if (isFirst) {
                        ObjectAnimator animator = AnimUtils.tada(book);
                        animator.start();
                        isFirst = false;
                    }
                }
            });
        }

        /**
         * 订阅的媒体视频关闭
         * @param strLivePeerId 订阅的媒体的视频像id
         */

        @Override
        public void onRTCCloseVideoRender(final String strLivePeerId) {
            WaWaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RTCP", "OnRTCCloseVideoRender:" + strLivePeerId);
                    if (rtcpKit != null) {
                        rtcpKit.setRTCVideoRender(strLivePeerId, 0);
                        rtcVideoView.OnRtcOpenRemoteRender(strLivePeerId);
                        finish();
                    }
                }
            });
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        rtcpKit.unSubscribe(peerid);
        if (isBook) {
            AnyRTCWaWaClient.getInstance().unbook();
        }
        AnyRTCWaWaClient.getInstance().leaveRoom();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rtcVideoView.OnRtcRemoveLocalRender();//移除视频图像
        rtcpKit.stop();//停止采集
        if (timeCount != null) {
            timeCount.cancel();
            timeCount = null;
        }
        if (resultListener != null) {
            resultListener.cancel();
            resultListener = null;
        }
    }


    //////////////////////////////////
    @Override
    public void onConnectServerSuccess() {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onReconnect() {

    }

    @Override
    public void onInitAnyRTCSuccess() {

    }

    @Override
    public void onInitAnyRTCFaild() {

    }


    @Override
    public void onGetRoomList(String strRoomList) {

    }

    @Override
    public void onJoinRoom(int code, String videoInfo, String strMemberNum) {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //一定要在加入房间成功后才能让用户u预约排队哦~
                book.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onLeaveRoom(int code) {

    }

    @Override
    public void onWaWaLeave(int code) {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WaWaActivity.this, "娃娃机离线了...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onBookResult(final int code, final String strBookNum) {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    if (code == 204) {
                        Toast.makeText(WaWaActivity.this, "您上一局游戏还未结束！请稍后再试", Toast.LENGTH_SHORT).show();
                        ObjectAnimator animator = AnimUtils.nope(book);
                        animator.start();
                        book.setChecked(false);
                        return;
                    }
                    isBook = true;
                    book.setChecked(true);
                    nowPeople.setText(strBookNum.equals("0") ? "" : "您当前排在：第" + (Integer.parseInt(strBookNum)) + "位");
                    book.setText("取消排队");


            }
        });
    }

    @Override
    public void onUnBookResult(int code, String strBookNum) {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                book.setText("申请排队");
                book.setChecked(false);
                isBook = false;
            }
        });
    }

    @Override
    public void onControlCmd(int code, String cmd) {

    }

    @Override
    public void onRoomUrlUpdate(String strVideoInfo) {

    }


    @Override
    public void onBookMemberUpdate(final String strBookMemberData) {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(strBookMemberData)) {
                    try {
                        JSONObject one = new JSONObject(strBookMemberData);
                        String num=one.getJSONObject("data").getString("book_member");
                        nowPeople.setText("当前排队人数：" + num);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    @Override
    public void onRoomMemberUpdate(final String strMemberNum) {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    tvPeopleNum.setText(strMemberNum + "人正在围观");

            }
        });
    }

    @Override
    public void onReadyStart() {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (DialogReady.isShow) {
                    return;
                }
                if (dialogReady == null) {
                    dialogReady = new DialogReady();
                }
                dialogReady.show(getSupportFragmentManager(), "ready");
                dialogReady.setOnReadyButtonListener(new DialogReady.onReadyButtonListener() {
                    @Override
                    public void onFangqi() {
                        isBook = false;
                        AnyRTCWaWaClient.getInstance().canclePlay();
                        book.setChecked(false);
                        book.setText("申请排队");
                        nowPeople.setVisibility(View.VISIBLE);
                        llCmd.setVisibility(View.GONE);
                        action.setVisibility(View.GONE);
                        flCamera.setVisibility(View.GONE);
                        book.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onStart() {
                        isBook = false;
                        llCmd.setVisibility(View.VISIBLE);
                        action.setVisibility(View.VISIBLE);
                        flCamera.setVisibility(View.VISIBLE);
                        showCameraAnim();
                        nowPeople.setVisibility(View.GONE);
                        book.setVisibility(View.GONE);
                        timeCount = new TimeCount(30000, 1000);
                        timeCount.start();
                        tvPeopleNum.setVisibility(View.GONE);
                        AnyRTCWaWaClient.getInstance().play();
                    }
                });
            }
        });
    }

    @Override
    public void onReadyTimeout() {

    }

    @Override
    public void onPlayTimeout() {

    }


    @Override
    public void onResult(final boolean result) {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogResult.result = result;
                if (DialogReady.isShow) {
                    return;
                }
                if (dialogResult == null) {
                    dialogResult = new DialogResult();
                    dialogResult.show(getSupportFragmentManager(), "result");
                } else {
                    dialogResult.show(getSupportFragmentManager(), "result");
                }
                if (resultListener != null) {
                    resultListener.cancel();
                }
                btnsEnable(true);
                flCamera.setVisibility(View.GONE);
                llCmd.setVisibility(View.GONE);
                action.setVisibility(View.GONE);
                book.setVisibility(View.VISIBLE);
                isBook = false;
                book.setChecked(false);
                nowPeople.setVisibility(View.VISIBLE);
                book.setText("申请排队");
                tvPeopleNum.setVisibility(View.VISIBLE);

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlVideo.getLayoutParams(); //取控件mRlVideoViewLayout当前的布局参数
        final float[] width = {this.getResources().getDisplayMetrics().widthPixels};
        rlVideo.post(new Runnable() {
            @Override
            public void run() {
                width[0] = rlVideo.getMeasuredWidth();
                final double heights = width[0] * 1.3333;//16:9
                params.height = (int) heights;// 强制设置控件的大小
                rlVideo.setLayoutParams(params); //使设置好的布局参数应用到控件
            }
        });
    }


    @OnClick({R.id.kefu, R.id.back, R.id.camera, R.id.ll_top, R.id.up, R.id.down, R.id.left, R.id.right, R.id.action, R.id.ll_cmd, R.id.book})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.kefu:
                showCall();
                break;
            case R.id.back:
                exit();
                break;
            case R.id.camera:
                SoundPlayUtils.play(4);
                AnyRTCWaWaClient.getInstance().sendControlCmd(AnyRTCWaWaClient.CMD.CMD_SWITCH_CAMERA);
                camera.setEnabled(false);
//                ObjectAnimator//
//                        .ofFloat(camera, "rotationY", 180, 360)//
//                        .setDuration(1000)//
//                        .start();
                camera.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        camera.setEnabled(true);
                    }
                }, 1000);
                break;
            case R.id.ll_top:
                break;
            case R.id.up:
                SoundPlayUtils.play(4);
                AnyRTCWaWaClient.getInstance().sendControlCmd(AnyRTCWaWaClient.CMD.CMD_FORWARD);
                break;
            case R.id.down:
                SoundPlayUtils.play(4);
                AnyRTCWaWaClient.getInstance().sendControlCmd(AnyRTCWaWaClient.CMD.CMD_BACKWARD);
                break;
            case R.id.left:
                SoundPlayUtils.play(4);
                AnyRTCWaWaClient.getInstance().sendControlCmd(AnyRTCWaWaClient.CMD.CMD_LEFT);
                break;
            case R.id.right:
                SoundPlayUtils.play(4);
                AnyRTCWaWaClient.getInstance().sendControlCmd(AnyRTCWaWaClient.CMD.CMD_RIGHT);
                break;
            case R.id.action:
                SoundPlayUtils.play(4);
                AnyRTCWaWaClient.getInstance().sendControlCmd(AnyRTCWaWaClient.CMD.CMD_GRAB);
                if (resultListener != null) {
                    resultListener.start();
                }
                btnsEnable(false);
                break;
            case R.id.ll_cmd:
                break;
            case R.id.book:
                SoundPlayUtils.play(4);
                if (isBook) {
                    AnyRTCWaWaClient.getInstance().unbook();
                } else {
                    AnyRTCWaWaClient.getInstance().book();
                }
                break;
        }
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

    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            action.setText("抓取中");
            action.setEnabled(false);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            action.setText(String.format(getString(R.string.time), millisUntilFinished / 1000 + ""));
        }
    }


    private class ResultListener extends CountDownTimer {
        public ResultListener(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            Toast.makeText(WaWaActivity.this, "抱歉！数据传输可能出了点问题，请再试试", Toast.LENGTH_SHORT).show();
            btnsEnable(true);
            flCamera.setVisibility(View.GONE);
            llCmd.setVisibility(View.GONE);
            action.setVisibility(View.GONE);
            book.setVisibility(View.VISIBLE);
            isBook = false;
            book.setChecked(false);
            nowPeople.setVisibility(View.VISIBLE);
            book.setText("申请排队");
            tvPeopleNum.setVisibility(View.VISIBLE);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }


    private void btnsEnable(boolean enable) {
        up.setEnabled(enable);
        left.setEnabled(enable);
        right.setEnabled(enable);
        down.setEnabled(enable);
        camera.setEnabled(enable);
        if (enable == false) {
            action.setText("抓取中");
            if (timeCount != null) {
                timeCount.cancel();
            }
        }
        action.setEnabled(enable);
    }


    public void showCameraAnim() {
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        translateAnimation.setDuration(1000);
        translateAnimation.setFillAfter(false);
        flCamera.startAnimation(translateAnimation);
    }

}
