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
package ac.simons.bikingFX.gallery;

import ac.simons.bikingFX.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael J. Simons
 * @since 2014-10-19
 */
public class GalleryPictureTableCell extends TableCell<GalleryPicture, Integer> {
	/**
	 * Those table cells are created twice, so we keep track of images used here... Very trivial solution using global state.
	 */
	private static final Map<Integer, Image> images = new ConcurrentHashMap<>();

	private final VBox container;
	/**
	 * The current image that is or should be displayed
	 */
	private Image image;

	/**
	 * Watches the progress of the given image to load. If the progress its 1.0
	 * it checks wether the image that is set for the given cell is still the
	 * same that was loaded.
	 */
	private class ImageLoadedListener implements ChangeListener<Number> {
		private final Image imageToLoad;

		public ImageLoadedListener(Image imageToLoad) {
			this.imageToLoad = imageToLoad;
		}

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if (newValue.intValue() == 1 && imageToLoad == GalleryPictureTableCell.this.image) {
				displayImage(imageToLoad);
			}
		}
	}

	public GalleryPictureTableCell(TableColumn<GalleryPicture, Integer> column) {
		this.container = new VBox(new ProgressIndicator());
		this.container.setFillWidth(true);
		this.container.setAlignment(Pos.CENTER);
		this.container.setMinWidth(400);
		this.container.setMaxWidth(800);
		this.container.setMinHeight(300);
		this.container.setMaxHeight(600);

		setPadding(Insets.EMPTY);
		setMaxWidth(800);
		setMaxHeight(600);
		setAlignment(Pos.CENTER);
		setGraphic(container);
	}

	@Override
	protected void updateItem(final Integer item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
			setText(null);
			setGraphic(null);
		} else {
			this.image = images.computeIfAbsent(item,
				id -> new Image(String.format("%s/galleryPictures/%d.jpg", Application.BASE_URL, id), 800, 600, true, true,
					true));
			if (image.getProgress() == 1.0) {
				displayImage(image);
			} else {
				final ProgressIndicator progressIndicator = new ProgressIndicator();
				container.getChildren().set(0, progressIndicator);
				progressIndicator.progressProperty().bind(image.progressProperty());
				image.progressProperty().addListener(new ImageLoadedListener(this.image));
			}
			setGraphic(container);
		}
	}

	void displayImage(final Image image) {
		final Node currentContent = this.container.getChildren().get(0);
		if (!(currentContent instanceof ProgressIndicator)) {
			final ImageView imageView = (ImageView) currentContent;
			imageView.setImage(image);
		} else {
			final ImageView imageView = new ImageView(image);
			imageView.fitWidthProperty().bind(this.container.widthProperty());
			imageView.setPreserveRatio(true);
			imageView.setCache(true);
			this.container.getChildren().set(0, imageView);
		}
	}
}
