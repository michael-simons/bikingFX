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

import static java.lang.Math.*;
import static java.lang.String.format;
import static javafx.beans.binding.Bindings.*;

import ac.simons.bikingFX.bikes.Bike;
import ac.simons.bikingFX.bikes.MilageChangeListener;
import ac.simons.bikingFX.bikingPictures.BikingPicture;
import ac.simons.bikingFX.bikingPictures.FlipImageService;
import ac.simons.bikingFX.common.ColorTableCell;
import ac.simons.bikingFX.common.LocalDateTableCell;
import ac.simons.bikingFX.gallery.GalleryPicture;
import ac.simons.bikingFX.gallery.GalleryPictureTableCell;
import ac.simons.bikingFX.tracks.Track;
import ac.simons.bikingFX.tracks.Track.Type;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author Michael J. Simons
 * @since 2014-10-07
 */
public class RootController implements Initializable {

	private static final Logger logger = Logger.getLogger(RootController.class.getName());

	public static class LoadedImageFilter implements Predicate<Node> {
		@Override
		public boolean test(Node node) {
			return node instanceof StackPane && ((StackPane) node).getChildren().get(0).getUserData() != null;
		}
	}

	@FunctionalInterface
	public interface PasswordSupplier {
		String getPassword(boolean refresh);
	}

	/**
	 * Currently used application bundle
	 */
	private ResourceBundle resources;

	@FXML
	private HBox bikingPicturesContainer;

	@FXML
	private TableView<Bike> viewBikes;
	@FXML
	private TableColumn<Bike, String> viewBikeName;
	@FXML
	private TableColumn<Bike, Color> viewBikeColor;
	@FXML
	private TableColumn<Bike, LocalDate> viewBikeBoughtOn;
	@FXML
	private TableColumn<Bike, LocalDate> viewBikeDecommissionedOn;
	@FXML
	private TableColumn<Bike, Integer> viewBikeMilage;
	@FXML
	private PieChart chartMilagePerBike;

	@FXML
	private TableView<GalleryPicture> viewGalleryPictures;
	@FXML
	private TableColumn<GalleryPicture, LocalDate> viewGalleryPictureTakenOn;
	@FXML
	private TableColumn<GalleryPicture, String> viewGalleryPictureDescription;
	@FXML
	private TableColumn<GalleryPicture, Integer> viewGalleryPictureImage;

	@FXML
	private TableView<Track> viewTracks;
	@FXML
	private TableColumn<Track, LocalDate> viewTrackCoveredOn;
	@FXML
	private TableColumn<Track, String> viewTrackName;
	@FXML
	private WebView viewTrackMap;

	private ObservableList<BikingPicture> bikingPictures;

	private final ThreadLocalRandom random = ThreadLocalRandom.current();

	/**
	 * Used to store the password for biking.michael-simons.eu. The password is actually
	 * stored in plain text. I propably wouldn't recommend that in a more serious
	 * application.
	 */
	private final Preferences preferences = Preferences.userNodeForPackage(this.getClass());

	private FlipImageService flipImageService;

	/**
	 * Listens for changes on the {@link Bike#milageProperty() } and transfers
	 * them to the server side api
	 */
	private MilageChangeListener milageChangeListener;

	private final HttpClient httpClient = HttpClient.newBuilder()
		.version(HttpClient.Version.HTTP_1_1)
		.followRedirects(HttpClient.Redirect.NORMAL)
		.build();

	private final ObjectMapper objectMapper = new ObjectMapper()
		.registerModule(new Jdk8Module())
		.registerModule(new JavaTimeModule())
		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;

		bikingPictures = retrieve("/bikingPictures", BikingPicture::new);
		// Start loading image views when pictures are available
		bikingPictures.addListener((Change<? extends BikingPicture> change) -> {
			if (!change.getList().isEmpty()) {
				loadPictures();
			}
		});

		// Load more images when size changes
		bikingPicturesContainer.widthProperty().addListener((observable, oldValue, newValue) -> loadPictures());

		// Prepare flipservice, depends on container so don't initialise in constructor
		this.flipImageService = new FlipImageService(this.bikingPictures, this.bikingPicturesContainer, this.random);

