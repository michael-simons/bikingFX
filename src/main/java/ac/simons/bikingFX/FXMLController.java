package ac.simons.bikingFX;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;

public class FXMLController implements Initializable {

    @FXML
    private TilePane test;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
	System.out.println("Dafuq is this?");
	for(int i=0;i<10;++i) {
	    System.out.println("Adding stuff");
	    test.getChildren().add(new ImageView("http://biking.michael-simons.eu/api/bikingPictures/23.jpg"));
	}
    }
    
    
    
}
