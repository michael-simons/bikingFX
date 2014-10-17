package ac.simons.bikingFX;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.application.Application;

import static javafx.application.Application.launch;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {	
	ResourceBundle bundle;
	final String bundleName = "bundles.BikingFX";
	try {	    
	    bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
	} catch(MissingResourceException e) {
	    Locale.setDefault(Locale.ENGLISH);
	    bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
	}
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"), bundle);
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("BikingFX");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
