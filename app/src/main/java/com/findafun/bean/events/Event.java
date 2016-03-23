
package com.findafun.bean.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Event implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("event_category_id")
    @Expose
    private String eventCategoryId;
    @SerializedName("event_name")
    @Expose
    private String eventName;
    @SerializedName("event_venue")
    @Expose
    private String eventVenue;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("category_img")
    @Expose
    private String categoryImg;
    @SerializedName("event_logo")
    @Expose
    private String eventLogo;
    @SerializedName("event_banner")
    @Expose
    private String eventBanner;
    @SerializedName("event_latitude")
    @Expose
    private String eventLatitude;
    @SerializedName("event_longitude")
    @Expose
    private String eventLongitude;
    @SerializedName("isAd")
    @Expose
    private String isAd;
    @SerializedName("event_cost")
    @Expose
    private String event_cost;
    @SerializedName("contact_email")
    @Expose
    private String eventEmail;

    public String getEventEmail() {
        return eventEmail;
    }

    public void setEventEmail(String eventEmail) {
        this.eventEmail = eventEmail;
    }

    public String getIsAd() {
        return isAd;
    }

    public String getEvent_cost() {
        return event_cost;
    }

    public void setEvent_cost(String event_cost) {
        this.event_cost = event_cost;
    }

    public void setIsAd(String isAd) {
        this.isAd = isAd;
    }

    public String getEventLatitude() {
        return eventLatitude;
    }

    public void setEventLatitude(String eventLatitude) {
        this.eventLatitude = eventLatitude;
    }

    public String getEventLongitude() {
        return eventLongitude;
    }

    public void setEventLongitude(String eventLongitude) {
        this.eventLongitude = eventLongitude;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The eventCategoryId
     */
    public String getEventCategoryId() {
        return eventCategoryId;
    }

    /**
     * @param eventCategoryId The event_category_id
     */
    public void setEventCategoryId(String eventCategoryId) {
        this.eventCategoryId = eventCategoryId;
    }

    /**
     * @return The eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * @param eventName The event_name
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * @return The eventVenue
     */
    public String getEventVenue() {
        return eventVenue;
    }

    /**
     * @param eventVenue The event_venue
     */
    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate The start_date
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return The endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate The end_date
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * @return The contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * @param contact The contact
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * @return The categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName The category_name
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return The categoryImg
     */
    public String getCategoryImg() {
        return categoryImg;
    }

    /**
     * @param categoryImg The category_img
     */
    public void setCategoryImg(String categoryImg) {
        this.categoryImg = categoryImg;
    }

    /**
     * @return The eventLogo
     */
    public String getEventLogo() {
        return eventLogo;
    }

    /**
     * @param eventLogo The event_logo
     */
    public void setEventLogo(String eventLogo) {
        this.eventLogo = eventLogo;
    }

    /**
     * @return The eventBanner
     */
    public String getEventBanner() {
        return eventBanner;
    }

    /**
     * @param eventBanner The event_banner
     */
    public void setEventBanner(String eventBanner) {
        this.eventBanner = eventBanner;
    }

}