		// Prepare listener for milage property
		this.milageChangeListener = new MilageChangeListener(this::retrievePassword, this::storePassword,
			this.resources, objectMapper);
		// Display a simple popup when adding milage fails
		this.milageChangeListener.setOnFailed(state -> {
			final Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(resources.getString("common.error"));
			alert.setHeaderText(null);
			alert.setContentText(state.getSource().getException().getMessage());
			alert.showAndWait();
		});

		// Prepare bike graph
		chartMilagePerBike.setData(FXCollections.observableArrayList());
		chartMilagePerBike.setLegendVisible(false);
		// Get all bikes
		final ObservableList<Bike> bikes = retrieve("/bikes?all=true", Bike::new);
		// Configure milage controller for each bike
		bikes.addListener(this::watchChangesToBikeList);

		viewBikes.setItems(bikes);
		viewBikeName.setCellValueFactory(new PropertyValueFactory<>("name"));
		viewBikeColor.setCellValueFactory(new PropertyValueFactory<>("color"));
		viewBikeColor.setCellFactory(ColorTableCell::new);
		viewBikeBoughtOn.setCellValueFactory(new PropertyValueFactory<>("boughtOn"));
		viewBikeBoughtOn.setCellFactory(LocalDateTableCell::new);
		viewBikeDecommissionedOn.setCellValueFactory(new PropertyValueFactory<>("decommissionedOn"));
		viewBikeDecommissionedOn.setCellFactory(LocalDateTableCell::new);
		viewBikeMilage.setCellValueFactory(new PropertyValueFactory<>("milage"));
		viewBikeMilage.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		viewGalleryPictures.setItems(retrieve("/galleryPictures", GalleryPicture::new));
		viewGalleryPictureTakenOn.setCellValueFactory(new PropertyValueFactory<>("takenOn"));
		viewGalleryPictureTakenOn.setCellFactory(LocalDateTableCell::new);
		viewGalleryPictureDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
		viewGalleryPictureDescription.setCellFactory((TableColumn<GalleryPicture, String> column) -> {
			final TableCell<GalleryPicture, String> cell = new TableCell<>();
			final Text text = new Text();
			cell.setGraphic(text);
			cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
			// Bind wrapping width of the text to the actual width of the cell
			text.wrappingWidthProperty().bind(cell.widthProperty());
			// Update text with content from cell
			text.textProperty().bind(cell.itemProperty());
			return cell;
		});
		// Bind preferred width of column to width of table minus the first column to fill up the remaining space.
		viewGalleryPictureDescription.prefWidthProperty().bind(
			viewGalleryPictures.widthProperty()
				.subtract(viewGalleryPictureTakenOn.prefWidthProperty())
				.subtract(viewGalleryPictureImage.widthProperty())
		);

		viewGalleryPictureImage.setCellValueFactory(new PropertyValueFactory<>("id"));
		viewGalleryPictureImage.setCellFactory(GalleryPictureTableCell::new);
		// Fill the remaining space of the table
		viewGalleryPictureImage.prefWidthProperty().bind(
			viewGalleryPictures.widthProperty()
				.subtract(viewGalleryPictureTakenOn.widthProperty())
				.subtract(viewGalleryPictureDescription.widthProperty())
		);

