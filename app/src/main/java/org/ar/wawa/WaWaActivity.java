package org.ar.wawa;

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

import org.ar.common.enums.ARNetQuality;
import org.ar.rtcp.ARVideoView;
import org.ar.rtcp.RtcpCore;
import org.ar.rtcp_kit.ARRtcpEngine;
import org.ar.rtcp_kit.ARRtcpEvent;
import org.ar.rtcp_kit.ARRtcpKit;
import org.ar.tools.AnimUtils;
import org.ar.tools.SoundPlayUtils;
import org.ar.wawaClient.ARWaWaClient;
import org.ar.wawaClient.WaWaServerListener;
import org.ar.weight.CustomDialog;
import org.ar.weight.DialogReady;
import org.ar.weight.DialogResult;
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
    ARRtcpKit rtcpKit;
    ARVideoView rtcVideoView;
    private String videoId="";
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
        ARWaWaClient.getInstance().setServerListener(this);
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
        rtcVideoView = new ARVideoView(rlVideo,  ARRtcpEngine.Inst().Egl(),this,false);
        //订阅媒体
        try {
            JSONObject jsonObject = new JSONObject(roomBean.getRoom_url());
            videoId = jsonObject.getString("RtcpUrl");
            rtcpKit.subscribe("",videoId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        resultListener = new ResultListener(15000, 1000);
    }

    private ARRtcpEvent anyRTCRTCPEvent = new ARRtcpEvent() {
        /**
         * 发布成功
         * @param strRtcpId 发布媒体id
         */
        @Override
        public void onPublishOK(final String strRtcpId, String strLiveInfo) {
        }
        /**
         * 发布媒体失败
         */
        @Override
        public void onPublishFailed(int code, String reason) {

        }

        @Override
        public void onPublishExOK(String rtcpId, String liveInfo) {

        }

        @Override
        public void onPublishExFailed(int code, String strReason) {

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
                    ARWaWaClient.getInstance().joinRoom(roomBean.getRoom_anyrtcid(), userid, "安卓用户" + userid, "");
                }
            });
        }

        @Override
        public void onSubscribeFailed(String rtcpId, final int code, String reason) {
            WaWaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RTCP", "onSubscribeFailed:" + code);
                    Toast.makeText(WaWaActivity.this, "Error:SubscribeFailed " + code, Toast.LENGTH_SHORT).show();
                    exit();
                }
            });
        }

        @Override
        public void onRTCOpenRemoteVideoRender(final String rtcpId) {
            WaWaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RTCP", "OnRTCOpenVideoRender:" + rtcpId);
                    long renderPointer = rtcVideoView.openRemoteVideoRender(rtcpId).GetRenderPointer();
                    rtcpKit.setRemoteVideoRender(rtcpId, renderPointer);
                    if (isFirst) {
                        ObjectAnimator animator = AnimUtils.tada(book);
                        animator.start();
                        isFirst = false;
                    }
                }
            });
        }

        @Override
        public void onRTCCloseRemoteVideoRender(final String rtcpId) {
            WaWaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RTCP", "OnRTCCloseVideoRender:" + rtcpId);
                    if (rtcpKit != null) {
                        rtcpKit.setRemoteVideoRender(rtcpId, 0);
                        rtcVideoView.removeRemoteRender(rtcpId);
                        finish();
                    }
                }
            });
        }

        @Override
        public void onRTCOpenRemoteAudioTrack(String rtcpId) {

        }

        @Override
        public void onRTCCloseRemoteAudioTrack(String rtcpId) {

        }

        @Override
        public void onRTCRemoteAVStatus(String rtcpId, boolean bAudio, boolean bVideo) {

        }

        @Override
        public void onRTCRemoteAudioActive(String rtcpId, int nLevel, int nTime) {

        }

        @Override
        public void onRTCLocalAudioActive(int nLevel, int nTime) {

        }

        @Override
        public void onRTCRemoteNetworkStatus(String rtcpId, int nNetSpeed, int nPacketLost, ARNetQuality netQuality) {

        }

        @Override
        public void onRTCLocalNetworkStatus(int nNetSpeed, int nPacketLost, ARNetQuality netQuality) {

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
        rtcpKit.unSubscribe(videoId);
        if (isBook) {
            ARWaWaClient.getInstance().unbook();
        }
        ARWaWaClient.getInstance().leaveRoom();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rtcVideoView.removeRemoteRender(videoId);//移除视频图像
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
                        ARWaWaClient.getInstance().canclePlay();
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
                        ARWaWaClient.getInstance().play();
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
                ARWaWaClient.getInstance().sendControlCmd(ARWaWaClient.CMD.CMD_SWITCH_CAMERA);
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
                ARWaWaClient.getInstance().sendControlCmd(ARWaWaClient.CMD.CMD_FORWARD);
                break;
            case R.id.down:
                SoundPlayUtils.play(4);
                ARWaWaClient.getInstance().sendControlCmd(ARWaWaClient.CMD.CMD_BACKWARD);
                break;
            case R.id.left:
                SoundPlayUtils.play(4);
                ARWaWaClient.getInstance().sendControlCmd(ARWaWaClient.CMD.CMD_LEFT);
                break;
            case R.id.right:
                SoundPlayUtils.play(4);
                ARWaWaClient.getInstance().sendControlCmd(ARWaWaClient.CMD.CMD_RIGHT);
                break;
            case R.id.action:
                SoundPlayUtils.play(4);
                ARWaWaClient.getInstance().sendControlCmd(ARWaWaClient.CMD.CMD_GRAB);
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
                    ARWaWaClient.getInstance().unbook();
                } else {
                    ARWaWaClient.getInstance().book();
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
