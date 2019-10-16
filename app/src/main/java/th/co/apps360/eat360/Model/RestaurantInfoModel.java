package th.co.apps360.eat360.Model;

import java.util.ArrayList;

/**
 * Created by dan on 12/12/16.
 */

public class RestaurantInfoModel {

    private String id;
    private String name;
    private String desc;
    private String url;
    private String email;
    private String phone;
    private String fax;
    private Double latitude;
    private Double longitude;
    private String postcode;
    private String address;
    private String city;
    private String state;
    private String country;
    private ArrayList<RestaurantTimeModel> openinghours;
    private ArrayList<RestaurantClosedayModel> weekenddates;
    private ArrayList<FacilityModel> facilities;
    private String paid_to;
    private Boolean is_active;
    private String color_title;
    private String owner_id;
    private String typeid;
    private String typename;
    private String imagePath;
    private ArrayList<String> images;
    private String facebooklink;
    private String instagramlink;
    private String twitterlink;
    private String googlelink;

    public RestaurantInfoModel(String id, String name, String desc, String url, String email,
                               String phone, String fax, Double latitude, Double longitude,
                               String postcode, String address, String city,
                               String state, String country, ArrayList<RestaurantTimeModel> openinghours,
                               ArrayList<RestaurantClosedayModel> weekenddates, ArrayList<FacilityModel> facilities,
                               String paidto, Boolean isactive, String colortitle, String ownerid,
                               String typeid, String typename, String imagepath, ArrayList<String> images,
                               String facebooklink, String instagramlink, String twitterlink, String googlelink) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.url = url;
        this.email = email;
        this.phone = phone;
        this.fax = fax;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postcode = postcode;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.openinghours = openinghours;
        this.weekenddates = weekenddates;
        this.facilities = facilities;
        this.paid_to = paidto;
        this.is_active = isactive;
        this.color_title = colortitle;
        this.owner_id = ownerid;
        this.typeid = typeid;
        this.typename = typename;
        this.imagePath = imagepath;
        this.images = images;
        this.facebooklink = facebooklink;
        this.instagramlink = instagramlink;
        this.twitterlink = twitterlink;
        this.googlelink = googlelink;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getTypeid() {
        return typeid;
    }

    public String getColor_title() {
        return color_title;
    }

    public String getEmail() {
        return email;
    }

    public String getFax() {
        return fax;
    }

    public String getImagePath() {
        return imagePath;
    }

    public ArrayList<RestaurantTimeModel> getOpeninghours() {
        return openinghours;
    }

    public ArrayList<RestaurantClosedayModel> getWeekenddates() {
        return weekenddates;
    }

    public ArrayList<FacilityModel> getFacilities() {
        return facilities;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public String getPhone() {
        return phone;
    }

    public String getState() {
        return state;
    }

    public String getTypename() {
        return typename;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public String getPaid_to() {
        return paid_to;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public String getFacebooklink() {
        return facebooklink;
    }

    public String getInstagramlink() {
        return instagramlink;
    }

    public String getTwitterlink() {
        return twitterlink;
    }

    public String getGooglelink() {
        return googlelink;
    }

}