		// This is necessary because the filtered list is immutable and therefor
		// not sortable, so we wrap it.
		final SortedList<Track> tracks = new SortedList<>(
			retrieve("/tracks", Track::new)
				.filtered(track -> track.getType() == Type.biking));
		viewTracks.setItems(tracks);
		tracks.comparatorProperty().bind(viewTracks.comparatorProperty());
		viewTrackCoveredOn.setCellFactory(LocalDateTableCell::new);
		viewTrackCoveredOn.setCellValueFactory(new PropertyValueFactory<>("coveredOn"));
		viewTrackName.setCellValueFactory(new PropertyValueFactory<>("name"));
		viewTrackName.prefWidthProperty().bind(viewTracks.widthProperty().subtract(viewTrackCoveredOn.widthProperty()));
		// Establish default sort order
		viewTracks.getSortOrder().add(viewTrackCoveredOn);
		// HTML view of selected track
		viewTrackMap.setContextMenuEnabled(false);
		final WebEngine webEngine = viewTrackMap.getEngine();
		webEngine.setJavaScriptEnabled(true);
		// Watch loading of map data
		webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
			if (newState == State.SUCCEEDED) {
				logger.log(Level.FINE, "Loading done, now preparing map.");
				// enable javascript and set every relevant width and height to 100%
				// so that the webview is automatically filled
				webEngine.executeScript(
					"$('html').height('100%'); $('body').height('100%'); $('#map').css('width', '100%').css('height', '100%'); ");
			}
		});
		viewTracks.getSelectionModel().selectedItemProperty().addListener((observable, oldTrack, newTrack) -> {
			if (newTrack != null) {
				final String mapUrl = String.format("%s/tracks/%s/embed?width=%d&height=%d",
					Application.HOST_AND_PORT,
					newTrack.getId(),
					viewTrackMap.widthProperty().intValue(),
					viewTrackMap.heightProperty().intValue()
				);
				logger.log(Level.FINE, "Loading embedded map {0}", new Object[] { mapUrl });
				webEngine.load(mapUrl);
			}
		});
	}

	public String retrievePassword() {
		String password = this.preferences.get("password", "");
		if (password.isEmpty()) {
			final Dialog<String> passwordDialog = new Dialog<>();
			passwordDialog.setTitle(resources.getString("enterPasswordDialog.title"));
			passwordDialog.setHeaderText(null);
			final DialogPane passwordDialogPane = passwordDialog.getDialogPane();

			final PasswordField passwordField = new PasswordField();
			passwordField.setPromptText(resources.getString("enterPasswordDialog.passwordFieldPrompt"));
			// Create and add new button type for confirmation
			final ButtonType confirmButtonType = new ButtonType(resources.getString("enterPasswordDialog.title"),
				ButtonData.OK_DONE);
			passwordDialogPane.getButtonTypes().add(confirmButtonType);
			// Retrieve node
			final Node confirmButton = passwordDialogPane.lookupButton(confirmButtonType);
			confirmButton.disableProperty().bind(passwordField.textProperty().isEmpty());

			// Result converter
			passwordDialog.setResultConverter(dialogButton -> {
				final ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
				return data == ButtonData.OK_DONE ? passwordField.getText() : null;
			});

			// Create content
			final GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setMaxWidth(Double.MAX_VALUE);
			grid.setAlignment(Pos.CENTER_LEFT);

			// Do layout
			passwordField.setMaxWidth(Double.MAX_VALUE);
			GridPane.setHgrow(passwordField, Priority.ALWAYS);
			GridPane.setFillWidth(passwordField, true);

			grid.add(new Label(passwordField.getPromptText()), 0, 0);
			grid.add(passwordField, 1, 0);
			passwordDialogPane.setContent(grid);

			Platform.runLater(() -> passwordField.requestFocus());
			password = passwordDialog.showAndWait().orElse(null);
		}
		return password;
	}

	public void storePassword(final Optional<String> password) {
		try {
			if (!password.isPresent() || password.get().isEmpty()) {
				this.preferences.remove("password");
			} else {
				this.preferences.put("password", password.get());
			}
			this.preferences.flush();
		} catch (BackingStoreException ex) {
			logger.log(Level.WARNING, "Had some problems storing a password", ex);
		}
	}

	/**
	 * Observes changes to the list of all bikes. If bikes are added, add the milage
	 * listener to the property and also add a data element to the graph
	 *
	 * @param change
	 */
	final void watchChangesToBikeList(final Change<? extends Bike> change) {
		while (change.next()) {
			if (!change.wasAdded()) {
				continue;
			}
			var addedSublist = change.getAddedSubList();

			// Add milage listener
			addedSublist.stream()
				.map(Bike::milageProperty)
				.forEach(milage -> milage.addListener(milageChangeListener));

			// Add data elements to chart
			this.chartMilagePerBike.getData().addAll(
				addedSublist.stream()
					.map(this::createDataElementForBike)
					.collect(Collectors.toList())
			);
		}
	}

	/**
	 * Creates a PieChart.Data element for the given bike. The data elements
	 * value is bound to the milageProperty of the bike. The node of the element
	 * then is watched and if it becomes available, it's style propert is bound
	 * to the color of the bike
	 *
	 * @param bike Bike whos milage is displayed in the pie chart
	 * @return a new pie chart data element
	 */
	final PieChart.Data createDataElementForBike(final Bike bike) {
		final Property<Integer> milageProperty = bike.milageProperty();
		final PieChart.Data data = new PieChart.Data(bike.getName(), 0);
		data.pieValueProperty().bind(milageProperty);
		// When the data is attached, the node becomes availabe
		data.nodeProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) {
				return;
			}
			// and is styleable
			newValue.styleProperty().bind(
				// create a binding to a String format containing the rgb representation of the color of the bike
				createStringBinding(() -> {
						final Color c = bike.getColor();
						return format("-fx-pie-color: rgb(%d, %d, %d);", round(c.getRed() * 255.0),
							round(c.getGreen() * 255.0), round(c.getBlue() * 255.0));
					},
					bike.colorProperty())
			);
		});
		return data;
	}

	final void loadPictures() {
		final ObservableList<Node> children = bikingPicturesContainer.getChildren();
		final int numberOfNeededElements =
			(int) Math.ceil(bikingPicturesContainer.getWidth() / 150.0) - children.size();
		if (bikingPictures.isEmpty() || numberOfNeededElements <= 0) {
			return;
		}

		// Get currently loaded images
		final Set<BikingPicture> loadedBikingPictures = children.stream()
			.filter(new LoadedImageFilter())
			.map(node -> (BikingPicture) ((StackPane) node).getChildren().get(0).getUserData())
			.collect(Collectors.toSet());
		final List<BikingPicture> available = bikingPictures
			.filtered(bikingPicture -> !loadedBikingPictures.contains(bikingPicture));
		int i = 0, remainingNumberOfPictures = available.size();
		int neededViewsLeft = numberOfNeededElements;
		while (neededViewsLeft > 0 && remainingNumberOfPictures > 0) {
			int rand = random.nextInt(remainingNumberOfPictures);
			if (rand < neededViewsLeft) {
				final ProgressIndicator progressIndicator = new ProgressIndicator();

				final HBox box = new HBox(progressIndicator);
				box.setFillHeight(false);
				box.setAlignment(Pos.CENTER);
				box.setMinHeight(113);
				box.setMaxHeight(113);
				box.setMinWidth(150);
				box.setMaxWidth(150);

				final StackPane stackPane = new StackPane(box);
				final BikingPicture bikingPicture = available.get(i);
				final Image image = new Image(bikingPicture.getSrc(), 150, 113, true, true, true);
				progressIndicator.progressProperty().bind(image.progressProperty());
				image.progressProperty().addListener((observable, oldValue, newValue) -> {
					if (newValue.intValue() == 1) {
						final ImageView imageView = new ImageView(image);
						imageView.setUserData(bikingPicture);
						stackPane.getChildren().set(0, imageView);
					}
				});

				children.add(stackPane);
				neededViewsLeft--;
			}
			remainingNumberOfPictures--;
			i++;
		}

		if (flipImageService != null && !flipImageService.isRunning()) {
			flipImageService.start();
		}
	}

	private <T> ObservableList<T> retrieve(
		final String endpoint,
		final Function<JsonNode, T> jsonToObject
	) {

		var rv = FXCollections.<T>observableArrayList();
		var apiEndpoint = URI.create(String.format("%s%s", Application.BASE_URL, endpoint));
		var request = HttpRequest.newBuilder()
			.uri(apiEndpoint)
			.timeout(Duration.ofSeconds(10))
			.header("Accept", "application/json")
			.GET()
			.build();

		httpClient
			.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
			.thenApply(HttpResponse::body)
			.thenApply(inputStream -> {
				try {
					var tree = objectMapper.readTree(inputStream);
					return StreamSupport.stream(tree.spliterator(), false)
						.map(jsonToObject)
						.collect(Collectors.toList());
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			})
			.whenComplete((f, e) -> {
				if(e != null) {
					e.printStackTrace();
				}
				Platform.runLater(() -> rv.addAll(f));
			});

		return rv;
	}
}