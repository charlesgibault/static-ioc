package org.staticioc.generator;

import org.staticioc.model.*;

public interface CodeGenerator {
	void comment( String comment );
	void headerComment( String comment );
	
	void declareBean( Bean bean);	
	void instantiateBean( Bean bean);

	void declareProperty( Bean bean, final Property property );

	void initClass(String className);

	void closeClass(String className);

	void initConstructor(String className);

	void closeConstructor(String className);
}