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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.application.Application;

import static javafx.application.Application.launch;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Michael J. Simons, 2014-10-07
 */
public class BikingFX extends Application {

    @Override
    public void start(final Stage stage) throws Exception {	
	ResourceBundle bundle;
	final String bundleName = "bundles.BikingFX";
	try {	    
	    bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
	} catch(MissingResourceException e) {
	    Locale.setDefault(Locale.ENGLISH);
	    bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
	}
	
        stage.setTitle("BikingFX");
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/fxml/root.fxml"), bundle)));
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
