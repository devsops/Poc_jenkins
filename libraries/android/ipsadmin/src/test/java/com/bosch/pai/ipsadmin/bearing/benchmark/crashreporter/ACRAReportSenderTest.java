package com.bosch.pai.ipsadmin.bearing.benchmark.crashreporter;

import android.app.Application;
import android.content.Context;

import junit.framework.Assert;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.config.ACRAConfigurationException;
import org.acra.config.ConfigurationBuilder;
import org.acra.sender.ReportSenderException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CrashReportData.class, ACRAReportSender.class, File.class})
public class ACRAReportSenderTest {

    @Mock
    private SimpleDateFormat simpleDateFormat;
    @Mock
    private Context context;
    @Mock
    private Application application;
    @Mock
    private File dir;
    @Mock
    private File file;
    @Mock
    private FileWriter fileWriter;
    @Mock
    private BufferedWriter bufferedWriter;
    @Mock
    private CrashReportData crashReportData;

    @Before
    public void init() throws Exception {
        PowerMockito.whenNew(SimpleDateFormat.class)
                .withAnyArguments().thenReturn(simpleDateFormat);
        PowerMockito.when(simpleDateFormat.format(Mockito.any(Date.class))).thenReturn("local_time");

        PowerMockito.whenNew(File.class).withArguments(Mockito.anyString()).thenReturn(dir);
        PowerMockito.whenNew(File.class).withArguments(Mockito.endsWith(".txt")).thenReturn(file);

        PowerMockito.when(context.getFilesDir()).thenReturn(dir);
        PowerMockito.when(context.getFilesDir().getAbsolutePath()).thenReturn(File.separator);
        PowerMockito.when(dir.exists()).thenReturn(false);
        PowerMockito.when(dir.mkdirs()).thenReturn(true);
        PowerMockito.when(file.exists()).thenReturn(false);
        PowerMockito.when(file.createNewFile()).thenReturn(true);

        PowerMockito.whenNew(FileWriter.class).withAnyArguments().thenReturn(fileWriter);
        PowerMockito.whenNew(BufferedWriter.class).withAnyArguments().thenReturn(bufferedWriter);
        PowerMockito.when(crashReportData.getProperty(Mockito.any(ReportField.class))).thenReturn("test");
        PowerMockito.doNothing().when(fileWriter, "write", Mockito.anyString());
    }

    @Test
    public void testSend() throws ACRAConfigurationException, ReportSenderException {
        final ACRAReportSender acraReportSender = (ACRAReportSender) new ACRAReportSender.ACRAReportSenderFactory()
                .create(context, new ConfigurationBuilder(application).build());
        Assert.assertNotNull(acraReportSender);
        acraReportSender.send(context, crashReportData);
    }

}
