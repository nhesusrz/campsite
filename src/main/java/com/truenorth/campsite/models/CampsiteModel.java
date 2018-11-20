package com.truenorth.campsite.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class CampsiteModel {

	@Id
	protected ObjectId _id;

	public CampsiteModel() {

	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}
}
