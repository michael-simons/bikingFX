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
package ac.simons.bikingFX.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 * Retrieves JSON from a given API endpoint and converts it to Java Objects
 * using the given {@link ObjectFactory}.
 * <br>
 * Be aware that no error checking (http etc.) whatsoever is done here.
 *
 * @author Michael J. Simons, 2014-10-18
 */
public class JsonRetrievalTask<T> extends Task<Collection<T>> {

    public static final String HOST_AND_PORT = "https://biking.michael-simons.eu"; 
    public static final String BASE_URL = HOST_AND_PORT + "/api";
    private static final Logger logger = Logger.getLogger(JsonRetrievalTask.class.getName());

    @FunctionalInterface
    public interface ObjectFactory<T> {

	T createObject(final JsonValue jsonValue);
    }
    
    /**
     * Instantiates a new retrieval task, sets up an observable list, starts the task in a
     * separate thread and fills the list on succeeded state.
     * 
     * @param <T>
     * @param objectFactory
     * @param endpoint
     * @return 
     */
    public static <T> ObservableList<T> get(final ObjectFactory<T> objectFactory, final String endpoint) {
	final ObservableList<T> rv = FXCollections.observableArrayList();

	final JsonRetrievalTask<T> bikesRetrievalTask = new JsonRetrievalTask<>(objectFactory, endpoint);
	bikesRetrievalTask.setOnSucceeded(state -> {
	    rv.addAll((Collection<T>) state.getSource().getValue());
	});
	new Thread(bikesRetrievalTask).start();
	return rv;
    }

    private final URL apiEndpoint;
    private final ObjectFactory<T> objectFactory;

    protected JsonRetrievalTask(final ObjectFactory<T> objectFactory, final String endpoint) {
	URL hlp = null;
	try {
	    hlp = new URL(String.format("%s%s", BASE_URL, endpoint));
	} catch (MalformedURLException e) {
	    // I hope so ;)
	    throw new RuntimeException(e);
	}
	this.apiEndpoint = hlp;
	this.objectFactory = objectFactory;
    }

    @Override
    protected Collection<T> call() throws Exception {
	logger.log(Level.FINE, "Retrieving list of objects from {0}", new Object[]{this.apiEndpoint.toString()});
	try (final JsonReader jsonReader = Json.createReader(apiEndpoint.openStream())) {
	    logger.log(Level.FINE, "Done.");
	    return jsonReader.readArray().stream().map(objectFactory::createObject).collect(Collectors.toList());
	}
    }
}
