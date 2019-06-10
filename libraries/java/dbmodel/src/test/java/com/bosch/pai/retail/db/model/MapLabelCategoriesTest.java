package com.bosch.pai.retail.db.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MapLabelCategoriesTest {

@Test
        public  void testMapLabelCategoriesTest() {

    MapLabelCategories mapLabelCategories = new MapLabelCategories();


    mapLabelCategories.setCategoryName("test");

    assertEquals("test",mapLabelCategories.getCategoryName());

}
}
