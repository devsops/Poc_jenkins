package com.bosch.pai.comms;

import android.util.Log;

import com.bosch.pai.comms.model.ResponseObject;
import com.bosch.pai.comms.util.CommsUtil;

import java.util.Map;
import java.util.TimerTask;

/**
 * Created by hug5kor on 9/14/2017.
 */

public class ScheduleTimerTask extends TimerTask {

    @Override
    public void run() {
        CommsUtil.addLogs(CommsUtil.LOG_STATUS.DEBUG, "ScheduleTimerTask", "Refreshing tokens!");
        CommsManager commsManager = CommsManager.getInstance();
        Map<String, String> urlMap = commsManager.getUrlContextIdMAP();
        for (Map.Entry<String, String> entry : urlMap.entrySet()) {
            commsManager.refreshToken(entry.getKey(), new CommsListener() {
                @Override
                public void onResponse(ResponseObject responseObject) {
                    Log.d("CommsManager_TimerTask", "onResponse: in ScheduleTimerTask");
                }

                @Override
                public void onFailure(int statusCode, String errMessage) {
                    Log.d("CommsManager_TimerTask", "comms on onFailure in ScheduleTimerTask.");
                }
            });
        }
    }
}