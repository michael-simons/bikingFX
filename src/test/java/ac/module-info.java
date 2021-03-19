open module bikingFX {
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;

	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.web;

	requires org.junit.jupiter.api;

	exports ac.simons.bikingFX.bikes;
	exports ac.simons.bikingFX.bikingPictures;
	exports ac.simons.bikingFX.gallery;
}
