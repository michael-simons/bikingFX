# bikingFX

## Abstract

This is a little companion project for [biking2][1], which is live right at [biking.michael-simons.eu][2]. It uses the same API the AngularJS frontend uses, but for a [JavaFX 8 / Java 8][3] application which was created from scratch after seeing several very interesting JavaFX sessions at JavaOne 2014. 

The source code is presented as is, the application maybe has no real use for other people than me, but I think it shows some of things that are possible with and very easy to create with JavaFX.

The project can be build using Maven, tested on Mac OS X and Windows 7. An additional profile (mvn -DcreateInstaller=true package) is provided to create native installers for the deployment platform.

The blog post describing that project and part of the application got a little bit out of hand, it has become kind of epic, at least it's proportions:

["Getting started with JavaFX 8: Developing a REST client application from scratch"][4]

[1]: https://github.com/michael-simons/biking2
[2]: http://biking.michael-simons.eu
[3]: http://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm
[4]: http://info.michael-simons.eu/2014/10/22/getting-started-with-javafx-8-developing-a-rest-client-application-from-scratch/