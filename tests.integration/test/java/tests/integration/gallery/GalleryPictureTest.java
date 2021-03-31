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
package tests.integration.gallery;

import ac.simons.bikingFX.gallery.GalleryPicture;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Michael J. Simons
 * @since 2014-10-18
 */
class GalleryPictureTest {

	@Test
	public void factoryMethodShouldWork() throws IOException {

		final JsonNode json = new ObjectMapper()
			.readTree(GalleryPictureTest.class.getResourceAsStream("/gallery/singleGalleryPicture.json"));
		final GalleryPicture galleryPicture = new GalleryPicture(json);
		Assertions.assertEquals(LocalDate.of(2009, 4, 10), galleryPicture.getTakenOn());
		Assertions.assertEquals("fe706cbe58f3da8c59d051bba5ede2d2.jpg", galleryPicture.getFilename());
		Assertions.assertEquals("Auf der Festung Königstein mit Blick auf die Elbe.", galleryPicture.getDescription());
	}

}
