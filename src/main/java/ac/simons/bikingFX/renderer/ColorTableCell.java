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

import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

import javafx.scene.paint.Color;

/**
 * @author Michael J. Simons, 2014-10-17
 */
public class ColorTableCell<T> extends TableCell<T, Color> {    
    public static <T> ColorTableCell<T> create(TableColumn<T, Color> column) {
	return new ColorTableCell<>();
    }
    
    @Override
    protected void updateItem(Color item, boolean empty) {
	super.updateItem(item, empty);
	if (item == null || empty) {
	    setText(null);
	    setGraphic(null);
	} else {
	    int r = (int)Math.round(item.getRed() * 255.0);
	    int g = (int)Math.round(item.getGreen() * 255.0);
	    int b = (int)Math.round(item.getBlue() * 255.0);
	    
	    setText(String.format("#%02x%02x%02x", r, g, b));
	    setBackground(new Background(new BackgroundFill(item, CornerRadii.EMPTY, Insets.EMPTY)));
	    setTextFill(item.invert());
	}
    }
}