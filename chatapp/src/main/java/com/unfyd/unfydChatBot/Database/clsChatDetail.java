package com.unfyd.unfydChatBot.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "chatData")
public class clsChatDetail {
    @DatabaseField(columnName = "id",generatedId = true)
    private int id;

    @DatabaseField(columnName = "user_Name")
    private String userName;
    @DatabaseField(columnName = "message")
    private  String message;
    @DatabaseField(columnName = "message_Type")
    private String messageType;
    @DatabaseField(columnName = "is_location_shared")
    private String isLocationShared;
    @DatabaseField(columnName = "latitude")
    private String latitude;
    @DatabaseField(columnName = "longitude")
    private String longitude;
    @DatabaseField(columnName = "response_Date_Time")
    private String responseDateTime;
    @DatabaseField(columnName = "created_Date_Time")
    private String createdDateTime;
    @DatabaseField(columnName = "is_File_Downloaded")
    private String isFileDownloaded;
    @DatabaseField(columnName = "file_Path")
    private String filePath;
    public String getIsFileDownloaded() {
        return isFileDownloaded;
    }

    public void setIsFileDownloaded(String isFileDownloaded) {
        this.isFileDownloaded = isFileDownloaded;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getIsLocationShared() {
        return isLocationShared;
    }

    public void setIsLocationShared(String isLocationShared) {
        this.isLocationShared = isLocationShared;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getResponseDateTime() {
        return responseDateTime;
    }

    public void setResponseDateTime(String responseDateTime) {
        this.responseDateTime = responseDateTime;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }
}
