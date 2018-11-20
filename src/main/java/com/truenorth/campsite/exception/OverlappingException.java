package com.truenorth.campsite.exception;

public class OverlappingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OverlappingException() {
        super("The reservation dates is overlapping another reservation");
    }

}
