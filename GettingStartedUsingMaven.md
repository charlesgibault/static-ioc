# Introduction #

A quick setup guide to get you started if you're using Maven as a build tool.

# Details #

## Importing staticioc artifacts in your Maven repository ##

Starting from version 0.3, static-ioc project is hosted on the Maven Central repository, so setting up your project is as simple as declaring the maven dependency as shown below:

For older release, you can either drop the content of the packaged "Maven repository to your Maven repository or, from the source distribution, use the following command: `mvn install`.

## Configuring your project ##
Declare the plugin as follows:

```
 <plugins>
   <plugin>
     <groupId>com.googlecode.static-ioc</groupId>
     <artifactId>staticioc-maven-plugin</artifactId>
     <version>0.4</version>
     <executions>
      <execution>
        <goals>
             <goal>generate</goal>
        </goals>
      </execution>
     </executions>
     <configuration>
             <targetLanguage>Java</targetLanguage>
             <targetMapping>
                <org.staticioc.samples.ApplicationContext>src/main/resources/applicationContext.xml</org.staticioc.samples.ApplicationContext>
             </targetMapping>
     </configuration>
   </plugin>
</plugins>
```

Mandatory configuration items are:
- targetLanguage (only Java for now),
- targetMapping in the form of a list of <target package.class>path to corresponding application context.xml</target package.class>.

Files will be generated in src/main/java by default. You can change that using the optional outputPath configuration item (see below).



Optional yet common configuration items includes:
- Compiled context generation output path,
- Extra namespace plugins,
- plugin dependencies definition (for extra namespace plugins and for custom code generators).

The following example displays a slightly more advanced configuration which outputs generated files in generated-sources/gwt inside project's build directory.

```
<plugins>
      <!-- StaticIoc Maven plugin -->
      <plugin>
        <groupId>com.googlecode.static-ioc</groupId>
        <artifactId>staticioc-maven-plugin</artifactId>
        <version>0.4</version>
        <executions>
         <execution>
           <goals>
                <goal>generate</goal>
           </goals>
         </execution>
        </executions>
        <configuration>
                <targetLanguage>Java</targetLanguage>
       		<outputPath>${project.build.directory}/generated-sources/gwt</outputPath>
                <targetMapping>
                	<org.staticioc.samples.gwt.client.ApplicationContext>${basedir}/src/main/resources/applicationContext.xml</org.staticioc.samples.gwt.client.ApplicationContext>
                </targetMapping>
                <namespacePlugins>
  	 		<namespacePlugin>org.staticioc.parser.namespace.gwt.GwtNamespaceParser</namespacePlugin>
  	 	</namespacePlugins>
        </configuration>
        <dependencies> <!-- Add namespace  or custom code generators plugins here -->
            <dependency>	
		<groupId>com.googlecode.static-ioc</groupId>
		<artifactId>static-ioc-namespace-gwt</artifactId>
		<version>${project.version}</version>
		<scope>compile</scope>
	    </dependency>
        </dependencies>
      </plugin>
</plugins>
```

## Exhaustive options listing ##
The full listing of options supported by the static-ioc maven plugin is displayed below:

| **Property** | **Mandatory** | **Description** |
|:-------------|:--------------|:----------------|
| targetLanguage | no            | Name of the target language for generated source code (for target language supported out of the box)  |
| generator    | no            | Fully qualified class of the Code generator to use. Note: either this or targetLanguage **must** be defined. Note if using custom generator: do not forget to add the corresponding implementation as a plugin compile dependency. |
| outputFileExtension | no            | Optional override of the output file extension (default extension provided by chosen code generator) |
| outputPath   | no            | Location of the generated file. By default : put into the project's source directory which is usually src/main/java |
| createOutputPathIfMissing | no            | Indicate whether to create the outputPath structure if not present. Default to true. It must be set to true to output in ${project.build.directory}/generated-sources |
| targetMapping | yes           | Target mapping configuration as a list ot target compiled context / comma separated configuration file list |
| namespacePlugins | no            | Optional additional NamespaceParser plugins to use to load XML configuration in addition to default's Spring Bean and Spring p. Note: do not forget to add the corresponding implementation as a plugin compile dependency. |


## Compilation of the application context and inclusion in maven phases cycle ##

The compiled application context will be generated automatically during the generate-sources phase.

If you want to invoke this goal manually, simply type
```
mvn statioc:generate
```

You can refer to static-ioc-samples project's source code for more detailed implementation examples.