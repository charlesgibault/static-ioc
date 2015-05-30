# Presentation #

Starting with release 0.4, static-ioc support namespace plugins and ships with a namespace dedicated to the [GWT](http://www.gwtproject.org/) technology.

## Usage ##
You can now enjoy a simplied configuration of the most used GWT objects like i18n interfaces, RPC services...

To use the GWT namespace, you need to
  * Configure static-ioc to add org.staticioc.parser.namespace.gwt.GwtNamespaceParser as a namespace plugin,
  * Make sure the static-ioc-namespace-gwt.jar is on your classpath.

For example with Maven:
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

## Example ##

The following configuration snippets demonstrates the use of the GWT namespace.

```
<beans 	xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:p="http://www.springframework.org/schema/p"
	xmlns:gwt="http://com.googlecode.static-ioc/schema/gwt"
	xsi:schemaLocation="http://com.googlecode.static-ioc/schema/gwt https://static-ioc.googlecode.com/git/static-ioc-namespaces/static-ioc-namespace-gwt/src/main/xsd/static-ioc-gwt-1.0.xsd http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd">

	<gwt:messages name="messages" class="org.staticioc.samples.gwt.client.Messages"/> 

	<gwt:service name="contactsService" interface="org.staticioc.samples.gwt.client.service.ContactsService"/>
	
	<gwt:eventBus name="eventBus" class="com.google.gwt.event.shared.EventBus" />

	<bean name="contactsPresenter" class="org.staticioc.samples.gwt.client.presenter.ContactsPresenterImpl">
   	    <property name="messages" ref="messages"/>
	    <property name="contactsService" ref="contactsService"/>
	    <property name="eventBus" ref="eventBus"/>
	</bean>

</beans>
```

For a (much) more detailed example in an MVP architectured - Activity/Places based GWT application, please refer to the statioc-ioc-samples-gwt project in the source distribution (or on the trunk).

## XML namespace schema definition ##

GWT namespace schema is http://com.googlecode.static-ioc/schema/gwt
Its definition is available in XSD format here : https://static-ioc.googlecode.com/git/static-ioc-namespaces/static-ioc-namespace-gwt/src/main/xsd/static-ioc-gwt-1.0.xsd

## Namespace elements ##

Below are listed all namespace element

### messages ###

The messages element has the following arguments:

| **Argument** | **Mandatory** | **Description** |
|:-------------|:--------------|:----------------|
| name         | no            | name of the Bean being instanciated. Default value: messages |
| id           | no            | Unique identifier of the Bean being instanciated. Default value: messages |
| class        | yes           | i18n interface to be implemented by GWT.create() mechanism |


### service ###

The service element has the following arguments:

| **Argument** | **Mandatory** | **Description** |
|:-------------|:--------------|:----------------|
| name         | no            | name of the Bean being instanciated. Default value: Auto-generated value |
| id           | no            | Unique identifier of the Bean being instanciated. Default value: Auto-generated value |
| interface    | yes           | Synchronous interface to be implemented as an asynchronous client stub by GWT.create() mechanism |


### eventBus ###

The eventBus element has the following arguments:

| **Argument** | **Mandatory** | **Description** |
|:-------------|:--------------|:----------------|
| name         | no            | name of the Bean being instanciated. Default value: eventBus |
| id           | no            | Unique identifier of the Bean being instanciated. Default value: eventBus |
| interface    | no            | Interface of the EventBus instanciated by GWT.create(). Default type: com.google.web.bindery.event.shared.EventBus (GWT 2.4+) |
| class        | no            | Actual implementation to be requested with GWT.create() for the eventBus. Default value: SimpleEventBus |


### historyMapper ###

The historyMapper element has the following arguments:

| **Argument** | **Mandatory** | **Description** |
|:-------------|:--------------|:----------------|
| name         | no            | name of the Bean being instanciated. Default value: historyMapper |
| id           | no            | Unique identifier of the Bean being instanciated. Default value: historyMapper |
| class        | yes           | Interface defining the PlaceHistoryMapper extension with the application's PlaceTokenizer |


### activityManager ###

The activityManager element has the following arguments:

| **Argument** | **Mandatory** | **Description** |
|:-------------|:--------------|:----------------|
| name         | no            | name of the Bean being instanciated. Default value: activityManager |
| id           | no            | Unique identifier of the Bean being instanciated. Default value: activityManager |
| eventBus     | no            | Reference to the EventBus instance to use. Default value: eventBus |
| activityMapper | no            | Reference to the ActivityMapper instance to use. Default value: activityMapper |
| display      | no            | Reference to the Widget to display activities' views. Default value: null (must be set manually then) |
| class        | no            | Class of the ActivityManager implementation. Default value: com.google.gwt.activity.shared.ActivityManager |

### placeController ###

The placeController has the following arguments:

| **Argument** | **Mandatory** | **Description** |
|:-------------|:--------------|:----------------|
| name         | no            | name of the Bean being instanciated. Default value: placeController |
| id           | no            | Unique identifier of the Bean being instanciated. Default value: placeController |
| eventBus     | no            | Reference to the EventBus instance to use. Default value: eventBus |
| class        | no            | Class of the PlaceController implementation. Default value: com.google.gwt.place.shared.PlaceController |


### create ###

The create element has the following arguments:

| **Argument** | **Mandatory** | **Description** |
|:-------------|:--------------|:----------------|
| name         | no            | name of the Bean being instanciated. Default value: Auto-generated value |
| id           | no            | Unique identifier of the Bean being instanciated. Default value: Auto-generated value |
| interface    | yes           | Interface to be stubbed by the GWT.create() mechanism |
| class        | yes           | Actual interface whose implementation stub is returned by the GWT.create() mechanism |