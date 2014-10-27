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
package ac.simons.bikingFX.tracks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javax.json.JsonObject;
import javax.json.JsonValue;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneId.systemDefault;

/**
 * @author Michael J. Simons, 2014-10-27
 */
public class Track {

    public static enum Type {

	biking, running
    }
    
    /**
     * (Pretty) ID is needed for URL construction
     */
    private final Property<String> id;

    private final Property<LocalDate> coveredOn;

    private final Property<String> name;
         
    private final Type type;

    public Track(final JsonValue jsonValue) {
	final JsonObject jsonObject = (JsonObject) jsonValue;

	this.id = new ReadOnlyObjectWrapper<>(this, "id", jsonObject.getString("id"));
	this.coveredOn = new ReadOnlyObjectWrapper<>(this, "coveredOn", LocalDateTime.ofInstant(ofEpochMilli(jsonObject.getJsonNumber("coveredOn").longValue()), systemDefault()).toLocalDate());
	this.name = new ReadOnlyObjectWrapper<>(this, "name", jsonObject.getString("name"));
	this.type = Type.valueOf(jsonObject.getString("type"));
    }

    public final String getId() {
	return id.getValue();
    }

    public Property<String> propertyId() {
	return id;
    }

    public final LocalDate getCoveredOn() {
	return coveredOn.getValue();
    }

    public Property<LocalDate> coveredOnProperty() {
	return coveredOn;
    }

    public final String getName() {
	return name.getValue();
    }

    public Property<String> nameProperty() {
	return name;
    }

    public final Type getType() {
	return type;
    }
    
    @Override
    public int hashCode() {
	int hash = 7;
	hash = 41 * hash + Objects.hashCode(this.getCoveredOn());
	hash = 41 * hash + Objects.hashCode(this.getName());
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final Track other = (Track) obj;
	if (!Objects.equals(this.getCoveredOn(), other.getCoveredOn())) {
	    return false;
	}
	return Objects.equals(this.getName(), other.getName());
    }
}
