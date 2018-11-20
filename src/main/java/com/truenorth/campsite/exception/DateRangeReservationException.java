package com.truenorth.campsite.exception;

public class DateRangeReservationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DateRangeReservationException() {
		super("The reservation is not in range with the campsite date.");
	}

}
