package com.bosch.pai.ipsadmin.retail.pmadminlib.authentication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PasswordChangeModel.class)
public class PasswordChangeModelTest {

    private PasswordChangeModel passwordChangeModel;

    @Before
    public void init() throws Exception {
        passwordChangeModel = new PasswordChangeModel();
    }

    @Test
    public void getAndSetOldPassword(){
        passwordChangeModel.setOldPassword("oldpassword");
        Assert.assertEquals("oldpassword",passwordChangeModel.getOldPassword());
    }

    @Test
    public void getAndSetNewPassword(){
        passwordChangeModel.setNewPassword("newpassword");
        Assert.assertEquals("newpassword",passwordChangeModel.getNewPassword());
    }

    @Test
    public void getAndSetCredentialsExpiry(){
        passwordChangeModel.setCredentialsExpiry(2020);
        Assert.assertEquals(2020,passwordChangeModel.getCredentialsExpiry());
    }
}
