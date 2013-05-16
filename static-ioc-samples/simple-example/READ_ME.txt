If you're using Maven:

  If you use the staticioc plugin as shown in the sample, the compiled application context will be generated automatically during generate-sources phase.
  If you want to invoke this goal manually, type mvn statioc:generate

If you're not using Maven, use the following commands:

  java -cp /path/to/staticioc-commandline-*.jar  org.staticioc.StaticIoCCommandLineCompiler -g org.staticioc.generator.JavaCodeGenerator -o src/main/java -t org.staticioc.samples.ApplicationContext:src/main/resources/applicationContext.xml 
