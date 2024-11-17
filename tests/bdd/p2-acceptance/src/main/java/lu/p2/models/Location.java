package lu.p2.models;

public class Location {
    private String address;
    private String latitude;
    private String longitude;
    private String cityLatitude;
    private String cityLongitude;

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(final String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(final String longitude) {
        this.longitude = longitude;
    }

    public String getCityLatitude() {
        return cityLatitude;
    }

    public void setCityLatitude(final String cityLatitude) {
        this.cityLatitude = cityLatitude;
    }

    public String getCityLongitude() {
        return cityLongitude;
    }

    public void setCityLongitude(final String cityLongitude) {
        this.cityLongitude = cityLongitude;
    }
}
