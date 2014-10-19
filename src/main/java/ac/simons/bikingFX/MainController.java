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
import ac.simons.bikingFX.bikes.Bike;
import ac.simons.bikingFX.bikingPictures.BikingPicture;
import ac.simons.bikingFX.bikingPictures.FlipImageService;
import ac.simons.bikingFX.gallery.GalleryPicture;
import ac.simons.bikingFX.gallery.GalleryPictureTableCell;
import ac.simons.bikingFX.common.ColorTableCell;
import ac.simons.bikingFX.common.LocalDateTableCell;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * @author Michael J. Simons, 2014-10-07
 */
public class MainController implements Initializable {
    
    public static class LoadedImageFilter implements Predicate<Node> {
	@Override
	public boolean test(Node node) {
	    return node instanceof StackPane && ((StackPane)node).getChildren().get(0).getUserData() != null;
	}	
    }        

    @FXML
    private HBox test;

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
    
    private FlipImageService flipImageService;
   
    @Override
    public void initialize(URL location, ResourceBundle resources) {
	bikingPictures = JsonRetrievalTask.get(BikingPicture::new, "/bikingPictures.json");
	// Start loading image views when pictures are available
	bikingPictures.addListener((Change<? extends BikingPicture> change) -> {
	    if (!change.getList().isEmpty()) {
		loadPictures();		
	    }
	});	
	
	// Load more images when size changes
	test.widthProperty().addListener((observable, oldValue, newValue) -> {
	    loadPictures();
	});
	
	// Prepare flipservice, depends on container so don't initialise in constructor
	this.flipImageService = new FlipImageService(this.bikingPictures, this.test, this.random);
	
	viewBikes.setItems(JsonRetrievalTask.get(Bike::new, "/bikes.json?all=true"));
	viewBikeName.setCellValueFactory(new PropertyValueFactory<>("name"));
	viewBikeColor.setCellValueFactory(new PropertyValueFactory<>("color"));
	viewBikeColor.setCellFactory(ColorTableCell::new);
	viewBikeBoughtOn.setCellValueFactory(new PropertyValueFactory<>("boughtOn"));
	viewBikeBoughtOn.setCellFactory(LocalDateTableCell::new);
	viewBikeDecommissionedOn.setCellValueFactory(new PropertyValueFactory<>("decommissionedOn"));	
	viewBikeDecommissionedOn.setCellFactory(LocalDateTableCell::new);
	viewBikeMilage.setCellValueFactory(new PropertyValueFactory<>("milage"));	
	
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

    final void loadPictures() {
	final ObservableList<Node> children = test.getChildren();
	final int numberOfNeededElements = (int) Math.ceil(test.getWidth() / 150.0) - children.size();
	if(bikingPictures.isEmpty() || numberOfNeededElements <= 0) {	    
	    return;
	}
		
	// Get currently loaded images
	final Set<BikingPicture> loadedBikingPictures = test
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