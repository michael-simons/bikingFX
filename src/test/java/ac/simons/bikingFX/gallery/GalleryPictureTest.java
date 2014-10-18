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
package ac.simons.bikingFX.gallery;

import java.time.LocalDate;
import javax.json.Json;
import javax.json.JsonReader;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael J. Simons, 2014-10-18
 */
public class GalleryPictureTest {

    @Test
    public void factoryMethodShouldWork() {
	final JsonReader jsonReader = Json.createReader(GalleryPictureTest.class.getResourceAsStream("/gallery/singleGalleryPicture.json"));
	final GalleryPicture galleryPicture = new GalleryPicture(jsonReader.readObject());
	Assert.assertEquals(LocalDate.of(2009, 4, 10), galleryPicture.getTakenOn());
	Assert.assertEquals("fe706cbe58f3da8c59d051bba5ede2d2.jpg", galleryPicture.getFilename());
	Assert.assertEquals("Auf der Festung Königstein mit Blick auf die Elbe.", galleryPicture.getDescription());
    }

}
