package com.truenorth.campsite.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.truenorth.campsite.models.Campsites;
import com.truenorth.campsite.services.CampsiteAdminServiceImpl;

@RestController
public class CampsiteController {

	@Autowired
	private CampsiteAdminServiceImpl campsiteService;

	@RequestMapping(value = { "/campsites" }, method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getAllCampsites() {
		Map<String, Object> responseMap = campsiteService.getAll();
		if (responseMap.containsKey("Error"))
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
	}

	@RequestMapping(value = { "/campsites/{id}" }, method = RequestMethod.GET)
	// @Cacheable(value = "campsites", key = "#id", sync = true)
	public ResponseEntity<Map<String, Object>> getCampsiteById(
			@PathVariable(value = "id", required = true) ObjectId id) {
		Map<String, Object> responseMap = campsiteService.getById(id);
		if (responseMap.containsKey("Error"))
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
	}

	@RequestMapping(value = { "/campsites" }, method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> createCampsite(@Valid @RequestBody(required = true) Campsites campsite) {
		Map<String, Object> responseMap = campsiteService.save(campsite);
		if (responseMap.containsKey("Error"))
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
	}

	@RequestMapping(value = { "/campsites/{id}" }, method = RequestMethod.PUT)
	// @CachePut(value = "campsites", key = "#id")
	public ResponseEntity<Map<String, Object>> modifyCampsiteById(
			@PathVariable(value = "id", required = true) ObjectId id,
			@Valid @RequestBody(required = true) Campsites campsite) {
		Map<String, Object> responseMap = campsiteService.update(id, campsite);
		if (responseMap.containsKey("Error"))
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
	}

	@RequestMapping(value = { "/campsites/{id}" }, method = RequestMethod.DELETE)
	@CacheEvict(value = "campsites", allEntries = true)
	public ResponseEntity<Map<String, Object>> deleteCampsite(
			@PathVariable(value = "id", required = true) ObjectId id) {
		Map<String, Object> responseMap = campsiteService.delete(id);
		if (responseMap.containsKey("Error"))
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
	}

}
