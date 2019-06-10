package com.bosch.pai.bearing.benchmark.bearinglogger.profiling;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;

import com.bosch.pai.bearing.logger.LoggerUtil;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ResourceProfiler.class, Build.VERSION.class, Build.class})
public class ResourceProfilerTest {

    @Mock
    private File file;
    @Mock
    private LoggerUtil loggerUtil;
    @Mock
    private StopWatch stopWatch;
    @Mock
    private Context context;
    @Mock
    private BatteryManager batteryManager;
    @Mock
    private Thread thread;

    @Before
    public void init() throws Exception {
        PowerMockito.mockStatic(Thread.class);
        PowerMockito.mockStatic(Build.class);
        PowerMockito.mockStatic(Build.VERSION.class);
        PowerMockito.whenNew(LoggerUtil.class).withNoArguments().thenReturn(loggerUtil);
        Calendar calendar = Calendar.getInstance();
        final String dateStamp = Integer.toString(calendar.get(5)) + "-" + calendar.getDisplayName(2, 2, Locale.getDefault()) + "-" + calendar.get(1);
        PowerMockito.when(loggerUtil.getDateStamp()).thenReturn(dateStamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        final String timeStamp = sdf.format(date);
        PowerMockito.when(loggerUtil.getLocalTime()).thenReturn(timeStamp);

        PowerMockito.whenNew(File.class).withArguments(Mockito.anyString()).thenReturn(file);
        PowerMockito.whenNew(StopWatch.class).withNoArguments().thenReturn(stopWatch);
        PowerMockito.when(stopWatch.getTime()).thenReturn(1L);
        PowerMockito.doNothing().when(stopWatch, "start");
        PowerMockito.when(context.getSystemService(Mockito.anyString())).thenReturn(batteryManager);
        PowerMockito.when(batteryManager.getIntProperty(Mockito.anyInt())).thenReturn(1);
        PowerMockito.when(Thread.currentThread()).thenReturn(thread);
        final StackTraceElement[] stackTraceElements = new StackTraceElement[]{new StackTraceElement("declaringClass", "writeDeviceInfo", "fileNAme", 1)};
        PowerMockito.when(thread.getStackTrace()).thenReturn(stackTraceElements);
        PowerMockito.field(Build.VERSION.class, "SDK_INT").set("SDK_INT", 21);

    }

    @Test
    public void testConstructorAndEnableDisableProfiling() throws Exception {
        final ResourceProfiler resourceProfiler = new ResourceProfiler();
        ResourceProfiler.enableDisableProfiling(true);
        Assert.assertNotNull(resourceProfiler);
        resourceProfiler.writeDeviceInfo(context);
    }
}
