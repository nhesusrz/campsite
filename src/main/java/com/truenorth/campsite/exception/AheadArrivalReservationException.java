package com.truenorth.campsite.exception;

public class AheadArrivalReservationException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public AheadArrivalReservationException() {
		super("The initial date must be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.");
	}

}
