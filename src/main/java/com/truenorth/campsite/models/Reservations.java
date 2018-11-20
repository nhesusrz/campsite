package com.truenorth.campsite.models;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class Reservations extends CampsiteModel implements Serializable {

	private static final long serialVersionUID = -3499739006724123845L;

	@NotNull
	private String email;
	@NotNull
	private ObjectId campsiteId;
	@NotNull
	private String name;
	@NotNull
	private String surname;
	@NotNull
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date arrivalDate;
	@NotNull
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date departureDate;

	public Reservations() {

	}

	public Reservations(ObjectId _id, String email, ObjectId campsiteId, String name, String surname, Date arrivalDate,
			Date departurelDate) {
		super();
		this._id = _id;
		this.email = email;
		this.campsiteId = campsiteId;
		this.name = name;
		this.surname = surname;
		this.arrivalDate = arrivalDate;
		this.departureDate = departurelDate;
	}

	public ObjectId getCampsiteId() {
		return campsiteId;
	}

	public void setCampsiteId(ObjectId campsiteId) {
		this.campsiteId = campsiteId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

}
