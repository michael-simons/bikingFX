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

import java.io.IOException;
import java.util.Collection;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Michael J. Simons, 2014-10-16
 */
public class BikingPictureRetrievalTaskTest { 
    @Test
    public void taskShouldWork() throws IOException, Exception {
	final BikingPictureRetrievalTask service = new BikingPictureRetrievalTask(this.getClass().getResource("/bikingPictures/bikingPictures.json"));
	final Collection<BikingPicture> pictures = service.call();
	Assert.assertNotNull(pictures);
	Assert.assertEquals(252, pictures.size());
    }
}
