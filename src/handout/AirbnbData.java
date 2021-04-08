package handout;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for loading and storing the AirBnB data.
 *
 * @author Handout, edited by Nicole Lehchevska 20041914 and Mikolaj Deja
 * @version 2021.04.01
 */
public class AirbnbData {

    private static final List<AirbnbListing> listingList;

    static {
        listingList = load();
    }

    /**
     * Returns the list of listings as an unmodifiable list.
     *
     * @return The unmodifiable list for all listings.
     */
    public static List<AirbnbListing> getListingList() {
        return Collections.unmodifiableList(listingList);
    }

    /**
     * Return a List containing the rows in the AirBnB London data set csv file.
     *
     * @return The list of the loaded listings.
     */
    public static List<AirbnbListing> load() {

        List<AirbnbListing> listings = new ArrayList<>();
        try {
            URL url = AirbnbData.class.getResource("airbnb-london.csv");
            CSVReader reader = new CSVReader(new FileReader(new File(url.toURI()).getAbsolutePath()));
            String[] line;
            //skip the first row (column headers)
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                String id = line[0];
                String name = line[1];
                String host_id = line[2];
                String host_name = line[3];
                String neighbourhood = line[4];
                double latitude = convertDouble(line[5]);
                double longitude = convertDouble(line[6]);
                String room_type = line[7];
                int price = convertInt(line[8]);
                int minimumNights = convertInt(line[9]);
                int numberOfReviews = convertInt(line[10]);
                String lastReview = line[11];
                double reviewsPerMonth = convertDouble(line[12]);
                int calculatedHostListingsCount = convertInt(line[13]);
                int availability365 = convertInt(line[14]);


                AirbnbListing listing = new AirbnbListing(id, name, host_id,
                        host_name, neighbourhood, latitude, longitude, room_type,
                        price, minimumNights, numberOfReviews, lastReview,
                        reviewsPerMonth, calculatedHostListingsCount, availability365
                );
                listings.add(listing);
            }
        } catch (IOException | URISyntaxException e) {
            System.out.println("Failure! Something went wrong");
            e.printStackTrace();
        }
        return listings;
    }

    /**
     * @param doubleString the string to be converted to Double type
     * @return the Double value of the string, or -1.0 if the string is
     * either empty or just whitespace
     */
    private static Double convertDouble(String doubleString) {
        if (doubleString != null && !doubleString.trim().equals(""))
            return Double.parseDouble(doubleString);
        return -1.0;
    }

    /**
     * @param intString the string to be converted to Integer type
     * @return the Integer value of the string, or -1 if the string is
     * either empty or just whitespace
     */
    private static Integer convertInt(String intString) {
        if (intString != null && !intString.trim().equals("")) {
            return Integer.parseInt(intString);
        }
        return -1;
    }

}