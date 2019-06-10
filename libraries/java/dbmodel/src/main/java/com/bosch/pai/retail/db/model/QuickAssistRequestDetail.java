package com.bosch.pai.retail.db.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Timestamp;

/**
 * Created by SJN8KOR on 1/25/2017.
 */
@Document(collection = "quick_assist_request_details")
public class QuickAssistRequestDetail {

    @Id
    private String id = new ObjectId().toHexString();
    @Field("REQUEST_ID")
    private String requestId;
    @Field("USER_ID")
    private String userId;
    @Field("REQUESTED_AT")
    private Timestamp requestedAt;
    @Field("RESPONDED_AT")
    private Timestamp respondedAt;
    @Field("LOCATION")
    private String location;
    @Field("IS_SERVED")
    private Boolean isServed;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getRequestedAt() {
        return requestedAt != null ? (Timestamp) requestedAt.clone() : null;
    }

    public void setRequestedAt(Timestamp requestedAt) {
        this.requestedAt = requestedAt != null ? (Timestamp) requestedAt.clone() : null;
    }

    public Timestamp getRespondedAt() {
        return respondedAt != null ? (Timestamp) respondedAt.clone() : null;
    }

    public void setRespondedAt(Timestamp respondedAt) {
        this.respondedAt = respondedAt != null ? (Timestamp) respondedAt.clone() : null;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getServed() {
        return isServed;
    }

    public void setServed(Boolean served) {
        isServed = served;
    }

    @Override
    public String toString() {
        return "QuickAssistRequestDetail{" +
                "requestId=" + requestId +
                ", userId=" + userId +
                ", requestedAt=" + requestedAt +
                ", respondedAt=" + respondedAt +
                ", location=" + location +
                ", isServed=" + isServed +
                '}';
    }
}
