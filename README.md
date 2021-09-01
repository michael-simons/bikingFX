# bikingFX

## Abstract

This is a little companion project for [biking2][1], which is live right at [biking.michael-simons.eu][2]. It uses the same API the AngularJS frontend uses, but for a [JavaFX 8 / Java 8][3] application which was created from scratch after seeing several very interesting JavaFX sessions at JavaOne 2014. 

The source code is presented as is, the application maybe has no real use for other people than me, but I think it shows some of things that are possible with and very easy to create with JavaFX.

This application is modularized for a time after JDK 8 and uses the ["bach"][5] build tool. 
It is included with the source code. All you have to do is bring JDK 17.

```
.bach/bin/bach build # Compile, test and packages the application
```

_bach_ orchestrates tools that are available in the JDK by default.

_bach_ lives in `.bach/bin` and it executes plain Java programs that perform the build and other tasks. 
This is the build program executed here: [.bach/src/build.java](.bach/src/build.java)

The tools used are:

* `javac` compiles Java sources
* `jar` packages up all kinds of things (class files, resources etc.)
* `jlink` creates a customized runtime image (this application and a VM)
* `jpackage` creates a host specific executable from the above image as well as an installer.

`junit` sticks out, as it is not a JDK tool but another module that is used via Java's [ToolProvider][6]. "What?" I hear you saying, JUnit is a library?
Yes, but it also comes with a console runner implementing `ToolProvider` and thus being usable by _bach_.

The blog post describing that project and part of the application got a little bit out of hand, it has become kind of epic, at least it's proportions:

["Getting started with JavaFX 8: Developing a REST client application from scratch"][4]

[1]: https://github.com/michael-simons/biking2
[2]: http://biking.michael-simons.eu
[3]: http://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm
[4]: http://info.michael-simons.eu/2014/10/22/getting-started-with-javafx-8-developing-a-rest-client-application-from-scratch/
[5]: https://github.com/sormuras/bach
[6]: https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/util/spi/ToolProvider.html
