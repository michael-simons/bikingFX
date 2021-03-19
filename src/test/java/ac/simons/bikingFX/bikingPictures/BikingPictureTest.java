/*
 * Copyright 2014-2021 michael-simons.eu.
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

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Michael J. Simons
 * @since 2014-10-16
 */
class BikingPictureTest {

	@Test
	void factoryMethodShouldWork() throws IOException {
		final JsonNode json = new ObjectMapper()
			.readTree(BikingPictureTest.class.getResourceAsStream("/bikingPictures/singleBikingPicture.json"));
		final BikingPicture bikingPicture = new BikingPicture(json);
		Assertions.assertEquals("https://biking.michael-simons.eu/api/bikingPictures/231.jpg", bikingPicture.getSrc());
		Assertions.assertEquals(bikingPicture.getSrc(), bikingPicture.srcProperty().getValue());
		Assertions.assertEquals("https://dailyfratze.de/michael/2005/8/29", bikingPicture.getLink());
		Assertions.assertEquals(bikingPicture.getLink(), bikingPicture.linkProperty().getValue());
	}

}
