package th.co.apps360.eat360.Model;

/**
 * Created by dan on 11/28/16.
 */

public class RestaurantSearchResultModel {

    private String id;
    private String name;
    private String type;
    private Double latitude;
    private Double longitude;
    private String url;
    private String fax;
    private String email;
    private String postcode;
    private String address;
    private String country;
    private String state;
    private String city;
    private String imagepath;

    public RestaurantSearchResultModel(String id, String name, String type,
                                       Double latitude, Double longitude,
                                       String url, String fax, String email,
                                       String postcode, String address,
                                       String country, String state, String city,
                                       String imagepath) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.url = url;
        this.fax = fax;
        this.email = email;
        this.postcode = postcode;
        this.address = address;
        this.country = country;
        this.state = state;
        this.city = city;
        this.imagepath = imagepath;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getUrl() {
        return url;
    }

    public String getFax() {
        return fax;
    }

    public String getEmail() {
        return email;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getImagePath() {
        return imagepath;
    }
}
