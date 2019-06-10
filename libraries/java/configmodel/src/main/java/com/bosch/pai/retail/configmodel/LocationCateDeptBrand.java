package com.bosch.pai.retail.configmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sjn8kor on 10/17/2017.
 */
public class LocationCateDeptBrand implements Serializable {

    @SerializedName("locationType")
    private String locationType;
    @SerializedName("locationDepartments")
    private Set<String> locationDepartments = new HashSet<>();
    @SerializedName("locationCategorys")
    private Set<String> locationCategorys = new HashSet<>();
    @SerializedName("locationBrands")
    private Set<String> locationBrands = new HashSet<>();
    @SerializedName("locationCateDeptBrands")
    private Set<String> locationCateDeptBrands = new HashSet<>();

    public LocationCateDeptBrand() {
        //sonar
    }

    public LocationCateDeptBrand(LocationCateDeptBrand locationCateDeptBrand) {
        this.locationType = locationCateDeptBrand.getLocationType();
        this.locationDepartments = locationCateDeptBrand.getLocationDepartments() != null ? locationCateDeptBrand.getLocationDepartments() : new HashSet<String>();
        this.locationCategorys = locationCateDeptBrand.getLocationCategorys()!= null ? locationCateDeptBrand.getLocationCategorys() : new HashSet<String>();
        this.locationBrands = locationCateDeptBrand.getLocationBrands() != null ? locationCateDeptBrand.getLocationBrands() : new HashSet<String>();
        this.locationCateDeptBrands = locationCateDeptBrand.getLocationCateDeptBrands() != null ? locationCateDeptBrand.getLocationCateDeptBrands() : new HashSet<String>();
    }

    public LocationCateDeptBrand(String locationType, Set<String> locationDepartment, Set<String> locationCategory, Set<String> locationBrand, Set<String> locDeptCateBrands) {
        this.locationType = locationType;
        this.locationDepartments = locationDepartment != null ? locationDepartment : new HashSet<String>();
        this.locationCategorys = locationCategory != null ? locationCategory : new HashSet<String>();
        this.locationBrands = locationBrand != null ? locationBrand : new HashSet<String>();
        this.locationCateDeptBrands = locDeptCateBrands != null ? locDeptCateBrands : new HashSet<String>();
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public Set<String> getLocationDepartments() {
        return new HashSet<>(locationDepartments);
    }

    public void setLocationDepartments(Set<String> locationDepartment) {
        this.locationDepartments = locationDepartment != null ? locationDepartment : new HashSet<String>();
    }

    public Set<String> getLocationCategorys() {
        return new HashSet<>(locationCategorys);
    }

    public void setLocationCategorys(Set<String> locationCategory) {
        this.locationCategorys = locationCategory != null ? locationCategory : new HashSet<String>();
    }

    public Set<String> getLocationBrands() {
        return new HashSet<>(locationBrands);
    }

    public void setLocationBrands(Set<String> locationBrand) {
        this.locationBrands = locationBrand != null ? locationBrand : new HashSet<String>();
    }

    public Set<String> getLocationCateDeptBrands() {
        return new HashSet<>(locationCateDeptBrands);
    }

    public void setLocationCateDeptBrands(Set<String> locationCateDeptBrands) {
        this.locationCateDeptBrands = locationCateDeptBrands != null ? locationCateDeptBrands : new HashSet<String>();
    }

    @Override
    public String toString() {
        return "LocationDeptCateBrand{" +
                "locationType='" + locationType + '\'' +
                ", locationDepartments=" + locationDepartments +
                ", locationCategorys=" + locationCategorys +
                ", locationBrands=" + locationBrands +
                ", locationCateDeptBrands=" + locationCateDeptBrands +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationCateDeptBrand that = (LocationCateDeptBrand) o;

        if (locationType != null ? !locationType.equals(that.locationType) : that.locationType != null)
            return false;
        if (locationDepartments != null ? !locationDepartments.equals(that.locationDepartments) : that.locationDepartments != null)
            return false;
        if (locationCategorys != null ? !locationCategorys.equals(that.locationCategorys) : that.locationCategorys != null)
            return false;
        if (locationBrands != null ? !locationBrands.equals(that.locationBrands) : that.locationBrands != null)
            return false;
        return locationCateDeptBrands != null ? locationCateDeptBrands.equals(that.locationCateDeptBrands) : that.locationCateDeptBrands == null;
    }

    @Override
    public int hashCode() {
        int result = locationType != null ? locationType.hashCode() : 0;
        result = 31 * result + (locationDepartments != null ? locationDepartments.hashCode() : 0);
        result = 31 * result + (locationCategorys != null ? locationCategorys.hashCode() : 0);
        result = 31 * result + (locationBrands != null ? locationBrands.hashCode() : 0);
        result = 31 * result + (locationCateDeptBrands != null ? locationCateDeptBrands.hashCode() : 0);
        return result;
    }
}
