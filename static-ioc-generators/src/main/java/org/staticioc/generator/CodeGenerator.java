/**
 *  Copyright (C) 2012 Charles Gibault
 *
 *  Static IoC - Compile XML based inversion of control configuration file into a single init class, for many languages.
 *  Project Home : http://code.google.com/p/static-ioc/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.staticioc.generator;

import org.staticioc.model.*;

/**
 * @author Charles Gibault
 * @date 6 sept. 2012
 */
public interface CodeGenerator
{
	/// Define were code (and comment is expected)
	enum Level {HEADER, CLASS, METHOD };
	
	/**
	 * Change the target StringBuilder to which actual generated code is appended 
	 * @param output
	 */
	void setOutput( StringBuilder output );
	
	/**
	 * Compute the path were a class's source code is expected (relative to the root of source directory)
	 *  
	 * @param fullClassName 
	 * @return the path to that file
	 */
	String getFilePath( String fullClassName );
	
	/**
	 * Extract the class name from a fully qualified (package.classname) class name
	 *  
	 * @param fullClassName  fully qualified class name
	 * @return class name
	 */
	String getClassName( String fullClassName );
	
	/**
	 * Extract the package name from a fully qualified (package.classname) class name
	 *  
	 * @param fullClassName  fully qualified class name
	 * @return package name
	 */
	String getPackageName( String fullClassName );
	
	/**
	 * @return the default source file extension for the target language
	 */
	String getDefaultSourceFileExtension();
	
	/**
	 * 
	 * @param comment to be inserted, to make generated code prettier (optional)
	 * @param level of the comment
	 */
	void comment( Level level, String comment );
	
	/**
	 * Declare a Bean :
	 * Anonymous and prototype beans are declared private while other beans are declared public
	 * @param bean
	 */
	void declareBean( Bean bean);
	
	/**
	 * Perform bean instantiation using provided constructor args (or default constructor)
	 * @param bean
	 */
	void instantiateBean( Bean bean);

	/**
	 * Perform bean setup for a given property
	 * @param bean
	 * @param property
	 */
	void declareProperty( Bean bean, final Property property );

	/**
	 * Define the package of a class (beginning)
	 * @param packageDef
	 */
	void initPackage(String packageDef );
	
	/**
	 * Close the package definition of a class
	 * @param packageDef
	 */
	void closePackage(String packageDef );
	
	/**
	 * Init the declaration of a class
	 */
	void initClass(String className);
	
	/**
	 * Terminate the declaration of a class
	 */
	void closeClass(String className);

	/**
	 * Declare the constructor of a given class
	 * @param className
	 */
	void initConstructor(String className);
	
	/**
	 * Terminate the declaration of a constructor
	 */
	void closeConstructor(String className);
	
	/**
	 * Declare the destructor of a given class : all declared beans are to be freed
	 * @param className
	 */
	void initDestructor(String className);
	
	/**
	 * Terminate the declaration of a destructor
	 */
	void closeDestructor(String className);
	
	/**
	 * Free memory for the given Bean
	 */
	void deleteBean(Bean bean);
}