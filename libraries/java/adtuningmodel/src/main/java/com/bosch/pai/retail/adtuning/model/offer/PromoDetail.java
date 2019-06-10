package com.bosch.pai.retail.adtuning.model.offer;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class PromoDetail implements Comparable,Serializable {
    @SerializedName("displayMessage")
    private String displayMessage;
    @SerializedName("messageCode")
    private String messageCode;
    @SerializedName("rank")
    private Integer rank;
    @SerializedName("customDetailMap")
    private Map<String, String> customDetailMap;

    @SerializedName("itemCode")
    private String itemCode;
    @SerializedName("itemDescription")
    private String itemDescription;
    @SerializedName("imageUrl")
    private String imageUrl;

    public PromoDetail() {
        //sonar
    }


    public PromoDetail(String displayMessage, String messageCode, Integer rank,
                       Map<String, String> customDetailMap, String itemCode,
                       String itemDescription, String imageUrl) {
        this.displayMessage = displayMessage;
        this.messageCode = messageCode;
        this.rank = rank;
        this.customDetailMap = customDetailMap;
        this.itemCode = itemCode;
        this.itemDescription = itemDescription;
        this.imageUrl = imageUrl;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public Integer getRank() {
        return rank;
    }

    public Map<String, String> getCustomDetailMap() {
        return customDetailMap;
    }

    public void setCustomDetailMap(Map<String, String> customDetailMap) {
        this.customDetailMap = customDetailMap;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        PromoDetail that = (PromoDetail) object;

        if (messageCode != null ? !messageCode.equals(that.messageCode) : that.messageCode != null)
            return false;
        if (itemCode != null ? !itemCode.equals(that.itemCode) : that.itemCode != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result = (messageCode != null ? messageCode.hashCode() : 0);
        result = 31 * result + (itemCode != null ? itemCode.hashCode() : 0);
        result = 31 * result + (displayMessage != null ? displayMessage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PromoDetail{" +
                " displayMessage=" + displayMessage +
                ", messageCode=" + messageCode +
                ", rank=" + rank +
                ", customDetailMap=" + customDetailMap +
                ", itemCode=" + itemCode +
                ", itemDescription=" + itemDescription +
                ", imageUrl=" + imageUrl +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        PromoDetail promoDetail = (PromoDetail) o;
        if (this.getRank() > promoDetail.getRank()) {
            return 1;
        } else {
            return -1;
        }
    }

}
