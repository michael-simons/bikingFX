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
package ac.simons.bikingFX.tracks;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Michael J. Simons
 * @since 2014-10-27
 */
public class Track {

	public enum Type {

		biking, running
	}

	/**
	 * (Pretty) ID is needed for URL construction
	 */
	private final Property<String> id;

	private final Property<LocalDate> coveredOn;

	private final Property<String> name;

	private final Type type;

	public Track(final JsonNode json) {

		this.id = new ReadOnlyObjectWrapper<>(this, "id", json.get("id").textValue());
		this.coveredOn = new ReadOnlyObjectWrapper<>(this, "coveredOn",
			LocalDate.parse(json.get("coveredOn").textValue()));
		this.name = new ReadOnlyObjectWrapper<>(this, "name", json.get("name").textValue());
		this.type = Type.valueOf(json.get("type").textValue());
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
