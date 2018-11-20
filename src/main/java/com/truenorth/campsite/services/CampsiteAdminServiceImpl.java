package com.truenorth.campsite.services;

import java.util.Map;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.truenorth.campsite.exception.DateRangeException;
import com.truenorth.campsite.exception.ObjectNotFoundException;
import com.truenorth.campsite.models.CampsiteModel;
import com.truenorth.campsite.models.Campsites;
import com.truenorth.campsite.repositories.CampsitesRepository;

@Service
public class CampsiteAdminServiceImpl implements CampsiteService {

	private static final Logger LOG = LoggerFactory.getLogger(CampsiteAdminServiceImpl.class);

	@Autowired
	private CampsitesRepository repository;

	@Override
	public Map<String, Object> getAll() {
		responseMap.clear();
		try {
			responseMap.put(ServiceEnum.CAMPSITES.toString(), repository.findAll());
		} catch (Exception e) {
			responseMap.put(ServiceEnum.ERROR.toString(), e.getMessage());
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getById(ObjectId id) {
		responseMap.clear();
		try {
			if (!repository.existsById(id.toString()))
				throw new ObjectNotFoundException("id");
			responseMap.put(ServiceEnum.CAMPSITE.toString(), repository.findBy_id(id));
		} catch (ObjectNotFoundException e) {
			responseMap.put(ServiceEnum.ERROR.toString(), e.getMessage());
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> save(CampsiteModel object) {
		responseMap.clear();
		Campsites campsite = (Campsites) object;
		try {
			if (campsite.getCloseDate().before(campsite.getOpenDate())
					|| campsite.getCloseDate().equals(campsite.getOpenDate()))
				throw new DateRangeException();
			if (((Campsites) object).get_id() == null)
				((Campsites) object).set_id(ObjectId.get());
			repository.save((Campsites) object);
			responseMap.put(ServiceEnum.CAMPSITES.toString(), (Campsites) object);
		} catch (DateRangeException e) {
			responseMap.put(ServiceEnum.ERROR.toString(), e.getMessage());
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> update(ObjectId id, CampsiteModel object) {
		responseMap.clear();
		Campsites campsite = (Campsites) object;
		try {
			if (!repository.existsById(id.toString()))
				throw new ObjectNotFoundException("id");
			if (campsite.getCloseDate().before(campsite.getOpenDate())
					|| campsite.getCloseDate().equals(campsite.getOpenDate()))
				throw new DateRangeException();
			campsite.set_id(id);
			repository.save((Campsites) object);
			responseMap.put(ServiceEnum.CAMPSITE.toString(), campsite);
		} catch (ObjectNotFoundException | DateRangeException e) {
			responseMap.put(ServiceEnum.ERROR.toString(), e.getMessage());
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> delete(ObjectId id) {
		responseMap.clear();
		try {
			if (!repository.existsById(id.toString()))
				throw new ObjectNotFoundException("id");
			repository.delete(repository.findBy_id(id));
			responseMap.put(ServiceEnum.MESSAGE.toString(), ServiceEnum.MESSAGE_DELETED.toString());
		} catch (ObjectNotFoundException e) {
			responseMap.put(ServiceEnum.ERROR.toString(), e.getMessage());
		}
		return responseMap;
	}

}
