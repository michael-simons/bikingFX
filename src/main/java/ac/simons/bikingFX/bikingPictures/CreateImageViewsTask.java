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
package ac.simons.bikingFX.bikingPictures;

import java.util.List;
import java.util.Random;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author Michael J. Simons, 2014-10-17
 */
public class CreateImageViewsTask extends Task<ObservableList<ImageView>> {
    /**
     * Static factory method for creating ImageViews from BikingPictures
     * @param bikingPicture
     * @return 
     */
    public static ImageView createImageView(final BikingPicture bikingPicture) {	
	final ImageView imageView = new ImageView(bikingPicture.getSrc());			
	imageView.setUserData(bikingPicture);
	return imageView;
    }
    
    private final ReadOnlyObjectWrapper<ObservableList<ImageView>> partialResults = new ReadOnlyObjectWrapper<>(this, "partialResults", FXCollections.observableArrayList());

    private final List<BikingPicture> bikingPictures;
    private final int numberOfNeededViews;
    private final Random r = new Random(System.currentTimeMillis());

    public CreateImageViewsTask(List<BikingPicture> bikingPictures, int numberOfNeededViews) {
	this.bikingPictures = bikingPictures;
	this.numberOfNeededViews = numberOfNeededViews;
    }

    public final ObservableList getPartialResults() {
	return partialResults.get();
    }

    public final ReadOnlyObjectProperty<ObservableList<ImageView>> partialResultsProperty() {
	return partialResults.getReadOnlyProperty();
    }

    @Override
    protected ObservableList<ImageView> call() throws Exception {
	int i = 0, remainingNumberOfPictures = bikingPictures.size();
	int neededViewsLeft = this.numberOfNeededViews;
	while (neededViewsLeft > 0 && remainingNumberOfPictures > 0) {	    
	    int rand = r.nextInt(remainingNumberOfPictures);
	    if (rand < neededViewsLeft) {
		final ImageView imageView = createImageView(bikingPictures.get(i));			
		neededViewsLeft--;			
		this.addResult(imageView);		
	    }
	    remainingNumberOfPictures--;
	    i++;
	}
	while(neededViewsLeft > 0) {	    
	    this.addResult(new ImageView(new Image(this.getClass().getResourceAsStream("/img/default-biking-picture.jpg"))));
	    --neededViewsLeft;		    
	}
	
	return partialResults.get();
    }

    private void addResult(final ImageView imageView) {
	Platform.runLater(() -> {
	    partialResults.get().add(imageView);
	});
    }
}