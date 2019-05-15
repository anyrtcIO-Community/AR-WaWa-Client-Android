package org.anyrtc.wawa;

import android.app.Application;

import org.anyrtc.rtcp_kit.AnyRTCRTCPEngine;
import org.anyrtc.tools.SoundPlayUtils;
import org.anyrtc.wawaClient.AnyRTCWaWaClient;

/**
 * Created by liuxiaozhong on 2017/12/7.
 */

public class WaWaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AnyRTCRTCPEngine.Inst().initEngineWithAnyrtcInfo(getApplicationContext(),"", "", "", "");
        AnyRTCWaWaClient.getInstance().initEngineWithAnyRTCInfo("","","","");
        SoundPlayUtils.init(this);
    }

}
