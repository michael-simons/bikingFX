/*
 * Copyright 2014-2019 michael-simons.eu.
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

import java.time.LocalDate;
import java.util.Objects;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * @author Michael J. Simons
 *
 * @since 2014-10-18
 */
public class GalleryPicture {
    
    /** ID is needed for URL construction */
    private final Property<Integer> id;
    
    private final Property<LocalDate> takenOn;

    private final Property<String> filename;

    private final Property<String> description;

    public GalleryPicture(final JsonValue jsonValue) {
	final JsonObject jsonObject = (JsonObject) jsonValue;

	this.id = new ReadOnlyObjectWrapper<>(this, "id", jsonObject.getInt("id"));
	this.takenOn = new ReadOnlyObjectWrapper<>(this, "takenOn", LocalDate.parse(jsonObject.getString("takenOn")));
	this.filename = new ReadOnlyObjectWrapper<>(this, "filename", jsonObject.getString("filename"));
	this.description = new SimpleStringProperty(this, "description", jsonObject.getString("description"));
    }

    public final Integer getId() {
	return id.getValue();
    }
    
    public Property<Integer> propertyId() {
	return id;
    }

    public final LocalDate getTakenOn() {
	return takenOn.getValue();
    }

    public Property<LocalDate> takenOnProperty() {
	return takenOn;
    }

    public final String getFilename() {
	return filename.getValue();
    }

    public Property<String> filenameProperty() {
	return filename;
    }

    public final String getDescription() {
	return description.getValue();
    }

    public final void setDescription(String description) {
	this.description.setValue(description);
    }

    public Property<String> descriptionProperty() {
	return description;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 89 * hash + Objects.hashCode(this.getTakenOn());
	hash = 89 * hash + Objects.hashCode(this.getFilename());
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
	final GalleryPicture other = (GalleryPicture) obj;
	if (!Objects.equals(this.getTakenOn(), other.getTakenOn())) {
	    return false;
	}
	return Objects.equals(this.getFilename(), other.getFilename());
    }
}
