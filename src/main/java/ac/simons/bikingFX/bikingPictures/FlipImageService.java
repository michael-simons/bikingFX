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
import javafx.animation.ScaleTransition;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;


/**
 *
 * @author Michael J. Simons
 */
public class FlipImageService extends ScheduledService<ImageView> {
    private final List<BikingPicture> bikingPictures;    
    private final Random r = new Random(System.currentTimeMillis());
    
    public FlipImageService(List<BikingPicture> bikingPictures, final Pane container) {
	this.bikingPictures = bikingPictures;
	this.setPeriod(Duration.seconds(5));
	this.setDelay(this.getPeriod());	
	this.setOnSucceeded(state -> {	    
	    // Check if images are loaded...
	    final List<Node> currentImageViews = container.getChildren().filtered(node -> node instanceof StackPane);
	    if(currentImageViews.size() > 0) {
		final StackPane pickedNoded = (StackPane) currentImageViews.get(r.nextInt(currentImageViews.size()));
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
    }
    
    @Override
    protected Task<ImageView> createTask() {
	return new Task<ImageView>() {
	    @Override
	    protected ImageView call() throws Exception {			
		return CreateImageViewsTask.createImageView(bikingPictures.get(r.nextInt(bikingPictures.size())));
	    }
	};
    }	        
}