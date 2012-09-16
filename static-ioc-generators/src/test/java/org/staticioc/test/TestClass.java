package org.staticioc.test;

public class TestClass {
	public final org.staticioc.model.Bean bean;

	public TestClass() {
		bean = new org.staticioc.model.Bean();
		bean.setName("value");
	}

	public void destroyContext() {
	}
}
