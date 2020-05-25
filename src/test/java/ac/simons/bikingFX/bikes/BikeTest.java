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
package ac.simons.bikingFX.bikes;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Michael J. Simons, 2014-1017
 */
public class BikeTest {

	@Test
	public void factoryMethodShouldWork() throws IOException {
		final ObjectMapper objectMapper = new ObjectMapper();

		JsonNode json = objectMapper.readTree(Bike.class.getResourceAsStream("/bikes/singleBike.json"));
		Bike bike = new Bike(json);
		Assert.assertNull(bike.getDecommissionedOn());

		json = objectMapper.readTree(Bike.class.getResourceAsStream("/bikes/singleDecommissionedBike.json"));
		bike = new Bike(json);
		Assert.assertEquals("MTB", bike.getName());
		Assert.assertEquals(Color.web("#CCCCCC"), bike.getColor());
		Assert.assertEquals(LocalDate.of(2007, 8, 2), bike.getBoughtOn());
		Assert.assertEquals(LocalDate.of(2012, 9, 30), bike.getDecommissionedOn());
		Assert.assertEquals(6451, bike.getMilage().intValue());
	}
}
