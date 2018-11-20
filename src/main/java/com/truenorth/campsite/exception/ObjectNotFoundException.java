package com.truenorth.campsite.exception;

public class ObjectNotFoundException  extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public ObjectNotFoundException(String element) {
		super("Element not found. Element: " + element);
	}
}
