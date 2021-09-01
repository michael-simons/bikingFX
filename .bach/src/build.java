import com.github.sormuras.bach.Bach;
import com.github.sormuras.bach.ToolCall;
import com.github.sormuras.bach.external.JUnit;
import com.github.sormuras.bach.external.Jackson;
import com.github.sormuras.bach.external.JavaFX;
import com.github.sormuras.bach.external.Maven;
import java.lang.module.ModuleDescriptor;

class build {
  public static void main(String[] args) {
    try (var bach = new Bach(args)) {
      var grabber =
          bach.grabber(
              JavaFX.version("18-ea+2"), build::locateJacksonModule, JUnit.version("5.8.0-RC1"));

      bach.logCaption("Build Application");
      var main = bach.builder().conventionalSpace("main", "ac.simons.bikingFX");
      main.grab(grabber, "javafx.base", "javafx.controls", "javafx.fxml", "javafx.web");
      main.grab(
          grabber,
          "com.fasterxml.jackson.core",
          "com.fasterxml.jackson.databind",
          "com.fasterxml.jackson.datatype.jdk8",
          "com.fasterxml.jackson.datatype.jsr310");
      main.compile(
          javac -> javac.with("-Xlint"),
          jar ->
              jar.with("--main-class", "ac.simons.bikingFX.Application")
                  .with("-C", "ac.simons.bikingFX/main/resources", "."));

      bach.logCaption("Perform automated checks");
      var test = main.dependentSpace("test", "tests.integration");
      test.grab(grabber, "org.junit.jupiter", "org.junit.platform.console");
      test.compile(
          javac -> javac.with("-encoding", "UTF-8"),
          jar -> jar.with("-C", "tests.integration/test/resources", "."));
      test.runAllTests();

      bach.logCaption("Link and package main modules into an installer");
      main.link(link -> link.with("--launcher", "bikingFX=ac.simons.bikingFX"));

      var distAppImageDir = bach.path().workspace("dist", "app-image");
      var createAppImage =
          ToolCall.of("jpackage")
              .with("--name", "bikingFX")
              .with("--type", "app-image")
              .with("--module", "ac.simons.bikingFX/ac.simons.bikingFX.Application")
              .with("--runtime-image", bach.path().workspace("main", "image"))
              .with("--dest", distAppImageDir);
      bach.run(createAppImage);

      var distAppPackageDir = bach.path().workspace("dist", "app-package");
      var createAppPackage =
          ToolCall.of("jpackage")
              .with("--name", "bikingFX")
              .with("--app-image", distAppImageDir)
              .with(
                  "--app-version",
                  bach.configuration()
                      .projectOptions()
                      .version()
                      .orElse(ModuleDescriptor.Version.parse("1-ea")))
              .with("--dest", distAppPackageDir);
      // TODO Only run of all native tools are available... bach.run(createAppPackage);
    }
  }

  static String locateJacksonModule(String module) {
    var version = "2.12.4";
    return switch (module) {
      case "com.fasterxml.jackson.datatype.jdk8" //
      -> Maven.central("com.fasterxml.jackson.datatype", "jackson-datatype-jdk8", version);
      case "com.fasterxml.jackson.datatype.jsr310" //
      -> Maven.central("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", version);
      default -> Jackson.version(version).locate(module);
    };
  }
}
