package com.truenorth.campsite.exception;

public class DateRangeException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public DateRangeException() {
		super("The second date must be greater than the first date.");
	}
}
