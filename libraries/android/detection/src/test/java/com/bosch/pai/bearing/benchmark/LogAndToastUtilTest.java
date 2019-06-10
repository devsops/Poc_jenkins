package com.bosch.pai.bearing.benchmark;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LogAndToastUtil.class)
public class LogAndToastUtilTest {

    LogAndToastUtil logAndToastUtil;
    String messageRecieved;
    String tagRecieved;
    LogAndToastUtil.LOG_STATUS log_status;


    @Before
    public void setUp()
    {
        logAndToastUtil = Mockito.mock(LogAndToastUtil.class);
        //   messageRecieved = Mockito.mock(String.class);
        //   tagRecieved = Mockito.mock(String.class);
        //   log_status = Mockito.mock(LogAndToastUtil.LOG_STATUS.class);

    }

    @Test
    public void testSetUp() throws Exception {

        mockStatic(LogAndToastUtil.class);

        PowerMockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                // return null;
                Object[] arguments = invocation.getArguments();
                if (arguments != null && arguments.length > 1 && arguments[0] != null && arguments[1] != null && arguments[2] != null) {
                    messageRecieved = (String) arguments[2];
                    tagRecieved = (String)arguments[1];
                    log_status = (LogAndToastUtil.LOG_STATUS) arguments[0];
                }

                return null;
            }
        }).when(LogAndToastUtil.class,"addLogs",anyObject(), anyString(), anyString());

        LogAndToastUtil.addLogs(LogAndToastUtil.LOG_STATUS.DEBUG,"TestTag","TestMessage");
        assertEquals("TestTag",tagRecieved);
        assertEquals("TestMessage",messageRecieved);
        assertEquals(LogAndToastUtil.LOG_STATUS.DEBUG,log_status);
    }
}
