package ac.simons.bikingFX;

import ac.simons.bikingFX.bikingPictures.BikingPicture;
import ac.simons.bikingFX.bikingPictures.BikingPictureRetrievalService;
import ac.simons.bikingFX.bikingPictures.CreateImageViewsTask;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

public class FXMLController implements Initializable {

    @FXML
    private HBox test;

    private final ObservableList<BikingPicture> bikingPictures = FXCollections.observableArrayList();   
    
    public FXMLController() {
	bikingPictures.addListener((Change<? extends BikingPicture> change) -> {
	    if (!change.getList().isEmpty()) {
		loadPictures();
	    }
	});	
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
	final Logger logger = Logger.getLogger(this.getClass().getName());

	// Start worker to retrieve the list of all available pictures
	final BikingPictureRetrievalService service = new BikingPictureRetrievalService();	
	service.setOnSucceeded(event -> {
	    bikingPictures.addAll((Collection<BikingPicture>) event.getSource().getValue());
	});
	service.start();
	
	test.widthProperty().addListener((observable, oldValue, newValue) -> {
	    loadPictures();
	});
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
	final CreateImageViewsTask currentCreateImageViewsTask = new CreateImageViewsTask(bikingPictures, boxes.size());	
	currentCreateImageViewsTask.getPartialResults().addListener((Change change) -> {
	    while(change.next()) {
		if(change.wasAdded() && boxes.size() > 0) {
		    change.getAddedSubList().forEach(imageView -> {
			children.set(children.indexOf(boxes.remove(0)), (Node)imageView);
		    });
		}
	    }
	});
	new Thread(currentCreateImageViewsTask).start();	
    }
}
