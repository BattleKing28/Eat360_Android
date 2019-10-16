package th.co.apps360.eat360.Model;


/**
 * Created by Dan on 2/17/17.
 */

public class LocationModel {

    private double latitude;
    private double longitude;
    private String city;
    private String country;
    private String postcode;
    private String address;

    public LocationModel(double latitude, double longitude, String city, String country, String postcode, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.country = country;
        this.postcode = postcode;
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getAddress() {
        return address;
    }

}
