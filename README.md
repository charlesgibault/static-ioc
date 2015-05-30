# static ioc in a nutshell

The goal of this project is to allow the use of the Inversion of Control pattern at compile time so that it can be applied in any context (and for virtually any langage in the future):

IoC configuration files are resolved at compile time and converted into the appropriate piece of source code.

IoC configuration file uses the very-wide spread Spring framework XML format. Generated code won't depend on any framework, making it easy to integrate it into any kind of application.

#Usage example

Using static-ioc, it is now possible to use IoC without having to bundle a full framework. This is especially useful is you're doing development with framework like GWT or Playn.

To quickly setup your project, please visit

- GettingStartedUsingMaven if you're using Maven,
- GettingStartedUsingCommandLine otherwise. 

For Maven user, project is available through the Maven Central repository!

If you're a GWT user, you should take a look at GettingStartedWithGwtNamespace page.

For concrete implementation, don't miss the samples part for:

- A simple example project with static-ioc integrated with maven.
- A simple GWT application that demonstrates how MVP architecture benefits from static-ioc injection. 

#Status

Version 0.4 has been released with

    A lots of bug fixes and of code cleaning,
    A better coverage of the Spring Bean elements,
    Better documentation (javadoc/wiki)
    XML namespace support
    A GWT namespace is also provided to ease your GWT configuration. 

Consult detailed Releasenotes page.
Where we are headed and current limitations

Consult Roadmap of the projet.

Consult KnownLimitations page. 

(formerly code.google.com/p/static-ioc)
