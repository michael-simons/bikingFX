/*
 * Copyright 2014-2020 michael-simons.eu.
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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Michael J. Simons
 * @since 2014-10-07
 */
public class BikingFX extends Application {

	public static final String HOST_AND_PORT = "https://biking.michael-simons.eu";
	public static final String BASE_URL = HOST_AND_PORT + "/api";

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

		// Load scene
		stage.setTitle("BikingFX");
		stage.setScene(new Scene(FXMLLoader.load(BikingFX.class.getResource("/fxml/root.fxml"), resources)));
		stage.show();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
