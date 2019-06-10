package com.bosch.pai.comms.operation;

import com.bosch.pai.comms.model.RequestObject;
import com.bosch.pai.comms.model.ResponseObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

@PrepareForTest({MULTIPARTPOSTRunnable.class, HTTPRunnableTask.class})
public class MULTIPARTPOSTRunnableTest extends HTTPRunnableTaskTest {

    @Mock
    private File file;
    @Mock
    private FileInputStream fileInputStream;

    @Before
    public void init() throws Exception {
        super.init();
        PowerMockito.when(file.getName()).thenReturn("TestFile.csv");
        PowerMockito.whenNew(FileInputStream.class).withAnyArguments().thenReturn(fileInputStream);
        PowerMockito.when(fileInputStream.read(Mockito.any(byte[].class))).thenReturn(1).thenReturn(-1);
    }

    @Test
    public void testConstructorAndRun() {
        final RequestObject requestObject = new RequestObject(RequestObject.RequestType.MULTIPART_POST, BASE_TEST_URL, END_POINT);
        requestObject.setMultipartFile(file);
        final MULTIPARTPOSTRunnable multipartpostRunnable = new MULTIPARTPOSTRunnable(UUID.randomUUID(), requestObject, retryConnectionHandler, commsListener);
        multipartpostRunnable.run();
        Mockito.verify(commsListener, Mockito.times(1)).onResponse(Mockito.any(ResponseObject.class));
    }
}
