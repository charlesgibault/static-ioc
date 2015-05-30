# Presentation #
Release known for released version of the project is shown below.

## Release 0.1 ##

  * Initial release. Java target with maven plugin and command line tool


## Release 0.2 ##
  * **Core** - Made beans declaration, instanciation, and setup order predictable
  * **Core** - Added name alias support for bean when id and name are both declared
  * **Core** - Fixed constructor arguments support when no index was provided or when using direct value/ref/idref attribute
  * **Core** - Add support for factory bean / factory method
  * **Generator** - Implement destructor workflow
  * **Java Generator** - Add proper @SuppressWarnings to have warnings free class when generics are in use
  * **Java Generator** - Fix handling of .class parameters
  * **Samples** - Add full GWT sample project using MVP


## Release 0.3 ##
  * **Releasing** - Changed project groupid to com.googlecode.static-ioc
  * **Releasing** - Added PGP signature for deployed artifacts
  * **Releasing** - Published version 0.3 on OSS Sonatype / Maven Central repository
  * **Core** - Refactor collection and parent dependency for cleaner static-ioc core module classes
  * **Core** - Add plugin chain mechanism to ease extension of supported nodes
  * **Core** - Add support for init-method/ afterPropertiesSet mechanism for post initing bean
  * **Core** - Add support for destroy-method / Teardown() mechanism
  * **Core** - Fix prototype bean's naming convention
  * **Core** - Handle dependency with constructor args reference for proper order declaration
  * **Core** - Improve dependency resolution with factory-bean for proper order declaration
  * **Core** - Improved Prototype and alias resolution
  * **Generator** - Add support for init-method and destroy-methods
  * **Generator** - Fixed indentation on destructor
  * **Java Generator** - Changed @SuppressWarnings annotations to a @SuppressWarnings(all)
  * **Samples** - Use init-method to simplify GWT sample
  * **Samples** - Cleaned-up GWT samples (removed warnings)


## Release 0.4 ##
  * **Core** - Add partial support for ref local="bean Id" declaration
  * **Core** Add alias node support
  * **Core** - Add namespace plugin mechanism
  * **Core** - Refactored SpringConfigParser for proper concern separation. Spring Bean parsing fully migrated to NodeSupport/NodeParser plugins
  * **Core** - Javadoc improvement
  * **Core** - Code cleaning
  * **Namespace** - Add namespace plugin for GWT to make bean definition less versbose.
  * **Namespace** - XML schema's (http://com.googlecode.static-ioc/schema/gwt) XSD is available at https://static-ioc.googlecode.com/git/static-ioc-namespaces/static-ioc-namespace-gwt/src/main/xsd/static-ioc-gwt-1.0.xsd
  * **Generator** - Javadoc improvement
  * **Generator** - Code cleaning
  * **Generator** - Add namespace plugin mechanism
  * **MavenPlugin** - Add namespace plugin mechanism
  * **CommandLine** - Add namespace plugin mechanism
  * **Samples** - Use GWT bean plugin to simplify GWT sample
  * **Wiki** Improve website documentation