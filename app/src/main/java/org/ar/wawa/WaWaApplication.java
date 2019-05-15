package org.ar.wawa;

import android.app.Application;

import org.ar.DeveloperInfo;
import org.ar.rtcp_kit.ARRtcpEngine;
import org.ar.tools.SoundPlayUtils;
import org.ar.wawaClient.ARWaWaClient;

/**
 * Created by liuxiaozhong on 2017/12/7.
 */

public class WaWaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ARRtcpEngine.Inst().initEngineWithARInfo(getApplicationContext(), DeveloperInfo.DEVELOPERID, DeveloperInfo.APPID, DeveloperInfo.APPKEY, DeveloperInfo.APPTOKEN);
        ARWaWaClient.getInstance().initEngineWithARInfo(DeveloperInfo.DEVELOPERID, DeveloperInfo.APPID, DeveloperInfo.APPKEY, DeveloperInfo.APPTOKEN);
        SoundPlayUtils.init(this);
    }

}
