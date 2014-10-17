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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.concurrent.Task;
import javax.json.Json;
import javax.json.JsonReader;

/**
 * Retrieves all biking pictures available at my biking page
 *
 * @author Michael J. Simons, 2014-10-16
 */
public class BikingPictureRetrievalTask extends Task<Collection<BikingPicture>> {
    private static final String DEFAULT_API_ENDPOINT = "http://biking.michael-simons.eu/api/bikingPictures.json";
    private static final Logger logger = Logger.getLogger(BikingPictureRetrievalTask.class.getName()); 
    
    private final URL apiEndpoint;
        
    public BikingPictureRetrievalTask() {
	URL hlp = null;
	try {	    
	    hlp = new URL(DEFAULT_API_ENDPOINT);
	} catch(MalformedURLException e) {
	    // Don't think so...
	}
	this.apiEndpoint = hlp;	
    }
    
    public BikingPictureRetrievalTask(final URL apiEndpoint)  {
	this.apiEndpoint = apiEndpoint;	
    }
    
    @Override
    protected Collection<BikingPicture> call() throws Exception {
	logger.log(Level.FINE, "Retrieve list of biking pictures...");
	try(final JsonReader jsonReader = Json.createReader(apiEndpoint.openStream())) {		    		    
	    logger.log(Level.FINE, "Done.");
	    return jsonReader.readArray().stream().map(BikingPicture::create).collect(Collectors.toList());		    
	}
    }
}
