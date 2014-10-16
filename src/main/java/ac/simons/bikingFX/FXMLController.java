package ac.simons.bikingFX;

import ac.simons.bikingFX.bikingPictures.BikingPicture;
import ac.simons.bikingFX.bikingPictures.BikingPictureRetrievalService;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class FXMLController implements Initializable {

    @FXML
    private HBox test;
    private final SimpleIntegerProperty numberOfHeaderElements = new SimpleIntegerProperty(this, "numberOfHeaderElements", 0);
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
	ObservableList<Node> answer = FXCollections.observableArrayList();
	final Logger logger = Logger.getLogger(this.getClass().getName());
	
	final BikingPictureRetrievalService service = new BikingPictureRetrievalService();
	service.stateProperty().addListener((Observable observable) -> {	
	    if (service.getState().equals(Worker.State.SUCCEEDED)) {
		answer.addAll(service.getValue().stream().map(BikingPicture::getSrc).map(ImageView::new).collect(Collectors.toList()));
	    }
	});
	
	test.widthProperty().addListener((observable, oldValue, newValue) -> {
	    final int numberOfNeededElements = (int) Math.ceil(newValue.doubleValue()/150.0)-test.getChildren().size();
	    
	    for(int i=0; i<numberOfNeededElements;++i) {
		final HBox box = new HBox(new ProgressIndicator());
		box.setFillHeight(false);
		box.setAlignment(Pos.CENTER);		
		box.setMinHeight(113);
		box.setMaxHeight(113);
		box.setMinWidth(150);
		box.setMaxWidth(150);
		test.getChildren().add(box);
	    }	   		    
	});
	
	// service.start();		
    } 
}
