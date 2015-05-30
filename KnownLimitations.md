# Known limitations #

Developers that are used to the [Spring Framework](http://projects.spring.io/spring-framework/) and especially the Inversion of Control part (IoC Container) should be aware of the following limitations when using static-ioc.

  * Auto-wiring of Bean is not supported
  * Annotation based configuration is not supported,
  * PropertyPlaceHolder mechanism is not available yet.
  * Specifying a ref-local bean reference may not prevent its resolution to an external bean.
  * Spring's InitializingBean interface is not supported. If your looking for an equivalent of the afterPropertiesSet() callback, you must use the init-method attribute in the Bean definition.

Most limitations will be lifted in the future. Please refer to the Roadmap page for current fix plan.

Note: this list contains all known limitations but may not be exhaustive. If you find something that is not working as you expect, please file a bug report.