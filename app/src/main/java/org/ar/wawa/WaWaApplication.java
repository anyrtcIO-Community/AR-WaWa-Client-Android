package org.ar.wawa;

import android.app.Application;

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
        ARRtcpEngine.Inst().initEngineWithARInfo(getApplicationContext(),"95878050", "anyrtcy0UkK1PZydLX", "q50MGCmTkDn/wXBWNY5YjnB54KOzwwV8RoQ02AV6R9Y", "fabf75cfe0090ef495bf628cd7b9270b");
        ARWaWaClient.getInstance().initEngineWithARInfo("95878050","anyrtcy0UkK1PZydLX","q50MGCmTkDn/wXBWNY5YjnB54KOzwwV8RoQ02AV6R9Y","fabf75cfe0090ef495bf628cd7b9270b");
        SoundPlayUtils.init(this);
    }

}
