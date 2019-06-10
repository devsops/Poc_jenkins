package com.bosch.pai;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;


import com.bosch.pai.bearing.benchmark.crashreporter.ACRAReportSender;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(reportSenderFactoryClasses = {ACRAReportSender.ACRAReportSenderFactory.class}
)
public class IPSApplication extends MultiDexApplication {

    public IPSApplication() {
        // empty constructor
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        ACRA.init(this);
    }
}
