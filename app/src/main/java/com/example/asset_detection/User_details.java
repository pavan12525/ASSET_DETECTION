package com.example.asset_detection;

public class User_details
{

    private String mac_address;
    private boolean status_device;
    private String user_id;
    private String latitude;
    private String longitude;

    public User_details()
    {

    }
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    User_details(String mac_address, boolean status_device, String user_id, String latitude, String longitude)
    {
        this.mac_address=mac_address;
        this.status_device=status_device;
        this.user_id=user_id;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public boolean isStatus_device() {
        return status_device;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setStatus_device(boolean status_device) {
        this.status_device = status_device;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }
}
