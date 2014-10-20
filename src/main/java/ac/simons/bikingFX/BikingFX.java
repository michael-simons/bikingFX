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
	ResourceBundle resources;
	final String bundleName = "bundles.BikingFX";
	try {
	    resources = ResourceBundle.getBundle(bundleName, Locale.getDefault());
	} catch (MissingResourceException e) {
	    Locale.setDefault(Locale.ENGLISH);
	    resources = ResourceBundle.getBundle(bundleName, Locale.getDefault());
	}

	// Get hold of the loader (don't use the factory methods)
	final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/root.fxml"), resources);

	// Load scene
	stage.setTitle("BikingFX");
	stage.setScene(new Scene(loader.load()));

	// Retrieve controller
	final MainController mainController = loader.getController();
	mainController.setPrimaryStage(stage);

	stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	launch(args);
    }
}
