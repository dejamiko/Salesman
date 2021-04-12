package gui;

import graphs.Location;
import data.handout.AirbnbData;
import data.handout.AirbnbListing;
import graphs.Vertex;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import salesman.DistanceCalculationMethod;
import salesman.Distances;
import salesman.TSPCalculationMethod;
import salesman.antColony.AntGraph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller class for the fourth panel.
 *
 * @author Mikolaj Deja 20010020
 * @version 2021.04.02
 */
public class Controller {
    // Fields for holding gui objects
    @FXML
    private Slider slider;
    @FXML
    private ComboBox<DistanceCalculationMethod> distanceMethodChoice;
    @FXML
    private ComboBox<TSPCalculationMethod> methodChoice;
    @FXML
    private BorderPane middle;
    @FXML
    private Label bottomLabel;
    @FXML
    private Button runButton;
    @FXML
    private Button newListingsButton;
    @FXML
    private Button saveToFileButton;
    @FXML
    private CheckBox opt2CheckBox;

    // Whether the route is drawn
    private boolean routeDrawn;
    private int sliderValue;
    private Stage stage;
    private Group group;
    private HashMap<TSPCalculationMethod, Integer> possibleCalculations;

    // All lists responsible for holding listings
    private List<Location> allListings;
    private List<Location> listingList;
    private List<Location> resultList;

    /**
     * Constructor for the controller.
     */
    public Controller() {
        routeDrawn = false;
        loadListings();
        listingList = new ArrayList<>();
    }

    /**
     * Set the stage. Add listeners to changes in size to rescale
     * the route and points.
     *
     * @param stage The stage used.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            group = new Group();
            if (routeDrawn)
                drawPolygon(resultList);
            else
                drawListingsPoints(listingList);
        };
        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
    }

    /**
     * Initialise the GUI objects.
     */
    @FXML
    private void initialize() {
        group = new Group();
        populatePossibleCalculations();
        distanceMethodChoice.setItems(FXCollections.observableArrayList(DistanceCalculationMethod.values()));
        distanceMethodChoice.setValue(DistanceCalculationMethod.LAMBERT);

        methodChoice.setItems(FXCollections.observableArrayList(Arrays.asList(TSPCalculationMethod.values())));
        methodChoice.setValue(TSPCalculationMethod.CHRISTOFIDES_DOUBLE_EDGES);

        setSliderValues();

        bottomLabel.setVisible(false);
        sliderValue = (int) Math.round(slider.getValue());
        slider.setOnMouseReleased(e -> sliderMoved());
        slider.setValue(slider.getMax());
        sliderMoved();
        // Create the listing list
        createListingList(true);
        // Set the stage when initialising is completed
        Platform.runLater(() -> {
            setStage((Stage) saveToFileButton.getScene().getWindow());
            drawListingsPoints(listingList);
        });
    }

    /**
     * Set the maximal value on the slider.
     */
    @FXML
    private void setSliderValues() {
        int maxValue;

        if (opt2CheckBox.isSelected() && methodChoice.getValue() != TSPCalculationMethod.BRUTE_FORCE) {
            maxValue = possibleCalculations.get(methodChoice.getValue()) / 2;
        } else {
            maxValue = possibleCalculations.get(methodChoice.getValue());
        }

        slider.setMax(maxValue);
        slider.setMajorTickUnit(maxValue / 5.0);
        slider.setMinorTickCount(10);
        if (stage != null)
            sliderMoved();
    }

    /**
     * Method chosen, set slider values and disable 2opt if brute force was selected.
     */
    @FXML
    private void methodChosen() {
        setSliderValues();
        opt2CheckBox.setDisable(methodChoice.getValue() == TSPCalculationMethod.BRUTE_FORCE);
    }

