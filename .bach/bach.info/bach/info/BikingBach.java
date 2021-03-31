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
package bach.info;

import com.github.sormuras.bach.Bach;
import com.github.sormuras.bach.Command;
import com.github.sormuras.bach.Options;
import com.github.sormuras.bach.ProjectInfo;

/**
 * @author Michael J. Simons
 */
public class BikingBach extends Bach {

	public static Provider<BikingBach> provider() {
		return BikingBach::new;
	}

	private BikingBach(Options options) {
		super(options);
	}

	@Override
	public void buildProjectTestSpace() {

		super.buildProjectTestSpace();

		var projectName = project().name();
		var distDir = ProjectInfo.WORKSPACE + "/dist";

		var createAppImage = Command.of("jpackage")
			.add("--name", projectName)
			.add("--type", "app-image")
			.add("--module", options().info().map(i -> i.launcher().module() + "/" + i.launcher().mainClass()).get())
			.add("--runtime-image", ProjectInfo.WORKSPACE + "/image")
			.add("--dest", distDir);
		bach().run(createAppImage);

		var createAppPackage = Command.of("jpackage")
			.add("--name", projectName)
			.add("--app-image", distDir)
			.add("--app-version", project().version())
			.add("--dest", distDir);
		bach().run(createAppPackage);
	}
}