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
package ac.simons.bikingFX.bikes;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents an updateable Bike in my collection of bikes.
 *
 * @author Michael J. Simons
 * @since 2014-10-16
 */
public class Bike {

	/**
	 * ID is needed for URL construction
	 */
	private final Property<Integer> id;

	private final Property<String> name;

	private final Property<Color> color;

	private final Property<LocalDate> boughtOn;

	private final Property<LocalDate> decommissionedOn;

	private final Property<Integer> milage;

	public Bike(final JsonNode json) {
		this.id = new ReadOnlyObjectWrapper<>(this, "id", json.get("id").intValue());
		this.name = new ReadOnlyObjectWrapper<>(this, "name", json.get("name").textValue());
		this.color = new SimpleObjectProperty<>(this, "color", Color.web(json.get("color").textValue()));
		this.boughtOn = new ReadOnlyObjectWrapper<>(this, "boughtOn",
			LocalDate.parse(json.get("boughtOn").asText()));
		final JsonNode decommissionedOn = json.get("decommissionedOn");
		this.decommissionedOn = new SimpleObjectProperty<>(this, "decommissionedOn",
			decommissionedOn.isNull() ? null :
				LocalDate.parse(decommissionedOn.asText()));
		final JsonNode milage = json.get("lastMilage");
		this.milage = new SimpleObjectProperty<>(this, "milage",
			milage.isNull() ? 0 : milage.intValue());
	}

	public final Integer getId() {
		return id.getValue();
	}

	public Property<Integer> propertyId() {
		return id;
	}

	public final String getName() {
		return name.getValue();
	}

	public Property<String> nameProperty() {
		return name;
	}

	public final Color getColor() {
		return color.getValue();
	}

	public final void setColor(Color color) {
		this.color.setValue(color);
	}

	public Property<Color> colorProperty() {
		return color;
	}

	public final LocalDate getBoughtOn() {
		return boughtOn.getValue();
	}

	public Property<LocalDate> boughtOnProperty() {
		return boughtOn;
	}

	public final LocalDate getDecommissionedOn() {
		return decommissionedOn.getValue();
	}

	public final void setDecommissionedOn(LocalDate decommissionedOn) {
		this.decommissionedOn.setValue(decommissionedOn);
	}

	public Property<LocalDate> decommissionedOnProperty() {
		return decommissionedOn;
	}

	public final Integer getMilage() {
		return milage.getValue();
	}

	public final void setMilage(Integer milage) {
		this.milage.setValue(milage);
	}

	public Property<Integer> milageProperty() {
		return milage;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + Objects.hashCode(this.getName());
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
		final Bike other = (Bike) obj;
		return Objects.equals(this.getName(), other.getName());
	}
}
