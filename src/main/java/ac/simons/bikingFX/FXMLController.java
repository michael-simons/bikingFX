package ac.simons.bikingFX;

import ac.simons.bikingFX.bikingPictures.BikingPicture;
import ac.simons.bikingFX.bikingPictures.BikingPictureRetrievalTask;
import ac.simons.bikingFX.bikingPictures.CreateImageViewsTask;
import ac.simons.bikingFX.bikingPictures.FlipImageService;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class FXMLController implements Initializable {

    @FXML
    private HBox test;

    private final ObservableList<BikingPicture> bikingPictures = FXCollections.observableArrayList();   
    
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
	this.flipImageService = new FlipImageService(this.bikingPictures, this.test);
    }

    final void loadPictures() {
	final ObservableList<Node> children = test.getChildren();
	final int numberOfNeededElements = (int) Math.ceil(test.getWidth() / 150.0) - children.size();
	if(bikingPictures.isEmpty() || numberOfNeededElements <= 0) {	    
	    return;
	}
		
	final List<Node> boxes = new ArrayList<>();
	for (int i = 0; i < numberOfNeededElements; ++i) {
	    final HBox box = new HBox(new ProgressIndicator());
	    box.setFillHeight(false);
	    box.setAlignment(Pos.CENTER);
	    box.setMinHeight(113);
	    box.setMaxHeight(113);
	    box.setMinWidth(150);
	    box.setMaxWidth(150);
	    boxes.add(box);
	    
	}
	children.addAll(boxes);
	
	// Now retrieve the images themself, one for every box
	final CreateImageViewsTask createImageViewsTask = new CreateImageViewsTask(bikingPictures, boxes.size());	
	createImageViewsTask.getPartialResults().addListener((Change change) -> {
	    while(change.next()) {
		if(change.wasAdded() && boxes.size() > 0) {

		    change.getAddedSubList().forEach(imageView -> {						
			children.set(children.indexOf(boxes.remove(0)), new StackPane((Node)imageView));
		    });
		}
	    }
	});
	
	// Start flipservice if not running after images are loaded for the 1st time
	createImageViewsTask.setOnSucceeded(state -> {
	    if(flipImageService != null && !flipImageService.isRunning()) {
		flipImageService.start();
	    }
	});
	new Thread(createImageViewsTask).start();	
    }        
}