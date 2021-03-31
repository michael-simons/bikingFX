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
package ac.simons.bikingFX.bikingPictures;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a biking picture.
 *
 * @author Michael J. Simons
 * @since 2014-10-15
 */
public class BikingPicture implements Serializable {
	private static final long serialVersionUID = 6729385561352721235L;

	/**
	 * Base url for biking pictures as String format string
	 */
	public static final String BASE_URL_FORMAT_STRING = "https://biking.michael-simons.eu/api/bikingPictures/%d.jpg";

	/**
	 * URL for the image source
	 */
	private final Property<String> src;

	/**
	 * Arbitrary link to a webpage
	 */
	private final Property<String> link;

	public BikingPicture(final JsonNode json) {

		this.src = new ReadOnlyObjectWrapper<>(this, "src",
			String.format(BASE_URL_FORMAT_STRING, json.get("id").longValue()));
		this.link = new ReadOnlyObjectWrapper<>(this, "link", json.get("link").textValue());
	}

	public final String getSrc() {
		return src.getValue();
	}

	public Property<String> srcProperty() {
		return src;
	}

	public final String getLink() {
		return link.getValue();
	}

	public Property<String> linkProperty() {
		return link;
	}

	@Override
	public String toString() {
		return "BikingPicture{" + "src=" + getSrc() + ", link=" + getLink() + '}';
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 19 * hash + Objects.hashCode(this.getSrc());
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
		final BikingPicture other = (BikingPicture) obj;
		if (!Objects.equals(this.getSrc(), other.getSrc())) {
			return false;
		}
		return true;
	}
}
