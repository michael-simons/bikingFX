/*
 * Copyright 2014 michael-simons.eu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ac.simons.bikingFX;

import ac.simons.bikingFX.api.JsonRetrievalTask;
import ac.simons.bikingFX.bikes.MilageChangeListener;
import ac.simons.bikingFX.bikes.Bike;
import ac.simons.bikingFX.bikingPictures.BikingPicture;
import ac.simons.bikingFX.bikingPictures.FlipImageService;
import ac.simons.bikingFX.gallery.GalleryPicture;
import ac.simons.bikingFX.gallery.GalleryPictureTableCell;
import ac.simons.bikingFX.common.ColorTableCell;
import ac.simons.bikingFX.common.LocalDateTableCell;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

/**
 * @author Michael J. Simons, 2014-10-07
 */
public class RootController implements Initializable {
    
    private static final Logger logger = Logger.getLogger(RootController.class.getName());
    
    public static class LoadedImageFilter implements Predicate<Node> {
	@Override
	public boolean test(Node node) {
	    return node instanceof StackPane && ((StackPane)node).getChildren().get(0).getUserData() != null;
	}	
    }        
    
    @FunctionalInterface
    public interface PasswordSupplier {
	public String getPassword(boolean refresh);
    }

    /** Used for popups and modal stages */
    private Stage primaryStage;
    /** Currently used application bundle */
    private ResourceBundle resources;
    
    @FXML
    private HBox bikingPicturesContainer;

    @FXML
    private TableView<Bike> viewBikes;
    @FXML
    private TableColumn<Bike, String> viewBikeName;
    @FXML
    private TableColumn<Bike, Color> viewBikeColor;
    @FXML
    private TableColumn<Bike, LocalDate> viewBikeBoughtOn;
    @FXML
    private TableColumn<Bike, LocalDate> viewBikeDecommissionedOn;
    @FXML
    private TableColumn<Bike, Integer> viewBikeMilage;
    
    @FXML
    private TableView<GalleryPicture> viewGalleryPictures;
    @FXML
    private TableColumn<GalleryPicture, LocalDate> viewGalleryPictureTakenOn;
    @FXML
    private TableColumn<GalleryPicture, String> viewGalleryPictureDescription;
    @FXML
    private TableColumn<GalleryPicture, Integer> viewGalleryPictureImage;
          
    private ObservableList<BikingPicture> bikingPictures;  
    
    private final Random random = new Random(System.currentTimeMillis());
    
    /** 
     * Used to store the password for biking.michael-simons.eu. The password is actually
     * stored in plain text. I propably wouldn't recommend that in a more serious 
     * application.
     */
    private final Preferences preferences = Preferences.userNodeForPackage(this.getClass());
    
    private FlipImageService flipImageService;
   
    @Override
    public void initialize(URL location, ResourceBundle resources) {
	this.resources = resources;
	
	bikingPictures = JsonRetrievalTask.get(BikingPicture::new, "/bikingPictures.json");
	// Start loading image views when pictures are available
	bikingPictures.addListener((Change<? extends BikingPicture> change) -> {
	    if (!change.getList().isEmpty()) {
		loadPictures();		
	    }
	});	
	
	// Load more images when size changes
	bikingPicturesContainer.widthProperty().addListener((observable, oldValue, newValue) -> {
	    loadPictures();
	});
	
	// Prepare flipservice, depends on container so don't initialise in constructor
	this.flipImageService = new FlipImageService(this.bikingPictures, this.bikingPicturesContainer, this.random);
	
	final MilageChangeListener addMilageController = new MilageChangeListener(this::retrievePassword, this::storePassword, this.resources);
	// Display a simple popup when adding milage fails
	addMilageController.setOnFailed(state -> {
	    final Popup popup = new Popup();
	    popup.setAutoFix(true);
	    popup.setAutoHide(true);
	    popup.setHideOnEscape(true);
	    final Label label = new Label(state.getSource().getException().getMessage());
	    label.setOnMouseClicked(e -> popup.hide());
	    label.getStylesheets().add("/css/default.css");
	    label.getStyleClass().add("error-notification");
	    popup.getContent().add(label);
	    popup.setOnShown(e -> {
		popup.setX(primaryStage.getX() + primaryStage.getWidth()/2 - popup.getWidth()/2);
		popup.setY(primaryStage.getY() + primaryStage.getHeight()/2 - popup.getHeight()/2);
	    });        
	    popup.show(primaryStage);
	});
	// Get all bikes
	final ObservableList<Bike> bikes = JsonRetrievalTask.get(Bike::new, "/bikes.json?all=true");
	// Configure milage controller for each bike
	bikes.addListener((Change<? extends Bike> change)  -> {
	    while(change.next()) {
		if(change.wasAdded()) {
		    change.getAddedSubList().forEach(bike -> bike.milageProperty().addListener(addMilageController));
		}
	    }
	});
		
	viewBikes.setItems(bikes);
	viewBikeName.setCellValueFactory(new PropertyValueFactory<>("name"));
	viewBikeColor.setCellValueFactory(new PropertyValueFactory<>("color"));
	viewBikeColor.setCellFactory(ColorTableCell::new);
	viewBikeBoughtOn.setCellValueFactory(new PropertyValueFactory<>("boughtOn"));
	viewBikeBoughtOn.setCellFactory(LocalDateTableCell::new);
	viewBikeDecommissionedOn.setCellValueFactory(new PropertyValueFactory<>("decommissionedOn"));	
	viewBikeDecommissionedOn.setCellFactory(LocalDateTableCell::new);
	viewBikeMilage.setCellValueFactory(new PropertyValueFactory<>("milage"));		
	viewBikeMilage.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
	
	viewGalleryPictures.setItems(JsonRetrievalTask.get(GalleryPicture::new, "/galleryPictures.json"));
	viewGalleryPictureTakenOn.setCellValueFactory(new PropertyValueFactory<>("takenOn"));
	viewGalleryPictureTakenOn.setCellFactory(LocalDateTableCell::new);
	viewGalleryPictureDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
	viewGalleryPictureDescription.setCellFactory((TableColumn<GalleryPicture, String> column) -> {	    
	    final TableCell<GalleryPicture, String> cell =  new TableCell<>();
	    final Text text = new Text();
	    cell.setGraphic(text);
	    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
	    // Bind wrapping width of the text to the actual width of the cell
	    text.wrappingWidthProperty().bind(cell.widthProperty());
	    // Update text with content from cell
	    text.textProperty().bind(cell.itemProperty());	   
	    return cell;
	});	
	// Bind prefered width of column to width of table minus the first column to fill up the remaining space.
	viewGalleryPictureDescription.prefWidthProperty().bind(
	    viewGalleryPictures.widthProperty()
		.subtract(viewGalleryPictureTakenOn.prefWidthProperty())
		.subtract(viewGalleryPictureImage.widthProperty())
	);
	
	viewGalleryPictureImage.setCellValueFactory(new PropertyValueFactory<>("id"));
	viewGalleryPictureImage.setCellFactory(GalleryPictureTableCell::new);	
	// Fill the remainig space of the table
	viewGalleryPictureImage.prefWidthProperty().bind(
	    viewGalleryPictures.widthProperty()
		.subtract(viewGalleryPictureTakenOn.widthProperty())
		.subtract(viewGalleryPictureDescription.widthProperty())
	);
    }

