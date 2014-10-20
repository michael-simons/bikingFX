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

import ac.simons.bikingFX.RootController.LoadedImageFilter;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.ScaleTransition;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * @author Michael J. Simons, 2014-10-17
 */
public class FlipImageService extends ScheduledService<ImageView> {
    private static final Logger logger = Logger.getLogger(FlipImageService.class.getName()); 
    
    private final ObservableList<BikingPicture> bikingPictures;    
    private final Random random;
    private final Pane container;    
    
    public FlipImageService(ObservableList<BikingPicture> bikingPictures, final Pane container, final Random random) {
	this.bikingPictures = bikingPictures;
	this.container = container;
	this.random = random;
	this.setPeriod(Duration.seconds(10));
	this.setDelay(this.getPeriod());	
	this.setOnSucceeded(state -> {	    
	    // Check if images are loaded...
	    final List<Node> currentImageViews = this.container.getChildren().filtered(node -> node instanceof StackPane && ((StackPane)node).getChildren().get(0) instanceof ImageView);
	    if(currentImageViews.size() > 0) {
		final StackPane pickedNoded = (StackPane) currentImageViews.get(this.random.nextInt(currentImageViews.size()));
		final Node back = (Node)state.getSource().getValue();
		back.setScaleX(0);
		pickedNoded.getChildren().add(back);
		
		final Duration animDuration = Duration.millis(500);
		final ScaleTransition hideFront = new ScaleTransition(animDuration, pickedNoded.getChildren().get(0));
		hideFront.setFromX(1);
		hideFront.setToX(0);

		final ScaleTransition showBack = new ScaleTransition(animDuration, back);
		showBack.setFromX(0);
		showBack.setToX(1);

		hideFront.setOnFinished(event -> showBack.play());		
		showBack.setOnFinished(event -> pickedNoded.getChildren().remove(0));
		hideFront.play();	
	    }	    
	});	
	this.setOnFailed(state -> logger.log(Level.INFO, "Could not create ImageView: {0}.", state.getSource().getException().getMessage()));
    }
    
    @Override
    protected Task<ImageView> createTask() {
	return new Task<ImageView>() {
	    @Override
	    protected ImageView call() throws Exception {		
		// Create fresh filtered list
		final Set<BikingPicture> loadedBikingPictures = container
		    .getChildren().stream()
		    .filter(new LoadedImageFilter())
		    .map(node -> (BikingPicture)((StackPane)node).getChildren().get(0).getUserData())
		    .collect(Collectors.toSet());		
		
		final List<BikingPicture> availableBikingPictures = bikingPictures.filtered(bikingPicture -> !loadedBikingPictures.contains(bikingPicture));
		if(availableBikingPictures.size() <= 0) {
		    throw new RuntimeException("No more pictures available");
		}
		
		final BikingPicture bikingPicture = availableBikingPictures.get(random.nextInt(availableBikingPictures.size()));
		final ImageView imageView = new ImageView(new Image(bikingPicture.getSrc(), 150, 113, true, true, false));			
		imageView.setUserData(bikingPicture);
		return imageView;
	    }
	};
    }
}