    /**
     * Run the calculations on a new thread and display a calculating label.
     * Display results.
     */
    @FXML
    private void runButton() {
        setBottomLabel("Calculating...", 0.0);
        setDisabilityOfGuiElements(true);
        new Thread(() -> {
            createListingList(false);
            AntGraph graph = new AntGraph(listingList, new Distances(distanceMethodChoice.getValue()));
            try {
                graph.solveTravellingSalesmanProblem(methodChoice.getValue());
                if (opt2CheckBox.isSelected() && !opt2CheckBox.isDisabled()) {
                    graph.opt2();
                }
                resultList = graph.getRoute().stream().map(Vertex::getContents).collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("There was a problem calculating the route.");
                alert.setContentText("Try changing the value on the slider.");
                alert.showAndWait();
            }
            Platform.runLater(() -> {
                setBottomLabel("Distance calculated: ", graph.getDist());
                drawPolygon(resultList);
                setDisabilityOfGuiElements(false);
            });
        }).start();
    }

    /**
     * Set disable parameter of the elements of the gui that would be unsafe to use
     * with a concurrent calculation.
     *
     * @param disable The new value of disable.
     */
    private void setDisabilityOfGuiElements(boolean disable) {
        slider.setDisable(disable);
        runButton.setDisable(disable);
        newListingsButton.setDisable(disable);
        saveToFileButton.setDisable(disable);
    }

    /**
     * Create the listing list for all calculations with a given size.
     * Either use all listings or the favourite ones.
     *
     * @param randomise Whether the list is to be randomised.
     */
    private void createListingList(boolean randomise) {
        if (randomise) {
            Collections.shuffle(allListings);
        }
        listingList = new ArrayList<>(allListings.subList(0, Math.min(sliderValue, allListings.size())));
    }

    /**
     * Draw a polygon (route between the locations)
     *
     * @param locations The list of the locations to be drawn.
     */
    private void drawPolygon(List<Location> locations) {
        double[] coordinates = getCoordinates(locations);
        Polygon route = new Polygon(coordinates);
        route.setFill(Paint.valueOf("ffffff00"));
        route.setStroke(Paint.valueOf("909090"));
        route.setStrokeWidth(2.0);
        route.setVisible(true);
        group = new Group();
        group.getChildren().add(route);
        middle.setCenter(group);
        drawListingsPoints(locations);
        routeDrawn = true;
    }

    /**
     * Draw points for every listing.
     *
     * @param locations The list of the locations to be drawn.
     */
    @FXML
    private void drawListingsPoints(List<Location> locations) {
        double[] coordinates = getCoordinates(locations);
        for (int i = 0; i < coordinates.length; i += 2) {
            group.getChildren().add(new Circle(coordinates[i], coordinates[i + 1], 3));
        }
        middle.setCenter(group);
    }

    /**
     * If the slider changed value, create a list of listings and draw the points.
     */
    @FXML
    private void sliderMoved() {
        if (Math.round((int) slider.getValue()) != sliderValue) {
            group = new Group();
            sliderValue = Math.round((int) slider.getValue());
            createListingList(false);
            drawListingsPoints(listingList);
            routeDrawn = false;
        }
    }

    /**
     * Get new listings list and draw them as points.
     */
    @FXML
    private void newListings() {
        createListingList(true);
        routeDrawn = false;
        group = new Group();
        drawListingsPoints(listingList);
    }

    /**
     * Save the current route to a file chosen by the user.
     */
    @FXML
    private void saveToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Save the route");
        fileChooser.setInitialFileName("route");
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                int i = 1;
                writer.write("Your route is:\n");
                for (Location location : resultList) {
                    if (location instanceof AirbnbListing)
                        writer.write("\n" + i++ + ". " + location.getName() + " rented by " + ((AirbnbListing) location).getHost_name());
                }
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("There was a problem saving to file.");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    /**
     * Set the bottom label to show the distance.
     *
     * @param distance The distance to be displayed.
     */
    private void setBottomLabel(String initial, double distance) {
        if (distance != 0.0) {
            // Round to two decimal places
            distance = Math.round(distance * 100.0) / 100.0;
            initial = initial + distance + " km";
        }
        bottomLabel.setText(initial);
        bottomLabel.setVisible(true);
    }

