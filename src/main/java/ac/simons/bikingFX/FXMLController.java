package ac.simons.bikingFX;

import ac.simons.bikingFX.bikingPictures.BikingPicture;
import ac.simons.bikingFX.bikingPictures.BikingPictureRetrievalTask;
import ac.simons.bikingFX.bikingPictures.FlipImageService;
import java.net.URL;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class FXMLController implements Initializable {
    
    public static class LoadedImageFilter implements Predicate<Node> {
	@Override
	public boolean test(Node node) {
	    return node instanceof StackPane && ((StackPane)node).getChildren().get(0).getUserData() != null;
	}	
    }
        

    @FXML
    private HBox test;

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
	final BikingPictureRetrievalTask bikingPictureRetrievalTask = new BikingPictureRetrievalTask();	
	bikingPictureRetrievalTask.setOnSucceeded(event -> {
	    bikingPictures.addAll((Collection<BikingPicture>) event.getSource().getValue());
	});
	new Thread(bikingPictureRetrievalTask).start();	
	
	// Prepare flipservice, depends on container so don't initialise in constructor
	this.flipImageService = new FlipImageService(this.bikingPictures, this.test, this.random);
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