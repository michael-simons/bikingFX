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
package ac.simons.bikingFX.bikes;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Michael J. Simons
 * @since 2014-1017
 */
class BikeTest {

	@Test
	void factoryMethodShouldWork() throws IOException {
		final ObjectMapper objectMapper = new ObjectMapper();

		JsonNode json = objectMapper.readTree(Bike.class.getResourceAsStream("/bikes/singleBike.json"));
		Bike bike = new Bike(json);
		Assertions.assertNull(bike.getDecommissionedOn());

		json = objectMapper.readTree(Bike.class.getResourceAsStream("/bikes/singleDecommissionedBike.json"));
		bike = new Bike(json);
		Assertions.assertEquals("MTB", bike.getName());
		Assertions.assertEquals(Color.web("#CCCCCC"), bike.getColor());
		Assertions.assertEquals(LocalDate.of(2007, 8, 2), bike.getBoughtOn());
		Assertions.assertEquals(LocalDate.of(2012, 9, 30), bike.getDecommissionedOn());
		Assertions.assertEquals(6451, bike.getMilage().intValue());
	}
}
