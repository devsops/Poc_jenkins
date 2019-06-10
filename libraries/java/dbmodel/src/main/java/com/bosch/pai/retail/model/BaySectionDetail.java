package com.bosch.pai.retail.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;


@Entity("bay_section_details")
public class BaySectionDetail {
    @Id
    @Indexed(options = @IndexOptions(unique = true))
    private String bayName;
    private String sectionName;

    public String getBayName() {
        return bayName;
    }

    public void setBayName(String bayName) {
        this.bayName = bayName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    @Override
    public String toString() {
        return "BaySectionDetail{" +
                ", bayName=" + bayName +
                ", sectionName=" + sectionName +
                '}';
    }
}
