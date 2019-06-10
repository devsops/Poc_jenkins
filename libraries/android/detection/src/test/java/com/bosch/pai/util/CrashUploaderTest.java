package com.bosch.pai.util;

import android.content.Context;

import com.bosch.pai.comms.CommsListener;
import com.bosch.pai.comms.CommsManager;
import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.net.HttpURLConnection;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CrashUploader.class})
public class CrashUploaderTest {

    @Mock
    private Context context;
    @Mock
    private File dir;
    @Mock
    private Thread thread;

    @Before
    public void init() throws Exception {
        PowerMockito.when(context.getFilesDir()).thenReturn(dir);
        PowerMockito.when(dir.getAbsolutePath()).thenReturn(File.separator);
        PowerMockito.whenNew(File.class).withArguments(Mockito.anyString()).thenReturn(dir);
        PowerMockito.when(dir.exists()).thenReturn(true);
        final File[] files = new File[]{new File("1"), new File("2")};
        PowerMockito.when(dir.listFiles()).thenReturn(files);
        PowerMockito.whenNew(Thread.class).withAnyArguments().thenReturn(thread);
    }

    @Test
    public void testUploadReports() {
        CrashUploader.uploadCrashReports(context);
        Mockito.verify(thread, Mockito.times(1)).start();
    }
}
