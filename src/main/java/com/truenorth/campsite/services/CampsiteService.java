package com.truenorth.campsite.services;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bson.types.ObjectId;

import com.truenorth.campsite.models.CampsiteModel;

public interface CampsiteService {

	Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

	public abstract Map<String, Object> getAll();

	public abstract Map<String, Object> getById(ObjectId id);

	public abstract Map<String, Object> save(CampsiteModel object);

	public abstract Map<String, Object> update(ObjectId id, CampsiteModel object);

	public abstract Map<String, Object> delete(ObjectId id);

}
