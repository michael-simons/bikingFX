package ac.simons.bikingFX;

import ac.simons.bikingFX.api.JsonRetrievalTask;
import ac.simons.bikingFX.bikes.Bike;
import ac.simons.bikingFX.bikingPictures.BikingPicture;
import ac.simons.bikingFX.bikingPictures.FlipImageService;
import ac.simons.bikingFX.renderer.ColorTableCell;
import ac.simons.bikingFX.renderer.LocalDateTableCell;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class FXMLController implements Initializable {
    
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
    
    private final ObservableList<BikingPicture> bikingPictures = FXCollections.observableArrayList();   
    private final Random random = new Random(System.currentTimeMillis());
    
    private FlipImageService flipImageService;
   
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

	// Start task to retrieve the list of all available pictures
	final JsonRetrievalTask<BikingPicture> bikingPictureRetrievalTask = new JsonRetrievalTask<>(BikingPicture::new, "/bikingPictures.json");
	bikingPictureRetrievalTask.setOnSucceeded(event -> {
	    bikingPictures.addAll((Collection<BikingPicture>) event.getSource().getValue());
	});
	new Thread(bikingPictureRetrievalTask).start();	
	
	// Prepare flipservice, depends on container so don't initialise in constructor
	this.flipImageService = new FlipImageService(this.bikingPictures, this.test, this.random);
	
	viewBikes.setItems(getObservableList());
	viewBikeName.setCellValueFactory(new PropertyValueFactory<>("name"));
	viewBikeColor.setCellValueFactory(new PropertyValueFactory<>("color"));
	viewBikeColor.setCellFactory(ColorTableCell::create);
	viewBikeBoughtOn.setCellValueFactory(new PropertyValueFactory<>("boughtOn"));
	viewBikeBoughtOn.setCellFactory(LocalDateTableCell::create);
	viewBikeDecommissionedOn.setCellValueFactory(new PropertyValueFactory<>("decommissionedOn"));	
	viewBikeDecommissionedOn.setCellFactory(LocalDateTableCell::create);
	viewBikeMilage.setCellValueFactory(new PropertyValueFactory<>("milage"));	
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
    
     ObservableList<Bike> getObservableList()  {
        final ObservableList<Bike> rv = FXCollections.observableArrayList();
	
	final JsonRetrievalTask<Bike> bikesRetrievalTask = new JsonRetrievalTask<>(Bike::new, "/bikes.json?all=true");
	bikesRetrievalTask.setOnSucceeded(state -> {
	    rv.addAll((Collection<Bike>)state.getSource().getValue());
	});
	new Thread(bikesRetrievalTask).start();	
        return rv;
    }
}