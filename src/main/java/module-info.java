module bikingFX {
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;

	requires java.logging;
	requires java.net.http;
	requires java.prefs;

	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.web;

	opens ac.simons.bikingFX to javafx.fxml;
	exports ac.simons.bikingFX;
	exports ac.simons.bikingFX.bikes;
	exports ac.simons.bikingFX.bikingPictures;
	exports ac.simons.bikingFX.gallery;
	exports ac.simons.bikingFX.tracks;
}
