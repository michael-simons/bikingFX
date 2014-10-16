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

import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javax.json.JsonObject;

/**
 * Represents a biking picture.
 *
 * @author Michael J. Simons, 2014-10-15
 */
public class BikingPicture implements Serializable {
    private static final long serialVersionUID = 6729385561352721235L;
    
    /** Base url for biking pictures as String format string */
    public static final String BASE_URL_FORMAT_STRING = "http://biking.michael-simons.eu/api/bikingPictures/%d.jpg";
    
    public static BikingPicture create(final JsonObject jsonObject) {
	return new BikingPicture(
		String.format(BASE_URL_FORMAT_STRING, jsonObject.getJsonNumber("id").longValue()), 
		jsonObject.getString("link")
	);
    }

    /**
     * URL for the image source
     */
    private final Property<String> src;

    /**
     * Arbitrary link to a webpage
     */
    private final Property<String> link;

    public BikingPicture(String src, String link) {
	this.src = new ReadOnlyObjectWrapper<>(this, "src", src);
	this.link = new ReadOnlyObjectWrapper<>(this, "link", link);
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
    public int hashCode() {
	int hash = 7;
	hash = 19 * hash + Objects.hashCode(this.src);
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
	if (!Objects.equals(this.src, other.src)) {
	    return false;
	}
	return true;
    }
}
