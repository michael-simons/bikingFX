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

import static com.github.sormuras.bach.ProjectInfo.Externals.Name.JAVAFX;
import static com.github.sormuras.bach.ProjectInfo.Externals.Name.JUNIT;

import com.github.sormuras.bach.ProjectInfo;
import com.github.sormuras.bach.ProjectInfo.External;
import com.github.sormuras.bach.ProjectInfo.Externals;
import com.github.sormuras.bach.ProjectInfo.Launcher;
import com.github.sormuras.bach.ProjectInfo.Tools;
import com.github.sormuras.bach.ProjectInfo.Tweak;
import com.github.sormuras.bach.project.JavaStyle;

/**
 * @author Michael J. Simons
 */
@ProjectInfo(
	name = "bikingFX",
	version = "2021.0.0",
	compileModulesForJavaRelease = 16,
	format = JavaStyle.GOOGLE,
	launcher = @Launcher(command = "bikingFX", module = "ac.simons.bikingFX", mainClass = "ac.simons.bikingFX.Application"),
	tools = @Tools(limit = { "javac", "jar", "junit", "jlink", "jpackage" }),
	modules = "*/main/java",
	testModules = "*/test/java",
	tweaks = {
		@Tweak(tool = "javac", option = "-encoding", value = "UTF-8"),
		@Tweak(tool = "javac", option = "-g"),
		@Tweak(tool = "javac", option = "-parameters"),
		@Tweak(tool = "javac", option = "-Xlint")
	},
	testTweaks = {
		@Tweak(tool = "javac", option = "-encoding", value = "UTF-8"),
		@Tweak(tool = "junit", option = "--fail-if-no-tests")
	},
	lookupExternals = {
		@Externals(name = JAVAFX, version = "16"),
		@Externals(name = JUNIT, version = "5.7.1")
	},
	lookupExternal = {
		@External(module = "com.fasterxml.jackson.core", via = "com.fasterxml.jackson.core:jackson-core:2.12.2"),
		@External(module = "com.fasterxml.jackson.annotation", via = "com.fasterxml.jackson.core:jackson-annotations:2.12.2"),
		@External(module = "com.fasterxml.jackson.databind", via = "com.fasterxml.jackson.core:jackson-databind:2.12.2"),
		@External(module = "com.fasterxml.jackson.datatype.jdk8", via = "com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.12.2"),
		@External(module = "com.fasterxml.jackson.datatype.jsr310", via = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.2")
	}
) module bach.info {
	requires com.github.sormuras.bach;
	provides com.github.sormuras.bach.Bach.Provider with bach.info.BikingBach;
}