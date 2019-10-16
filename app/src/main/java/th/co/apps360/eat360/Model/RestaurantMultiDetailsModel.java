package th.co.apps360.eat360.Model;

/**
 * Created by dan on 11/28/16.
 */

public class RestaurantMultiDetailsModel {

    private Integer id;
    private String name;
    private String url;
    private String email;
    private String phone;
    private String fax;
    private Double latitude;
    private Double longitude;
    private Integer postcode;
    private String address;
    private String city1;
    private String city2;
    private String state;
    private String country;
    private String openinghour;
    private String opentime;
    private String closetime;
//    private String paid_to;
//    private Boolean is_active;
    private String color_title;
    private String owner_id;
    private Integer typeid;
    private String typename;
    private String imagePath;

    public RestaurantMultiDetailsModel(Integer id, String name, String url, String email,
                                       String phone, String fax, Double latitude, Double longitude,
                                       Integer postcode, String address, String city1, String city2,
                                       String state, String country, String openinghour,
                                       String opentime, String closetime,
//                                       String paidto, Boolean isactive,
                                       String colortitle, String ownerid,
                                       String imagepath, Integer typeid, String typename) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.email = email;
        this.phone = phone;
        this.fax = fax;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postcode = postcode;
        this.address = address;
        this.city1 = city1;
        this.city2 = city2;
        this.state = state;
        this.country = country;
        this.openinghour = openinghour;
        this.opentime = opentime;
        this.closetime = closetime;
//        this.paid_to = paidto;
//        this.is_active = isactive;
        this.color_title = colortitle;
        this.owner_id = ownerid;
        this.typeid = typeid;
        this.typename = typename;
        this.imagePath = imagepath;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Integer getPostcode() {
        return postcode;
    }

    public String getAddress() {
        return address;
    }

    public String getCity1() {
        return city1;
    }

    public String getCity2() {
        return city2;
    }

    public String getCountry() {
        return country;
    }

    public Integer getTypeid() {
        return typeid;
    }

    public String getClosetime() {
        return closetime;
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

    public String getOpeninghour() {
        return openinghour;
    }

    public String getOpentime() {
        return opentime;
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

}