    public void setPrimaryStage(Stage primaryStage) {
	this.primaryStage = primaryStage;
    }
 
    public String retrievePassword() {	
	String password = this.preferences.get("password", "");	
	if(password.isEmpty()) {
	    try {		
		final Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(this.primaryStage);
		dialogStage.setResizable(false);
		dialogStage.setTitle(resources.getString("enterPasswordDialog.title"));		
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/enterPasswordDialog.fxml"), resources);		
		dialogStage.setScene(new Scene(loader.load()));				
		dialogStage.showAndWait();
		password = ((EnterPasswordDialogController)loader.getController()).getPassword();		
	    } catch (IOException ex) {	
		logger.log(Level.WARNING, "Had some problems retrieving a password", ex);
	    }	    
	}
	return password;
    }
 
    public void storePassword(final Optional<String> password) {	
	try {
	    if(!password.isPresent() || password.get().isEmpty()) {
		this.preferences.remove("password");
	    } else {
		this.preferences.put("password", password.get());
	    }
	    this.preferences.flush();
	} catch (BackingStoreException ex) {
	    logger.log(Level.WARNING, "Had some problems storing a password", ex);
	}	
    }
    
    final void loadPictures() {
	final ObservableList<Node> children = bikingPicturesContainer.getChildren();
	final int numberOfNeededElements = (int) Math.ceil(bikingPicturesContainer.getWidth() / 150.0) - children.size();
	if(bikingPictures.isEmpty() || numberOfNeededElements <= 0) {	    
	    return;
	}
		
	// Get currently loaded images
	final Set<BikingPicture> loadedBikingPictures = bikingPicturesContainer
		.getChildren().stream()
		.filter(new LoadedImageFilter())
		.map(node -> (BikingPicture)((StackPane)node).getChildren().get(0).getUserData())
		.collect(Collectors.toSet());	
	final List<BikingPicture> available = bikingPictures.filtered(bikingPicture -> !loadedBikingPictures.contains(bikingPicture));	
	int i = 0, remainingNumberOfPictures = available.size();
	int neededViewsLeft = numberOfNeededElements;	
	while (neededViewsLeft > 0 && remainingNumberOfPictures > 0) {	    
	    int rand = random.nextInt(remainingNumberOfPictures);
	    if (rand < neededViewsLeft) {				
		final ProgressIndicator progressIndicator = new ProgressIndicator();
		
		final HBox box = new HBox(progressIndicator);
		box.setFillHeight(false);
		box.setAlignment(Pos.CENTER);
		box.setMinHeight(113);
		box.setMaxHeight(113);
		box.setMinWidth(150);
		box.setMaxWidth(150);
		
		final StackPane stackPane = new StackPane(box);
		final BikingPicture bikingPicture = available.get(i);
		final Image image = new Image(bikingPicture.getSrc(), 150, 113, true, true, true);
		progressIndicator.progressProperty().bind(image.progressProperty());		
		image.progressProperty().addListener((observable, oldValue, newValue) -> {
		    if(newValue.intValue() == 1) {
			final ImageView imageView = new ImageView(image);			
			imageView.setUserData(bikingPicture);
			stackPane.getChildren().set(0, imageView);
		    }
		});
 
		children.add(stackPane);		
		neededViewsLeft--;
	    }
	    remainingNumberOfPictures--;
	    i++;
	}	
	
	if(flipImageService != null && !flipImageService.isRunning()) {
	    flipImageService.start();
	}
    }    
}