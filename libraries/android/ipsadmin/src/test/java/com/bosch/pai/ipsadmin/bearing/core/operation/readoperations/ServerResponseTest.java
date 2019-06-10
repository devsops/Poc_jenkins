package com.bosch.pai.ipsadmin.bearing.core.operation.readoperations;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
public class ServerResponseTest {

    public boolean responseState;
    public List<String> names;

  //  @Mock
    ServerResponse serverResponse;

    @Before
    public void beforeResponseSetUp()
    {
        MockitoAnnotations.initMocks(this);

     //   when(serverResponse.isResponseStatusSuccess()).thenReturn(anyBoolean())
        responseState = true;
        names = new ArrayList<String>();
        names.add("Site1");
        names.add("Site2");
        serverResponse = new ServerResponse(responseState,names);

    }

    @Test
    public void statusTest()
    {
        boolean stateToVerify = serverResponse.isResponseStatusSuccess();
      //  assertEquals(stateToVerify,responseState);
        assertEquals(stateToVerify,true);

       // Mockito.verify(serverResponse,Mockito.times(1)).isResponseStatusSuccess();
    }

    @Test
    public void listTest()
    {
        List<String> namesOnly = serverResponse.getNames();
        assertEquals(namesOnly.size(),names.size());
        assertEquals("Site1",namesOnly.get(0));
        assertEquals("Site2",namesOnly.get(1));
    }

}
