package ac.simons.bikingFX;

import ac.simons.bikingFX.bikingPictures.BikingPicture;
import ac.simons.bikingFX.bikingPictures.BikingPictureRetrievalService;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;

public class FXMLController implements Initializable {

    @FXML
    private HBox test;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
	ObservableList<Node> answer = FXCollections.observableArrayList();
	
	final BikingPictureRetrievalService service = new BikingPictureRetrievalService();
	service.stateProperty().addListener((Observable observable) -> {	
	    if (service.getState().equals(Worker.State.SUCCEEDED)) {
		answer.addAll(service.getValue().stream().map(BikingPicture::getSrc).map(ImageView::new).collect(Collectors.toList()));
	    }
	});
	
	Bindings.bindContentBidirectional(answer, test.getChildren());
	service.start();	
    } 
}
