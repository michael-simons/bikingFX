module ac.simons.bikingFX {
	requires java.logging;
	requires java.net.http;
	requires java.prefs;
	requires jdk.crypto.ec;

	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires javafx.web;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;

	opens ac.simons.bikingFX to javafx.fxml;

	exports ac.simons.bikingFX;
	exports ac.simons.bikingFX.bikes;
	exports ac.simons.bikingFX.bikingPictures;
	exports ac.simons.bikingFX.gallery;
	exports ac.simons.bikingFX.tracks;
}
