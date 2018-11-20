package com.truenorth.campsite.exception;

public class DifferenceDayReservationException  extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DifferenceDayReservationException() {
		super("The arrival and departure dates does not have a difference of 3 days");
	}
}
