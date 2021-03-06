package data.handout;

import graphs.Location;

/**
 * Represents one listing of a property for rental on Airbnb.
 * This is essentially one row in the data table. Each column
 * has a corresponding field.
 *
 * @author Handout, edited by Nicole Lehchevska 20041914
 */
public class AirbnbListing extends Location {
    /**
     * The id and name of the individual property
     */
    private String id;
    /**
     * The id and name of the host for this listing.
     * Each listing has only one host, but one host may
     * list many properties.
     */
    private String host_id;
    private String host_name;

    /**
     * The grouped location to where the listed property is situated.
     * For this data set, it is a london borough.
     */
    private String neighbourhood;

    /**
     * The type of property, either "Private room" or "Entire Home/apt".
     */
    private String room_type;

    /**
     * The price per night's stay
     */
    private int price;

    /**
     * The minimum number of nights the listed property must be booked for.
     */
    private int minimumNights;
    private int numberOfReviews;

    /**
     * The date of the last review, but as a String
     */
    private String lastReview;
    private double reviewsPerMonth;

    /**
     * The total number of listings the host holds across AirBnB
     */
    private int calculatedHostListingsCount;
    /**
     * The total number of days in the year that the property is available for
     */
    private int availability365;



    public AirbnbListing(String id, String name, String host_id,
                         String host_name, String neighbourhood, double latitude,
                         double longitude, String room_type, int price,
                         int minimumNights, int numberOfReviews, String lastReview,
                         double reviewsPerMonth, int calculatedHostListingsCount, int availability365) {
        super(name + " " + id, latitude, longitude);
        this.id = id;
        this.host_id = host_id;
        this.host_name = host_name;
        this.neighbourhood = neighbourhood;
        this.room_type = room_type;
        this.price = price;
        this.minimumNights = minimumNights;
        this.numberOfReviews = numberOfReviews;
        this.lastReview = lastReview;
        this.reviewsPerMonth = reviewsPerMonth;
        this.calculatedHostListingsCount = calculatedHostListingsCount;
        this.availability365 = availability365;
    }

    public String getId() {
        return id;
    }

    public String getHost_name() {
        return host_name;
    }

    // I presume that ID determines objects in this database

    /**
     * Equality of objects of type AirbnbListing
     *
     * @param other Other object to compare to.
     * @return True if two objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AirbnbListing))
            return false;
        else
            return ((AirbnbListing) other).getId().equals(this.getId());
    }

    /**
     * Get hashcode of this object.
     *
     * @return The hashcode of this object.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
