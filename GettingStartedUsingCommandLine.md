# Introduction #

A quick setup guide to get you started if you're using the command line tool. (Note: if you are using Maven, you should refer to GettingStartedUsingMaven)

# Details #

## Grab command line tool ##

Command line tool is packaged as a single jar in the release package that you can download from [The project's download page](http://code.google.com/p/static-ioc/downloads/list).

Alternatively, you can also download it from [The Central Maven jar repository](http://search.maven.org/#search|ga|1|staticioc-commandline)

## Usage ##
Usage of the command line tool is as follows:
```
java -cp /path/to/staticioc-commandline.jar org.staticioc.StaticIoCCommandLineCompiler
-g,--generator <package.codeGeneratorClass>
-h,--help
-L,--target-language <Java>
-n,--namespace-plugins  <package1.nameSpacePlugin1,package2.nameSpacePlugin2,...>
-o,--output-path <path/to/output/folder>
-t,--target-mapping <package.name.target1:source1,source2;target2:source1,...>
-x,--output-file-extension <.abc>
```

For example
```

 java -cp /usr/bin/tools/java/staticioc/staticioc-commandline-0.4.jar org.staticioc.StaticIoCCommandLineCompiler -g org.staticioc.generator.JavaCodeGenerator -o src/main/java -t org.staticioc.samples.ApplicationContext:src/main/resources/applicationContext.xml
```

Mandatory command line arguments are:
- targetLanguage (only Java for now),
- targetMapping in the form of a list of <target package.class>path to corresponding application context.xml</target package.class>.

## Exhaustive options listing ##
The full listing of options supported by the static-ioc command line tool is displayed below:

| **Argument** | **Short argument** | **Mandatory** | **Description** |
|:-------------|:-------------------|:--------------|:----------------|
| --target-language | -L                 | no            | Name of the target language for generated source code (for target language supported out of the box)  |
| --generator  | -g                 | no            | Fully qualified class of the Code generator to use. Note: either this or targetLanguage **must** be defined. Note if using custom generator: do not forget to add the corresponding implementation jar on the java classpath. |
| --output-file-extension | -x                 | no            | Optional override of the output file extension (default extension provided by chosen code generator) |
| --output-path | -o                 | yes           | Location of the generated file. |
| --target-mapping | -t                 | yes           | Target mapping configurationis as a semicolon separated list ot target compiled context : comma separated configuration file list |
| --namespace-plugins | -n                 | no            | Optional additional NamespaceParser plugins to use to load XML configuration in addition to default's Spring Bean and Spring p. Note: do not forget to add the corresponding implementation jar on the java classpath. |