    /**
     * Load all the listings from the AirbnbData.
     */
    private void loadListings() {
        allListings = new ArrayList<>(AirbnbData.getListingList());
    }

    /**
     * Get coordinates of all the places.
     *
     * @param locations The list of places.
     * @return The coordinates of places.
     */
    public double[] getCoordinates(List<Location> locations) {
        if (locations == null || locations.size() == 0)
            throw new IllegalArgumentException("getCoordinates had an empty or null input.");

        double[] coordinates = new double[locations.size() * 2];
        int i = 0;
        for (Location location : locations) {
            coordinates[i++] = location.getLongitude(); // x coordinate
            coordinates[i++] = location.getLatitude(); // y coordinate
        }

        return scaleCoordinates(coordinates, middle.getWidth() * 0.85, middle.getHeight() * 0.85);
    }

    /**
     * Normalize an array of coordinates using min-max normalisation.
     * <p>
     * https://en.wikipedia.org/wiki/Feature_scaling
     * <p>
     * I normalise x and y coordinates separately.
     *
     * @param coordinates The array to be normalised.
     */
    private void normalise(double[] coordinates) {
        if (coordinates == null || coordinates.length == 0)
            throw new IllegalArgumentException("normalise had an empty or null input.");

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (int i = 0; i < coordinates.length; ++i) {
            if (i % 2 == 0) {
                if (coordinates[i] < minX)
                    minX = coordinates[i];
                if (coordinates[i] > maxX)
                    maxX = coordinates[i];
            } else {
                if (coordinates[i] < minY)
                    minY = coordinates[i];
                if (coordinates[i] > maxY)
                    maxY = coordinates[i];
            }
        }

        for (int i = 0; i < coordinates.length; ++i) {
            if (i % 2 == 0)
                coordinates[i] = (coordinates[i] - minX) / (maxX - minX);
            else
                coordinates[i] = (coordinates[i] - minY) / (maxY - minY);
        }
    }

    /**
     * Scale the coordinates to match a given width and height
     * and shift them so that 0 is in the middle. Reflect over
     * the x-axis (JavaFX why is y growing downwards?)
     *
     * @param coordinates Coordinates to match.
     * @param width       Width of the result.
     * @param height      Height of the result.
     * @return The scaled array.
     */
    private double[] scaleCoordinates(double[] coordinates, double width, double height) {
        if (coordinates == null || coordinates.length == 0)
            throw new IllegalArgumentException("scaleCoordinates had an empty or null input.");

        normalise(coordinates);
        for (int i = 0; i < coordinates.length; ++i) {
            if (i % 2 == 0) {
                coordinates[i] = (coordinates[i] - 0.5) * width;
            } else {
                coordinates[i] = (coordinates[i] - 0.5) * height;
                coordinates[i] *= (-1);
            }
        }

        return coordinates;
    }

    /**
     * Populate the hashmap that keeps the method to number of
     * listings pairs.
     */
    private void populatePossibleCalculations() {
        possibleCalculations = new HashMap<>();
        possibleCalculations.put(TSPCalculationMethod.BRUTE_FORCE, 10);
        possibleCalculations.put(TSPCalculationMethod.ANT_COLONY_OPTIMISATION, 200);
        possibleCalculations.put(TSPCalculationMethod.CHRISTOFIDES_MATCHING, 400);
        possibleCalculations.put(TSPCalculationMethod.CHRISTOFIDES_DOUBLE_EDGES, 400);
        possibleCalculations.put(TSPCalculationMethod.IMPROVED_NEAREST_NEIGHBOUR, 200);
        possibleCalculations.put(TSPCalculationMethod.NEAREST_NEIGHBOUR, 500);
    }
}