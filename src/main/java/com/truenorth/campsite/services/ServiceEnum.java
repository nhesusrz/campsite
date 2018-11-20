package com.truenorth.campsite.services;

public enum ServiceEnum {
	
	RESERVATIONS("Reservations"),
	RESERVATION("Reservation"),
	
	CAMPSITES("Campsites"),
	CAMPSITE("Campsite"),
	
	ERROR("Error"),
	MESSAGE("Message"),
	MESSAGE_DELETED("Sucessfully deleted.");
	
	private final String description;
	
	private ServiceEnum(String description) {
		this.description = description;
		
	}
	
	public String getDescription( ) {
		return description;
	}
	
	@Override
	public String toString() {
		return description;
	}

}
