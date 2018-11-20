package com.truenorth.campsite.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class Campsites extends CampsiteModel implements Serializable{

	private static final long serialVersionUID = -1060143392154579815L;
	
	@NotNull
	private String name;
	@NotNull
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date openDate;
	@NotNull
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date closeDate;
	private List<ObjectId> reservationIds;

	public Campsites() {
	}

	public Campsites(ObjectId _id, String name, Date initialDate, Date finalDate) {
		this._id = _id;
		this.name = name;
		this.openDate = initialDate;
		this.closeDate = finalDate;
		this.reservationIds = new ArrayList<ObjectId>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getOpenDate() {
		return openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public List<ObjectId> getReservationIds() {
		return reservationIds;
	}

	public void setReservationIds(List<ObjectId> reservationIds) {
		this.reservationIds = reservationIds;
	}

}
