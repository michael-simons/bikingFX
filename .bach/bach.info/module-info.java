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

import com.github.sormuras.bach.ProjectInfo;
import com.github.sormuras.bach.ProjectInfo.*;
import com.github.sormuras.bach.project.CodeStyle;

/** @author Michael J. Simons */
@ProjectInfo(
    name = "bikingFX",
    version = "2021.0.0",
    options =
        @Options(
            compileModulesForJavaRelease = 16,
            formatSourceFilesWithCodeStyle = CodeStyle.GOOGLE,
            launcher =
                @Launcher(
                    command = "bikingFX",
                    module = "ac.simons.bikingFX",
                    mainClass = "ac.simons.bikingFX.Application"),
            tools = @Tools(limit = {"javac", "jar", "junit", "jlink", "jpackage"})),
    main =
        @MainSpace(
            modules = "*/main/java",
            tweaks = {
              @Tweak(tool = "javac", option = "-encoding", value = "UTF-8"),
              @Tweak(tool = "javac", option = "-g"),
              @Tweak(tool = "javac", option = "-parameters"),
              @Tweak(tool = "javac", option = "-Xlint")
            }),
    test =
        @TestSpace(
            modules = "*/test/java",
            tweaks = {
              @Tweak(tool = "javac", option = "-encoding", value = "UTF-8"),
              @Tweak(tool = "junit", option = "--fail-if-no-tests")
            }),
    libraries =
        @Libraries(
            requires = "org.junit.platform.console",
            externalModules = {
              @ExternalModule(
                  name = "com.fasterxml.jackson.core",
                  link = "com.fasterxml.jackson.core:jackson-core:2.12.2"),
              @ExternalModule(
                  name = "com.fasterxml.jackson.annotation",
                  link = "com.fasterxml.jackson.core:jackson-annotations:2.12.2"),
              @ExternalModule(
                  name = "com.fasterxml.jackson.databind",
                  link = "com.fasterxml.jackson.core:jackson-databind:2.12.2"),
              @ExternalModule(
                  name = "com.fasterxml.jackson.datatype.jdk8",
                  link = "com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.12.2"),
              @ExternalModule(
                  name = "com.fasterxml.jackson.datatype.jsr310",
                  link = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.2")
            },
            externalLibraries = {
              @ExternalLibrary(name = LibraryName.JAVAFX, version = "16"),
              @ExternalLibrary(name = LibraryName.JUNIT, version = "5.7.1"),
            }))
module bach.info {
  requires com.github.sormuras.bach;
  provides com.github.sormuras.bach.Bach.OnTestsSuccessful with bach.info.BikingBach;
}
