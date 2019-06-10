package com.bosch.pai.retail.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

@Entity("map_label_details")
public class MapLabelDetail {

    @Id
    private String mapLabelId = new ObjectId().toHexString();
    @Property("CATEGORY_CODE")
    private String categoryCode;
    @Property("CATEGORY_NAME")
    private String categoryName;
    @Property("MAP_LABEL")
    private String mapLabel;

    public String getMapLabelId() {
        return mapLabelId;
    }

    public void setMapLabelId(String mapLabelId) {
        this.mapLabelId = mapLabelId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMapLabel() {
        return mapLabel;
    }

    public void setMapLabel(String mapLabel) {
        this.mapLabel = mapLabel;
    }

    @Override
    public String toString() {
        return "MapLabelDetail{" +
                "mapLabelId=" + mapLabelId + 
                ", categoryCode=" + categoryCode + 
                ", categoryName=" + categoryName + 
                ", mapLabel=" + mapLabel + 
                '}';
    }
}
