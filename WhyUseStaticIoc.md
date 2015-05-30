# Why use Inversion of Control pattern #

Dependency injection is a programming technique which allows to enforce interface based relationships between components.
By preventing components to know the actual implementation class of their neighboring components, this technique helps achieving loosely coupled components.

Another benefit of IoC is for unit tests: one can easily isolate one component of the system and test it thoroughly by injecting mock dependencies. Anyone who tried to do some unit testing in a large singleton based system will agree here.

More details available on the [Inversion of Control Wiki page](http://en.wikipedia.org/wiki/Inversion_of_control).

# Popular implementations #

There a numerous popular IoC frameworks out there, for a lot of languages. The Java language is especially well furnished in this matter with quality and well established frameworks like [Spring IoC](http://docs.spring.io/spring/docs/3.0.x/reference/beans.html) and [Google Guice](http://code.google.com/p/google-guice/).

These frameworks work by inspectings your code and/or configuration files at run time, during the start-up of your application, and assembling it dynamically.

# Why use static-ioc #

## Using your usual IoC framework is not possible in your context ##

There are cases where having your well known IoC framework instanciate your application dynamically at run time is not possible.

For example, if you are working on a large Java application these days, there are rather high chances that the application components run in the Spring IOC container.
If the presentation part of your system uses the GWT technology, GWT compiler constraints will prevent you from using the Spring IOC container for the presentation part.
Indeed, it requires a lot of runtime dependencies like the XML stack to be available, with is not possible (nor wanted) with GWT.

There are of course solutions available to achieve Inversion of Control with GWT.
[GIN](http://code.google.com/p/google-gin/) - a bridge to use [Google Guice](http://code.google.com/p/google-guice/) and perform IOC during GWT compile time - is one of them.
But now that your development team knows Spring IOC configuration file formalism, wouldn't it be nice if they could use the same formalism for the presentation part ?
Wouldn't that be a good reason to use static-ioc for your development.

## Start-up performance of your application is critical ##

Sometime, while it would be technically possible to instanciate the application dynamicallly, it is just not wanted, since it involves consuming extra resources and is slower than a compiled library.
Embedded and mobile applications are a good example for this, with smartphones still having a limited amount of CPU and RAM available (despite continuous improvements lately).
In that case, leveradging the benefits of the Inversion of Control pattern without compromising start-up performance can be done with static-ioc.
