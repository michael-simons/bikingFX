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
package ac.simons.bikingFX.renderer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import static java.time.format.FormatStyle.MEDIUM;

/**
 * Renderes a localized LocalDate
 * 
 * @author Michael J. Simons, 2014-10-17
 */
public class LocalDateTableCell<T> extends TableCell<T, LocalDate> {
    private static final DateTimeFormatter localDateFormatter = DateTimeFormatter.ofLocalizedDate(MEDIUM);
        
    public static <T> LocalDateTableCell<T> create(TableColumn<T, LocalDate> column) {
	return new LocalDateTableCell<>();
    }
    
    @Override
    protected void updateItem(LocalDate item, boolean empty) {
	super.updateItem(item, empty);
	if (item != null && !empty) {
	    setText(localDateFormatter.format(item));
	}
    }
}
