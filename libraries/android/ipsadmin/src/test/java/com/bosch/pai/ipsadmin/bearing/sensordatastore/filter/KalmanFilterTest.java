package com.bosch.pai.ipsadmin.bearing.sensordatastore.filter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({KalmanFilter.class})
public class KalmanFilterTest {

    @Test
    public void testFilter() {
        final KalmanFilter filter = new KalmanFilter(1.0, 2.0, 3.0, 4.0, 5.0);
        final double run1 = filter.filter(3.2, 2.2);
        // Keeping current predictions as correctness, this assertion is made. If algo is changed for any reason need to change this
        Assert.assertEquals(0.6400000000000001, run1, 0.0);
        final double run2 = filter.filter(2.2, 1.5);
        Assert.assertEquals(0.772444444444444, run2, 0.0);
    }
}
